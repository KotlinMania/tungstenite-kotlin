// port-lint: tests handshake/client.rs
package io.github.kotlinmania.tungstenite.handshake

import io.github.kotlinmania.tungstenite.protocol.WebSocketConfig
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class HandshakeTest {
    @Test
    fun testDeriveAcceptKey() {
        val key = "dGhlIHNhbXBsZSBub25jZQ=="
        val accept = deriveAcceptKey(key)
        assertEquals("s3pPLMBiTxaQ9kYGzzhZRbK+xOo=", accept)
    }

    @Test
    fun testClientHandshakeBuildRequest() {
        val req = Request.get("ws://example.com/socket")
        val handshake =
            ClientHandshake(
                stream = Any(),
                request = req,
                config = WebSocketConfig.default(),
                key = "dGhlIHNhbXBsZSBub25jZQ==",
            )
        val bytes = handshake.buildRequestBytes()
        val text = bytes.decodeToString()
        assertTrue(text.startsWith("GET /socket HTTP/1.1\r\n"))
        assertTrue(text.contains("Host: example.com\r\n"))
        assertTrue(text.contains("Upgrade: websocket\r\n"))
        assertTrue(text.contains("Connection: Upgrade\r\n"))
        assertTrue(text.contains("Sec-WebSocket-Key: dGhlIHNhbXBsZSBub25jZQ==\r\n"))
        assertTrue(text.contains("Sec-WebSocket-Version: 13\r\n"))
    }

    @Test
    fun testServerHandshakeCreateResponse() {
        val req =
            Request(
                uri = "ws://example.com/socket",
                method = "GET",
                version = "HTTP/1.1",
                headers =
                    mapOf(
                        "Host" to "example.com",
                        "Upgrade" to "websocket",
                        "Connection" to "Upgrade",
                        "Sec-WebSocket-Key" to "dGhlIHNhbXBsZSBub25jZQ==",
                        "Sec-WebSocket-Version" to "13",
                    ),
            )
        val resp = createResponse(req)
        assertEquals(101, resp.statusCode)
        assertEquals("s3pPLMBiTxaQ9kYGzzhZRbK+xOo=", resp.headers["Sec-WebSocket-Accept"])
        assertEquals("websocket", resp.headers["Upgrade"])
        assertEquals("Upgrade", resp.headers["Connection"])
    }
}
