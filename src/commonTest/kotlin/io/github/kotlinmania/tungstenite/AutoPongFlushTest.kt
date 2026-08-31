// port-lint: tests ../tests/auto_pong_flush.rs
package io.github.kotlinmania.tungstenite

import io.github.kotlinmania.tungstenite.protocol.Message
import io.github.kotlinmania.tungstenite.protocol.Role
import io.github.kotlinmania.tungstenite.protocol.WebSocketContext
import io.github.kotlinmania.tungstenite.protocol.frame.Frame
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

public class AutoPongFlushTest {
    @Test
    public fun readUsageAutoPongFlush() {
        val clientContext = WebSocketContext.new(Role.Client)

        // Send a ping frame into client context
        val pingFrame = Frame.ping(ByteArray(0))
        val pingBytes = pingFrame.format()
        var pos = 0
        val readFn: (ByteArray) -> Int = { target ->
            if (pos >= pingBytes.size) {
                0
            } else {
                val available = pingBytes.size - pos
                val toRead = minOf(target.size, available)
                pingBytes.copyInto(target, 0, pos, pos + toRead)
                pos += toRead
                toRead
            }
        }

        var writeCalls = 0
        val writtenBytes = mutableListOf<Byte>()
        val writeFn: (ByteArray, Int, Int) -> Int = { buf, off, len ->
            writeCalls++
            for (i in 0 until len) {
                writtenBytes.add(buf[off + i])
            }
            len
        }

        var flushCalls = 0
        val flushFn: () -> Unit = { flushCalls++ }

        val msg = clientContext.read(readFn, writeFn, flushFn)
        assertTrue(msg is Message.Ping)

        // Read again, auto-pong was written & flushed
        val next = clientContext.read(readFn, writeFn, flushFn)
        assertEquals(null, next)
        assertTrue(writeCalls > 0)
        assertTrue(flushCalls > 0)
    }
}
