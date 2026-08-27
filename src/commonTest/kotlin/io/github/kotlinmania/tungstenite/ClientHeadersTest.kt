// port-lint: tests tests/client_headers.rs
package io.github.kotlinmania.tungstenite

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

public class ClientHeadersTest {
    @Test
    public fun testHeaders() {
        val uri = "ws://127.0.0.1:3013/socket"
        val token = "my_jwt_token"
        val fullToken = "Bearer $token"
        val subProtocol = "my_sub_protocol"

        var req = uri.intoClientRequest()
        req = req.header("Authorization", fullToken)
        req = req.header("Sec-WebSocket-Protocol", subProtocol)

        assertEquals("GET", req.method)
        assertEquals("127.0.0.1:3013", req.headers["Host"])
        assertEquals("websocket", req.headers["Upgrade"])
        assertEquals("Upgrade", req.headers["Connection"])
        assertEquals("13", req.headers["Sec-WebSocket-Version"])
        assertEquals(fullToken, req.headers["Authorization"])
        assertEquals(subProtocol, req.headers["Sec-WebSocket-Protocol"])
        assertNotNull(req.headers["Sec-WebSocket-Key"])
    }
}
