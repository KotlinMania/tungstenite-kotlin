// port-lint: tests proxy.rs
package io.github.kotlinmania.tungstenite

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ProxyTest {
    @Test
    fun splitHostPortBasic() {
        val (h1, p1) = splitHostPort("example.com:8080")
        assertEquals("example.com", h1)
        assertEquals(8080, p1)

        val (h2, p2) = splitHostPort("example.com")
        assertEquals("example.com", h2)
        assertEquals(null, p2)
    }

    @Test
    fun splitHostPortIpv6() {
        val (h1, p1) = splitHostPort("[::1]:8080")
        assertEquals("[::1]", h1)
        assertEquals(8080, p1)

        val (h2, p2) = splitHostPort("[::1]")
        assertEquals("[::1]", h2)
        assertEquals(null, p2)
    }

    @Test
    fun noProxyStar() {
        assertTrue(shouldBypassProxy("example.com", 80, "*"))
        assertTrue(shouldBypassProxy("localhost", 8080, "*"))
    }

    @Test
    fun noProxySuffix() {
        assertTrue(shouldBypassProxy("api.internal.net", 443, "internal.net"))
        assertTrue(shouldBypassProxy("internal.net", 443, "internal.net"))
        assertTrue(shouldBypassProxy("api.internal.net", 443, ".internal.net"))
        assertFalse(shouldBypassProxy("notinternal.net", 443, "internal.net"))
    }

    @Test
    fun httpConnectRequestWithAuth() {
        val auth = ProxyAuth("user", "pass")
        val req = buildHttpConnectRequest("example.com:443", auth).decodeToString()
        assertTrue(req.startsWith("CONNECT example.com:443 HTTP/1.1\r\n"))
        assertTrue(req.contains("Host: example.com:443\r\n"))
        assertTrue(req.contains("Proxy-Authorization: Basic dXNlcjpwYXNz\r\n"))
    }

    @Test
    fun httpConnectParseOk() {
        val resp = "HTTP/1.1 200 Connection Established\r\n\r\n".encodeToByteArray()
        val code = parseHttpConnectResponse(resp)
        assertEquals(200, code)
    }

    @Test
    fun proxyConfigParse() {
        val cfg = ProxyConfig.parse("http://user:pass@proxy.example.com:8080")
        assertEquals(ProxyScheme.Http, cfg.scheme)
        assertEquals("proxy.example.com", cfg.host)
        assertEquals(8080, cfg.port)
        assertEquals("user", cfg.auth?.username)
        assertEquals("pass", cfg.auth?.password)
    }
}
