// port-lint: source src/protocol/frame/coding.rs
package io.github.kotlinmania.tungstenite.protocol.frame

import kotlin.test.Test
import kotlin.test.assertEquals

class CodingTest {
    @Test
    fun opcodeFromUByte() {
        val byte: UByte = 2u
        assertEquals(OpCode.Data(Data.Binary), OpCode.fromUByte(byte))
    }

    @Test
    fun opcodeIntoUByte() {
        val text: OpCode = OpCode.Data(Data.Text)
        val byte: UByte = text.toUByte()
        assertEquals(1u.toUByte(), byte)
    }

    @Test
    fun closecodeFromUShort() {
        val byte: UShort = 1008u
        assertEquals(CloseCode.Policy, CloseCode.fromUShort(byte))
    }

    @Test
    fun closecodeIntoUShort() {
        val text: CloseCode = CloseCode.Away
        val byte: UShort = text.toUShort()
        assertEquals(1001u.toUShort(), byte)
        assertEquals(1001u.toUShort(), text.toUShort())
    }
}
