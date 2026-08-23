// port-lint: tests extensions/headers/sec_websocket_extensions.rs
package io.github.kotlinmania.tungstenite.extensions.headers

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SecWebsocketExtensionsTest {
    @Test
    fun parseSingleExtensionNoParams() {
        val ext = SecWebsocketExtensions.parse("permessage-deflate")
        assertEquals(1, ext.len())
        assertEquals("permessage-deflate", ext.extensions[0].name)
        assertTrue(ext.extensions[0].params.isEmpty())
    }

    @Test
    fun parseSingleExtensionWithParams() {
        val ext = SecWebsocketExtensions.parse("permessage-deflate; client_max_window_bits=15; server_no_context_takeover")
        assertEquals(1, ext.len())
        val first = ext.extensions[0]
        assertEquals("permessage-deflate", first.name)
        assertEquals(2, first.params.size)
        assertEquals("client_max_window_bits", first.params[0].name)
        assertEquals("15", first.params[0].value)
        assertEquals("server_no_context_takeover", first.params[1].name)
        assertEquals(null, first.params[1].value)
    }

    @Test
    fun parseMultipleExtensions() {
        val ext = SecWebsocketExtensions.parse("deflate-stream, permessage-deflate; server_no_context_takeover")
        assertEquals(2, ext.len())
        assertEquals("deflate-stream", ext.extensions[0].name)
        assertEquals("permessage-deflate", ext.extensions[1].name)
    }

    @Test
    fun roundtripExtensionHeader() {
        val raw = "permessage-deflate; client_max_window_bits=15; server_no_context_takeover"
        val parsed = SecWebsocketExtensions.parse(raw)
        val serialized = parsed.headerValue()
        assertEquals("permessage-deflate; client_max_window_bits=15; server_no_context_takeover", serialized)
    }
}
