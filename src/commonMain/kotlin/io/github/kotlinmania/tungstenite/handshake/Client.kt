// port-lint: source handshake/client.rs
package io.github.kotlinmania.tungstenite.handshake

import io.github.kotlinmania.tungstenite.ProtocolError
import io.github.kotlinmania.tungstenite.SubProtocolError
import io.github.kotlinmania.tungstenite.TungsteniteException
import io.github.kotlinmania.tungstenite.extensions.Extensions
import io.github.kotlinmania.tungstenite.extensions.ExtensionsConfig
import io.github.kotlinmania.tungstenite.extensions.ExtensionsError
import io.github.kotlinmania.tungstenite.extensions.headers.SecWebsocketExtensions
import io.github.kotlinmania.tungstenite.protocol.Role
import io.github.kotlinmania.tungstenite.protocol.WebSocket
import io.github.kotlinmania.tungstenite.protocol.WebSocketConfig
import io.github.kotlinmania.tungstenite.uriMode
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

        public fun tryParse(data: ByteArray): Result<Pair<Int, Request>?> = RequestParser.tryParse(data)
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

    public companion object {
        public fun tryParse(data: ByteArray): Result<Pair<Int, Response>?> = ResponseParser.tryParse(data)
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
 * Extract path and query from URI.
 */
public fun extractPathAndQuery(uri: String): String {
    val schemeEnd = uri.indexOf("://")
    val afterScheme = if (schemeEnd != -1) uri.substring(schemeEnd + 3) else uri
    val slashIdx = afterScheme.indexOf('/')
    val path = if (slashIdx != -1) afterScheme.substring(slashIdx) else "/"
    return if (path.isEmpty()) "/" else path
}

/**
 * Extract host from URI.
 */
public fun extractHost(uri: String): String {
    val schemeEnd = uri.indexOf("://")
    val afterScheme = if (schemeEnd != -1) uri.substring(schemeEnd + 3) else uri
    val slashIdx = afterScheme.indexOf('/')
    val authority = if (slashIdx != -1) afterScheme.substring(0, slashIdx) else afterScheme
    val atIdx = authority.indexOf('@')
    return if (atIdx != -1) authority.substring(atIdx + 1) else authority
}

/**
 * Verifies and generates a client WebSocket request from the original request and extracts a WebSocket key from it.
 */
public fun generateRequest(
    request: Request,
    extensions: ExtensionsConfig? = null,
): Pair<ByteArray, String> {
    val path = extractPathAndQuery(request.uri)
    val version = versionAsStr(request.version)

    val sb = StringBuilder()
    sb.append("GET ").append(path).append(" ").append(version).append("\r\n")

    val websocketHeaders = listOf("Host", "Connection", "Upgrade", "Sec-WebSocket-Version", "Sec-WebSocket-Key")

    val key =
        request.headers.entries
            .firstOrNull { it.key.equals("Sec-WebSocket-Key", ignoreCase = true) }
            ?.value
            ?: throw TungsteniteException.ProtocolViolation(ProtocolError.InvalidHeader("Sec-WebSocket-Key"))

    val remainingHeaders = request.headers.toMutableMap()

    for (header in websocketHeaders) {
        val entry =
            remainingHeaders.entries.firstOrNull { it.key.equals(header, ignoreCase = true) }
                ?: throw TungsteniteException.ProtocolViolation(ProtocolError.InvalidHeader(header))
        sb.append(header).append(": ").append(entry.value).append("\r\n")
        remainingHeaders.remove(entry.key)
    }

    if (extensions != null) {
        val offers = extensions.generateOffers()
        if (offers.isNotEmpty()) {
            val extHeader = SecWebsocketExtensions.new(offers)
            sb.append("sec-websocket-extensions: ").append(extHeader.headerValue()).append("\r\n")
        }
    }

    val websocketHeadersContains = { name: String -> websocketHeaders.any { it.equals(name, ignoreCase = true) } }

    for ((k, v) in remainingHeaders) {
        if (websocketHeadersContains(k)) {
            throw TungsteniteException.ProtocolViolation(ProtocolError.InvalidHeader(k))
        }

        var name = k
        if (name.equals("sec-websocket-protocol", ignoreCase = true)) {
            name = "Sec-WebSocket-Protocol"
        } else if (name.equals("origin", ignoreCase = true)) {
            name = "Origin"
        }
        sb.append(name).append(": ").append(v).append("\r\n")
    }

    sb.append("\r\n")
    return Pair(sb.toString().encodeToByteArray(), key)
}

/**
 * Extracts subprotocols from request.
 */
public fun extractSubprotocolsFromRequest(request: Request): List<String>? {
    val subprotocols =
        request.headers.entries
            .firstOrNull { it.key.equals("Sec-WebSocket-Protocol", ignoreCase = true) }
            ?.value
            ?: return null
    return subprotocols.split(',').map { it.trim() }
}

/**
 * Information for handshake verification.
 */
public class VerifyData(
    public val acceptKey: String,
    public val subprotocols: List<String>? = null,
) {
    public fun verifyResponse(
        response: Response,
        extensions: ExtensionsConfig? = null,
    ): Pair<Response, Extensions> {
        if (response.statusCode != 101) {
            throw TungsteniteException.Http(response.statusCode, "Switching Protocols expected")
        }

        val upgrade =
            response.headers.entries
                .firstOrNull { it.key.equals("Upgrade", ignoreCase = true) }
                ?.value
        if (upgrade == null || !upgrade.equals("websocket", ignoreCase = true)) {
            throw TungsteniteException.ProtocolViolation(ProtocolError.MissingUpgradeWebSocketHeader)
        }

        val connection =
            response.headers.entries
                .firstOrNull { it.key.equals("Connection", ignoreCase = true) }
                ?.value
        if (connection == null || !connection.split(',', ' ').any { it.trim().equals("Upgrade", ignoreCase = true) }) {
            throw TungsteniteException.ProtocolViolation(ProtocolError.MissingConnectionUpgradeHeader)
        }

        val accept =
            response.headers.entries
                .firstOrNull { it.key.equals("Sec-WebSocket-Accept", ignoreCase = true) }
                ?.value
        if (accept != acceptKey) {
            throw TungsteniteException.ProtocolViolation(ProtocolError.SecWebSocketAcceptKeyMismatch)
        }

        val extHeaderStr =
            response.headers.entries
                .firstOrNull { it.key.equals("Sec-WebSocket-Extensions", ignoreCase = true) }
                ?.value

        val negotiatedExtensions =
            if (extHeaderStr != null) {
                val agreed =
                    try {
                        SecWebsocketExtensions.parse(extHeaderStr)
                    } catch (_: Exception) {
                        throw TungsteniteException.ProtocolViolation(ProtocolError.InvalidHeader("Sec-WebSocket-Extensions"))
                    }
                val config =
                    extensions
                        ?: throw TungsteniteException.ProtocolViolation(ProtocolError.InvalidHeader("Sec-WebSocket-Extensions"))
                try {
                    config.verifyAgreedOn(agreed)
                } catch (e: ExtensionsError) {
                    throw TungsteniteException.ProtocolViolation(ProtocolError.InvalidExtensionsHeader(e.message ?: ""))
                }
            } else {
                Extensions()
            }

        val returnedSubprotocol =
            response.headers.entries
                .firstOrNull { it.key.equals("Sec-WebSocket-Protocol", ignoreCase = true) }
                ?.value

        if (returnedSubprotocol == null && subprotocols != null) {
            throw TungsteniteException.ProtocolViolation(
                ProtocolError.SecWebSocketSubProtocol(SubProtocolError.NoSubProtocol),
            )
        }

        if (returnedSubprotocol != null && subprotocols == null) {
            throw TungsteniteException.ProtocolViolation(
                ProtocolError.SecWebSocketSubProtocol(SubProtocolError.ServerSentSubProtocolNoneRequested),
            )
        }

        if (returnedSubprotocol != null && subprotocols != null) {
            if (!subprotocols.contains(returnedSubprotocol.trim())) {
                throw TungsteniteException.ProtocolViolation(
                    ProtocolError.SecWebSocketSubProtocol(SubProtocolError.InvalidSubProtocol),
                )
            }
        }

        return Pair(response, negotiatedExtensions)
    }
}

/**
 * Client handshake role machine.
 */
public class ClientHandshake<S>(
    public val stream: S,
    public val verifyData: VerifyData,
    public val config: WebSocketConfig? = null,
) : HandshakeRole<Response, S, Pair<WebSocket<S>, Response>> {
    public val acceptKey: String get() = verifyData.acceptKey

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
                val (verifiedResponse, extensions) =
                    try {
                        verifyData.verifyResponse(response, config?.extensions)
                    } catch (e: Throwable) {
                        return Result.failure(e)
                    }

                val ws =
                    WebSocket.fromPartiallyRead(
                        stream = finish.stream,
                        part = finish.tail,
                        role = Role.Client,
                        config = config,
                    )
                Result.success(ProcessingResult.Done(Pair(ws, verifiedResponse)))
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

            uriMode(request.uri)

            val subprotocols = extractSubprotocolsFromRequest(request)
            val (requestBytes, key) = generateRequest(request, config?.extensions)
            val acceptKey = deriveAcceptKey(key)

            val client =
                ClientHandshake(
                    stream = stream,
                    verifyData = VerifyData(acceptKey = acceptKey, subprotocols = subprotocols),
                    config = config,
                )
            val machine = HandshakeMachine.startWrite(stream, requestBytes)
            return MidHandshake(client, machine)
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
        if (version < "HTTP/1.1") {
            return Result.failure(TungsteniteException.ProtocolViolation(ProtocolError.WrongHttpVersion))
        }

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
