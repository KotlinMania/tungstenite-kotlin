// port-lint: source protocol/frame/coding.rs
@file:OptIn(kotlin.experimental.ExperimentalObjCRefinement::class)

package io.github.kotlinmania.tungstenite.protocol.frame

import kotlin.native.HiddenFromObjC

// Various codes defined in RFC 6455.

/** WebSocket message opcode as in RFC 6455. */
@HiddenFromObjC
public sealed class OpCode {
    /** Data (text or binary). */
    public data class Data(
        public val code: io.github.kotlinmania.tungstenite.protocol.frame.Data,
    ) : OpCode() {
        override fun toString(): String = code.toString()
    }

    /** Control message (close, ping, pong). */
    public data class Control(
        public val code: io.github.kotlinmania.tungstenite.protocol.frame.Control,
    ) : OpCode() {
        override fun toString(): String = code.toString()
    }

    public fun toUByte(): UByte =
        when (this) {
            is Data ->
                when (val d = code) {
                    is io.github.kotlinmania.tungstenite.protocol.frame.Data.Continue -> 0u
                    is io.github.kotlinmania.tungstenite.protocol.frame.Data.Text -> 1u
                    is io.github.kotlinmania.tungstenite.protocol.frame.Data.Binary -> 2u
                    is io.github.kotlinmania.tungstenite.protocol.frame.Data.Reserved -> d.value
                }
            is Control ->
                when (val c = code) {
                    is io.github.kotlinmania.tungstenite.protocol.frame.Control.Close -> 8u
                    is io.github.kotlinmania.tungstenite.protocol.frame.Control.Ping -> 9u
                    is io.github.kotlinmania.tungstenite.protocol.frame.Control.Pong -> 10u
                    is io.github.kotlinmania.tungstenite.protocol.frame.Control.Reserved -> c.value
                }
        }

    public companion object {
        public fun fromUByte(byte: UByte): OpCode =
            when (val b = byte.toInt()) {
                0 -> Data(io.github.kotlinmania.tungstenite.protocol.frame.Data.Continue)
                1 -> Data(io.github.kotlinmania.tungstenite.protocol.frame.Data.Text)
                2 -> Data(io.github.kotlinmania.tungstenite.protocol.frame.Data.Binary)
                in 3..7 ->
                    Data(
                        io.github.kotlinmania.tungstenite.protocol.frame.Data
                            .Reserved(byte),
                    )
                8 -> Control(io.github.kotlinmania.tungstenite.protocol.frame.Control.Close)
                9 -> Control(io.github.kotlinmania.tungstenite.protocol.frame.Control.Ping)
                10 -> Control(io.github.kotlinmania.tungstenite.protocol.frame.Control.Pong)
                in 11..15 ->
                    Control(
                        io.github.kotlinmania.tungstenite.protocol.frame.Control
                            .Reserved(byte),
                    )
                else -> error("Bug: OpCode out of range: $b")
            }
    }
}

/** Data opcodes as in RFC 6455 */
@HiddenFromObjC
public sealed class Data {
    /** 0x0 denotes a continuation frame */
    public object Continue : Data() {
        override fun toString(): String = "CONTINUE"
    }

    /** 0x1 denotes a text frame */
    public object Text : Data() {
        override fun toString(): String = "TEXT"
    }

    /** 0x2 denotes a binary frame */
    public object Binary : Data() {
        override fun toString(): String = "BINARY"
    }

    /** 0x3-7 are reserved for further non-control frames */
    public data class Reserved(
        public val value: UByte,
    ) : Data() {
        override fun toString(): String = "RESERVED_DATA_$value"
    }
}

/** Control opcodes as in RFC 6455 */
@HiddenFromObjC
public sealed class Control {
    /** 0x8 denotes a connection close */
    public object Close : Control() {
        override fun toString(): String = "CLOSE"
    }

    /** 0x9 denotes a ping */
    public object Ping : Control() {
        override fun toString(): String = "PING"
    }

    /** 0xa denotes a pong */
    public object Pong : Control() {
        override fun toString(): String = "PONG"
    }

    /** 0xb-f are reserved for further control frames */
    public data class Reserved(
        public val value: UByte,
    ) : Control() {
        override fun toString(): String = "RESERVED_CONTROL_$value"
    }
}

/** Status code used to indicate why an endpoint is closing the WebSocket connection. */
@HiddenFromObjC
public sealed class CloseCode {
    /**
     * Indicates a normal closure, meaning that the purpose for
     * which the connection was established has been fulfilled.
     */
    public object Normal : CloseCode()

    /**
     * Indicates that an endpoint is "going away", such as a server
     * going down or a browser having navigated away from a page.
     */
    public object Away : CloseCode()

