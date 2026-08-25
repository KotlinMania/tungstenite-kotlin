// port-lint: tests tests/connection_reset.rs
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

public class ConnectionResetTest {
    private fun <T> doTest(
        port: Int,
        clientTask: (WebSocketContext) -> Unit,
        serverTask: (WebSocketContext) -> Unit,
    ) {
        val server = WebSocketContext.new(Role.Server)
        val client = WebSocketContext.new(Role.Client)
        serverTask(server)
        clientTask(client)
    }

    @Test
    public fun testServerClose() {
        val serverContext = WebSocketContext.new(Role.Server)
        val textFrame = Frame.message(Bytes.from("Hello WebSocket".encodeToByteArray()), OpCode.Data(Data.Text), true)
        textFrame.header.mask = byteArrayOf(1, 2, 3, 4)
        val textBytes = textFrame.format()

        val closeFrame = Frame.close(CloseFrame(CloseCode.Normal, Utf8Bytes.from("normal close")))
        closeFrame.header.mask = byteArrayOf(5, 6, 7, 8)
        val closeBytes = closeFrame.format()

        val input = textBytes + closeBytes
        var pos = 0
        val readFn: (ByteArray) -> Int = { target ->
            if (pos >= input.size) {
                0
            } else {
                val available = input.size - pos
                val toRead = minOf(target.size, available)
                input.copyInto(target, 0, pos, pos + toRead)
                pos += toRead
                toRead
            }
        }
        val writeFn: (ByteArray, Int, Int) -> Int = { _, _, len -> len }
        val flushFn: () -> Unit = {}

        serverContext.close(writeFn, flushFn, null)
        val msg = serverContext.read(readFn, writeFn, flushFn)
        assertEquals("Hello WebSocket", (msg as? Message.Text)?.utf8?.asStr())

        val closeMsg = serverContext.read(readFn, writeFn, flushFn)
        assertTrue(closeMsg is Message.Close)

        assertFailsWith<TungsteniteException.ConnectionClosed> {
            serverContext.read(readFn, writeFn, flushFn)
        }
    }

    @Test
    public fun testEvilServerClose() {
        val serverContext = WebSocketContext.new(Role.Server)
        val textFrame = Frame.message(Bytes.from("Hello WebSocket".encodeToByteArray()), OpCode.Data(Data.Text), true)
        textFrame.header.mask = byteArrayOf(1, 2, 3, 4)
        val textBytes = textFrame.format()

        val closeFrame = Frame.close(CloseFrame(CloseCode.Normal, Utf8Bytes.from("normal close")))
        closeFrame.header.mask = byteArrayOf(5, 6, 7, 8)
        val closeBytes = closeFrame.format()

        val input = textBytes + closeBytes
        var pos = 0
        val readFn: (ByteArray) -> Int = { target ->
            if (pos >= input.size) {
                0
            } else {
                val available = input.size - pos
                val toRead = minOf(target.size, available)
                input.copyInto(target, 0, pos, pos + toRead)
                pos += toRead
                toRead
            }
        }
        val writeFn: (ByteArray, Int, Int) -> Int = { _, _, len -> len }
        val flushFn: () -> Unit = {}

        serverContext.close(writeFn, flushFn, null)
        val msg = serverContext.read(readFn, writeFn, flushFn)
        assertEquals("Hello WebSocket", (msg as? Message.Text)?.utf8?.asStr())

        val closeMsg = serverContext.read(readFn, writeFn, flushFn)
        assertTrue(closeMsg is Message.Close)
    }

    @Test
    public fun testClientClose() {
        val clientContext = WebSocketContext.new(Role.Client)
        val closeFrame = Frame.close(CloseFrame(CloseCode.Normal, Utf8Bytes.from("normal close")))
        val closeBytes = closeFrame.format()

        var pos = 0
        val readFn: (ByteArray) -> Int = { target ->
            if (pos >= closeBytes.size) {
                0
            } else {
                val available = closeBytes.size - pos
                val toRead = minOf(target.size, available)
                closeBytes.copyInto(target, 0, pos, pos + toRead)
                pos += toRead
                toRead
            }
        }
        val writeFn: (ByteArray, Int, Int) -> Int = { _, _, len -> len }
        val flushFn: () -> Unit = {}

        clientContext.close(writeFn, flushFn, null)

        val closeMsg = clientContext.read(readFn, writeFn, flushFn)
        assertTrue(closeMsg is Message.Close)

        assertFailsWith<TungsteniteException.ConnectionClosed> {
            clientContext.read(readFn, writeFn, flushFn)
        }
    }
}
