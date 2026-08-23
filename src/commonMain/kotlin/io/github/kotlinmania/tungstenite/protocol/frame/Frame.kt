// port-lint: source protocol/frame/frame.rs
package io.github.kotlinmania.tungstenite.protocol.frame

import io.github.kotlinmania.bytes.Bytes
import io.github.kotlinmania.tungstenite.ProtocolError
import io.github.kotlinmania.tungstenite.TungsteniteException

/** A struct representing the close command. */
public data class CloseFrame(
    /** The reason as a code. */
    public val code: CloseCode,
    /** The reason as text string. */
    public val reason: Utf8Bytes,
) {
    override fun toString(): String = "$reason ($code)"
}

/** Handling of the length format. */
internal sealed class LengthFormat {
    data class U8(
        val byteValue: UByte,
    ) : LengthFormat()

    object U16 : LengthFormat()

    object U64 : LengthFormat()

    fun extraBytes(): Int =
        when (this) {
            is U8 -> 0
            is U16 -> 2
            is U64 -> 8
        }

    fun lengthByte(): UByte =
        when (this) {
            is U8 -> byteValue
            is U16 -> 126u
            is U64 -> 127u
        }

    companion object {
        fun forLength(length: Long): LengthFormat =
            if (length < 126L) {
                U8(length.toUByte())
            } else if (length < 65536L) {
                U16
            } else {
                U64
            }

        fun forByte(byte: UByte): LengthFormat =
            when (byte.toInt() and 0x7F) {
                126 -> U16
                127 -> U64
                else -> U8((byte.toInt() and 0x7F).toUByte())
            }
    }
}

/** A struct representing a WebSocket frame header. */
public data class FrameHeader(
    /** Indicates that the frame is the last one of a possibly fragmented message. */
    public var isFinal: Boolean = true,
    /** Reserved for protocol extensions. */
    public var rsv1: Boolean = false,
    /** Reserved for protocol extensions. */
    public var rsv2: Boolean = false,
    /** Reserved for protocol extensions. */
    public var rsv3: Boolean = false,
    /** WebSocket protocol opcode. */
    public var opcode: OpCode = OpCode.Control(Control.Close),
    /** A frame mask, if any. */
    public var mask: ByteArray? = null,
) {
    public fun len(payloadLength: Long): Int =
        2 + LengthFormat.forLength(payloadLength).extraBytes() + (if (mask != null) 4 else 0)

    public fun format(payloadLength: Long): ByteArray {
        val code = opcode.toUByte().toInt()
        val one =
            code or
                (if (isFinal) 0x80 else 0) or
                (if (rsv1) 0x40 else 0) or
                (if (rsv2) 0x20 else 0) or
                (if (rsv3) 0x10 else 0)

        val lenFmt = LengthFormat.forLength(payloadLength)
        val two = lenFmt.lengthByte().toInt() or (if (mask != null) 0x80 else 0)

        val headerSize = len(payloadLength)
        val result = ByteArray(headerSize)
        result[0] = one.toByte()
        result[1] = two.toByte()
        var pos = 2

        when (lenFmt) {
            is LengthFormat.U8 -> {}
            is LengthFormat.U16 -> {
                val lenUShort = payloadLength.toUShort().toInt()
                result[pos++] = ((lenUShort shr 8) and 0xFF).toByte()
                result[pos++] = (lenUShort and 0xFF).toByte()
            }
            is LengthFormat.U64 -> {
                for (shift in 56 downTo 0 step 8) {
                    result[pos++] = ((payloadLength ushr shift) and 0xFFL).toByte()
                }
            }
        }

        val m = mask
        if (m != null) {
            require(m.size >= 4) { "Mask must be at least 4 bytes" }
            result[pos++] = m[0]
            result[pos++] = m[1]
            result[pos++] = m[2]
            result[pos++] = m[3]
        }

        return result
    }

    public fun setRandomMask() {
        mask = generateMask()
    }

    public companion object {
        public const val MAX_SIZE: Int = 14

        public fun default(): FrameHeader = FrameHeader()

        /**
         * Parse a header from a byte slice.
         * Returns `(header, payloadLength, bytesConsumed)` or `null` if insufficient data.
         */
        public fun parse(data: ByteArray, offset: Int = 0, length: Int = data.size - offset): Triple<FrameHeader, Long, Int>? {
            if (length < 2) return null
            val first = data[offset].toUByte().toInt()
            val second = data[offset + 1].toUByte().toInt()

            val isFinal = (first and 0x80) != 0
            val rsv1 = (first and 0x40) != 0
            val rsv2 = (first and 0x20) != 0
            val rsv3 = (first and 0x10) != 0
            val opcode = OpCode.fromUByte((first and 0x0F).toUByte())

            val masked = (second and 0x80) != 0
            val lenByte = (second and 0x7F).toUByte()
            val lenFmt = LengthFormat.forByte(lenByte)
            val extraBytes = lenFmt.extraBytes()
            val maskBytes = if (masked) 4 else 0
            val totalHeaderSize = 2 + extraBytes + maskBytes

            if (length < totalHeaderSize) return null

            var pos = offset + 2
            val payloadLen: Long =
                when (lenFmt) {
                    is LengthFormat.U8 -> lenByte.toLong()
                    is LengthFormat.U16 -> {
                        val b0 = data[pos++].toUByte().toLong()
                        val b1 = data[pos++].toUByte().toLong()
                        (b0 shl 8) or b1
                    }
                    is LengthFormat.U64 -> {
                        var l = 0L
                        for (i in 0 until 8) {
                            l = (l shl 8) or data[pos++].toUByte().toLong()
                        }
                        l
                    }
                }

            val mask: ByteArray? =
                if (masked) {
                    val m = ByteArray(4)
                    m[0] = data[pos++]
                    m[1] = data[pos++]
                    m[2] = data[pos++]
                    m[3] = data[pos++]
                    m
                } else {
                    null
                }

            // Disallow bad opcode
            when (opcode) {
                is OpCode.Control ->
                    if (opcode.code is Control.Reserved) {
                        throw TungsteniteException.Protocol(ProtocolError.InvalidOpcode((first and 0x0F).toUByte()))
                    }
                is OpCode.Data ->
                    if (opcode.code is Data.Reserved) {
                        throw TungsteniteException.Protocol(ProtocolError.InvalidOpcode((first and 0x0F).toUByte()))
                    }
            }

            val header =
                FrameHeader(
                    isFinal = isFinal,
                    rsv1 = rsv1,
                    rsv2 = rsv2,
                    rsv3 = rsv3,
                    opcode = opcode,
                    mask = mask,
                )
            return Triple(header, payloadLen, totalHeaderSize)
        }
    }
}

