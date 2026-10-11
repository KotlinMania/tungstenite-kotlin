// port-lint: source protocol/message.rs
package io.github.kotlinmania.tungstenite.protocol

import io.github.kotlinmania.bytes.Bytes
import io.github.kotlinmania.tungstenite.CapacityError
import io.github.kotlinmania.tungstenite.TungsteniteException
import io.github.kotlinmania.tungstenite.protocol.frame.CloseFrame
import io.github.kotlinmania.tungstenite.protocol.frame.Frame
import io.github.kotlinmania.tungstenite.protocol.frame.Utf8Bytes

internal class StringCollector {
    private val buffer = ArrayList<Byte>()

    fun len(): Int = buffer.size

    fun extend(tail: ByteArray) {
        for (b in tail) {
            buffer.add(b)
        }
    }

    fun intoString(): String =
        buffer.toByteArray().decodeToString()
}

/** The type of incomplete message. */
public enum class MessageType {
    Text,
    Binary,
}

/**
 * Collector holding incomplete message buffer.
 */
internal sealed class IncompleteMessageCollector {
    /** Text message collector. */
    internal class Text(
        internal val collector: StringCollector = StringCollector(),
    ) : IncompleteMessageCollector()

    /** Binary message collector. */
    internal class Binary(
        internal val buffer: ArrayList<Byte> = ArrayList(),
    ) : IncompleteMessageCollector()
}

/** A struct representing the incomplete message. */
public class IncompleteMessage(
    public val messageType: MessageType,
    private val compressed: Boolean = false,
) {
    internal val collector: IncompleteMessageCollector =
        when (messageType) {
            MessageType.Text -> IncompleteMessageCollector.Text()
            MessageType.Binary -> IncompleteMessageCollector.Binary()
        }

    public fun compressed(): Boolean = compressed

    public fun len(): Int =
        when (val c = collector) {
            is IncompleteMessageCollector.Text -> c.collector.len()
            is IncompleteMessageCollector.Binary -> c.buffer.size
        }

    public fun extend(tail: ByteArray, sizeLimit: Long? = null) {
        val maxSize = sizeLimit ?: Long.MAX_VALUE
        val mySize = len().toLong()
        val portionSize = tail.size.toLong()
        if (mySize > maxSize || portionSize > maxSize - mySize) {
            throw TungsteniteException.Capacity(
                CapacityError.MessageTooLong(
                    size = mySize + portionSize,
                    maxSize = maxSize,
                ),
            )
        }

        when (val c = collector) {
            is IncompleteMessageCollector.Text -> c.collector.extend(tail)
            is IncompleteMessageCollector.Binary -> {
                for (b in tail) {
                    c.buffer.add(b)
                }
            }
        }
    }

    public fun complete(): Message =
        when (val c = collector) {
            is IncompleteMessageCollector.Binary -> Message.binary(c.buffer.toByteArray())
            is IncompleteMessageCollector.Text -> Message.text(c.collector.intoString())
        }

    public companion object {
        public fun new(messageType: MessageType): IncompleteMessage =
            IncompleteMessage(messageType)

        public fun newCompressed(messageType: MessageType): IncompleteMessage =
            IncompleteMessage(messageType, compressed = true)
    }
}

/** An enum / sealed class representing the various forms of a WebSocket message. */
public sealed class Message {
    /** A text WebSocket message. */
    public data class Text(
        public val utf8: Utf8Bytes,
    ) : Message() {
        override fun toString(): String = utf8.asStr()
    }

    /** A binary WebSocket message. */
    public data class Binary(
        public val data: Bytes,
    ) : Message() {
        override fun toString(): String = "Binary Data<length=${len()}>"
    }

    /** A ping message with the specified payload. */
    public data class Ping(
        public val data: Bytes,
    ) : Message() {
        override fun toString(): String = "Ping<length=${len()}>"
    }

    /** A pong message with the specified payload. */
    public data class Pong(
        public val data: Bytes,
    ) : Message() {
        override fun toString(): String = "Pong<length=${len()}>"
    }

    /** A close message with the optional close frame. */
    public data class Close(
        public val frame: CloseFrame?,
    ) : Message() {
        override fun toString(): String = "Close(${frame?.toString() ?: "None"})"
    }

    /** Raw frame. */
    public data class FrameMsg(
        public val frame: Frame,
    ) : Message() {
        override fun toString(): String = frame.toString()
    }

    public fun isText(): Boolean = this is Text

    public fun isBinary(): Boolean = this is Binary

    public fun isPing(): Boolean = this is Ping

    public fun isPong(): Boolean = this is Pong

    public fun isClose(): Boolean = this is Close

    public fun len(): Int =
        when (this) {
            is Text -> utf8.length
            is Binary -> data.len()
            is Ping -> data.len()
            is Pong -> data.len()
            is Close -> frame?.reason?.length ?: 0
            is FrameMsg -> frame.len()
        }

    public fun isEmpty(): Boolean = len() == 0

    public fun intoData(): Bytes =
        when (this) {
            is Text -> utf8.bytes
            is Binary -> data
            is Ping -> data
            is Pong -> data
            is Close -> frame?.reason?.bytes ?: Bytes.new()
            is FrameMsg -> frame.intoPayload()
        }

    public fun intoText(): Utf8Bytes =
        when (this) {
            is Text -> utf8
            is Binary -> Utf8Bytes.tryFrom(data)
            is Ping -> Utf8Bytes.tryFrom(data)
            is Pong -> Utf8Bytes.tryFrom(data)
            is Close -> frame?.reason ?: Utf8Bytes.EMPTY
            is FrameMsg -> frame.intoText()
        }

    public fun toText(): String =
        when (this) {
            is Text -> utf8.asStr()
            is Binary -> data.asString()
            is Ping -> data.asString()
            is Pong -> data.asString()
            is Close -> frame?.reason?.asStr() ?: ""
            is FrameMsg -> frame.toText()
        }

    override fun toString(): String =
        when (this) {
            is Text -> utf8.asStr()
            is Binary -> "Binary Data<length=${len()}>"
            is Ping -> "Ping<length=${len()}>"
            is Pong -> "Pong<length=${len()}>"
            is Close -> "Close(${frame?.toString() ?: "None"})"
            is FrameMsg -> frame.toString()
        }

    public companion object {
        public fun text(string: String): Message =
            Text(Utf8Bytes.fromStatic(string))

        public fun text(string: Utf8Bytes): Message =
            Text(string)

        public fun binary(bin: Bytes): Message =
            Binary(bin)

        public fun binary(bin: ByteArray): Message =
            Binary(Bytes.from(bin))

        public fun ping(data: Bytes): Message =
            Ping(data)

        public fun ping(data: ByteArray): Message =
            Ping(Bytes.from(data))

        public fun pong(data: Bytes): Message =
            Pong(data)

        public fun pong(data: ByteArray): Message =
            Pong(Bytes.from(data))

        public fun close(msg: CloseFrame? = null): Message =
            Close(msg)

        public fun from(string: String): Message =
            text(string)

        public fun from(data: ByteArray): Message =
            binary(data)

        public fun from(data: Bytes): Message =
            binary(data)
    }
}
