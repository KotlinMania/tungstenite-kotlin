@file:OptIn(kotlin.experimental.ExperimentalObjCRefinement::class)

// port-lint: source error.rs

package io.github.kotlinmania.tungstenite

import io.github.kotlinmania.tungstenite.protocol.Message
import io.github.kotlinmania.tungstenite.protocol.frame.Data
import kotlin.native.HiddenFromObjC

/** Result type of all Tungstenite library calls. */
public typealias Result<T> = kotlin.Result<T>

/** Result type of Tungstenite library calls. */
public typealias TungsteniteResult<T> = Result<T>

/** Type alias for WebSocket errors. */
public typealias Error = TungsteniteException

/**
 * Possible WebSocket errors.
 */
public sealed class TungsteniteException(
    message: String,
    cause: Throwable? = null,
) : Exception(message, cause),
    NonBlockingError {
    override fun intoNonBlocking(): Throwable? = this

    /**
     * WebSocket connection closed normally. This informs you of the close.
     * It's not an error as such and nothing wrong happened.
     *
     * This is returned as soon as the close handshake is finished (we have both sent and
     * received a close frame) on the server end and as soon as the server has closed the
     * underlying connection if this endpoint is a client.
     *
     * Thus when you receive this, it is safe to drop the underlying connection.
     *
     * Receiving this error means that the WebSocket object is not usable anymore and the
     * only meaningful action with it is dropping it.
     */
    public class ConnectionClosed : TungsteniteException("Connection closed normally")

    /**
     * Trying to work with already closed connection.
     *
     * Trying to read or write after receiving `ConnectionClosed` causes this.
     *
     * As opposed to `ConnectionClosed`, this indicates your code tries to operate on the
     * connection when it really shouldn't anymore, so this really indicates a programmer
     * error on your part.
     */
    public class AlreadyClosed : TungsteniteException("Trying to work with closed connection")

    /**
     * Input-output error. Apart from WouldBlock, these are generally errors with the
     * underlying connection and you should probably consider them fatal.
     */
    public class Io(
        message: String,
        cause: Throwable? = null,
    ) : TungsteniteException("IO error: $message", cause) {
        override fun intoNonBlocking(): Throwable? =
            if (message?.contains("WouldBlock", ignoreCase = true) == true) null else this
    }

    /**
     * TLS error.
     *
     * Note that this error variant is enabled unconditionally even if no TLS feature is enabled,
     * to provide a feature-agnostic API surface.
     */
    public class Tls(
        public val error: TlsError,
    ) : TungsteniteException("TLS error: $error")

    /**
     * - When reading: buffer capacity exhausted.
     * - When writing: your message is bigger than the configured max message size (64MB by default).
     */
    public class Capacity(
        public val error: CapacityError,
    ) : TungsteniteException("Space limit exceeded: $error")

    /** Protocol violation. */
    public class ProtocolViolation(
        public val error: ProtocolError,
    ) : TungsteniteException("WebSocket protocol error: $error")

    /** Protocol violation (Rust Error::Protocol equivalent). */
    @HiddenFromObjC
    public class Protocol(
        public val error: ProtocolError,
    ) : TungsteniteException("WebSocket protocol error: $error")

    /** Message write buffer is full. */
    public class WriteBufferFull(
        public val frameMessage: Message,
    ) : TungsteniteException("Write buffer is full")

    /** UTF-8 encoding error. */
    public class Utf8(
        message: String,
    ) : TungsteniteException("UTF-8 encoding error: $message")

    /** Attack attempt detected. */
    public class AttackAttempt : TungsteniteException("Attack attempt detected")

    /** Invalid URL. */
    public class Url(
        public val error: UrlError,
    ) : TungsteniteException("URL error: $error")

    /** HTTP error. */
    public class Http(
        public val statusCode: Int,
        message: String,
    ) : TungsteniteException("HTTP error: $statusCode ($message)")

    /** HTTP format error. */
    public class HttpFormat(
        message: String,
    ) : TungsteniteException("HTTP format error: $message")

    public companion object {
        public fun from(err: String): TungsteniteException = TungsteniteException.Utf8(err)

        public fun from(err: Throwable): TungsteniteException =
            if (err is TungsteniteException) err else TungsteniteException.Io(err.message ?: err.toString(), err)

        public fun from(err: CapacityError): TungsteniteException = TungsteniteException.Capacity(err)

        public fun from(err: ProtocolError): TungsteniteException = TungsteniteException.Protocol(err)

        public fun from(err: UrlError): TungsteniteException = TungsteniteException.Url(err)

        public fun from(err: TlsError): TungsteniteException = TungsteniteException.Tls(err)
    }
}

/** Indicates the specific type/cause of a capacity error. */
public sealed class CapacityError {
    /** Too many headers provided (see `httparse::Error::TooManyHeaders`). */
    public object TooManyHeaders : CapacityError() {
        override fun toString(): String = "Too many headers"
    }

