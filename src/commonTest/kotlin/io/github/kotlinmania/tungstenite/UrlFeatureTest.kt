// port-lint: tests tungstenite/tests/url_feature.rs
package io.github.kotlinmania.tungstenite

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

public class UrlFeatureTest {
    @Test
    public fun testWithUrl() {
        val url = "ws://127.0.0.1:3013"
        val request = url.intoClientRequest()
        assertEquals("GET", request.method)
        assertEquals("127.0.0.1:3013", request.headers["Host"])
        assertEquals("websocket", request.headers["Upgrade"])
        assertEquals("Upgrade", request.headers["Connection"])
        assertEquals("13", request.headers["Sec-WebSocket-Version"])
        assertNotNull(request.headers["Sec-WebSocket-Key"])
    }
}
