// port-lint: tests tungstenite/src/handshake/client.rs
package io.github.kotlinmania.tungstenite.handshake

import io.github.kotlinmania.tungstenite.TungsteniteException
import io.github.kotlinmania.tungstenite.extensions.ExtensionsConfig
import io.github.kotlinmania.tungstenite.extensions.compression.deflate.DeflateConfig
import io.github.kotlinmania.tungstenite.intoClientRequest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class ClientTest {
    @Test
    fun randomKeys() {
        val k1 = generateKey()
        val k2 = generateKey()
        assertNotEquals(k1, k2)
        assertEquals(k1.length, k2.length)
        assertEquals(24, k1.length)
        assertEquals(24, k2.length)
        assertTrue(k1.endsWith("=="))
        assertTrue(k2.endsWith("=="))
        assertTrue(!k1.substring(0, 22).contains('='))
        assertTrue(!k2.substring(0, 22).contains('='))
    }

    private fun constructExpected(
        host: String,
        key: String,
    ): ByteArray {
        val expected =
            "GET /getCaseCount HTTP/1.1\r\n" +
                "Host: $host\r\n" +
                "Connection: Upgrade\r\n" +
                "Upgrade: websocket\r\n" +
                "Sec-WebSocket-Version: 13\r\n" +
                "Sec-WebSocket-Key: $key\r\n\r\n"
        return expected.encodeToByteArray()
    }

    @Test
    fun requestFormatting() {
        val request = "ws://localhost/getCaseCount".intoClientRequest()
        val (bytes, key) = generateRequest(request, null)
        val correct = constructExpected("localhost", key)
        assertEquals(correct.decodeToString(), bytes.decodeToString())
    }

    @Test
    fun requestFormattingWithHost() {
        val request = "wss://localhost:9001/getCaseCount".intoClientRequest()
        val (bytes, key) = generateRequest(request, null)
        val correct = constructExpected("localhost:9001", key)
        assertEquals(correct.decodeToString(), bytes.decodeToString())
    }

    @Test
    fun requestFormattingWithAt() {
        val request = "wss://user:pass@localhost:9001/getCaseCount".intoClientRequest()
        val (bytes, key) = generateRequest(request, null)
        val correct = constructExpected("localhost:9001", key)
        assertEquals(correct.decodeToString(), bytes.decodeToString())
    }

    @Test
    fun requestWithCompression() {
        val request = "ws://localhost/getCaseCount".intoClientRequest()
        val (bytes, key) =
            generateRequest(
                request,
                ExtensionsConfig(permessageDeflate = DeflateConfig.default()),
            )
        val correct =
            "GET /getCaseCount HTTP/1.1\r\n" +
                "Host: localhost\r\n" +
                "Connection: Upgrade\r\n" +
                "Upgrade: websocket\r\n" +
                "Sec-WebSocket-Version: 13\r\n" +
                "Sec-WebSocket-Key: $key\r\n" +
                "sec-websocket-extensions: permessage-deflate; client_max_window_bits\r\n\r\n"
        assertEquals(correct, bytes.decodeToString())
    }

    @Test
    fun responseParsing() {
        val data = "HTTP/1.1 200 OK\r\nContent-Type: text/html\r\n\r\n".encodeToByteArray()
        val parsed = Response.tryParse(data).getOrThrow()
        assertNotNull(parsed)
        val (_, resp) = parsed
        assertEquals(200, resp.statusCode)
        assertEquals("text/html", resp.headers["Content-Type"])
    }

    @Test
    fun invalidCustomRequest() {
        val request = Request(uri = "/", method = "GET", headers = emptyMap())
        assertFailsWith<TungsteniteException> {
            generateRequest(request, null)
        }
    }
}