    /**
     * Received header is too long.
     * Message is bigger than the maximum allowed size.
     */
    public data class MessageTooLong(
        /** The size of the message. */
        public val size: Long,
        /** The maximum allowed message size. */
        public val maxSize: Long,
    ) : CapacityError() {
        override fun toString(): String = "Message too long: $size > $maxSize"
    }
}

/** Indicates the specific type/cause of a subprotocol header error. */
public sealed class SubProtocolError {
    /** The server sent a subprotocol to a client handshake request but none was requested. */
    public object ServerSentSubProtocolNoneRequested : SubProtocolError() {
        override fun toString(): String = "Server sent a subprotocol but none was requested"
    }

    /** The server sent an invalid subprotocol to a client handshake request. */
    public object InvalidSubProtocol : SubProtocolError() {
        override fun toString(): String = "Server sent an invalid subprotocol"
    }

    /**
     * The server sent no subprotocol to a client handshake request that requested one or more
     * subprotocols.
     */
    public object NoSubProtocol : SubProtocolError() {
        override fun toString(): String = "Server sent no subprotocol"
    }
}

/** Indicates the specific type/cause of a protocol error. */
public sealed class ProtocolError {
    /** Use of the wrong HTTP method (the WebSocket protocol requires the GET method be used). */
    public object WrongHttpMethod : ProtocolError() {
        override fun toString(): String = "Unsupported HTTP method used - only GET is allowed"
    }

    /** Wrong HTTP version used (the WebSocket protocol requires version 1.1 or higher). */
    public object WrongHttpVersion : ProtocolError() {
        override fun toString(): String = "HTTP version must be 1.1 or higher"
    }

    /** Missing `Connection: upgrade` HTTP header. */
    public object MissingConnectionUpgradeHeader : ProtocolError() {
        override fun toString(): String = "No \"Connection: upgrade\" header"
    }

    /** Missing `Upgrade: websocket` HTTP header. */
    public object MissingUpgradeWebSocketHeader : ProtocolError() {
        override fun toString(): String = "No \"Upgrade: websocket\" header"
    }

    /** Missing `Sec-WebSocket-Version: 13` HTTP header. */
    public object MissingSecWebSocketVersionHeader : ProtocolError() {
        override fun toString(): String = "No \"Sec-WebSocket-Version: 13\" header"
    }

    /** Missing `Sec-WebSocket-Key` HTTP header. */
    public object MissingSecWebSocketKey : ProtocolError() {
        override fun toString(): String = "No \"Sec-WebSocket-Key\" header"
    }

    /** The `Sec-WebSocket-Accept` header is either not present or does not specify the correct key value. */
    public object SecWebSocketAcceptKeyMismatch : ProtocolError() {
        override fun toString(): String = "Key mismatch in \"Sec-WebSocket-Accept\" header"
    }

    /** The `Sec-WebSocket-Protocol` header was invalid. */
    public data class SecWebSocketSubProtocol(
        public val error: SubProtocolError,
    ) : ProtocolError() {
        override fun toString(): String = "SubProtocol error: $error"
    }

    /** The `Sec-WebSocket-Extensions` header is invalid. */
    public data class InvalidExtensionsHeader(
        public val reason: String,
    ) : ProtocolError() {
        override fun toString(): String = "Invalid \"Sec-WebSocket-Extensions\" header: $reason"
    }

    /** Garbage data encountered after client request. */
    public object JunkAfterRequest : ProtocolError() {
        override fun toString(): String = "Junk after client request"
    }

    /** Custom responses must be unsuccessful. */
    public object CustomResponseSuccessful : ProtocolError() {
        override fun toString(): String = "Custom response must not be successful"
    }

    /** Invalid header is passed. Or the header is missing in the request. Or not present at all. Check the request that you pass. */
    public data class InvalidHeader(
        public val headerName: String,
    ) : ProtocolError() {
        override fun toString(): String = "Missing, duplicated or incorrect header $headerName"
    }

    /** No more data while still performing handshake. */
    public object HandshakeIncomplete : ProtocolError() {
        override fun toString(): String = "Handshake not finished"
    }

    /** Wrapper around a `httparse::Error` value. */
    public data class Httparse(
        public val message: String,
    ) : ProtocolError() {
        override fun toString(): String = "httparse error: $message"
    }

    /** Not allowed to send after having sent a closing frame. */
    public object SendAfterClosing : ProtocolError() {
        override fun toString(): String = "Sending after closing is not allowed"
    }

    /** Remote sent data after sending a closing frame. */
    public object ReceivedAfterClosing : ProtocolError() {
        override fun toString(): String = "Remote sent after having closed"
    }

    /** Reserved bits in frame header are non-zero. */
    public object NonZeroReservedBits : ProtocolError() {
        override fun toString(): String = "Reserved bits are non-zero"
    }

