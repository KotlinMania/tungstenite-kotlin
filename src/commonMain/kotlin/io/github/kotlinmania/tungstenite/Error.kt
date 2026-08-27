// port-lint: source tungstenite/src/error.rs
package io.github.kotlinmania.tungstenite

import io.github.kotlinmania.tungstenite.protocol.Message
import io.github.kotlinmania.tungstenite.protocol.frame.Data

/** Result type of Tungstenite library calls. */
public typealias TungsteniteResult<T> = Result<T>

/** Type alias for WebSocket errors. */
public typealias Error = TungsteniteException

/** Possible WebSocket errors. */
public sealed class TungsteniteException(
    message: String,
    cause: Throwable? = null,
) : Exception(message, cause),
    NonBlockingError {
    override fun intoNonBlocking(): Throwable? = this

    /** WebSocket connection closed normally. */
    public class ConnectionClosed : TungsteniteException("Connection closed normally")

    /** Trying to work with already closed connection. */
    public class AlreadyClosed : TungsteniteException("Trying to work with closed connection")

    /** Input-output error. */
    public class Io(
        message: String,
        cause: Throwable? = null,
    ) : TungsteniteException("IO error: $message", cause) {
        override fun intoNonBlocking(): Throwable? =
            if (message?.contains("WouldBlock", ignoreCase = true) == true) null else this
    }

    /** TLS error. */
    public class Tls(
        public val error: TlsError,
    ) : TungsteniteException("TLS error: $error")

    /** Buffer capacity exhausted or message too large. */
    public class Capacity(
        public val error: CapacityError,
    ) : TungsteniteException("Space limit exceeded: $error")

    /** Protocol violation. */
    public class ProtocolViolation(
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

    /** HTTP error with status code. */
    public class Http(
        public val statusCode: Int,
        message: String,
    ) : TungsteniteException("HTTP error: $statusCode ($message)")

    /** HTTP format error. */
    public class HttpFormat(
        message: String,
    ) : TungsteniteException("HTTP format error: $message")
}

/** Indicates the specific type/cause of a capacity error. */
public sealed class CapacityError {
    /** Too many headers provided. */
    public object TooManyHeaders : CapacityError() {
        override fun toString(): String = "Too many headers"
    }

    /** Received message is bigger than the maximum allowed size. */
    public data class MessageTooLong(
        public val size: Long,
        public val maxSize: Long,
    ) : CapacityError() {
        override fun toString(): String = "Message too long: $size > $maxSize"
    }
}

/** Indicates the specific type/cause of a subprotocol header error. */
public sealed class SubProtocolError {
    public object ServerSentSubProtocolNoneRequested : SubProtocolError() {
        override fun toString(): String = "Server sent a subprotocol but none was requested"
    }

    public object InvalidSubProtocol : SubProtocolError() {
        override fun toString(): String = "Server sent an invalid subprotocol"
    }

    public object NoSubProtocol : SubProtocolError() {
        override fun toString(): String = "Server sent no subprotocol"
    }
}

/** Indicates the specific type/cause of a protocol error. */
public sealed class ProtocolError {
    public object WrongHttpMethod : ProtocolError() {
        override fun toString(): String = "Unsupported HTTP method used - only GET is allowed"
    }

    public object WrongHttpVersion : ProtocolError() {
        override fun toString(): String = "HTTP version must be 1.1 or higher"
    }

    public object MissingConnectionUpgradeHeader : ProtocolError() {
        override fun toString(): String = "No \"Connection: upgrade\" header"
    }

    public object MissingUpgradeWebSocketHeader : ProtocolError() {
        override fun toString(): String = "No \"Upgrade: websocket\" header"
    }

    public object MissingSecWebSocketVersionHeader : ProtocolError() {
        override fun toString(): String = "No \"Sec-WebSocket-Version: 13\" header"
    }

    public object MissingSecWebSocketKey : ProtocolError() {
        override fun toString(): String = "No \"Sec-WebSocket-Key\" header"
    }

    public object SecWebSocketAcceptKeyMismatch : ProtocolError() {
        override fun toString(): String = "Key mismatch in \"Sec-WebSocket-Accept\" header"
    }

    public data class SecWebSocketSubProtocol(
        public val error: SubProtocolError,
    ) : ProtocolError() {
        override fun toString(): String = "SubProtocol error: $error"
    }

    public data class InvalidExtensionsHeader(
        public val reason: String,
    ) : ProtocolError() {
        override fun toString(): String = "Invalid \"Sec-WebSocket-Extensions\" header: $reason"
    }

    public object JunkAfterRequest : ProtocolError() {
        override fun toString(): String = "Junk after client request"
    }

    public object CustomResponseSuccessful : ProtocolError() {
        override fun toString(): String = "Custom response must not be successful"
    }

    public data class InvalidHeader(
        public val headerName: String,
    ) : ProtocolError() {
        override fun toString(): String = "Missing, duplicated or incorrect header $headerName"
    }

    public object HandshakeIncomplete : ProtocolError() {
        override fun toString(): String = "Handshake not finished"
    }

    public data class Httparse(
        public val message: String,
    ) : ProtocolError() {
        override fun toString(): String = "httparse error: $message"
    }

    public object SendAfterClosing : ProtocolError() {
        override fun toString(): String = "Sending after closing is not allowed"
    }

    public object ReceivedAfterClosing : ProtocolError() {
        override fun toString(): String = "Remote sent after having closed"
    }

    public object NonZeroReservedBits : ProtocolError() {
        override fun toString(): String = "Reserved bits are non-zero"
    }

    public object UnmaskedFrameFromClient : ProtocolError() {
        override fun toString(): String = "Received an unmasked frame from client"
    }

    public object MaskedFrameFromServer : ProtocolError() {
        override fun toString(): String = "Received a masked frame from server"
    }

    public object FragmentedControlFrame : ProtocolError() {
        override fun toString(): String = "Fragmented control frame"
    }

    public object CompressedControlFrame : ProtocolError() {
        override fun toString(): String = "Compressed control frame"
    }

    public object ControlFrameTooBig : ProtocolError() {
        override fun toString(): String = "Control frame too big (payload must be 125 bytes or less)"
    }

    public data class UnknownControlFrameType(
        public val opcode: UByte,
    ) : ProtocolError() {
        override fun toString(): String = "Unknown control frame type: $opcode"
    }

    public data class UnknownDataFrameType(
        public val opcode: UByte,
    ) : ProtocolError() {
        override fun toString(): String = "Unknown data frame type: $opcode"
    }

    public object UnexpectedContinueFrame : ProtocolError() {
        override fun toString(): String = "Continue frame but nothing to continue"
    }

    public object CompressedContinueFrame : ProtocolError() {
        override fun toString(): String = "Continue frame must not have compress bit set"
    }

    public data class ExpectedFragment(
        public val data: Data,
    ) : ProtocolError() {
        override fun toString(): String = "While waiting for more fragments received: $data"
    }

    public object ResetWithoutClosingHandshake : ProtocolError() {
        override fun toString(): String = "Connection reset without closing handshake"
    }

    public data class InvalidOpcode(
        public val opcode: UByte,
    ) : ProtocolError() {
        override fun toString(): String = "Encountered invalid opcode: $opcode"
    }

    public object InvalidCloseSequence : ProtocolError() {
        override fun toString(): String = "Invalid close sequence"
    }

    public data class CompressionFailure(
        public val reason: String,
    ) : ProtocolError() {
        override fun toString(): String = "Compression/decompression failed: $reason"
    }
}

/** Indicates the specific type/cause of URL error. */
public sealed class UrlError {
    public object TlsFeatureNotEnabled : UrlError() {
        override fun toString(): String = "TLS support not compiled in"
    }

    public object NoHostName : UrlError() {
        override fun toString(): String = "No host name in the URL"
    }

    public data class UnableToConnect(
        public val host: String,
    ) : UrlError() {
        override fun toString(): String = "Unable to connect to $host"
    }

    public object UnsupportedUrlScheme : UrlError() {
        override fun toString(): String = "URL scheme not supported"
    }

    public object EmptyHostName : UrlError() {
        override fun toString(): String = "URL contains empty host name"
    }

    public object NoPathOrQuery : UrlError() {
        override fun toString(): String = "No path/query in URL"
    }

    public object UnsupportedProxyScheme : UrlError() {
        override fun toString(): String = "Proxy URL scheme not supported"
    }

    public data class InvalidProxyConfig(
        public val message: String,
    ) : UrlError() {
        override fun toString(): String = "Invalid proxy configuration: $message"
    }

    public data class ProxyConnect(
        public val message: String,
    ) : UrlError() {
        override fun toString(): String = "Proxy connection failed: $message"
    }
}

/** TLS errors. */
public sealed class TlsError {
    public data class Native(
        public val message: String,
    ) : TlsError() {
        override fun toString(): String = "native-tls error: $message"
    }

    public data class Rustls(
        public val message: String,
    ) : TlsError() {
        override fun toString(): String = "rustls error: $message"
    }

    public object InvalidDnsName : TlsError() {
        override fun toString(): String = "Invalid DNS name"
    }
}
