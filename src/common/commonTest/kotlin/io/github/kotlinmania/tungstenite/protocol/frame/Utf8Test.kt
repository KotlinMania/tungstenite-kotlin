// port-lint: tests protocol/frame/utf8.rs
package io.github.kotlinmania.tungstenite.protocol.frame

import io.github.kotlinmania.bytes.Bytes
import kotlin.test.Test
import kotlin.test.assertEquals

class Utf8Test {
    @Test
    fun hashConsistency() {
        val bytes = Utf8Bytes.fromStatic("hash_consistency")
        assertEquals("hash_consistency".hashCode(), bytes.hashCode())
        assertEquals("hash_consistency", bytes.asStr())
        assertEquals("hash_consistency", bytes.toString())
    }

    @Test
    fun conversionsAndEquality() {
        val payload = Utf8Bytes.fromStatic("foo123")
        assertEquals("foo123", payload.asStr())
        assertEquals(6, payload.length)
        assertEquals('f', payload[0])
        assertEquals('3', payload[5])

        val fromBytes = Utf8Bytes.from(Bytes.from("hello".encodeToByteArray()))
        assertEquals("hello", fromBytes.asStr())
        assertEquals(Utf8Bytes.fromStatic("hello").asStr(), fromBytes.asStr())
    }
}
