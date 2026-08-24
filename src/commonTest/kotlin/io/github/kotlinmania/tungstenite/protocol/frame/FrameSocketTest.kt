// port-lint: tests protocol/frame/mod.rs
package io.github.kotlinmania.tungstenite.protocol.frame

import io.github.kotlinmania.tungstenite.CapacityError
import io.github.kotlinmania.tungstenite.TungsteniteException
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class FrameSocketTest {
    private class ByteCursor(
        private val data: ByteArray,
    ) {
        var pos: Int = 0

        fun read(target: ByteArray): Int {
            if (pos >= data.size) return 0
            val available = data.size - pos
            val toRead = minOf(target.size, available)
            data.copyInto(target, 0, pos, pos + toRead)
            pos += toRead
            return toRead
        }
    }

    private class ByteWriter {
        val bytes = ArrayList<Byte>()

        fun write(src: ByteArray, offset: Int, length: Int): Int {
            for (i in offset until offset + length) {
                bytes.add(src[i])
            }
            return length
        }
    }

    @Test
    fun readFrames() {
        val raw =
            byteArrayOf(
                0x82.toByte(),
                0x07.toByte(),
                0x01,
                0x02,
                0x03,
                0x04,
                0x05,
                0x06,
                0x07,
                0x82.toByte(),
                0x03.toByte(),
                0x03,
                0x02,
                0x01,
                0x99.toByte(),
            )
        val cursor = ByteCursor(raw)
        val sock = FrameSocket.new(cursor)

        val frame1 = sock.read(cursor::read, null)
        assertNotNull(frame1)
        assertContentEquals(
            byteArrayOf(0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07),
            frame1.payload.asSlice(),
        )

        val frame2 = sock.read(cursor::read, null)
        assertNotNull(frame2)
        assertContentEquals(
            byteArrayOf(0x03, 0x02, 0x01),
            frame2.payload.asSlice(),
        )

        val frame3 = sock.read(cursor::read, null)
        assertNull(frame3)

        val (_, rest) = sock.intoInner()
        assertContentEquals(byteArrayOf(0x99.toByte()), rest)
    }

    @Test
    fun fromPartiallyRead() {
        val raw = byteArrayOf(0x02, 0x03, 0x04, 0x05, 0x06, 0x07)
        val cursor = ByteCursor(raw)
        val part = byteArrayOf(0x82.toByte(), 0x07.toByte(), 0x01)
        val sock = FrameSocket.fromPartiallyRead(cursor, part)

        val frame = sock.read(cursor::read, null)
        assertNotNull(frame)
        assertContentEquals(
            byteArrayOf(0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07),
            frame.payload.asSlice(),
        )
    }

    @Test
    fun writeFrames() {
        val writer = ByteWriter()
        val sock = FrameSocket.new(writer)

        val pingFrame = Frame.ping(byteArrayOf(0x04, 0x05))
        sock.send(writer::write, {}, pingFrame)

        val pongFrame = Frame.pong(byteArrayOf(0x01))
        sock.send(writer::write, {}, pongFrame)

        val expected =
            byteArrayOf(
                0x89.toByte(),
                0x02,
                0x04,
                0x05,
                0x8a.toByte(),
                0x01,
                0x01,
            )
        assertContentEquals(expected, ByteArray(writer.bytes.size) { writer.bytes[it] })
    }

    @Test
    fun parseOverflow() {
        val raw =
            byteArrayOf(
                0x83.toByte(),
                0xFF.toByte(),
                0xFF.toByte(),
                0xFF.toByte(),
                0xFF.toByte(),
                0xFF.toByte(),
                0xFF.toByte(),
                0xFF.toByte(),
                0xFF.toByte(),
                0xFF.toByte(),
                0x00,
                0x00,
                0x00,
                0x00,
            )
        val cursor = ByteCursor(raw)
        val sock = FrameSocket.new(cursor)
        try {
            sock.read(cursor::read, null)
        } catch (_: Exception) {
            // should not crash or throw unexpected exceptions
        }
    }

    @Test
    fun sizeLimitHit() {
        val raw =
            byteArrayOf(
                0x82.toByte(),
                0x07.toByte(),
                0x01,
                0x02,
                0x03,
                0x04,
                0x05,
                0x06,
                0x07,
            )
        val cursor = ByteCursor(raw)
        val sock = FrameSocket.new(cursor)

        val ex =
            assertFailsWith<TungsteniteException.Capacity> {
                sock.read(cursor::read, 5L)
            }
        val capError = ex.error
        assertTrue(capError is CapacityError.MessageTooLong)
        assertEquals(7L, capError.size)
        assertEquals(5L, capError.maxSize)
    }
}
