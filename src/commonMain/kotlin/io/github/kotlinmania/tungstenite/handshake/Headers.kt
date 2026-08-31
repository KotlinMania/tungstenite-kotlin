// port-lint: source tungstenite/src/handshake/headers.rs
package io.github.kotlinmania.tungstenite.handshake

import io.github.kotlinmania.tungstenite.CapacityError
import io.github.kotlinmania.tungstenite.ProtocolError
import io.github.kotlinmania.tungstenite.TungsteniteException

/** Limit for the number of header lines. */
public const val MAX_HEADERS: Int = 124

/**
 * HTTP header map preserving case-insensitive lookup.
 */
public class HeaderMap {
    private val entries: MutableList<Pair<String, ByteArray>> = mutableListOf()

    /**
     * Append a header [name] and [value].
     */
    public fun append(name: String, value: ByteArray) {
        entries.add(Pair(name, value))
    }

    /**
     * Append a header [name] and string [value].
     */
    public fun append(name: String, value: String) {
        append(name, value.encodeToByteArray())
    }

    /**
     * Get the first value for [name] (case-insensitive) as bytes.
     */
    public fun get(name: String): ByteArray? = entries.firstOrNull { it.first.equals(name, ignoreCase = true) }?.second

    /**
     * Get the first value for [name] (case-insensitive) as a UTF-8 string.
     */
    public fun getString(name: String): String? = get(name)?.decodeToString()

    /**
     * Get all values for [name] (case-insensitive) as bytes.
     */
    public fun getAll(name: String): List<ByteArray> = entries.filter { it.first.equals(name, ignoreCase = true) }.map { it.second }

    /**
     * Get all values for [name] (case-insensitive) as UTF-8 strings.
     */
    public fun getAllString(name: String): List<String> = getAll(name).map { it.decodeToString() }

    /**
     * Return all header entries as (name, value) pairs.
     */
    public fun entries(): List<Pair<String, ByteArray>> = entries.toList()

    public companion object : TryParse<HeaderMap> {
        /**
         * Create an empty HeaderMap.
         */
        public fun new(): HeaderMap = HeaderMap()

        /**
         * Try to parse headers from the given byte buffer.
         *
         * Returns Ok(null) if partial/incomplete, Err on syntax error or header limit exceeded,
         * or Ok(Pair(consumedBytes, HeaderMap)).
         */
        override fun tryParse(data: ByteArray): Result<Pair<Int, HeaderMap>?> {
            var pos = 0
            val headerMap = HeaderMap()
            var count = 0

            while (pos < data.size) {
                // Check for empty line ending header block (\r\n or \n)
                if (data[pos] == '\r'.code.toByte() && pos + 1 < data.size && data[pos + 1] == '\n'.code.toByte()) {
                    return Result.success(Pair(pos + 2, headerMap))
                }
                if (data[pos] == '\n'.code.toByte()) {
                    return Result.success(Pair(pos + 1, headerMap))
                }

                // Find end of line
                var lineEnd = -1
                var nextLineStart = -1
                for (i in pos until data.size) {
                    if (data[i] == '\n'.code.toByte()) {
                        lineEnd = if (i > pos && data[i - 1] == '\r'.code.toByte()) i - 1 else i
                        nextLineStart = i + 1
                        break
                    }
                }

                if (lineEnd == -1) {
                    // Line not complete yet
                    return Result.success(null)
                }

                count++
                if (count > MAX_HEADERS) {
                    return Result.failure(TungsteniteException.Capacity(CapacityError.TooManyHeaders))
                }

                // Find colon separator
                var colonPos = -1
                for (i in pos until lineEnd) {
                    if (data[i] == ':'.code.toByte()) {
                        colonPos = i
                        break
                    }
                }

                if (colonPos == -1) {
                    return Result.failure(
                        TungsteniteException.ProtocolViolation(ProtocolError.Httparse("missing colon in header line")),
                    )
                }

                val name = data.copyOfRange(pos, colonPos).decodeToString().trim()
                var valStart = colonPos + 1
                while (valStart < lineEnd && (data[valStart] == ' '.code.toByte() || data[valStart] == '\t'.code.toByte())) {
                    valStart++
                }
                var valEnd = lineEnd
                while (valEnd > valStart && (data[valEnd - 1] == ' '.code.toByte() || data[valEnd - 1] == '\t'.code.toByte())) {
                    valEnd--
                }
                val value = data.copyOfRange(valStart, valEnd)

                headerMap.append(name, value)
                pos = nextLineStart
            }

            return Result.success(null)
        }
    }
}
