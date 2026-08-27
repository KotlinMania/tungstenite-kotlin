// port-lint: tests tungstenite/src/handshake/mod.rs
package io.github.kotlinmania.tungstenite.handshake

import kotlin.test.Test
import kotlin.test.assertEquals

class HandshakeTest {
    @Test
    fun keyConversion() {
        // example from RFC 6455
        val key = "dGhlIHNhbXBsZSBub25jZQ=="
        val accept = deriveAcceptKey(key)
        assertEquals("s3pPLMBiTxaQ9kYGzzhZRbK+xOo=", accept)
    }
}
