// port-lint: tests tungstenite/src/extensions/headers/sec_websocket_extensions.rs
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

    @Test
    fun parseSeparateHeaders() {
        val rawHeaders = listOf("foo", "bar; baz=2")
        val combined = rawHeaders.joinToString(", ")
        val extensions = SecWebsocketExtensions.parse(combined)

        assertEquals(
            SecWebsocketExtensions(
                listOf(
                    WebsocketProtocolExtension("foo", emptyList()),
                    WebsocketProtocolExtension(
                        "bar",
                        listOf(WebsocketExtensionParam("baz", "2")),
                    ),
                ),
            ),
            extensions,
        )
    }

    @Test
    fun roundTripComplex() {
        val rawHeaders =
            listOf(
                "deflate-stream",
                "mux; max-channels=4; flow-control, deflate-stream",
                "private-extension",
            )
        val combined = rawHeaders.joinToString(", ")
        val extensions = SecWebsocketExtensions.parse(combined)

        assertEquals(
            "deflate-stream, mux; max-channels=4; flow-control, deflate-stream, private-extension",
            extensions.headerValue(),
        )
    }

    @Test
    fun writeToExactEncodedLen() {
        val cases: List<WriteTo> =
            listOf(
                CommaDelimited(
                    listOf(
                        WebsocketProtocolExtension.parse("extension-name"),
                        WebsocketProtocolExtension.parse("with-params; a=5; b=8"),
                    ),
                ),
                CommaDelimited<WebsocketProtocolExtension>(emptyList()),
                CommaDelimited(
                    listOf(
                        WebsocketProtocolExtension.parse("duplicate-name"),
                        WebsocketProtocolExtension.parse("duplicate-name"),
                        WebsocketProtocolExtension.parse("duplicate-name"),
                    ),
                ),
                WebsocketProtocolExtension.new(
                    "name",
                    listOf(
                        WebsocketExtensionParam.parse("foo=123"),
                        WebsocketExtensionParam.parse("bar"),
                        WebsocketExtensionParam.parse("baz=four"),
                    ),
                ),
            )

        for (case in cases) {
            val bytes = mutableListOf<Byte>()
            val expectedLen = case.encodedLen()
            case.writeWith { slice ->
                for (b in slice) {
                    bytes.add(b)
                }
            }
            assertEquals(expectedLen, bytes.size)
        }
    }

    private fun testDecode(values: List<String>): SecWebsocketExtensions? {
        val combined = values.joinToString(", ")
        return try {
            SecWebsocketExtensions.parse(combined)
        } catch (_: Exception) {
            null
        }
    }

    private fun testEncode(header: SecWebsocketExtensions): Map<String, String> {
        return mapOf("Sec-WebSocket-Extensions" to header.headerValue())
    }

    @Test
    fun testDecode() {
        val decoded = testDecode(listOf("foo", "bar; baz=2"))
        assertEquals(2, decoded?.len())
    }

    @Test
    fun testEncode() {
        val ext = SecWebsocketExtensions(listOf(WebsocketProtocolExtension("foo", emptyList())))
        val encoded = testEncode(ext)
        assertEquals("foo", encoded["Sec-WebSocket-Extensions"])
    }
}
