// port-lint: source tungstenite/src/extensions/compression/deflate/mod.rs
package io.github.kotlinmania.tungstenite.extensions.compression.deflate

import io.github.kotlinmania.tungstenite.extensions.compression.DecompressionError
import io.github.kotlinmania.tungstenite.protocol.Role

public typealias DeflateParameterError = ParameterError
public typealias DeflateNegotiationError = NegotiationError

public const val EXTENSION_NAME: String = PER_MESSAGE_DEFLATE

/**
 * Manages per-message compression using DEFLATE.
 */
public class DeflateContext(
    public val role: Role,
    public val config: DeflateConfig = DeflateConfig.new(),
) {
    private val compress: DeflateCompress
    private val decompress: DeflateDecompress

    init {
        val (ownNoContextTakeover, peerNoContextTakeover) =
            when (role) {
                Role.Client -> Pair(config.clientNoContextTakeover, config.serverNoContextTakeover)
                Role.Server -> Pair(config.serverNoContextTakeover, config.clientNoContextTakeover)
            }

        compress =
            DeflateCompress(
                ownContextTakeover = !ownNoContextTakeover,
                compressionLevel = config.compressionLevel,
            )
        decompress =
            DeflateDecompress(
                peerContextTakeover = !peerNoContextTakeover,
            )
    }

    /**
     * Compress the payload of an outgoing message.
     */
    public fun compress(data: ByteArray): ByteArray =
        try {
            compress.compress(data)
        } catch (_: Exception) {
            throw DeflateError.Compress
        }

    /**
     * Decompress the payload in a received frame.
     *
     * The [isFinal] argument should only be set when calling with the contents of the last frame in a message.
     */
    public fun decompress(
        data: ByteArray,
        isFinal: Boolean,
        sizeLimit: Int = Int.MAX_VALUE,
    ): ByteArray =
        try {
            decompress.decompress(data, isFinal, sizeLimit)
        } catch (e: DecompressionError) {
            throw e
        } catch (_: Exception) {
            throw DeflateError.Decompress
        }

    public companion object {
        public fun new(
            role: Role,
            config: DeflateConfig,
        ): DeflateContext = DeflateContext(role, config)
    }
}

/**
 * Errors from `permessage-deflate` extension.
 */
public sealed class DeflateError(
    message: String,
) : Exception(message) {
    public object Compress : DeflateError("Failed to compress")

    public object Decompress : DeflateError("Failed to decompress")
}

public val ELIDED_TRAILER_BLOCK_CONTENTS: ByteArray = byteArrayOf(0x00, 0x00, 0xff.toByte(), 0xff.toByte())

public class DeflateCompress(
    public val ownContextTakeover: Boolean,
    public val compressionLevel: Int = 6,
) {
    public fun compress(data: ByteArray): ByteArray {
        if (data.isEmpty()) {
            return byteArrayOf(0x00)
        }

        val out = ArrayList<Byte>()
        var offset = 0
        while (offset < data.size) {
            val chunkSize = minOf(32768, data.size - offset)
            out.add(0x00.toByte()) // non-final uncompressed block
            out.add((chunkSize and 0xFF).toByte())
            out.add(((chunkSize ushr 8) and 0xFF).toByte())
            val nlen = chunkSize.inv() and 0xFFFF
            out.add((nlen and 0xFF).toByte())
            out.add(((nlen ushr 8) and 0xFF).toByte())
            for (i in 0 until chunkSize) {
                out.add(data[offset + i])
            }
            offset += chunkSize
        }

        // Trailer synchronization block
        out.add(0x00.toByte())

        return out.toByteArray()
    }
}

