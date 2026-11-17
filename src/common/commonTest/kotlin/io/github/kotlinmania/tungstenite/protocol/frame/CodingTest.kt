// port-lint: tests protocol/frame/coding.rs
package io.github.kotlinmania.tungstenite.protocol.frame

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class CodingTest {
    @Test
    fun opcodeFromU8() {
        val byte: UByte = 2u
        assertEquals(OpCode.Data(Data.Binary), OpCode.fromUByte(byte))
    }

    @Test
    fun opcodeIntoU8() {
        val text: OpCode = OpCode.Data(Data.Text)
        val byte: UByte = text.toUByte()
        assertEquals(1u.toUByte(), byte)
    }

    @Test
    fun closecodeFromU16() {
        val byte: UShort = 1008u
        assertEquals(CloseCode.Policy, CloseCode.fromUShort(byte))
    }

    @Test
    fun closecodeIntoU16() {
        val text: CloseCode = CloseCode.Away
        val byte: UShort = text.toUShort()
        assertEquals(1001u.toUShort(), byte)
        assertEquals(1001u.toUShort(), text.toUShort())
    }

    @Test
    fun reservedOpcodeRangesRoundTrip() {
        for (byte in 3u..7u) {
            val code = OpCode.fromUByte(byte.toUByte())
            assertEquals(OpCode.Data(Data.Reserved(byte.toUByte())), code)
            assertEquals(byte.toUByte(), code.toUByte())
            assertEquals("RESERVED_DATA_$byte", code.toString())
        }

        for (byte in 11u..15u) {
            val code = OpCode.fromUByte(byte.toUByte())
            assertEquals(OpCode.Control(Control.Reserved(byte.toUByte())), code)
            assertEquals(byte.toUByte(), code.toUByte())
            assertEquals("RESERVED_CONTROL_$byte", code.toString())
        }
    }

    @Test
    fun closeCodeReservedRangesAndAllowedStatus() {
        val reserved = CloseCode.fromUShort(1016u)
        val iana = CloseCode.fromUShort(3000u)
        val library = CloseCode.fromUShort(4000u)
        val bad = CloseCode.fromUShort(999u)

        assertEquals(CloseCode.Reserved(1016u), reserved)
        assertEquals(1016u.toUShort(), reserved.toUShort())
        assertEquals(CloseCode.Iana(3000u), iana)
        assertEquals(3000u.toUShort(), iana.toUShort())
        assertEquals(CloseCode.Library(4000u), library)
        assertEquals(4000u.toUShort(), library.toUShort())
        assertEquals(CloseCode.Bad(999u), bad)
        assertEquals(999u.toUShort(), bad.toUShort())

        assertTrue(CloseCode.Normal.isAllowed())
        assertTrue(iana.isAllowed())
        assertTrue(library.isAllowed())
        assertFalse(CloseCode.Status.isAllowed())
        assertFalse(CloseCode.Abnormal.isAllowed())
        assertFalse(CloseCode.Tls.isAllowed())
        assertFalse(reserved.isAllowed())
        assertFalse(bad.isAllowed())
    }
}
