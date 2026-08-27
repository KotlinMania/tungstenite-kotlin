// port-lint: source tungstenite/src/protocol/frame/mask.rs
package io.github.kotlinmania.tungstenite.protocol.frame

import kotlin.experimental.xor
import kotlin.random.Random

/** Generate a random frame mask. */
public fun generateMask(): ByteArray {
    val mask = ByteArray(4)
    Random.Default.nextBytes(mask)
    return mask
}

/** Mask/unmask a frame. */
public fun applyMask(
    buf: ByteArray,
    mask: ByteArray,
    offset: Int = 0,
    length: Int = buf.size - offset,
) {
    applyMaskFast32(buf, mask, offset, length)
}

/** A safe unoptimized mask application. */
internal fun applyMaskFallback(
    buf: ByteArray,
    mask: ByteArray,
    offset: Int = 0,
    length: Int = buf.size - offset,
) {
    require(mask.size >= 4) { "Mask must be at least 4 bytes" }
    for (i in 0 until length) {
        buf[offset + i] = (buf[offset + i] xor mask[i and 3])
    }
}

/** Faster version of [applyMask] which operates on 4-byte blocks. */
public fun applyMaskFast32(
    buf: ByteArray,
    mask: ByteArray,
    offset: Int = 0,
    length: Int = buf.size - offset,
) {
    require(mask.size >= 4) { "Mask must be at least 4 bytes" }
    val end = offset + length
    var i = offset
    val end4 = offset + (length and 3.inv())
    while (i < end4) {
        val maskIdx = (i - offset) and 3
        buf[i] = (buf[i] xor mask[maskIdx])
        buf[i + 1] = (buf[i + 1] xor mask[(maskIdx + 1) and 3])
        buf[i + 2] = (buf[i + 2] xor mask[(maskIdx + 2) and 3])
        buf[i + 3] = (buf[i + 3] xor mask[(maskIdx + 3) and 3])
        i += 4
    }
    while (i < end) {
        buf[i] = (buf[i] xor mask[(i - offset) and 3])
        i++
    }
}