    /**
     * Indicates that an endpoint is terminating the connection due
     * to a protocol error.
     */
    public object Protocol : CloseCode()

    /**
     * Indicates that an endpoint is terminating the connection
     * because it has received a type of data it cannot accept (e.g., an
     * endpoint that understands only text data MAY send this if it
     * receives a binary message).
     */
    public object Unsupported : CloseCode()

    /**
     * Indicates that no status code was included in a closing frame. This
     * close code makes it possible to use a single method, `onClose` to
     * handle even cases where no close code was provided.
     */
    public object Status : CloseCode()

    /**
     * Indicates an abnormal closure. If the abnormal closure was due to an
     * error, this close code will not be used. Instead, the `onError` method
     * of the handler will be called with the error. However, if the connection
     * is simply dropped, without an error, this close code will be sent to the
     * handler.
     */
    public object Abnormal : CloseCode()

    /**
     * Indicates that an endpoint is terminating the connection
     * because it has received data within a message that was not
     * consistent with the type of the message (e.g., non-UTF-8 \[RFC3629\]
     * data within a text message).
     */
    public object Invalid : CloseCode()

    /**
     * Indicates that an endpoint is terminating the connection
     * because it has received a message that violates its policy.  This
     * is a generic status code that can be returned when there is no
     * other more suitable status code (e.g., Unsupported or Size) or if there
     * is a need to hide specific details about the policy.
     */
    public object Policy : CloseCode()

    /**
     * Indicates that an endpoint is terminating the connection
     * because it has received a message that is too big for it to
     * process.
     */
    public object Size : CloseCode()

    /**
     * Indicates that an endpoint (client) is terminating the
     * connection because it has expected the server to negotiate one or
     * more extension, but the server didn't return them in the response
     * message of the WebSocket handshake.  The list of extensions that
     * are needed should be given as the reason for closing.
     * Note that this status code is not used by the server, because it
     * can fail the WebSocket handshake instead.
     */
    public object Extension : CloseCode()

    /**
     * Indicates that a server is terminating the connection because
     * it encountered an unexpected condition that prevented it from
     * fulfilling the request.
     */
    public object Error : CloseCode()

    /**
     * Indicates that the server is restarting. A client may choose to reconnect,
     * and if it does, it should use a randomized delay of 5-30 seconds between attempts.
     */
    public object Restart : CloseCode()

    /**
     * Indicates that the server is overloaded and the client should either connect
     * to a different IP (when multiple targets exist), or reconnect to the same IP
     * when a user has performed an action.
     */
    public object Again : CloseCode()

    public object Tls : CloseCode()

    public data class Reserved(
        public val value: UShort,
    ) : CloseCode() {
        override fun toString(): String = value.toString()
    }

    public data class Iana(
        public val value: UShort,
    ) : CloseCode() {
        override fun toString(): String = value.toString()
    }

    public data class Library(
        public val value: UShort,
    ) : CloseCode() {
        override fun toString(): String = value.toString()
    }

    public data class Bad(
        public val value: UShort,
    ) : CloseCode() {
        override fun toString(): String = value.toString()
    }

    /** Check if this CloseCode is allowed. */
    public fun isAllowed(): Boolean =
        when (this) {
            is Bad, is Reserved, Status, Abnormal, Tls -> false
            else -> true
        }

    override fun toString(): String = toUShort().toString()

    public fun toUShort(): UShort =
        when (this) {
            Normal -> 1000u
            Away -> 1001u
            Protocol -> 1002u
            Unsupported -> 1003u
            Status -> 1005u
            Abnormal -> 1006u
            Invalid -> 1007u
            Policy -> 1008u
            Size -> 1009u
            Extension -> 1010u
            Error -> 1011u
            Restart -> 1012u
            Again -> 1013u
            Tls -> 1015u
            is Reserved -> value
            is Iana -> value
            is Library -> value
            is Bad -> value
        }

    public companion object {
        public fun fromUShort(code: UShort): CloseCode =
            when (val c = code.toInt()) {
                1000 -> Normal
                1001 -> Away
                1002 -> Protocol
                1003 -> Unsupported
                1005 -> Status
                1006 -> Abnormal
                1007 -> Invalid
                1008 -> Policy
                1009 -> Size
                1010 -> Extension
                1011 -> Error
                1012 -> Restart
                1013 -> Again
                1015 -> Tls
                in 1..999 -> Bad(code)
                in 1016..2999 -> Reserved(code)
                in 3000..3999 -> Iana(code)
                in 4000..4999 -> Library(code)
                else -> Bad(code)
            }
    }
}
