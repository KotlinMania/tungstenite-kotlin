// port-lint: tests handshake/mod.rs
package io.github.kotlinmania.tungstenite.handshake

import kotlin.test.Test
import kotlin.test.assertEquals

class ModTest {
    @Test
    fun keyConversion() {
        // example from RFC 6455
        val requestKey = "dGhlIHNhbXBsZSBub25jZQ==".encodeToByteArray()
        val acceptKey = deriveAcceptKey(requestKey)
        assertEquals("s3pPLMBiTxaQ9kYGzzhZRbK+xOo=", acceptKey)
    }

    @Test
    fun keyConversionString() {
        val acceptKey = deriveAcceptKey("dGhlIHNhbXBsZSBub25jZQ==")
        assertEquals("s3pPLMBiTxaQ9kYGzzhZRbK+xOo=", acceptKey)
    }
}