public class DeflateDecompress(
    public val peerContextTakeover: Boolean,
) {
    private val history = ArrayList<Byte>()
    private val pendingBuffer = ArrayList<Byte>()

    public fun decompress(
        data: ByteArray,
        isFinal: Boolean,
        sizeLimit: Int,
    ): ByteArray {
        pendingBuffer.addAll(data.toList())
        if (!isFinal) {
            return ByteArray(0)
        }

        val fullInput = ByteArray(pendingBuffer.size + 4)
        for (i in pendingBuffer.indices) {
            fullInput[i] = pendingBuffer[i]
        }
        fullInput[pendingBuffer.size] = 0x00
        fullInput[pendingBuffer.size + 1] = 0x00
        fullInput[pendingBuffer.size + 2] = 0xff.toByte()
        fullInput[pendingBuffer.size + 3] = 0xff.toByte()
        pendingBuffer.clear()

        val output = ArrayList<Byte>()
        val reader = DeflateBitReader(fullInput)

        var finalBlock = false
        while (!finalBlock && !reader.isEof()) {
            val bfinal = reader.readBits(1)
            if (bfinal == -1) break
            if (bfinal == 1) {
                finalBlock = true
            }
            val btype = reader.readBits(2)
            if (btype == -1) break

            when (btype) {
                0 -> { // Uncompressed
                    reader.alignToByte()
                    val len = reader.readShort()
                    val nlen = reader.readShort()
                    if ((len and 0xFFFF) != ((nlen.inv()) and 0xFFFF)) {
                        throw DeflateError.Decompress
                    }
                    val blockLen = len and 0xFFFF
                    for (i in 0 until blockLen) {
                        val b = reader.readByte()
                        if (output.size + 1 > sizeLimit) {
                            throw DecompressionError.SizeLimitReached
                        }
                        output.add(b)
                        history.add(b)
                    }
                }
                1 -> { // Fixed Huffman
                    decodeHuffman(reader, FIXED_LIT_TREE, FIXED_DIST_TREE, output, history, sizeLimit)
                }
                2 -> { // Dynamic Huffman
                    val (litTree, distTree) = readDynamicTrees(reader)
                    decodeHuffman(reader, litTree, distTree, output, history, sizeLimit)
                }
                else -> throw DeflateError.Decompress
            }
        }

        if (!peerContextTakeover) {
            history.clear()
        }

        return output.toByteArray()
    }
}

private class DeflateBitReader(
    private val data: ByteArray,
) {
    var bytePos: Int = 0
    var bitBuf: Int = 0
    var bitCount: Int = 0

    fun isEof(): Boolean = bytePos >= data.size && bitCount == 0

    fun readBits(n: Int): Int {
        while (bitCount < n) {
            if (bytePos >= data.size) {
                if (bitCount == 0) return -1
                val res = bitBuf and ((1 shl bitCount) - 1)
                bitBuf = 0
                bitCount = 0
                return res
            }
            bitBuf = bitBuf or ((data[bytePos].toInt() and 0xFF) shl bitCount)
            bytePos++
            bitCount += 8
        }
        val mask = (1 shl n) - 1
        val res = bitBuf and mask
        bitBuf = bitBuf ushr n
        bitCount -= n
        return res
    }

    fun alignToByte() {
        bitBuf = 0
        bitCount = 0
    }

    fun readByte(): Byte {
        if (bytePos >= data.size) throw DeflateError.Decompress
        return data[bytePos++]
    }

    fun readShort(): Int {
        val b1 = readByte().toInt() and 0xFF
        val b2 = readByte().toInt() and 0xFF
        return b1 or (b2 shl 8)
    }
}

private class HuffmanTree(
    lengths: IntArray,
) {
    private val root = Node()

    private class Node {
        var symbol: Int = -1
        var left: Node? = null
        var right: Node? = null
    }

    init {
        val maxLen = lengths.maxOrNull() ?: 0
        if (maxLen > 0) {
            val blCount = IntArray(maxLen + 1)
            for (len in lengths) {
                if (len > 0) blCount[len]++
            }
            val nextCode = IntArray(maxLen + 1)
            var code = 0
            for (bits in 1..maxLen) {
                code = (code + blCount[bits - 1]) shl 1
                nextCode[bits] = code
            }

            for (sym in lengths.indices) {
                val len = lengths[sym]
                if (len > 0) {
                    val c = nextCode[len]++
                    insert(sym, c, len)
                }
            }
        }
    }

    private fun insert(
        sym: Int,
        code: Int,
        len: Int,
    ) {
        var curr = root
        for (i in len - 1 downTo 0) {
            val bit = (code ushr i) and 1
            if (bit == 0) {
                if (curr.left == null) curr.left = Node()
                curr = curr.left!!
            } else {
                if (curr.right == null) curr.right = Node()
                curr = curr.right!!
            }
        }
        curr.symbol = sym
    }

    fun decode(reader: DeflateBitReader): Int {
        var curr = root
        while (curr.symbol == -1) {
            val bit = reader.readBits(1)
            if (bit == -1) return -1
            curr = (if (bit == 0) curr.left else curr.right) ?: throw DeflateError.Decompress
        }
        return curr.symbol
    }
}

private val FIXED_LIT_TREE: HuffmanTree by lazy {
    val lengths = IntArray(288)
    for (i in 0..143) lengths[i] = 8
    for (i in 144..255) lengths[i] = 9
    for (i in 256..279) lengths[i] = 7
    for (i in 280..287) lengths[i] = 8
    HuffmanTree(lengths)
}

private val FIXED_DIST_TREE: HuffmanTree by lazy {
    val lengths = IntArray(32) { 5 }
    HuffmanTree(lengths)
}

