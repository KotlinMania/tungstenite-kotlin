// port-lint: tests handshake/server.rs
package io.github.kotlinmania.tungstenite.handshake

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class ServerTest {
    @Test
    fun requestParsing() {
        val data = "GET /script.ws HTTP/1.1\r\nHost: foo.com\r\n\r\n".encodeToByteArray()
        val parsed = Request.tryParse(data).getOrThrow()
        assertNotNull(parsed)
        val (_, req) = parsed
        assertEquals("/script.ws", req.uri)
        assertEquals("foo.com", req.headers["Host"])
    }

    @Test
    fun requestReplying() {
        val data =
            (
                "GET /script.ws HTTP/1.1\r\n" +
                    "Host: foo.com\r\n" +
                    "Connection: upgrade\r\n" +
                    "Upgrade: websocket\r\n" +
                    "Sec-WebSocket-Version: 13\r\n" +
                    "Sec-WebSocket-Key: dGhlIHNhbXBsZSBub25jZQ==\r\n\r\n"
            ).encodeToByteArray()
        val parsed = Request.tryParse(data).getOrThrow()
        assertNotNull(parsed)
        val (_, req) = parsed
        val response = createResponse(req)
        assertEquals("s3pPLMBiTxaQ9kYGzzhZRbK+xOo=", response.headers["Sec-WebSocket-Accept"])
    }
}
