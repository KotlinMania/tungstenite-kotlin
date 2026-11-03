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

    private class MockStream(
        val readData: ByteArray,
        val writeData: MutableList<Byte> = mutableListOf(),
    ) {
        var pos: Int = 0

        fun read(buf: ByteArray): Int {
            if (pos >= readData.size) return 0
            val remaining = readData.size - pos
            val len = minOf(remaining, buf.size)
            readData.copyInto(buf, 0, pos, pos + len)
            pos += len
            return len
        }

        fun write(buf: ByteArray) {
            for (b in buf) {
                writeData.add(b)
            }
        }
    }

    @Test
    fun httpConnectHandshakeOk() {
        val response = "HTTP/1.1 200 OK\r\n\r\n".encodeToByteArray()
        val stream = MockStream(response)
        httpConnect(stream::read, stream::write, "example.com", 443, null)

        val expected = buildHttpConnectRequest("example.com:443", null)
        assertEquals(expected.toList(), stream.writeData)
    }

    @Test
    fun socks5HandshakeNoAuth() {
        val response =
            byteArrayOf(
                0x05,
                0x00, // method select
                0x05,
                0x00,
                0x00,
                0x01,
                0,
                0,
                0,
                0,
                0,
                0, // connect reply
            )
        val stream = MockStream(response)
        socks5Handshake(stream::read, stream::write, "example.com", 443, null)

        val expected =
            mutableListOf<Byte>(
                0x05,
                0x01,
                0x00, // greeting
                0x05,
                0x01,
                0x00,
                0x03,
                11, // connect header + domain length
            )
        for (b in "example.com".encodeToByteArray()) {
            expected.add(b)
        }
        expected.add(((443 ushr 8) and 0xFF).toByte())
        expected.add((443 and 0xFF).toByte())
        assertEquals(expected, stream.writeData)
    }

    @Test
    fun socks5HandshakeWithAuth() {
        val response =
            byteArrayOf(
                0x05,
                0x02, // method select (user/pass)
                0x01,
                0x00, // auth success
                0x05,
                0x00,
                0x00,
                0x01,
                0,
                0,
                0,
                0,
                0,
                0, // connect reply
            )
        val auth = ProxyAuth("user", "pass")
        val stream = MockStream(response)
        socks5Handshake(stream::read, stream::write, "example.com", 443, auth)

        val expected =
            mutableListOf<Byte>(
                0x05,
                0x02,
                0x00,
                0x02, // greeting with auth methods
                0x01,
                4, // userpass auth subnegotiation + username len
            )
        for (b in "user".encodeToByteArray()) {
            expected.add(b)
        }
        expected.add(4) // password len
        for (b in "pass".encodeToByteArray()) {
            expected.add(b)
        }
        expected.addAll(listOf(0x05.toByte(), 0x01.toByte(), 0x00.toByte(), 0x03.toByte(), 11.toByte()))
        for (b in "example.com".encodeToByteArray()) {
            expected.add(b)
        }
        expected.add(((443 ushr 8) and 0xFF).toByte())
        expected.add((443 and 0xFF).toByte())
        assertEquals(expected, stream.writeData)
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
