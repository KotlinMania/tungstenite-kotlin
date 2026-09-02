// port-lint: source client.rs
package io.github.kotlinmania.tungstenite

import io.github.kotlinmania.tungstenite.handshake.ClientHandshake
import io.github.kotlinmania.tungstenite.handshake.MidHandshake
import io.github.kotlinmania.tungstenite.handshake.Request
import io.github.kotlinmania.tungstenite.handshake.generateKey
import io.github.kotlinmania.tungstenite.protocol.WebSocketConfig

/**
 * Trait / interface to convert into a client request.
 */
public interface IntoClientRequest {
    public fun intoClientRequest(): Request
}

/**
 * Convert string to a client request.
 */
public fun String.intoClientRequest(): Request {
    uriMode(this)

    val schemeEnd = indexOf("://")
    if (schemeEnd == -1) {
        throw TungsteniteException.Url(UrlError.NoHostName)
    }
    val afterScheme = substring(schemeEnd + 3)
    val slashIdx = afterScheme.indexOf('/')
    val authority = if (slashIdx != -1) afterScheme.substring(0, slashIdx) else afterScheme

    if (authority.isEmpty()) {
        throw TungsteniteException.Url(UrlError.NoHostName)
    }

    val atIdx = authority.indexOf('@')
    val host = if (atIdx != -1) authority.substring(atIdx + 1) else authority

    if (host.isEmpty()) {
        throw TungsteniteException.Url(UrlError.EmptyHostName)
    }

    val key = generateKey()
    val headers =
        mutableMapOf(
            "Host" to host,
            "Connection" to "Upgrade",
            "Upgrade" to "websocket",
            "Sec-WebSocket-Version" to "13",
            "Sec-WebSocket-Key" to key,
        )

    return Request(
        uri = this,
        method = "GET",
        version = "HTTP/1.1",
        headers = headers,
    )
}

/**
 * Determine the stream mode (Plain or Tls) from a WebSocket URI.
 */
public fun uriMode(uri: String): Mode {
    val trimmed = uri.trim()
    return when {
        trimmed.startsWith("ws://", ignoreCase = true) -> Mode.Plain
        trimmed.startsWith("wss://", ignoreCase = true) -> Mode.Tls
        else -> throw TungsteniteException.Url(UrlError.UnsupportedUrlScheme)
    }
}

/**
 * Start a client WebSocket handshake over the given stream.
 */
public fun <Stream> client(
    request: IntoClientRequest,
    stream: Stream,
): MidHandshake<ClientHandshake<Stream>> =
    clientWithConfig(request, stream, null)

/**
 * Start a client WebSocket handshake over the given stream with custom configuration.
 */
public fun <Stream> clientWithConfig(
    request: IntoClientRequest,
    stream: Stream,
    config: WebSocketConfig? = null,
): MidHandshake<ClientHandshake<Stream>> =
    ClientHandshake.start(stream, request.intoClientRequest(), config)

/**
 * Start a client WebSocket handshake over the given stream with Request.
 */
public fun <Stream> client(
    request: Request,
    stream: Stream,
): MidHandshake<ClientHandshake<Stream>> =
    clientWithConfig(request, stream, null)

/**
 * Start a client WebSocket handshake over the given stream with Request and custom configuration.
 */
public fun <Stream> clientWithConfig(
    request: Request,
    stream: Stream,
    config: WebSocketConfig? = null,
): MidHandshake<ClientHandshake<Stream>> =
    ClientHandshake.start(stream, request, config)

/**
 * Helper to build a client request.
 */
public class ClientRequestBuilder(
    public val uri: String,
) : IntoClientRequest {
    private val additionalHeaders: MutableList<Pair<String, String>> = mutableListOf()
    private val subprotocols: MutableList<String> = mutableListOf()

    public fun header(name: String, value: String): ClientRequestBuilder =
        apply {
            additionalHeaders.add(Pair(name, value))
        }

    public fun withHeader(key: String, value: String): ClientRequestBuilder =
        apply {
            additionalHeaders.add(Pair(key, value))
        }

    public fun withSubProtocol(protocol: String): ClientRequestBuilder =
        apply {
            subprotocols.add(protocol)
        }

    override fun intoClientRequest(): Request {
        val baseRequest = uri.intoClientRequest()
        val headers = baseRequest.headers.toMutableMap()
        for ((k, v) in additionalHeaders) {
            headers[k] = v
        }
        if (subprotocols.isNotEmpty()) {
            headers["Sec-WebSocket-Protocol"] = subprotocols.joinToString(", ")
        }
        return baseRequest.copy(headers = headers)
    }

    public companion object {
        public fun new(uri: String): ClientRequestBuilder = ClientRequestBuilder(uri)

        public fun get(uri: String): ClientRequestBuilder = ClientRequestBuilder(uri)
    }
}
