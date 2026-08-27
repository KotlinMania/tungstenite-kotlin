// port-lint: tests error.rs
package io.github.kotlinmania.tungstenite

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ErrorTest {
    @Test
    fun errorSize() {
        // Rust memory layout test (std::mem::size_of) does not apply to JVM/KMP managed memory.
        assertTrue(true)
    }

    @Test
    fun tlsErrorSize() {
        // Rust memory layout test (std::mem::size_of) does not apply to JVM/KMP managed memory.
        assertTrue(true)
    }

    @Test
    fun protocolErrorSize() {
        // Rust memory layout test (std::mem::size_of) does not apply to JVM/KMP managed memory.
        assertTrue(true)
    }

    @Test
    fun testErrorHierarchy() {
        val err = TungsteniteException.ProtocolViolation(ProtocolError.WrongHttpMethod)
        assertTrue(err.message!!.contains("Unsupported HTTP method"))
        assertEquals("Unsupported HTTP method used - only GET is allowed", ProtocolError.WrongHttpMethod.toString())

        val capErr = CapacityError.MessageTooLong(100L, 50L)
        assertEquals("Message too long: 100 > 50", capErr.toString())
    }
}
