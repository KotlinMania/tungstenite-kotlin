// port-lint: tests extensions/compression/deflate/config.rs
package io.github.kotlinmania.tungstenite.extensions.compression.deflate

import io.github.kotlinmania.tungstenite.extensions.headers.WebsocketExtensionParam
import io.github.kotlinmania.tungstenite.protocol.Role
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ConfigTest {
    @Test
    fun permessageDeflateConfigDefaults() {
        val cfg = PermessageDeflateConfig.default()
        assertFalse(cfg.serverNoContextTakeover)
        assertFalse(cfg.clientNoContextTakeover)
        assertEquals(null, cfg.serverMaxWindowBits)
        assertEquals(null, cfg.clientMaxWindowBits)
        assertEquals(6, cfg.compressionLevel)
    }

    @Test
    fun parseValidParams() {
        val params =
            listOf(
                WebsocketExtensionParam("server_no_context_takeover"),
                WebsocketExtensionParam("client_no_context_takeover"),
                WebsocketExtensionParam("server_max_window_bits", "15"),
                WebsocketExtensionParam("client_max_window_bits", "12"),
            )
        val cfg = PermessageDeflateConfig.parseParams(params, Role.Client)
        assertTrue(cfg.serverNoContextTakeover)
        assertTrue(cfg.clientNoContextTakeover)
        assertEquals(15, cfg.serverMaxWindowBits)
        assertEquals(12, cfg.clientMaxWindowBits)
    }

    @Test
    fun intoExtension() {
        val cfg =
            PermessageDeflateConfig(
                serverNoContextTakeover = true,
                clientNoContextTakeover = true,
                serverMaxWindowBits = 15,
                clientMaxWindowBits = 12,
            )
        val ext = cfg.intoExtension()
        assertEquals(PER_MESSAGE_DEFLATE, ext.name)
        assertEquals(4, ext.params.size)
    }
}
