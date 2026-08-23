// port-lint: tests buffer.rs
package io.github.kotlinmania.tungstenite

import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals

class BufferTest {
    @Test
    fun simpleReading() {
        val input = "Hello World!".encodeToByteArray()
        var inputPos = 0
        val buffer = ReadBuffer.new(4096)
        val size = buffer.readFrom { chunk ->
            val available = input.size - inputPos
            val toRead = minOf(chunk.size, available)
            if (toRead > 0) {
                input.copyInto(chunk, 0, inputPos, inputPos + toRead)
                inputPos += toRead
            }
            toRead
        }
        assertEquals(12, size)
        assertContentEquals("Hello World!".encodeToByteArray(), buffer.chunk())
    }

    @Test
    fun readingInChunks() {
        val input = "Hello World!".encodeToByteArray()
        var inputPos = 0
        val buf = ReadBuffer.new(4)

        fun readNext(): Int = buf.readFrom { chunk ->
            val available = input.size - inputPos
            val toRead = minOf(chunk.size, available)
            if (toRead > 0) {
                input.copyInto(chunk, 0, inputPos, inputPos + toRead)
                inputPos += toRead
            }
            toRead
        }

        var size = readNext()
        assertEquals(4, size)
        assertContentEquals("Hell".encodeToByteArray(), buf.chunk())

        buf.advance(2)
        assertContentEquals("ll".encodeToByteArray(), buf.chunk())

        size = readNext()
        assertEquals(4, size)
        assertContentEquals("llo Wo".encodeToByteArray(), buf.chunk())

        size = readNext()
        assertEquals(4, size)
        assertContentEquals("llo World!".encodeToByteArray(), buf.chunk())
    }

    @Test
    fun testFromPartiallyReadAndIntoByteArray() {
        val initial = "InitialData".encodeToByteArray()
        val buf = ReadBuffer.fromPartiallyRead(initial, 4096)
        assertEquals(11, buf.remaining())
        buf.advance(7)
        assertEquals(4, buf.remaining())
        assertContentEquals("Data".encodeToByteArray(), buf.chunk())
        val remaining = buf.intoByteArray()
        assertContentEquals("Data".encodeToByteArray(), remaining)
        assertEquals(0, buf.remaining())
    }
}