    /** The server must close the connection when an unmasked frame is received. */
    public object UnmaskedFrameFromClient : ProtocolError() {
        override fun toString(): String = "Received an unmasked frame from client"
    }

    /** The client must close the connection when a masked frame is received. */
    public object MaskedFrameFromServer : ProtocolError() {
        override fun toString(): String = "Received a masked frame from server"
    }

    /** Control frames must not be fragmented. */
    public object FragmentedControlFrame : ProtocolError() {
        override fun toString(): String = "Fragmented control frame"
    }

    /** Control frames must not be compressed. */
    public object CompressedControlFrame : ProtocolError() {
        override fun toString(): String = "Compressed control frame"
    }

    /** Control frames must have a payload of 125 bytes or less. */
    public object ControlFrameTooBig : ProtocolError() {
        override fun toString(): String = "Control frame too big (payload must be 125 bytes or less)"
    }

    /** Type of control frame not recognised. */
    public data class UnknownControlFrameType(
        public val opcode: UByte,
    ) : ProtocolError() {
        override fun toString(): String = "Unknown control frame type: $opcode"
    }

    /** Type of data frame not recognised. */
    public data class UnknownDataFrameType(
        public val opcode: UByte,
    ) : ProtocolError() {
        override fun toString(): String = "Unknown data frame type: $opcode"
    }

    /** Received a continue frame despite there being nothing to continue. */
    public object UnexpectedContinueFrame : ProtocolError() {
        override fun toString(): String = "Continue frame but nothing to continue"
    }

    /** Received a compressed continue frame. */
    public object CompressedContinueFrame : ProtocolError() {
        override fun toString(): String = "Continue frame must not have compress bit set"
    }

    /** Received data while waiting for more fragments. */
    public data class ExpectedFragment(
        public val data: Data,
    ) : ProtocolError() {
        override fun toString(): String = "While waiting for more fragments received: $data"
    }

    /** Connection closed without performing the closing handshake. */
    public object ResetWithoutClosingHandshake : ProtocolError() {
        override fun toString(): String = "Connection reset without closing handshake"
    }

    /** Encountered an invalid opcode. */
    public data class InvalidOpcode(
        public val opcode: UByte,
    ) : ProtocolError() {
        override fun toString(): String = "Encountered invalid opcode: $opcode"
    }

    /** The payload for the closing frame is invalid. */
    public object InvalidCloseSequence : ProtocolError() {
        override fun toString(): String = "Invalid close sequence"
    }

    /** Compression or decompression failure. */
    public data class CompressionFailure(
        public val reason: String,
    ) : ProtocolError() {
        override fun toString(): String = "Compression/decompression failed: $reason"
    }
}

/** Indicates the specific type/cause of URL error. */
public sealed class UrlError {
    /** TLS is used despite not being compiled with the TLS feature enabled. */
    public object TlsFeatureNotEnabled : UrlError() {
        override fun toString(): String = "TLS support not compiled in"
    }

    /** The URL does not include a host name. */
    public object NoHostName : UrlError() {
        override fun toString(): String = "No host name in the URL"
    }

    /** Failed to connect with this URL. */
    public data class UnableToConnect(
        public val host: String,
    ) : UrlError() {
        override fun toString(): String = "Unable to connect to $host"
    }

    /** Unsupported URL scheme used (only `ws://` or `wss://` may be used). */
    public object UnsupportedUrlScheme : UrlError() {
        override fun toString(): String = "URL scheme not supported"
    }

    /** The URL host name, though included, is empty. */
    public object EmptyHostName : UrlError() {
        override fun toString(): String = "URL contains empty host name"
    }

    /** The URL does not include a path/query. */
    public object NoPathOrQuery : UrlError() {
        override fun toString(): String = "No path/query in URL"
    }

    /** The proxy URL uses an unsupported scheme. */
    public object UnsupportedProxyScheme : UrlError() {
        override fun toString(): String = "Proxy URL scheme not supported"
    }

    /** The proxy configuration was invalid. */
    public data class InvalidProxyConfig(
        public val message: String,
    ) : UrlError() {
        override fun toString(): String = "Invalid proxy configuration: $message"
    }

    /** The proxy connection failed. */
    public data class ProxyConnect(
        public val message: String,
    ) : UrlError() {
        override fun toString(): String = "Proxy connection failed: $message"
    }
}

/** TLS errors. */
public sealed class TlsError {
    /** Native TLS error. */
    public data class Native(
        public val message: String,
    ) : TlsError() {
        override fun toString(): String = "native-tls error: $message"
    }

    /** Rustls error. */
    public data class Rustls(
        public val message: String,
    ) : TlsError() {
        override fun toString(): String = "rustls error: $message"
    }

    /** DNS name resolution error. */
    public object InvalidDnsName : TlsError() {
        override fun toString(): String = "Invalid DNS name"
    }
}
