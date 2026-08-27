// port-lint: tests tests/write.rs
package io.github.kotlinmania.tungstenite

import io.github.kotlinmania.tungstenite.protocol.Message
import io.github.kotlinmania.tungstenite.protocol.Role
import io.github.kotlinmania.tungstenite.protocol.WebSocket
import io.github.kotlinmania.tungstenite.protocol.WebSocketConfig
import kotlin.test.Test
import kotlin.test.assertEquals

public class MockWrite {
    public var writtenBytes: Int = 0
    public var writeCount: Int = 0
    public var flushCount: Int = 0

    public fun write(buf: ByteArray, offset: Int, length: Int): Int {
        writtenBytes += length
        writeCount += 1
        return length
    }

    public fun flush() {
        flushCount += 1
    }
}

public class WriteTest {
    @Test
    public fun writeFlushBehaviour() {
        val sendMeLen = 10
        val batchMeLen = 11
        val writeBufferSize = 600

        val mock = MockWrite()
        val ws =
            WebSocket.fromRawSocket(
                mock,
                Role.Server,
                WebSocketConfig.default().writeBufferSize(writeBufferSize),
            )

        assertEquals(0, ws.getRef().writtenBytes)
        assertEquals(0, ws.getRef().writeCount)
        assertEquals(0, ws.getRef().flushCount)

        // `send` writes & flushes immediately
        ws.send(mock::write, mock::flush, Message.text("Send me!"))
        assertEquals(sendMeLen, ws.getRef().writtenBytes)
        assertEquals(1, ws.getRef().writeCount)
        assertEquals(1, ws.getRef().flushCount)

        // send a batch of messages
        for (i in 0 until 100) {
            ws.write(mock::write, Message.text("Batch me!"))
        }
        assertEquals(55 * batchMeLen + sendMeLen, ws.getRef().writtenBytes)
        assertEquals(2, ws.getRef().writeCount)
        assertEquals(1, ws.getRef().flushCount)

        // flushing will perform a single write for the remaining out_buffer & flush.
        ws.flush(mock::write, mock::flush)
        assertEquals(100 * batchMeLen + sendMeLen, ws.getRef().writtenBytes)
        assertEquals(3, ws.getRef().writeCount)
        assertEquals(2, ws.getRef().flushCount)
    }
}
