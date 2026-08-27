// port-lint: source tungstenite/src/protocol/frame/utf8.rs
package io.github.kotlinmania.tungstenite.protocol.frame

import io.github.kotlinmania.bytes.Bytes

/** Utf8 payload. */
public class Utf8Bytes internal constructor(
    public val bytes: Bytes,
) : Comparable<Utf8Bytes> {
    private val cachedString: String by lazy {
        bytes.asString()
    }

    public fun asStr(): String = cachedString

    public fun asBytes(): ByteArray = bytes.asSlice()

    public fun asBytesObj(): Bytes = bytes

    public fun asRef(): String = asStr()

    public fun borrow(): String = asStr()

    public fun toBytes(): Bytes = bytes

    public val length: Int
        get() = asStr().length

    public fun len(): Int = length

    public fun isEmpty(): Boolean = bytes.isEmpty()

    public operator fun get(index: Int): Char = asStr()[index]

    public fun subSequence(startIndex: Int, endIndex: Int): String =
        asStr().substring(startIndex, endIndex)

    override fun compareTo(other: Utf8Bytes): Int =
        asStr().compareTo(other.asStr())

    override fun equals(other: Any?): Boolean =
        when (other) {
            is Utf8Bytes -> bytes == other.bytes
            is String -> asStr() == other
            is CharSequence -> asStr() == other.toString()
            else -> false
        }

    override fun hashCode(): Int = asStr().hashCode()

    override fun toString(): String = asStr()

    public companion object {
        public val EMPTY: Utf8Bytes = Utf8Bytes(Bytes.new())

        /** Creates from a static str. */
        public fun fromStatic(str: String): Utf8Bytes =
            Utf8Bytes(Bytes.fromStatic(str))

        /**
         * Creates from a [Bytes] object without checking the encoding.
         */
        public fun fromBytesUnchecked(bytes: Bytes): Utf8Bytes =
            Utf8Bytes(bytes)

        /** Creates [Utf8Bytes] from [Bytes], validating UTF-8 encoding. */
        public fun tryFrom(bytes: Bytes): Utf8Bytes {
            val str = bytes.asString()
            return Utf8Bytes(bytes)
        }

        /** Creates [Utf8Bytes] from [ByteArray], validating UTF-8 encoding. */
        public fun tryFrom(bytes: ByteArray): Utf8Bytes {
            val str = bytes.decodeToString()
            return Utf8Bytes(Bytes.from(bytes))
        }

        public fun from(string: String): Utf8Bytes =
            fromStatic(string)

        public fun from(bytes: Bytes): Utf8Bytes =
            tryFrom(bytes)
    }
}
