// port-lint: source extensions/headers/sec_websocket_extensions.rs
package io.github.kotlinmania.tungstenite.extensions.headers

/**
 * Named parameter for an extension in a `Sec-Websocket-Extensions` header.
 */
public data class WebsocketExtensionParam(
    public val name: String,
    public val value: String? = null,
) {
    override fun toString(): String =
        if (value != null) "$name=$value" else name

    public companion object {
        public fun parse(s: String): WebsocketExtensionParam {
            val parts = s.split('=', limit = 2)
            val name = parts[0].trim()
            val value = if (parts.size > 1) parts[1].trim() else null
            return WebsocketExtensionParam(name, value)
        }
    }
}

/**
 * An extension listed in a [SecWebsocketExtensions] header.
 */
public data class WebsocketProtocolExtension(
    public val name: String,
    public val params: List<WebsocketExtensionParam> = emptyList(),
) {
    public fun name(): String = name

    public fun params(): List<WebsocketExtensionParam> = params

    override fun toString(): String {
        if (params.isEmpty()) return name
        val sb = StringBuilder(name)
        for (p in params) {
            sb.append("; ").append(p.toString())
        }
        return sb.toString()
    }

    public companion object {
        public fun parse(s: String): WebsocketProtocolExtension {
            val parts = s.split(';')
            val name = parts[0].trim()
            val params = parts.drop(1).map { WebsocketExtensionParam.parse(it) }
            return WebsocketProtocolExtension(name, params)
        }
    }
}

/**
 * The `Sec-Websocket-Extensions` header.
 */
public data class SecWebsocketExtensions(
    public val extensions: List<WebsocketProtocolExtension> = emptyList(),
) : Iterable<WebsocketProtocolExtension> {
    public fun len(): Int = extensions.size

    public val size: Int get() = extensions.size

    public fun isEmpty(): Boolean = extensions.isEmpty()

    override fun iterator(): Iterator<WebsocketProtocolExtension> = extensions.iterator()

    public fun headerValue(): String = extensions.joinToString(", ") { it.toString() }

    override fun toString(): String = headerValue()

    public companion object {
        public fun parse(headerValue: String): SecWebsocketExtensions {
            val extList = fromCommaDelimited(headerValue).map { WebsocketProtocolExtension.parse(it) }
            return SecWebsocketExtensions(extList)
        }

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
