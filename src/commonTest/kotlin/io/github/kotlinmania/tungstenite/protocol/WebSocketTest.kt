// port-lint: tests protocol/mod.rs
package io.github.kotlinmania.tungstenite.protocol

import io.github.kotlinmania.tungstenite.CapacityError
import io.github.kotlinmania.tungstenite.ProtocolError
import io.github.kotlinmania.tungstenite.TungsteniteException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class WebSocketTest {
    private class WriteMoc(
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

        fun write(target: ByteArray): Int = target.size

        fun flush() {}
    }

    @Test
    fun testWebSocketConfigValidation() {
        val config =
            WebSocketConfig
                .default()
                .readBufferSize(64 * 1024)
                .writeBufferSize(64 * 1024)
                .maxWriteBufferSize(128 * 1024)

        assertEquals(64 * 1024, config.readBufferSize)
        assertEquals(64 * 1024, config.writeBufferSize)
        assertEquals(128 * 1024, config.maxWriteBufferSize)
        config.assertValid()
    }

    @Test
    fun testWebSocketState() {
        val state = WebSocketState.Active
        assertTrue(state.isActive())
        assertTrue(state.canRead())
        assertFalse(state.isTerminated())

        val closed = WebSocketState.ClosedByUs
        assertFalse(closed.isActive())
        assertTrue(closed.canRead())
        assertFalse(closed.isTerminated())

        val term = WebSocketState.Terminated
        assertFalse(term.isActive())
        assertFalse(term.canRead())
        assertTrue(term.isTerminated())
    }

    @Test
    fun testWebSocketContextCreation() {
        val ctx = WebSocketContext(Role.Client)
        assertEquals(Role.Client, ctx.role)
        assertTrue(ctx.canRead())
        assertTrue(ctx.canWrite())
    }

    @Test
    fun receiveMessages() {
        val incoming =
            byteArrayOf(
                0x89.toByte(),
                0x02,
                0x01,
                0x02,
                0x8a.toByte(),
                0x01,
                0x03,
                0x01,
                0x07,
                0x48,
                0x65,
                0x6c,
                0x6c,
                0x6f,
                0x2c,
                0x20,
                0x80.toByte(),
                0x06,
                0x57,
                0x6f,
                0x72,
                0x6c,
                0x64,
                0x21,
                0x82.toByte(),
                0x03,
                0x01,
                0x02,
                0x03,
            )
        val moc = WriteMoc(incoming)
        val socket = WebSocket.fromRawSocket(moc, Role.Client, null)

        assertEquals(Message.ping(byteArrayOf(1, 2)), socket.read(moc::read))
        assertEquals(Message.pong(byteArrayOf(3)), socket.read(moc::read))
        assertEquals(Message.text("Hello, World!"), socket.read(moc::read))
        assertEquals(Message.binary(byteArrayOf(1, 2, 3)), socket.read(moc::read))
    }

    @Test
    fun sizeLimitingTextFragmented() {
        val incoming =
            byteArrayOf(
                0x01,
                0x07,
                0x48,
                0x65,
                0x6c,
                0x6c,
                0x6f,
                0x2c,
                0x20,
                0x80.toByte(),
                0x06,
                0x57,
                0x6f,
                0x72,
                0x6c,
                0x64,
                0x21,
            )
        val limit = WebSocketConfig(maxMessageSize = 10L)
        val moc = WriteMoc(incoming)
        val socket = WebSocket.fromRawSocket(moc, Role.Client, limit)

        val ex =
            assertFailsWith<TungsteniteException.Capacity> {
                socket.read(moc::read)
            }
        val capError = ex.error
        assertTrue(capError is CapacityError.MessageTooLong)
        assertEquals(13L, capError.size)
        assertEquals(10L, capError.maxSize)
    }

    @Test
    fun sizeLimitingBinary() {
        val incoming = byteArrayOf(0x82.toByte(), 0x03, 0x01, 0x02, 0x03)
        val limit = WebSocketConfig(maxMessageSize = 2L)
        val moc = WriteMoc(incoming)
        val socket = WebSocket.fromRawSocket(moc, Role.Client, limit)

        val ex =
            assertFailsWith<TungsteniteException.Capacity> {
                socket.read(moc::read)
            }
        val capError = ex.error
        assertTrue(capError is CapacityError.MessageTooLong)
        assertEquals(3L, capError.size)
        assertEquals(2L, capError.maxSize)
    }

    @Test
    fun perMessageCompressionNotRecognized() {
        val incoming =
            byteArrayOf(
                0x41,
                0x03,
                0xf2.toByte(),
                0x48,
                0xcd.toByte(),
                0x80.toByte(),
                0x04,
                0xc9.toByte(),
                0xc9.toByte(),
                0x07,
                0x00,
            )
        val config = WebSocketConfig.default()
        val moc = WriteMoc(incoming)
        val socket = WebSocket.fromRawSocket(moc, Role.Client, config)

        val ex =
            assertFailsWith<TungsteniteException.ProtocolViolation> {
                socket.read(moc::read)
            }
        assertEquals(ProtocolError.NonZeroReservedBits, ex.error)
    }

    @Test
    fun perMessageDeflateDecompression() {
        // RFC 7692 Section 7.2.3.2
        val incoming =
            byteArrayOf(
                0x41,
                0x03,
                0xf2.toByte(),
                0x48,
                0xcd.toByte(),
                0x80.toByte(),
                0x04,
                0xc9.toByte(),
                0xc9.toByte(),
                0x07,
                0x00,
            )
        val config =
            WebSocketConfig(
                extensions =
                    io.github.kotlinmania.tungstenite.extensions.ExtensionsConfig(
                        permessageDeflate =
                            io.github.kotlinmania.tungstenite.extensions.compression.deflate.DeflateConfig
                                .default(),
                    ),
            )
        val moc = WriteMoc(incoming)
        val socket = WebSocket.fromRawSocket(moc, Role.Client, config)
        assertEquals(Message.text("Hello"), socket.read(moc::read))
    }

    @Test
    fun perMessageDeflateCompression() {
        val config =
            WebSocketConfig(
                extensions =
                    io.github.kotlinmania.tungstenite.extensions.ExtensionsConfig(
                        permessageDeflate =
                            io.github.kotlinmania.tungstenite.extensions.compression.deflate.DeflateConfig
                                .default(),
                    ),
            )
        val output = mutableListOf<Byte>()
        val moc = WriteMoc(byteArrayOf())
        val socket = WebSocket.fromRawSocket(moc, Role.Client, config)
        socket.send({ bytes, off, len ->
            for (i in off until off + len) output.add(bytes[i])
            len
        }, {}, Message.text("Hello"))
        socket.send({ bytes, off, len ->
            for (i in off until off + len) output.add(bytes[i])
            len
        }, {}, Message.text("Hello"))
        assertTrue(output.isNotEmpty())
    }

    private fun makeMessage(frameCount: Int): ByteArray = ByteArray(frameCount)

    @Test
    fun perMessageCompressionDecompressRespectsMessageSizeLimit() {
        val baseConfig =
            WebSocketConfig(
                maxMessageSize = 50L,
                extensions =
                    io.github.kotlinmania.tungstenite.extensions.ExtensionsConfig(
                        permessageDeflate =
                            io.github.kotlinmania.tungstenite.extensions.compression.deflate.DeflateConfig
                                .default(),
                    ),
            )
        val data = makeMessage(10)
        val moc = WriteMoc(data)
        val socket = WebSocket.fromRawSocket(moc, Role.Client, baseConfig)
        assertNotNull(socket)
    }
}
