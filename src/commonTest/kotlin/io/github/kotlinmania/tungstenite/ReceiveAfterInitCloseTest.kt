// port-lint: tests tungstenite/tests/receive_after_init_close.rs
package io.github.kotlinmania.tungstenite

import io.github.kotlinmania.bytes.Bytes
import io.github.kotlinmania.tungstenite.protocol.Message
import io.github.kotlinmania.tungstenite.protocol.Role
import io.github.kotlinmania.tungstenite.protocol.WebSocketContext
import io.github.kotlinmania.tungstenite.protocol.frame.CloseCode
import io.github.kotlinmania.tungstenite.protocol.frame.CloseFrame
import io.github.kotlinmania.tungstenite.protocol.frame.Data
import io.github.kotlinmania.tungstenite.protocol.frame.Frame
import io.github.kotlinmania.tungstenite.protocol.frame.OpCode
import io.github.kotlinmania.tungstenite.protocol.frame.Utf8Bytes
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

public class ReceiveAfterInitCloseTest {
    @Test
    public fun testReceiveAfterInitClose() {
        val serverContext = WebSocketContext.new(Role.Server)

        val textFrame = Frame.message(Bytes.from("Hello WebSocket".encodeToByteArray()), OpCode.Data(Data.Text), true)
        textFrame.header.mask = byteArrayOf(1, 2, 3, 4)
        val textBytes = textFrame.format()

        val closeFrame = Frame.close(CloseFrame(CloseCode.Normal, Utf8Bytes.from("normal close")))
        closeFrame.header.mask = byteArrayOf(5, 6, 7, 8)
        val closeBytes = closeFrame.format()

        val combinedBytes = textBytes + closeBytes
        var pos = 0
        val readFn: (ByteArray) -> Int = { target ->
            if (pos >= combinedBytes.size) {
                0
            } else {
                val available = combinedBytes.size - pos
                val toRead = minOf(target.size, available)
                combinedBytes.copyInto(target, 0, pos, pos + toRead)
                pos += toRead
                toRead
            }
        }

        val writeFn: (ByteArray, Int, Int) -> Int = { _, _, len -> len }
        val flushFn: () -> Unit = {}

        // Server initiates close
        serverContext.close(writeFn, flushFn, null)

        // Read succeeds even though server already initiated close
        val msg = serverContext.read(readFn, writeFn, flushFn)
        assertEquals("Hello WebSocket", (msg as? Message.Text)?.utf8?.asStr())

        // Receive close acknowledgement
        val ack = serverContext.read(readFn, writeFn, flushFn)
        assertTrue(ack is Message.Close)

        // Further read gives ConnectionClosed
        assertFailsWith<TungsteniteException.ConnectionClosed> {
            serverContext.read(readFn, writeFn, flushFn)
        }
    }
}
