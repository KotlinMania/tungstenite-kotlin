// port-lint: source tungstenite/src/extensions/headers/sec_websocket_extensions.rs
package io.github.kotlinmania.tungstenite.extensions.headers

/**
 * Trait/interface for types that can calculate their encoded length and write bytes.
 */
public interface WriteTo {
    /**
     * Pre-calculated length in bytes when encoded.
     */
    public fun encodedLen(): Int

    /**
     * Writes encoded bytes to the provided consumer.
     */
    public fun writeWith(write: (ByteArray) -> Unit)
}

/**
 * Comma-delimited list of encodable items.
 */
public class CommaDelimited<T : WriteTo>(
    public val items: List<T>,
) : WriteTo {
    override fun encodedLen(): Int {
        val allLen = items.sumOf { it.encodedLen() }
        val sepLen = if (items.size > 1) (items.size - 1) * 2 else 0
        return allLen + sepLen
    }

    override fun writeWith(write: (ByteArray) -> Unit) {
        for (i in items.indices) {
            if (i > 0) {
                write(SEPARATOR)
            }
            items[i].writeWith(write)
        }
    }

    public companion object {
        private val SEPARATOR = ", ".encodeToByteArray()
    }
}

/**
 * Named parameter for an extension in a `Sec-Websocket-Extensions` header.
 */
public data class WebsocketExtensionParam(
    public val name: String,
    public val value: String? = null,
) : WriteTo {
    override fun encodedLen(): Int =
        name.length + if (value != null) 1 + value.length else 0

    override fun writeWith(write: (ByteArray) -> Unit) {
        write(name.encodeToByteArray())
        if (value != null) {
            write("=".encodeToByteArray())
            write(value.encodeToByteArray())
        }
    }

    override fun toString(): String =
        if (value != null) "$name=$value" else name

    public companion object {
        /** Constructs a new parameter. */
        public fun new(
            name: String,
            value: String? = null,
        ): WebsocketExtensionParam = WebsocketExtensionParam(name, value)

        /** Parses a parameter from a string. */
        public fun parse(s: String): WebsocketExtensionParam {
            val parts = s.split('=', limit = 2)
            val name = parts[0].trim()
            val value = if (parts.size > 1) parts[1].trim() else null
            return WebsocketExtensionParam(name, value)
        }

        public fun fromStr(s: String): WebsocketExtensionParam = parse(s)
    }
}

/**
 * An extension listed in a [SecWebsocketExtensions] header.
 */
public data class WebsocketProtocolExtension(
    public val name: String,
    public val params: List<WebsocketExtensionParam> = emptyList(),
) : WriteTo {
    override fun encodedLen(): Int {
        val paramsLen = params.sumOf { it.encodedLen() + 2 }
        return name.length + paramsLen
    }

    override fun writeWith(write: (ByteArray) -> Unit) {
        write(name.encodeToByteArray())
        for (p in params) {
            write("; ".encodeToByteArray())
            p.writeWith(write)
        }
    }

    override fun toString(): String {
        if (params.isEmpty()) return name
        val sb = StringBuilder(name)
        for (p in params) {
            sb.append("; ").append(p.toString())
        }
        return sb.toString()
    }

    public companion object {
        /** Constructs a new extension directive. */
        public fun new(
            name: String,
            params: List<WebsocketExtensionParam> = emptyList(),
        ): WebsocketProtocolExtension = WebsocketProtocolExtension(name, params)

        /** Parses an extension directive from a string. */
        public fun parse(s: String): WebsocketProtocolExtension {
            val parts = s.split(';')
            val name = parts[0].trim()
            val params = parts.drop(1).map { WebsocketExtensionParam.parse(it) }
            return WebsocketProtocolExtension(name, params)
        }

        public fun fromStr(s: String): WebsocketProtocolExtension = parse(s)
    }
}

/**
 * The `Sec-Websocket-Extensions` header.
 */
public data class SecWebsocketExtensions(
    public val extensions: List<WebsocketProtocolExtension> = emptyList(),
) : Iterable<WebsocketProtocolExtension> {
    /** Returns an iterator over extensions. */
    public fun iter(): Iterator<WebsocketProtocolExtension> = iterator()

    /** Into iterator equivalent. */
    public fun intoIter(): Iterator<WebsocketProtocolExtension> = iterator()

    /** Number of extensions. */
    public fun len(): Int = extensions.size

    /** Number of extensions. */
    public val size: Int get() = extensions.size

    /** Returns `true` if empty. */
    public fun isEmpty(): Boolean = extensions.isEmpty()

    override fun iterator(): Iterator<WebsocketProtocolExtension> = extensions.iterator()

    /** Serialized header value. */
    public fun headerValue(): String = extensions.joinToString(", ") { it.toString() }

    override fun toString(): String = headerValue()

    public companion object {
        /** Constructs a new header from extensions. */
        public fun new(extensions: List<WebsocketProtocolExtension> = emptyList()): SecWebsocketExtensions =
            SecWebsocketExtensions(extensions)

        /** Decodes from a list of header values. */
        public fun decode(values: List<String>): SecWebsocketExtensions =
            parse(values.joinToString(", "))

        /** Encodes header value. */
        public fun encode(header: SecWebsocketExtensions): String =
            header.headerValue()

        /** Parses a header from a string. */
        public fun parse(headerValue: String): SecWebsocketExtensions {
            val extList = fromCommaDelimited(headerValue).map { WebsocketProtocolExtension.parse(it) }
            return SecWebsocketExtensions(extList)
        }

        public fun fromStr(headerValue: String): SecWebsocketExtensions = parse(headerValue)

        private fun fromCommaDelimited(value: String): List<String> {
            val result = ArrayList<String>()
            var inQuotes = false
            val current = StringBuilder()

            for (c in value) {
                when (c) {
                    '"' -> {
                        inQuotes = !inQuotes
                        current.append(c)
                    }
                    ',' -> {
                        if (!inQuotes) {
                            val trimmed = current.toString().trim()
                            if (trimmed.isNotEmpty()) {
                                result.add(trimmed)
                            }
                            current.clear()
                        } else {
                            current.append(c)
                        }
                    }
                    else -> current.append(c)
                }
            }

            val trimmed = current.toString().trim()
            if (trimmed.isNotEmpty()) {
                result.add(trimmed)
            }
            return result
        }
    }
}
