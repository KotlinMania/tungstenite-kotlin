// port-lint: tests util.rs
package io.github.kotlinmania.tungstenite

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class UtilTest {
    @Test
    fun testNoBlockSuccess() {
        val res: Result<String> = Result.success("hello")
        val noBlocked = res.noBlock()
        assertTrue(noBlocked.isSuccess)
        assertEquals("hello", noBlocked.getOrNull())
    }

    @Test
    fun testNoBlockWouldBlockIoError() {
        val wouldBlock = TungsteniteException.Io("Resource temporarily unavailable (WouldBlock)")
        val res: Result<String> = Result.failure(wouldBlock)
        val noBlocked = res.noBlock()
        assertTrue(noBlocked.isSuccess)
        assertNull(noBlocked.getOrNull())
    }

    @Test
    fun testNoBlockOtherTungsteniteError() {
        val err = TungsteniteException.Protocol(ProtocolError.HandshakeIncomplete)
        val res: Result<String> = Result.failure(err)
        val noBlocked = res.noBlock()
        assertTrue(noBlocked.isFailure)
        assertEquals(err, noBlocked.exceptionOrNull())
    }

    @Test
    fun testNoBlockCustomNonBlockingError() {
        val custom = object : RuntimeException("blocking"), NonBlockingError {
            override fun intoNonBlocking(): Throwable? = null
        }
        val res: Result<Int> = Result.failure(custom)
        val noBlocked = res.noBlock()
        assertTrue(noBlocked.isSuccess)
        assertNull(noBlocked.getOrNull())
    }
}
