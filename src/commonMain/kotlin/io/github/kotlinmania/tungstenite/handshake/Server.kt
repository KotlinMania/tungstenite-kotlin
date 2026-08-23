// port-lint: source handshake/server.rs
package io.github.kotlinmania.tungstenite.handshake

import io.github.kotlinmania.tungstenite.ProtocolError
import io.github.kotlinmania.tungstenite.TungsteniteException
import io.github.kotlinmania.tungstenite.protocol.Role
import io.github.kotlinmania.tungstenite.protocol.WebSocket
import io.github.kotlinmania.tungstenite.protocol.WebSocketConfig

/**
 * Callback trait to inspect or modify the handshake response.
 */
public interface Callback {
    /** Called when the request is parsed, before sending the response. */
    public fun apply(request: Request, response: Response): Result<Response>
}

/**
 * Default no-op callback.
 */
public class NoCallback : Callback {
    override fun apply(request: Request, response: Response): Result<Response> = Result.success(response)
}

/**
 * Create a response for a WebSocket handshake request.
 */
public fun createResponse(request: Request): Response {
    if (request.method != "GET") {
        throw TungsteniteException.Protocol(ProtocolError.WrongHttpMethod)
    }
    if (request.version != "HTTP/1.1") {
        throw TungsteniteException.Protocol(ProtocolError.WrongHttpVersion)
    }

    val connectionHeader =
        request.headers.entries
            .firstOrNull {
                it.key.equals("Connection", ignoreCase = true)
            }?.value ?: throw TungsteniteException.Protocol(ProtocolError.MissingConnectionUpgradeHeader)

    val hasUpgrade = connectionHeader.split(',', ' ').any { it.trim().equals("Upgrade", ignoreCase = true) }
    if (!hasUpgrade) {
        throw TungsteniteException.Protocol(ProtocolError.MissingConnectionUpgradeHeader)
    }

    val upgradeHeader =
        request.headers.entries
            .firstOrNull {
                it.key.equals("Upgrade", ignoreCase = true)
            }?.value ?: throw TungsteniteException.Protocol(ProtocolError.MissingUpgradeWebSocketHeader)

    if (!upgradeHeader.trim().equals("websocket", ignoreCase = true)) {
        throw TungsteniteException.Protocol(ProtocolError.MissingUpgradeWebSocketHeader)
    }

    val versionHeader =
        request.headers.entries
            .firstOrNull {
                it.key.equals("Sec-WebSocket-Version", ignoreCase = true)
            }?.value ?: throw TungsteniteException.Protocol(ProtocolError.MissingSecWebSocketVersionHeader)

    if (versionHeader.trim() != "13") {
        throw TungsteniteException.Protocol(ProtocolError.MissingSecWebSocketVersionHeader)
    }

    val key =
        request.headers.entries
            .firstOrNull {
                it.key.equals("Sec-WebSocket-Key", ignoreCase = true)
            }?.value ?: throw TungsteniteException.Protocol(ProtocolError.MissingSecWebSocketKey)

    val acceptKey = deriveAcceptKey(key)

    val responseHeaders =
        mapOf(
            "Upgrade" to "websocket",
            "Connection" to "Upgrade",
            "Sec-WebSocket-Accept" to acceptKey,
        )

    return Response(
        statusCode = 101,
        version = "HTTP/1.1",
        headers = responseHeaders,
    )
}

/**
 * Server handshake machine.
 */
public class ServerHandshake<S, C : Callback>(
    public val stream: S,
    public val callback: C,
    public val config: WebSocketConfig? = null,
) : HandshakeRole<Request, S, WebSocket<S>> {
    public var parsedRequest: Request? = null

    override fun stageFinished(
        finish: StageResult<Request, S>,
    ): Result<ProcessingResult<S, WebSocket<S>>> {
        return when (finish) {
            is StageResult.DoneReading -> {
                val request = finish.result
                parsedRequest = request

                val baseResponse =
                    try {
                        createResponse(request)
                    } catch (e: Throwable) {
                        return Result.failure(e)
                    }

                val finalResponseResult = callback.apply(request, baseResponse)
                if (finalResponseResult.isFailure) {
                    return Result.failure(finalResponseResult.exceptionOrNull()!!)
                }
                val finalResponse = finalResponseResult.getOrThrow()

                val responseBytes = buildResponseBytes(finalResponse)
                val machine = HandshakeMachine.startWrite(finish.stream, responseBytes)
                Result.success(ProcessingResult.Continue(machine))
            }
            is StageResult.DoneWriting -> {
                val ws =
                    WebSocket.fromRawSocket(
                        stream = finish.stream,
                        role = Role.Server,
                        config = config,
                    )
                Result.success(ProcessingResult.Done(ws))
            }
        }
    }

    private fun buildResponseBytes(response: Response): ByteArray {
        val sb = StringBuilder()
        sb
            .append(response.version)
            .append(" ")
            .append(response.statusCode)
            .append(" Switching Protocols\r\n")
        for ((k, v) in response.headers) {
            sb
                .append(k)
                .append(": ")
                .append(v)
                .append("\r\n")
        }
        sb.append("\r\n")
        return sb.toString().encodeToByteArray()
    }

    public companion object {
        public fun <S, C : Callback> start(
            stream: S,
            callback: C,
            config: WebSocketConfig? = null,
        ): MidHandshake<ServerHandshake<S, C>> {
            val server = ServerHandshake(stream, callback, config)
            val machine = HandshakeMachine.startRead(stream)
            return MidHandshake(server, machine)
        }
    }
}

internal object RequestParser : TryParse<Request> {
    override fun tryParse(data: ByteArray): Result<Pair<Int, Request>?> {
        val text = data.decodeToString()
        val headerEnd = text.indexOf("\r\n\r\n")
        if (headerEnd == -1) {
            return Result.success(null)
        }

        val headerText = text.substring(0, headerEnd)
        val lines = headerText.lines()
        if (lines.isEmpty()) {
            return Result.failure(TungsteniteException.Protocol(ProtocolError.Httparse("empty request")))
        }

        val requestLine = lines[0].trim().split(Regex("\\s+"))
        if (requestLine.size < 3) {
            return Result.failure(TungsteniteException.Protocol(ProtocolError.Httparse("invalid request line")))
        }
        val method = requestLine[0]
        val uri = requestLine[1]
        val version = requestLine[2]

        val headers = mutableMapOf<String, String>()
        for (i in 1 until lines.size) {
            val line = lines[i]
            val colon = line.indexOf(':')
            if (colon != -1) {
                val k = line.substring(0, colon).trim()
                val v = line.substring(colon + 1).trim()
                headers[k] = v
            }
        }

        val consumed = headerEnd + 4
        return Result.success(Pair(consumed, Request(uri = uri, method = method, version = version, headers = headers)))
    }
}
