// port-lint: tests tests/no_send_after_close.rs
package io.github.kotlinmania.tungstenite

import io.github.kotlinmania.tungstenite.protocol.Message
import io.github.kotlinmania.tungstenite.protocol.Role
import io.github.kotlinmania.tungstenite.protocol.WebSocketContext
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

public class NoSendAfterCloseTest {
    @Test
    public fun testNoSendAfterClose() {
        val serverContext = WebSocketContext.new(Role.Server)
        val writeFn: (ByteArray, Int, Int) -> Int = { _, _, len -> len }
        val flushFn: () -> Unit = {}

        // Server initiates close
        serverContext.close(writeFn, flushFn, null)

        // Attempting to send a message after closing should fail with SendAfterClosing
        val ex =
            assertFailsWith<TungsteniteException.ProtocolViolation> {
                serverContext.write(writeFn, Message.text("Hello WebSocket"))
            }
        assertEquals(ProtocolError.SendAfterClosing, ex.error)
    }
}
