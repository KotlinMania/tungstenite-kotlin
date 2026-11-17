// port-lint: source tls.rs
package io.github.kotlinmania.tungstenite

import io.github.kotlinmania.tungstenite.handshake.ClientHandshake
import io.github.kotlinmania.tungstenite.handshake.HandshakeError
import io.github.kotlinmania.tungstenite.handshake.MidHandshake
import io.github.kotlinmania.tungstenite.handshake.Request
import io.github.kotlinmania.tungstenite.protocol.WebSocketConfig

/** Type alias for TLS handshake error. */
public typealias TlsHandshakeError<Stream> = HandshakeError<ClientHandshake<MaybeTlsStream<Stream>>>

/**
 * Plain TLS wrapper module.
 */
public object PlainTls {
    /**
     * Wrap stream in plain or fail if TLS mode is requested without TLS enabled.
     */
    public fun <Stream> wrapStream(socket: Stream, mode: Mode): MaybeTlsStream<Stream> =
        when (mode) {
            Mode.Plain -> MaybeTlsStream.Plain(socket)
            Mode.Tls -> throw TungsteniteException.Url(UrlError.TlsFeatureNotEnabled)
        }
}

/**
 * A connector that can be used when establishing connections, allowing to control whether
 * TLS or Plain (non-TLS) connector is used.
 */
public sealed class Connector {
    /** Plain (non-TLS) connector. */
    public object Plain : Connector()

    /** Native TLS connector. */
    public class NativeTls : Connector()

    /** Rustls TLS connector. */
    public class Rustls : Connector()
}

/**
 * Creates a WebSocket handshake from a request and a stream, upgrading the stream to TLS if required.
 */
public fun <Stream> clientTls(
    request: Request,
    stream: Stream,
): MidHandshake<ClientHandshake<MaybeTlsStream<Stream>>> =
    clientTlsWithConfig(request, stream, null, null)

/**
 * Creates a WebSocket handshake from a request and a stream with custom configuration and connector.
 */
public fun <Stream> clientTlsWithConfig(
    request: Request,
    stream: Stream,
    config: WebSocketConfig? = null,
    connector: Connector? = null,
): MidHandshake<ClientHandshake<MaybeTlsStream<Stream>>> {
    val mode = uriMode(request.uri)

    val tlsStream: MaybeTlsStream<Stream> =
        when (mode) {
            Mode.Plain -> MaybeTlsStream.Plain(stream)
            Mode.Tls -> {
                when (connector) {
                    is Connector.NativeTls -> MaybeTlsStream.NativeTls(stream)
                    is Connector.Rustls -> MaybeTlsStream.Rustls(stream)
                    is Connector.Plain -> throw TungsteniteException.Url(UrlError.TlsFeatureNotEnabled)
                    null -> MaybeTlsStream.Plain(stream)
                }
            }
        }

    return clientWithConfig(request, tlsStream, config)
}
