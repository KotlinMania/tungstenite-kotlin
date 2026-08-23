// port-lint: tests protocol/mod.rs
package io.github.kotlinmania.tungstenite.protocol

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class WebSocketTest {
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
        val ctx = WebSocketContext.new(Role.Client)
        assertEquals(Role.Client, ctx.role)
        assertTrue(ctx.canRead())
        assertTrue(ctx.canWrite())
    }
}
