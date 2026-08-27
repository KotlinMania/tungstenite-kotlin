// port-lint: tests tungstenite/src/protocol/frame/frame.rs
package io.github.kotlinmania.tungstenite.protocol.frame

import io.github.kotlinmania.bytes.Bytes
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class FrameTest {
    @Test
    fun parse() {
        val raw = byteArrayOf(0x82.toByte(), 0x07.toByte(), 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07)
        val parsed = FrameHeader.parse(raw)
        assertNotNull(parsed)
        val (header, length, consumed) = parsed
        assertEquals(7L, length)
        assertEquals(2, consumed)
        val payload = raw.copyOfRange(consumed, raw.size)
        val frame = Frame.fromPayload(header, Bytes.from(payload))
        assertContentEquals(byteArrayOf(0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07), frame.intoPayload().asSlice())
    }

    @Test
    fun format() {
        val frame = Frame.ping(byteArrayOf(0x01, 0x02))
        val buf = frame.format()
        assertContentEquals(byteArrayOf(0x89.toByte(), 0x02.toByte(), 0x01, 0x02), buf)
    }

    @Test
    fun formatIntoBuf() {
        val frame = Frame.ping(byteArrayOf(0x01, 0x02))
        val buf = ByteArray(frame.len())
        frame.formatIntoBuf(buf)
        assertContentEquals(byteArrayOf(0x89.toByte(), 0x02.toByte(), 0x01, 0x02), buf)
    }

    @Test
    fun display() {
        val f = Frame.message(Bytes.from("hi there".encodeToByteArray()), OpCode.Data(Data.Text), true)
        val view = f.toString()
        assertTrue(view.contains("payload:"))
    }
}
