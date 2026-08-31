// port-lint: tests tungstenite/src/protocol/message.rs
package io.github.kotlinmania.tungstenite.protocol

import io.github.kotlinmania.bytes.Bytes
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class MessageTest {
    @Test
    fun display() {
        val t = Message.text("test")
        assertEquals("test", t.toString())

        val bin = Message.binary(byteArrayOf(0, 1, 3, 4, 241.toByte()))
        assertEquals("Binary Data<length=5>", bin.toString())
    }

    @Test
    fun binaryConvert() {
        val bin = byteArrayOf(6, 7, 8, 9, 10, 241.toByte())
        val msg = Message.from(bin)
        assertTrue(msg.isBinary())
    }

    @Test
    fun binaryConvertBytes() {
        val bin = Bytes.from(byteArrayOf(6, 7, 8, 9, 10, 241.toByte()))
        val msg = Message.from(bin)
        assertTrue(msg.isBinary())
    }

    @Test
    fun binaryConvertVec() {
        val bin = byteArrayOf(6, 7, 8, 9, 10, 241.toByte())
        val msg = Message.from(bin)
        assertTrue(msg.isBinary())
    }

    @Test
    fun binaryConvertIntoBytes() {
        val bin = byteArrayOf(6, 7, 8, 9, 10, 241.toByte())
        val msg = Message.from(bin)
        val serialized = msg.intoData().asSlice()
        assertContentEquals(bin, serialized)
    }

    @Test
    fun textConvert() {
        val s = "kiwotsukete"
        val msg = Message.from(s)
        assertTrue(msg.isText())
        assertEquals("kiwotsukete", msg.toText())
    }
}