/** A struct representing a WebSocket frame. */
public data class Frame(
    public var header: FrameHeader,
    public var payload: Bytes,
) {
    public fun len(): Int {
        val payloadLen = payload.len()
        return header.len(payloadLen.toLong()) + payloadLen
    }

    public fun isEmpty(): Boolean = len() == 0

    public fun header(): FrameHeader = header

    public fun payload(): ByteArray = payload.asSlice()

    public fun isMasked(): Boolean = header.mask != null

    public fun setRandomMask() {
        header.setRandomMask()
    }

    public fun intoText(): Utf8Bytes =
        Utf8Bytes.tryFrom(payload)

    public fun intoPayload(): Bytes = payload

    public fun toText(): String =
        payload.asString()

    public fun intoClose(): CloseFrame? {
        val bytes = payload.asSlice()
        return when (bytes.size) {
            0 -> null
            1 -> throw TungsteniteException.Protocol(ProtocolError.InvalidCloseSequence)
            else -> {
                val codeVal = (((bytes[0].toInt() and 0xFF) shl 8) or (bytes[1].toInt() and 0xFF)).toUShort()
                val code = CloseCode.fromUShort(codeVal)
                val reasonBytes = bytes.copyOfRange(2, bytes.size)
                val reason = Utf8Bytes.tryFrom(reasonBytes)
                CloseFrame(code, reason)
            }
        }
    }

    public fun format(): ByteArray {
        val headerBytes = header.format(payload.len().toLong())
        val payloadBytes = payload.asSlice()
        val m = header.mask
        if (m != null) {
            applyMask(payloadBytes, m)
        }
        val out = ByteArray(headerBytes.size + payloadBytes.size)
        headerBytes.copyInto(out, 0)
        payloadBytes.copyInto(out, headerBytes.size)
        return out
    }

    public fun formatIntoBuf(buf: ByteArray): Int {
        val formatted = format()
        formatted.copyInto(buf, 0)
        return formatted.size
    }

    override fun toString(): String {
        val hexPayload = payload.asSlice().joinToString("") { (it.toInt() and 0xFF).toString(16).padStart(2, '0') }
        return "\n<FRAME>\nfinal: ${header.isFinal}\nreserved: ${header.rsv1} ${header.rsv2} ${header.rsv3}\nopcode: ${header.opcode}\nlength: ${len()}\npayload length: ${payload.len()}\npayload: 0x$hexPayload\n"
    }

    public fun headerMut(): FrameHeader = header

    public companion object {
        public fun default(): Frame = Frame(FrameHeader.default(), Bytes.new())

        public fun message(data: Bytes, opcode: OpCode, isFinal: Boolean = true): Frame {
            require(opcode is OpCode.Data) { "Invalid opcode for data frame." }
            return Frame(FrameHeader(isFinal = isFinal, opcode = opcode), data)
        }

        public fun message(data: ByteArray, opcode: OpCode, isFinal: Boolean = true): Frame =
            message(Bytes.from(data), opcode, isFinal)

        public fun compressedMessage(data: Bytes, opcode: OpCode, isFinal: Boolean = true): Frame {
            require(opcode is OpCode.Data) { "Invalid opcode for data frame." }
            return Frame(FrameHeader(isFinal = isFinal, rsv1 = true, opcode = opcode), data)
        }

        public fun compressedMessage(data: ByteArray, opcode: OpCode, isFinal: Boolean = true): Frame =
            compressedMessage(Bytes.from(data), opcode, isFinal)

        public fun pong(data: Bytes): Frame =
            Frame(FrameHeader(opcode = OpCode.Control(Control.Pong)), data)

        public fun pong(data: ByteArray): Frame =
            pong(Bytes.from(data))

        public fun ping(data: Bytes): Frame =
            Frame(FrameHeader(opcode = OpCode.Control(Control.Ping)), data)

        public fun ping(data: ByteArray): Frame =
            ping(Bytes.from(data))

        public fun close(msg: CloseFrame? = null): Frame {
            val payload =
                if (msg != null) {
                    val codeUShort = msg.code.toUShort().toInt()
                    val reasonBytes = msg.reason.asBytes()
                    val p = ByteArray(2 + reasonBytes.size)
                    p[0] = ((codeUShort shr 8) and 0xFF).toByte()
                    p[1] = (codeUShort and 0xFF).toByte()
                    reasonBytes.copyInto(p, 2)
                    Bytes.from(p)
                } else {
                    Bytes.new()
                }
            return Frame(FrameHeader(), payload)
        }

        public fun fromPayload(header: FrameHeader, payload: Bytes): Frame =
            Frame(header, payload)
    }
}
