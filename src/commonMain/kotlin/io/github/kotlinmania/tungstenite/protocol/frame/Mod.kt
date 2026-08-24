// port-lint: source protocol/frame/mod.rs
package io.github.kotlinmania.tungstenite.protocol.frame

import io.github.kotlinmania.bytes.Bytes
import io.github.kotlinmania.tungstenite.CapacityError
import io.github.kotlinmania.tungstenite.ProtocolError
import io.github.kotlinmania.tungstenite.TungsteniteException
import io.github.kotlinmania.tungstenite.protocol.Message

/** Default read buffer size used for frame operations (128 KiB). */
public const val READ_BUF_LEN: Int = 128 * 1024

/**
 * A reader and writer for WebSocket frames.
 */
public class FrameSocket<Stream>(
    public var stream: Stream,
    public val codec: FrameCodec = FrameCodec.new(READ_BUF_LEN),
) {
    /** Extract a stream from the socket along with any unconsumed input buffer bytes. */
    public fun intoInner(): Pair<Stream, ByteArray> =
        Pair(stream, codec.inBuffer.toByteArray())

    /** Returns a reference to the inner stream. */
    public fun getRef(): Stream = stream

    /** Returns a mutable reference to the inner stream. */
    public fun getMut(): Stream = stream

    /** Read a frame using a stream read function. */
    public fun read(readFn: (ByteArray) -> Int, maxSize: Long? = null): Frame? =
        codec.readFrame(readFn, maxSize, unmask = false, acceptUnmasked = true)

    /** Write and immediately flush a frame using stream callbacks. */
    public fun send(
        writeFn: (ByteArray, Int, Int) -> Int,
        flushFn: () -> Unit,
        frame: Frame,
    ) {
        write(writeFn, frame)
        flush(flushFn, writeFn)
    }

    /** Write a frame into the buffer. */
    public fun write(writeFn: (ByteArray, Int, Int) -> Int, frame: Frame) {
        codec.bufferFrame(writeFn, frame)
    }

    /** Flush buffered writes to stream. */
    public fun flush(flushFn: () -> Unit, writeFn: (ByteArray, Int, Int) -> Int) {
        codec.writeOutBuffer(writeFn)
        flushFn()
    }

    public companion object {
        /** Create a new frame socket. */
        public fun <Stream> new(stream: Stream): FrameSocket<Stream> =
            FrameSocket(stream, FrameCodec.new(READ_BUF_LEN))

        /** Create a new frame socket from partially read data. */
        public fun <Stream> fromPartiallyRead(stream: Stream, part: ByteArray): FrameSocket<Stream> =
            FrameSocket(stream, FrameCodec.fromPartiallyRead(part, READ_BUF_LEN))
    }
}

/**
 * A codec for WebSocket frames.
 */
public class FrameCodec(
    inBufLen: Int = READ_BUF_LEN,
) {
    internal val inBuffer: ArrayList<Byte> = ArrayList(inBufLen)
    private var inBufMaxRead: Int = maxOf(inBufLen, FrameHeader.MAX_SIZE)
    internal val outBuffer: ArrayList<Byte> = ArrayList()
    public var maxOutBufferLen: Int = Int.MAX_VALUE
        private set
    public var outBufferWriteLen: Int = 0
        private set
    private var header: Pair<FrameHeader, Long>? = null

    /** Sets a maximum size for the out buffer. */
    public fun setMaxOutBufferLen(max: Int) {
        maxOutBufferLen = max
    }

    /** Sets buffer target length to reach before writing to the stream. */
    public fun setOutBufferWriteLen(len: Int) {
        outBufferWriteLen = len
    }

    /** Read a frame from the provided stream reader. */
    public fun readFrame(
        readFn: (ByteArray) -> Int,
        maxSize: Long? = null,
        unmask: Boolean = false,
        acceptUnmasked: Boolean = true,
    ): Frame? {
        val limit = maxSize ?: Long.MAX_VALUE

        while (true) {
            if (header == null) {
                val inBytes = inBuffer.toByteArray()
                val parsed = FrameHeader.parse(inBytes)
                if (parsed != null) {
                    val (h, payloadLen, headerBytesConsumed) = parsed
                    // Advance inBuffer by headerBytesConsumed
                    inBuffer.subList(0, headerBytesConsumed).clear()
                    if (payloadLen > limit) {
                        throw TungsteniteException.Capacity(
                            CapacityError.MessageTooLong(
                                size = payloadLen,
                                maxSize = limit,
                            ),
                        )
                    }
                    header = Pair(h, payloadLen)
                }
            }

            val currentHeader = header
            if (currentHeader != null) {
                val payloadLen = currentHeader.second.toInt()
                if (payloadLen <= inBuffer.size) {
                    val payloadBytes = ByteArray(payloadLen)
                    for (i in 0 until payloadLen) {
                        payloadBytes[i] = inBuffer[i]
                    }
                    inBuffer.subList(0, payloadLen).clear()
                    val (h, _) = header!!
                    header = null

                    if (unmask) {
                        val m = h.mask
                        if (m != null) {
                            applyMask(payloadBytes, m)
                            h.mask = null
                        } else if (!acceptUnmasked) {
                            throw TungsteniteException.ProtocolViolation(ProtocolError.UnmaskedFrameFromClient)
                        }
                    }

                    return Frame.fromPayload(h, Bytes.from(payloadBytes))
                }
            }

            // Not enough data in buffer; read into available buffer
            if (readIn(readFn) <= 0) {
                return null
            }
        }
    }

    /** Read into available inBuffer capacity. */
    public fun readIn(readFn: (ByteArray) -> Int): Int {
        val temp = ByteArray(inBufMaxRead)
        val readCount = readFn(temp)
        if (readCount > 0) {
            for (i in 0 until readCount) {
                inBuffer.add(temp[i])
            }
        }
        return readCount
    }

    /** Writes a frame into the out buffer and flushes if over outBufferWriteLen. */
    public fun bufferFrame(
        writeFn: (ByteArray, Int, Int) -> Int,
        frame: Frame,
    ) {
        if (frame.len() + outBuffer.size > maxOutBufferLen) {
            throw TungsteniteException.WriteBufferFull(Message.FrameMsg(frame))
        }

        val formatted = frame.format()
        for (b in formatted) {
            outBuffer.add(b)
        }

        if (outBuffer.size > outBufferWriteLen) {
            writeOutBuffer(writeFn)
        }
    }

    /** Writes the outBuffer to the provided stream write callback. */
    public fun writeOutBuffer(writeFn: (ByteArray, Int, Int) -> Int) {
        while (outBuffer.isNotEmpty()) {
            val bytes = outBuffer.toByteArray()
            val written = writeFn(bytes, 0, bytes.size)
            if (written <= 0) {
                throw TungsteniteException.Io("Connection reset while sending")
            }
            outBuffer.subList(0, written).clear()
        }
    }

    public companion object {
        /** Create a new frame codec. */
        public fun new(inBufLen: Int = READ_BUF_LEN): FrameCodec =
            FrameCodec(inBufLen)

        /** Create a new frame codec from partially read data. */
        public fun fromPartiallyRead(part: ByteArray, minInBufLen: Int = READ_BUF_LEN): FrameCodec {
            val codec = FrameCodec(minInBufLen)
            for (b in part) {
                codec.inBuffer.add(b)
            }
            return codec
        }
    }
}
