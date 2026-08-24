// port-lint: tests extensions/mod.rs
package io.github.kotlinmania.tungstenite.extensions

import io.github.kotlinmania.tungstenite.extensions.compression.deflate.DeflateConfig
import io.github.kotlinmania.tungstenite.extensions.compression.deflate.EXTENSION_NAME
import io.github.kotlinmania.tungstenite.extensions.headers.SecWebsocketExtensions
import io.github.kotlinmania.tungstenite.extensions.headers.WebsocketExtensionParam
import io.github.kotlinmania.tungstenite.extensions.headers.WebsocketProtocolExtension
import io.github.kotlinmania.tungstenite.protocol.Role
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class ModTest {
    @Test
    fun acceptOffersIgnoresUnknownExtensions() {
        val config = ExtensionsConfig.default()
        val extensions =
            SecWebsocketExtensions(
                listOf(
                    WebsocketProtocolExtension("unknown-1"),
                    WebsocketProtocolExtension("other-unknown"),
                ),
            )
        val (ext, resp) = config.acceptOffers(extensions)
        assertNull(ext.perMessageCompression)
        assertNull(resp)
    }

    @Test
    fun acceptOffersWithDeflateEnabled() {
        val config = ExtensionsConfig(permessageDeflate = DeflateConfig.new())
        val extensions =
            SecWebsocketExtensions(
                listOf(
                    WebsocketProtocolExtension(EXTENSION_NAME),
                    WebsocketProtocolExtension("some-other-extension"),
                ),
            )
        val (ext, resp) = config.acceptOffers(extensions)
        assertNotNull(ext.perMessageCompression)
        assertNotNull(resp)
        assertEquals(1, resp.extensions.size)
        assertEquals(EXTENSION_NAME, resp.extensions[0].name)
    }

    @Test
    fun verifyAgreedOnDeflateMultipleTimes() {
        val config = ExtensionsConfig(permessageDeflate = DeflateConfig.new())
        val extensions =
            SecWebsocketExtensions(
                listOf(
                    WebsocketProtocolExtension(EXTENSION_NAME),
                    WebsocketProtocolExtension(
                        EXTENSION_NAME,
                        listOf(WebsocketExtensionParam("client_no_context_takeover")),
                    ),
                ),
            )
        assertFailsWith<ExtensionsError.ExtensionConflict> {
            config.verifyAgreedOn(extensions)
        }
    }

    @Test
    fun verifyAgreedOnDeflateThenGarbage() {
        val config = ExtensionsConfig(permessageDeflate = DeflateConfig.new())
        val extensions =
            SecWebsocketExtensions(
                listOf(
                    WebsocketProtocolExtension(EXTENSION_NAME),
                    WebsocketProtocolExtension("unrecognized"),
                ),
            )
        assertFailsWith<ExtensionsError.InvalidExtension> {
            config.verifyAgreedOn(extensions)
        }
    }

    @Test
    fun intoUnnegotiatedContext() {
        val config = ExtensionsConfig(permessageDeflate = DeflateConfig.new())
        val ext = config.intoUnnegotiatedContext(Role.Client)
        assertNotNull(ext.perMessageCompression)
        assertNotNull(ext.perMessageCompressor())
        assertNotNull(ext.perMessageDecompressor())
    }
}
