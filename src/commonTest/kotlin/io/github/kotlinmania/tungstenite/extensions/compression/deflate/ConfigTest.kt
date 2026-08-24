// port-lint: tests extensions/compression/deflate/config.rs
package io.github.kotlinmania.tungstenite.extensions.compression.deflate

import io.github.kotlinmania.tungstenite.extensions.headers.WebsocketExtensionParam
import io.github.kotlinmania.tungstenite.protocol.Role
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
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
        val cfg = PermessageDeflateConfig.parseParams(params)
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

    @Test
    fun deflateRejectUnknownParameters() {
        assertFailsWith<ParameterError.UnknownParameter> {
            PermessageDeflateConfig.parseParams(listOf(WebsocketExtensionParam("unknown", null)))
        }
        assertFailsWith<ParameterError.UnknownParameter> {
            PermessageDeflateConfig.parseParams(
                listOf(
                    WebsocketExtensionParam("client_max_window_bits", "13"),
                    WebsocketExtensionParam("after-valid", null),
                ),
            )
        }
    }

    @Test
    fun deflateRejectDuplicateParameters() {
        assertFailsWith<ParameterError.DuplicateParameter> {
            PermessageDeflateConfig.parseParams(
                listOf(
                    WebsocketExtensionParam("client_max_window_bits", "12"),
                    WebsocketExtensionParam("server_no_context_takeover", null),
                    WebsocketExtensionParam("client_max_window_bits", "12"),
                ),
            )
        }
    }

    @Test
    fun deflateConfigMinimalClientOffer() {
        val clientConfig = DeflateConfig.new()
        val offer = clientConfig.asOffer()
        assertEquals(true, offer.clientMaxWindowBitsPresent)
        assertEquals(null, offer.clientMaxWindowBits)
    }

    @Test
    fun deflateServerRespectsOfferServerNoContextTakeover() {
        val serverCfg = DeflateConfig.new()
        val clientOffer = PermessageDeflateConfig(serverNoContextTakeover = true)
        val result = serverCfg.acceptOffer(clientOffer)
        val (connConfig, offerResponse) = requireNotNull(result)
        assertTrue(connConfig.serverNoContextTakeover)
        assertTrue(offerResponse.serverNoContextTakeover)
    }

    @Test
    fun rejectsUnsupportedClientMaxWindowBitsOffer() {
        val serverConfig = DeflateConfig.new()
        val offer =
            PermessageDeflateConfig(
                clientMaxWindowBitsPresent = true,
                clientMaxWindowBits = 12,
            )
        val result = serverConfig.acceptOffer(offer)
        val (connConfig, offerResponse) = requireNotNull(result)
        assertEquals(12, connConfig.clientMaxWindowBits)
        assertEquals(12, offerResponse.clientMaxWindowBits)
    }

    @Test
    fun interopMatrix() {
        val modifiers: List<(DeflateConfig) -> DeflateConfig> =
            listOf(
                { cfg -> cfg.setNoContextTakeover(Role.Client, true) },
                { cfg -> cfg.setNoContextTakeover(Role.Server, true) },
                { cfg -> cfg.setMaxWindowBits(Role.Client, 12) },
                { cfg -> cfg.setMaxWindowBits(Role.Server, 10) },
            )

        fun makeConfig(selector: Int): DeflateConfig {
            var cfg = DeflateConfig.new()
            for (i in modifiers.indices) {
                if ((selector and (1 shl i)) != 0) {
                    cfg = modifiers[i](cfg)
                }
            }
            return cfg
        }

        val total = 1 shl modifiers.size
        for (clientSel in 0 until total) {
            val clientConfig = makeConfig(clientSel)
            for (serverSel in 0 until total) {
                val serverConfig = makeConfig(serverSel)
                val offer = clientConfig.asOffer()
                val result = serverConfig.acceptOffer(offer)
                assertNotNull(result, "client: $clientConfig, server: $serverConfig")
                val (_, response) = result
                val accepted = clientConfig.acceptResponse(response)
                assertNotNull(accepted)
            }
        }
    }

    @Test
    fun deflateContextCompressionCycle() {
        val client = DeflateContext.new(Role.Client, DeflateConfig.new())
        val server = DeflateContext.new(Role.Server, DeflateConfig.new())

        val payload = "Hello, WebSocket Permessage Deflate!".encodeToByteArray()
        val compressed = client.compress(payload)
        val decompressed = server.decompress(compressed, isFinal = true)
        assertEquals(payload.decodeToString(), decompressed.decodeToString())
    }
}
