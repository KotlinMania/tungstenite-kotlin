// port-lint: tests error.rs
package io.github.kotlinmania.tungstenite

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ErrorTest {
    // Unported: error_size, tls_error_size, protocol_error_size test Rust memory layout (std::mem::size_of) which has no Kotlin equivalent.

    @Test
    fun testErrorHierarchy() {
        val err = TungsteniteException.ProtocolViolation(ProtocolError.WrongHttpMethod)
        assertTrue(err.message!!.contains("Unsupported HTTP method"))
        assertEquals("Unsupported HTTP method used - only GET is allowed", ProtocolError.WrongHttpMethod.toString())

        val capErr = CapacityError.MessageTooLong(100L, 50L)
        assertEquals("Message too long: 100 > 50", capErr.toString())
    }
}
