// port-lint: source handshake/client.rs
package io.github.kotlinmania.tungstenite.handshake

import io.github.kotlinmania.tungstenite.ProtocolError
import io.github.kotlinmania.tungstenite.TungsteniteException
import io.github.kotlinmania.tungstenite.protocol.Role
import io.github.kotlinmania.tungstenite.protocol.WebSocket
import io.github.kotlinmania.tungstenite.protocol.WebSocketConfig
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi
import kotlin.random.Random

/**
 * Client request representation.
 */
public data class Request(
    public val uri: String,
    public val method: String = "GET",
    public val version: String = "HTTP/1.1",
    public val headers: Map<String, String> = emptyMap(),
) {
    public fun header(name: String, value: String): Request {
        val newHeaders = headers.toMutableMap()
        newHeaders[name] = value
        return copy(headers = newHeaders)
    }

    public companion object {
        public fun get(uri: String): Request = Request(uri = uri)
    }
}

/**
 * Client response representation.
 */
public data class Response(
    public val statusCode: Int = 101,
    public val version: String = "HTTP/1.1",
    public val headers: Map<String, String> = emptyMap(),
    public val body: ByteArray? = null,
) {
    public fun isRedirection(): Boolean = statusCode in 300..399

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Response) return false
        if (statusCode != other.statusCode) return false
        if (version != other.version) return false
        if (headers != other.headers) return false
        if (body != null) {
            if (other.body == null) return false
            if (!body.contentEquals(other.body)) return false
        } else if (other.body != null) {
            return false
        }
        return true
    }

    override fun hashCode(): Int {
        var result = statusCode
        result = 31 * result + version.hashCode()
        result = 31 * result + headers.hashCode()
        result = 31 * result + (body?.contentHashCode() ?: 0)
        return result
    }
}

/**
 * Generate a random 16-byte base64 encoded WebSocket key.
 */
@OptIn(ExperimentalEncodingApi::class)
public fun generateKey(): String {
    val nonce = Random.Default.nextBytes(16)
    return Base64.Default.encode(nonce)
}

/**
 * Client handshake role machine.
 */
public class ClientHandshake<S>(
    public val stream: S,
    public val request: Request,
    public val config: WebSocketConfig? = null,
    public val key: String = generateKey(),
) : HandshakeRole<Response, S, Pair<WebSocket<S>, Response>> {
    public val acceptKey: String = deriveAcceptKey(key)

    public fun buildRequestBytes(): ByteArray {
        val sb = StringBuilder()
        val path = extractPathAndQuery(request.uri)
        sb.append("GET ").append(path).append(" HTTP/1.1\r\n")

        val host = extractHost(request.uri)
        sb.append("Host: ").append(host).append("\r\n")
        sb.append("Upgrade: websocket\r\n")
        sb.append("Connection: Upgrade\r\n")
        sb.append("Sec-WebSocket-Key: ").append(key).append("\r\n")
        sb.append("Sec-WebSocket-Version: 13\r\n")

        for ((k, v) in request.headers) {
            if (!k.equals("Host", ignoreCase = true) &&
                !k.equals("Upgrade", ignoreCase = true) &&
                !k.equals("Connection", ignoreCase = true) &&
                !k.equals("Sec-WebSocket-Key", ignoreCase = true) &&
                !k.equals("Sec-WebSocket-Version", ignoreCase = true)
            ) {
                sb
                    .append(k)
                    .append(": ")
                    .append(v)
                    .append("\r\n")
            }
        }
        sb.append("\r\n")
        return sb.toString().encodeToByteArray()
    }

    override fun stageFinished(
        finish: StageResult<Response, S>,
    ): Result<ProcessingResult<S, Pair<WebSocket<S>, Response>>> {
        return when (finish) {
            is StageResult.DoneWriting -> {
                val machine = HandshakeMachine.startRead(finish.stream)
                Result.success(ProcessingResult.Continue(machine))
            }
            is StageResult.DoneReading -> {
                val response = finish.result
                val wsAccept =
                    response.headers.entries
                        .firstOrNull {
                            it.key.equals("Sec-WebSocket-Accept", ignoreCase = true)
                        }?.value

                if (wsAccept != acceptKey) {
                    return Result.failure(
                        TungsteniteException.ProtocolViolation(ProtocolError.SecWebSocketAcceptKeyMismatch),
                    )
                }

                val ws =
                    WebSocket.fromPartiallyRead(
                        stream = finish.stream,
                        part = finish.tail,
                        role = Role.Client,
                        config = config,
                    )
                Result.success(ProcessingResult.Done(Pair(ws, response)))
            }
        }
    }

    public companion object {
        public fun <S> start(
            stream: S,
            request: Request,
            config: WebSocketConfig? = null,
        ): MidHandshake<ClientHandshake<S>> {
            if (request.method != "GET") {
                throw TungsteniteException.ProtocolViolation(ProtocolError.WrongHttpMethod)
            }
            if (request.version != "HTTP/1.1") {
                throw TungsteniteException.ProtocolViolation(ProtocolError.WrongHttpVersion)
            }

            val client = ClientHandshake(stream, request, config)
            val requestBytes = client.buildRequestBytes()
            val machine = HandshakeMachine.startWrite(stream, requestBytes)
            return MidHandshake(client, machine)
        }

        private fun extractPathAndQuery(uri: String): String {
            val schemeEnd = uri.indexOf("://")
            val afterScheme = if (schemeEnd != -1) uri.substring(schemeEnd + 3) else uri
            val slashIdx = afterScheme.indexOf('/')
            return if (slashIdx != -1) afterScheme.substring(slashIdx) else "/"
        }

        private fun extractHost(uri: String): String {
            val schemeEnd = uri.indexOf("://")
            val afterScheme = if (schemeEnd != -1) uri.substring(schemeEnd + 3) else uri
            val slashIdx = afterScheme.indexOf('/')
            return if (slashIdx != -1) afterScheme.substring(0, slashIdx) else afterScheme
        }
    }
}

internal object ResponseParser : TryParse<Response> {
    override fun tryParse(data: ByteArray): Result<Pair<Int, Response>?> {
        val text = data.decodeToString()
        val headerEnd = text.indexOf("\r\n\r\n")
        if (headerEnd == -1) {
            return Result.success(null)
        }

        val headerText = text.substring(0, headerEnd)
        val lines = headerText.lines()
        if (lines.isEmpty()) {
            return Result.failure(TungsteniteException.ProtocolViolation(ProtocolError.Httparse("empty response")))
        }

        val statusLine = lines[0].trim().split(Regex("\\s+"))
        if (statusLine.size < 2) {
            return Result.failure(TungsteniteException.ProtocolViolation(ProtocolError.Httparse("invalid status line")))
        }
        val statusCode = statusLine[1].toIntOrNull() ?: 101
        val version = statusLine[0]

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
        return Result.success(Pair(consumed, Response(statusCode = statusCode, version = version, headers = headers)))
    }
}
