// port-lint: source tungstenite/src/handshake/server.rs
package io.github.kotlinmania.tungstenite.handshake

import io.github.kotlinmania.tungstenite.ProtocolError
import io.github.kotlinmania.tungstenite.TungsteniteException
import io.github.kotlinmania.tungstenite.extensions.Extensions
import io.github.kotlinmania.tungstenite.extensions.headers.SecWebsocketExtensions
import io.github.kotlinmania.tungstenite.protocol.Role
import io.github.kotlinmania.tungstenite.protocol.WebSocket
import io.github.kotlinmania.tungstenite.protocol.WebSocketConfig

/**
 * Callback trait to inspect or modify the handshake response.
 */
public interface Callback {
    /** Called when the request is parsed, before sending the response. */
    public fun onRequest(
        request: Request,
        response: Response,
    ): Result<Response>

    /** Backward compatibility alias. */
    public fun apply(
        request: Request,
        response: Response,
    ): Result<Response> = onRequest(request, response)
}

/**
 * Default no-op callback.
 */
public class NoCallback : Callback {
    override fun onRequest(
        request: Request,
        response: Response,
    ): Result<Response> = Result.success(response)
}

/**
 * Create base response headers and status for a WebSocket handshake request.
 */
public fun createParts(request: Request): Response {
    if (request.method != "GET") {
        throw TungsteniteException.ProtocolViolation(ProtocolError.WrongHttpMethod)
    }
    if (request.version < "HTTP/1.1") {
        throw TungsteniteException.ProtocolViolation(ProtocolError.WrongHttpVersion)
    }

    val connectionHeader =
        request.headers.entries
            .firstOrNull {
                it.key.equals("Connection", ignoreCase = true)
            }?.value ?: throw TungsteniteException.ProtocolViolation(ProtocolError.MissingConnectionUpgradeHeader)

    val hasUpgrade = connectionHeader.split(',', ' ').any { it.trim().equals("Upgrade", ignoreCase = true) }
    if (!hasUpgrade) {
        throw TungsteniteException.ProtocolViolation(ProtocolError.MissingConnectionUpgradeHeader)
    }

    val upgradeHeader =
        request.headers.entries
            .firstOrNull {
                it.key.equals("Upgrade", ignoreCase = true)
            }?.value ?: throw TungsteniteException.ProtocolViolation(ProtocolError.MissingUpgradeWebSocketHeader)

    if (!upgradeHeader.trim().equals("websocket", ignoreCase = true)) {
        throw TungsteniteException.ProtocolViolation(ProtocolError.MissingUpgradeWebSocketHeader)
    }

    val versionHeader =
        request.headers.entries
            .firstOrNull {
                it.key.equals("Sec-WebSocket-Version", ignoreCase = true)
            }?.value ?: throw TungsteniteException.ProtocolViolation(ProtocolError.MissingSecWebSocketVersionHeader)

    if (versionHeader.trim() != "13") {
        throw TungsteniteException.ProtocolViolation(ProtocolError.MissingSecWebSocketVersionHeader)
    }

    val key =
        request.headers.entries
            .firstOrNull {
                it.key.equals("Sec-WebSocket-Key", ignoreCase = true)
            }?.value ?: throw TungsteniteException.ProtocolViolation(ProtocolError.MissingSecWebSocketKey)

    val acceptKey = deriveAcceptKey(key)

    val responseHeaders =
        mapOf(
            "Upgrade" to "websocket",
            "Connection" to "Upgrade",
            "Sec-WebSocket-Accept" to acceptKey,
        )

    return Response(
        statusCode = 101,
        version = request.version,
        headers = responseHeaders,
    )
}

/**
 * Create a response for the request.
 */
public fun createResponse(request: Request): Response = createParts(request)

/**
 * Create a response for the request with a custom body.
 */
public fun createResponseWithBody(
    request: Request,
    generateBody: () -> ByteArray?,
): Response {
    val base = createParts(request)
    return base.copy(body = generateBody())
}

/**
 * Write response into wire bytes.
 */
public fun writeResponse(response: Response): ByteArray {
    val sb = StringBuilder()
    val statusText = if (response.statusCode == 101) "Switching Protocols" else "OK"
    sb
        .append(versionAsStr(response.version))
        .append(" ")
        .append(response.statusCode)
        .append(" ")
        .append(statusText)
        .append("\r\n")

    for ((k, v) in response.headers) {
        sb
            .append(k)
            .append(": ")
            .append(v)
            .append("\r\n")
    }

    sb.append("\r\n")
    val headerBytes = sb.toString().encodeToByteArray()
    val bodyBytes = response.body
    return if (bodyBytes != null && bodyBytes.isNotEmpty()) {
        headerBytes + bodyBytes
    } else {
        headerBytes
    }
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
    public var extensions: Extensions = Extensions()

    override fun stageFinished(
        finish: StageResult<Request, S>,
    ): Result<ProcessingResult<S, WebSocket<S>>> {
        return when (finish) {
            is StageResult.DoneReading -> {
                if (finish.tail.isNotEmpty()) {
                    return Result.failure(TungsteniteException.ProtocolViolation(ProtocolError.JunkAfterRequest))
                }

                val request = finish.result
                parsedRequest = request

                var baseResponse =
                    try {
                        createResponse(request)
                    } catch (e: Throwable) {
                        return Result.failure(e)
                    }

                val extHeader =
                    request.headers.entries
                        .firstOrNull { it.key.equals("Sec-WebSocket-Extensions", ignoreCase = true) }
                        ?.value

                if (extHeader != null) {
                    val parsedExt =
                        try {
                            SecWebsocketExtensions.parse(extHeader)
                        } catch (_: Exception) {
                            return Result.failure(
                                TungsteniteException.ProtocolViolation(
                                    ProtocolError.InvalidHeader("Sec-WebSocket-Extensions"),
                                ),
                            )
                        }

                    val extConfig =
                        config?.extensions
                            ?: return Result.failure(
                                TungsteniteException.ProtocolViolation(
                                    ProtocolError.InvalidHeader("Sec-WebSocket-Extensions"),
                                ),
                            )

                    val (negotiated, agreed) =
                        try {
                            extConfig.acceptOffers(parsedExt)
                        } catch (e: Exception) {
                            return Result.failure(
                                TungsteniteException.ProtocolViolation(
                                    ProtocolError.InvalidExtensionsHeader(e.message ?: ""),
                                ),
                            )
                        }

                    if (agreed != null && !agreed.isEmpty()) {
                        val newHeaders = baseResponse.headers.toMutableMap()
                        newHeaders["Sec-WebSocket-Extensions"] = agreed.headerValue()
                        baseResponse = baseResponse.copy(headers = newHeaders)
                    }
                    this.extensions = negotiated
                }

                val callbackResult = callback.onRequest(request, baseResponse)
                if (callbackResult.isFailure) {
                    return Result.failure(callbackResult.exceptionOrNull()!!)
                }
                val finalResponse = callbackResult.getOrThrow()

                val responseBytes = writeResponse(finalResponse)
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
            return Result.failure(TungsteniteException.ProtocolViolation(ProtocolError.Httparse("empty request")))
        }

        val requestLine = lines[0].trim().split(Regex("\\s+"))
        if (requestLine.size < 3) {
            return Result.failure(TungsteniteException.ProtocolViolation(ProtocolError.Httparse("invalid request line")))
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
