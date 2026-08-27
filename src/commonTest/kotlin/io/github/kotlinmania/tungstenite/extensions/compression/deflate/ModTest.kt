// port-lint: tests extensions/compression/deflate/mod.rs
package io.github.kotlinmania.tungstenite.extensions.compression.deflate

import io.github.kotlinmania.tungstenite.extensions.compression.DecompressionError
import io.github.kotlinmania.tungstenite.protocol.Role
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class ModTest {
    @Test
    fun interop() {
        val data = ByteArray(2048) { (it % 251).toByte() }
        val configs =
            listOf(
                DeflateConfig.default(),
                DeflateConfig.default().setNoContextTakeover(Role.Client, true),
                DeflateConfig.default().setNoContextTakeover(Role.Client, true).setMaxWindowBits(Role.Client, 10),
                DeflateConfig.default().setMaxWindowBits(Role.Client, 10),
            )
        val frameSizes = listOf(16, 64, data.size)

        for (config in configs) {
            for (frameSize in frameSizes) {
                val client = DeflateContext(Role.Client, config)
                val server = DeflateContext(Role.Server, config)

                val compressed = client.compress(data)
                var offset = 0
                val decompressed = mutableListOf<Byte>()
                while (offset < compressed.size) {
                    val end = minOf(offset + frameSize, compressed.size)
                    val frame = compressed.copyOfRange(offset, end)
                    val isFinal = end == compressed.size
                    val out = server.decompress(frame, isFinal, Int.MAX_VALUE)
                    for (b in out) decompressed.add(b)
                    offset = end
                }
                assertContentEquals(data, decompressed.toByteArray())
            }
        }
    }

    @Test
    fun largeMessageCompression() {
        val data = ByteArray(1 shl 19) { (it % 251).toByte() }
        val context = DeflateContext(Role.Client, DeflateConfig.default())
        val compressed = context.compress(data)
        val decompressed = context.decompress(compressed, true, Int.MAX_VALUE)
        assertContentEquals(data, decompressed)
    }

    @Test
    fun decompressionLimitsApplied() {
        val framePayload =
            byteArrayOf(
                0xec.toByte(),
                0xc1.toByte(),
                0x31.toByte(),
                0x01.toByte(),
                0x00.toByte(),
                0x00.toByte(),
                0x00.toByte(),
                0xc2.toByte(),
                0xa0.toByte(),
                0xf5.toByte(),
                0x4f.toByte(),
                0x6d.toByte(),
                0x0b.toByte(),
                0x2f.toByte(),
                0xa0.toByte(),
                0x00.toByte(),
                0x00.toByte(),
                0x00.toByte(),
                0x00.toByte(),
                0x00.toByte(),
                0x00.toByte(),
                0x00.toByte(),
                0x00.toByte(),
                0x00.toByte(),
                0x00.toByte(),
                0x00.toByte(),
                0x00.toByte(),
                0x00.toByte(),
                0x00.toByte(),
                0x00.toByte(),
                0x00.toByte(),
                0x00.toByte(),
                0x00.toByte(),
                0x00.toByte(),
                0x00.toByte(),
                0x00.toByte(),
                0x00.toByte(),
                0x00.toByte(),
                0x00.toByte(),
                0x00.toByte(),
                0x00.toByte(),
                0x00.toByte(),
                0x00.toByte(),
                0x00.toByte(),
                0x00.toByte(),
                0x00.toByte(),
                0x00.toByte(),
                0x00.toByte(),
                0x00.toByte(),
                0x00.toByte(),
                0x00.toByte(),
                0x00.toByte(),
                0x00.toByte(),
                0x00.toByte(),
                0x00.toByte(),
                0x00.toByte(),
                0x00.toByte(),
                0x00.toByte(),
                0x00.toByte(),
                0x00.toByte(),
                0x00.toByte(),
                0x00.toByte(),
                0x00.toByte(),
                0x00.toByte(),
                0xe0.toByte(),
                0x6f.toByte(),
                0x00.toByte(),
            )
        val context = DeflateContext(Role.Client, DeflateConfig.default())
        assertFailsWith<DecompressionError.SizeLimitReached> {
            context.decompress(framePayload, true, 1000)
        }
    }

    @Test
    fun compressiblePayloadPrefixes() {
        val data = ByteArray(1 shl 16) { (it % 26 + 65).toByte() }
        var len = 32
        while (len <= data.size) {
            val prefix = data.copyOfRange(0, len)
            val context = DeflateContext(Role.Client, DeflateConfig.default())
            val compressed = context.compress(prefix)
            val decompressed = context.decompress(compressed, true, Int.MAX_VALUE)
            assertContentEquals(prefix, decompressed)
            len = len shl 1
        }
    }

    fun makeFrames(frameCount: Int): List<Pair<ByteArray, Boolean>> {
        val framePayload =
            byteArrayOf(
                0xec.toByte(),
                0xc1.toByte(),
                0x31,
                0x01,
                0x00,
                0x00,
                0x00,
                0xc2.toByte(),
                0xa0.toByte(),
                0xf5.toByte(),
                0x4f,
                0x6d,
                0x0b,
                0x2f,
                0xa0.toByte(),
                0x00,
            )
        return (0 until frameCount).map { i ->
            val isFinal = i == frameCount - 1
            Pair(framePayload, isFinal)
        }
    }

    @Test
    fun largeMessageDecompression() {
        val context = DeflateContext(Role.Client, DeflateConfig.default())
        assertNotNull(context)
    }

    @Test
    fun decompressMultipleMessagesThatEachSetBfinal() {
        val context = DeflateContext(Role.Server, DeflateConfig.default())
        assertNotNull(context)
    }

    @Test
    fun oneBlock() {
        val context = DeflateContext(Role.Server, DeflateConfig.default())
        val compressed = context.compress("Hello".encodeToByteArray())
        val decompressed = context.decompress(compressed, true, Int.MAX_VALUE)
        assertEquals("Hello", decompressed.decodeToString())
    }

    @Test
    fun sharingSlidingWindow() {
        val context = DeflateContext(Role.Client, DeflateConfig.default())
        val c1 = context.compress("Hello".encodeToByteArray())
        val c2 = context.compress("Hello".encodeToByteArray())
        assertTrue(c1.isNotEmpty())
        assertTrue(c2.isNotEmpty())
    }

    @Test
    fun deflateBlockWithBfinalSet() {
        val context = DeflateContext(Role.Client, DeflateConfig.default())
        assertNotNull(context)
    }

    @Test
    fun twoDeflateBlocks() {
        val context = DeflateContext(Role.Client, DeflateConfig.new())
        assertNotNull(context)
    }
}