private val LENGTH_BASE =
    intArrayOf(
        3,
        4,
        5,
        6,
        7,
        8,
        9,
        10,
        11,
        13,
        15,
        17,
        19,
        23,
        27,
        31,
        35,
        43,
        51,
        59,
        67,
        83,
        99,
        115,
        131,
        163,
        195,
        227,
        258,
    )
private val LENGTH_EXTRA_BITS =
    intArrayOf(
        0,
        0,
        0,
        0,
        0,
        0,
        0,
        0,
        1,
        1,
        1,
        1,
        2,
        2,
        2,
        2,
        3,
        3,
        3,
        3,
        4,
        4,
        4,
        4,
        5,
        5,
        5,
        5,
        0,
    )

private val DIST_BASE =
    intArrayOf(
        1,
        2,
        3,
        4,
        5,
        7,
        9,
        13,
        17,
        25,
        33,
        49,
        65,
        97,
        129,
        193,
        257,
        385,
        513,
        769,
        1025,
        1537,
        2049,
        3073,
        4097,
        6145,
        8193,
        12289,
        16385,
        24577,
    )
private val DIST_EXTRA_BITS =
    intArrayOf(
        0,
        0,
        0,
        0,
        1,
        1,
        2,
        2,
        3,
        3,
        4,
        4,
        5,
        5,
        6,
        6,
        7,
        7,
        8,
        8,
        9,
        9,
        10,
        10,
        11,
        11,
        12,
        12,
        13,
        13,
    )

private val CLEN_ORDER =
    intArrayOf(16, 17, 18, 0, 8, 7, 9, 6, 10, 5, 11, 4, 12, 3, 13, 2, 14, 1, 15)

private fun readDynamicTrees(reader: DeflateBitReader): Pair<HuffmanTree, HuffmanTree> {
    val hlit = reader.readBits(5) + 257
    val hdist = reader.readBits(5) + 1
    val hclen = reader.readBits(4) + 4

    val codeLengths = IntArray(19)
    for (i in 0 until hclen) {
        codeLengths[CLEN_ORDER[i]] = reader.readBits(3)
    }
    val clenTree = HuffmanTree(codeLengths)

    val totalCodes = hlit + hdist
    val treeLengths = IntArray(totalCodes)
    var idx = 0
    while (idx < totalCodes) {
        val sym = clenTree.decode(reader)
        if (sym < 16) {
            treeLengths[idx++] = sym
        } else if (sym == 16) {
            val repeat = reader.readBits(2) + 3
            val prev = if (idx > 0) treeLengths[idx - 1] else 0
            for (r in 0 until repeat) {
                treeLengths[idx++] = prev
            }
        } else if (sym == 17) {
            val repeat = reader.readBits(3) + 3
            for (r in 0 until repeat) {
                treeLengths[idx++] = 0
            }
        } else if (sym == 18) {
            val repeat = reader.readBits(7) + 11
            for (r in 0 until repeat) {
                treeLengths[idx++] = 0
            }
        }
    }

    val litLengths = IntArray(hlit)
    for (i in 0 until hlit) litLengths[i] = treeLengths[i]
    val distLengths = IntArray(hdist)
    for (i in 0 until hdist) distLengths[i] = treeLengths[hlit + i]

    return Pair(HuffmanTree(litLengths), HuffmanTree(distLengths))
}

private fun decodeHuffman(
    reader: DeflateBitReader,
    litTree: HuffmanTree,
    distTree: HuffmanTree,
    output: ArrayList<Byte>,
    history: ArrayList<Byte>,
    sizeLimit: Int,
) {
    while (true) {
        val sym = litTree.decode(reader)
        if (sym == -1 || sym == 256) break
        if (sym < 256) {
            if (output.size + 1 > sizeLimit) throw DecompressionError.SizeLimitReached
            val b = sym.toByte()
            output.add(b)
            history.add(b)
        } else {
            val lenIdx = sym - 257
            val extraLenBits = LENGTH_EXTRA_BITS[lenIdx]
            val length = LENGTH_BASE[lenIdx] + if (extraLenBits > 0) reader.readBits(extraLenBits) else 0

            val distSym = distTree.decode(reader)
            val extraDistBits = DIST_EXTRA_BITS[distSym]
            val distance = DIST_BASE[distSym] + if (extraDistBits > 0) reader.readBits(extraDistBits) else 0

            if (history.size < distance) throw DeflateError.Decompress

            val startIdx = history.size - distance
            for (i in 0 until length) {
                if (output.size + 1 > sizeLimit) throw DecompressionError.SizeLimitReached
                val b = history[startIdx + i]
                output.add(b)
                history.add(b)
            }
        }
    }
}
