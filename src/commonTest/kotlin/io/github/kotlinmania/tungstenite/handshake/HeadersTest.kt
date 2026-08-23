// port-lint: tests handshake/headers.rs
package io.github.kotlinmania.tungstenite.handshake

import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class HeadersTest {
    @Test
    fun headers() {
        val data =
            (
                "Host: foo.com\r\n" +
                    "Connection: Upgrade\r\n" +
                    "Upgrade: websocket\r\n" +
                    "\r\n"
            ).encodeToByteArray()

        val parsed = HeaderMap.tryParse(data).getOrThrow()
        assertNotNull(parsed)
        val (_, hdr) = parsed
        assertContentEquals("foo.com".encodeToByteArray(), hdr.get("Host"))
        assertContentEquals("websocket".encodeToByteArray(), hdr.get("Upgrade"))
        assertContentEquals("Upgrade".encodeToByteArray(), hdr.get("Connection"))
    }

    @Test
    fun headersIter() {
        val data =
            (
                "Host: foo.com\r\n" +
                    "Sec-WebSocket-Extensions: permessage-deflate\r\n" +
                    "Connection: Upgrade\r\n" +
                    "Sec-WebSocket-ExtenSIONS: permessage-unknown\r\n" +
                    "Upgrade: websocket\r\n" +
                    "\r\n"
            ).encodeToByteArray()

        val parsed = HeaderMap.tryParse(data).getOrThrow()
        assertNotNull(parsed)
        val (_, hdr) = parsed
        val all = hdr.getAll("Sec-WebSocket-Extensions")
        assertEquals(2, all.size)
        assertContentEquals("permessage-deflate".encodeToByteArray(), all[0])
        assertContentEquals("permessage-unknown".encodeToByteArray(), all[1])
    }

    @Test
    fun headersIncomplete() {
        val data =
            (
                "Host: foo.com\r\n" +
                    "Connection: Upgrade\r\n" +
                    "Upgrade: websocket\r\n"
            ).encodeToByteArray()

        val hdr = HeaderMap.tryParse(data).getOrThrow()
        assertNull(hdr)
    }
}
