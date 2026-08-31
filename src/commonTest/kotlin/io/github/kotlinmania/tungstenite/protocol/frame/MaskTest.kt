// port-lint: tests tungstenite/src/protocol/frame/mask.rs
package io.github.kotlinmania.tungstenite.protocol.frame

import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals

class MaskTest {
    @Test
    fun testApplyMask() {
        val mask = byteArrayOf(0x6d.toByte(), 0xb6.toByte(), 0xb2.toByte(), 0x80.toByte())
        val unmasked =
            byteArrayOf(
                0xf3.toByte(),
                0x00.toByte(),
                0x01.toByte(),
                0x02.toByte(),
                0x03.toByte(),
                0x80.toByte(),
                0x81.toByte(),
                0x82.toByte(),
                0xff.toByte(),
                0xfe.toByte(),
                0x00.toByte(),
                0x17.toByte(),
                0x74.toByte(),
                0xf9.toByte(),
                0x12.toByte(),
                0x03.toByte(),
            )

        for (dataLen in 0..unmasked.size) {
            val slice = unmasked.copyOfRange(0, dataLen)
            for (off in 0..3) {
                if (slice.size < off) {
                    continue
                }
                val masked = slice.copyOf()
                applyMaskFallback(masked, mask, off, masked.size - off)

                val maskedFast = slice.copyOf()
                applyMaskFast32(maskedFast, mask, off, maskedFast.size - off)

                assertContentEquals(masked, maskedFast)
            }
        }
    }

    @Test
    fun testGenerateMask() {
        val mask = generateMask()
        assertEquals(4, mask.size)
    }
}
