// port-lint: source client.rs
package io.github.kotlinmania.tungstenite

import io.github.kotlinmania.tungstenite.handshake.ClientHandshake
import io.github.kotlinmania.tungstenite.handshake.MidHandshake
import io.github.kotlinmania.tungstenite.handshake.Request
import io.github.kotlinmania.tungstenite.protocol.WebSocketConfig

/**
 * Trait / interface to convert into a client request.
 */
public interface IntoClientRequest {
    public fun intoClientRequest(): Request
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
    request: Request,
    stream: Stream,
): MidHandshake<ClientHandshake<Stream>> =
    clientWithConfig(request, stream, null)

/**
 * Start a client WebSocket handshake over the given stream with custom configuration.
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
    private var uri: String,
) : IntoClientRequest {
    private val headers: MutableMap<String, String> = mutableMapOf()

    public fun header(name: String, value: String): ClientRequestBuilder =
        apply {
            headers[name] = value
        }

    override fun intoClientRequest(): Request =
        Request(uri = uri, headers = headers)

    public companion object {
        public fun get(uri: String): ClientRequestBuilder = ClientRequestBuilder(uri)
    }
}
