// port-lint: source proxy.rs
package io.github.kotlinmania.tungstenite

import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

public const val MAX_CONNECT_RESPONSE_SIZE: Int = 8192

/**
 * Proxy scheme supported by tungstenite.
 */
public enum class ProxyScheme {
    /** HTTP CONNECT proxy. */
    Http,

    /** SOCKS5 proxy with remote DNS resolution. */
    Socks5,

    /** SOCKS5 proxy with local DNS resolution. */
    Socks5h,
}

/**
 * Proxy authentication credentials.
 */
public data class ProxyAuth(
    public val username: String,
    public val password: String,
)

/**
 * Resolved proxy configuration.
 */
public data class ProxyConfig(
    public val scheme: ProxyScheme,
    public val host: String,
    public val port: Int,
    public val auth: ProxyAuth? = null,
) {
    /** Render the proxy authority as `host:port`. */
    public fun authority(): String = "$host:$port"

    public companion object {
        /** Parse a proxy configuration from a proxy URL string. */
        public fun parse(value: String): ProxyConfig = parseProxyConfig(value)
    }
}

/**
 * Build the bytes for an HTTP CONNECT request.
 */
@OptIn(ExperimentalEncodingApi::class)
public fun buildHttpConnectRequest(
    authority: String,
    auth: ProxyAuth? = null,
): ByteArray {
    val builder = StringBuilder()
    builder.append("CONNECT ").append(authority).append(" HTTP/1.1\r\n")
    builder.append("Host: ").append(authority).append("\r\n")
    builder.append("Proxy-Connection: Keep-Alive\r\n")
    if (auth != null) {
        val token = basicAuthHeader(auth)
        builder.append("Proxy-Authorization: ").append(token).append("\r\n")
    }
    builder.append("\r\n")
    return builder.toString().encodeToByteArray()
}

/**
 * Parse an HTTP CONNECT response and return the status code.
 */
public fun parseHttpConnectResponse(response: ByteArray): Int {
    val text =
        try {
            response.decodeToString()
        } catch (_: Exception) {
            throw TungsteniteException.Url(
                UrlError.ProxyConnect("HTTP CONNECT response not valid UTF-8"),
            )
        }

    val lines = text.lines()
    val statusLine =
        lines.firstOrNull()
            ?: throw TungsteniteException.Url(
                UrlError.ProxyConnect("HTTP CONNECT response missing status line"),
            )

    val parts = statusLine.trim().split(Regex("\\s+"))
    if (parts.size < 2) {
        throw TungsteniteException.Url(
            UrlError.ProxyConnect("HTTP CONNECT response missing status code"),
        )
    }

    return parts[1].toIntOrNull()
        ?: throw TungsteniteException.Url(
            UrlError.ProxyConnect("HTTP CONNECT response invalid status code"),
        )
}

/**
 * Split host and optional port from a host:port token or [ipv6]:port token.
 */
public fun splitHostPort(token: String): Pair<String, Int?> {
    val trimmed = token.trim()
    if (trimmed.startsWith('[')) {
        val close = trimmed.indexOf(']')
        if (close != -1) {
            val host = trimmed.substring(0, close + 1)
            val remainder = trimmed.substring(close + 1)
            if (remainder.startsWith(':')) {
                val port = remainder.substring(1).toIntOrNull()
                return Pair(host, port)
            }
            return Pair(host, null)
        }
        return Pair(trimmed, null)
    }

    val colonCount = trimmed.count { it == ':' }
    if (colonCount == 1) {
        val idx = trimmed.lastIndexOf(':')
        val host = trimmed.substring(0, idx)
        val port = trimmed.substring(idx + 1).toIntOrNull()
        return Pair(host, port)
    }

    return Pair(trimmed, null)
}

/**
 * Normalize host removing bracket wrappers.
 */
public fun normalizeHost(host: String): String =
    if (host.startsWith('[') && host.endsWith(']')) {
        host.substring(1, host.length - 1)
    } else {
        host
    }

/**
 * Check if the host and port should bypass proxy given a NO_PROXY environment string.
 */
public fun shouldBypassProxy(host: String, port: Int, noProxy: String?): Boolean {
    if (noProxy == null) return false
    val trimmedNoProxy = noProxy.trim()
    if (trimmedNoProxy.isEmpty()) return false
    if (trimmedNoProxy == "*") return true

    val normalizedHost = normalizeHost(host)

    for (rawToken in trimmedNoProxy.split(',')) {
        val token = rawToken.trim()
        if (token.isEmpty()) continue

        val (tokenHost, tokenPort) = splitHostPort(token)
        if (tokenPort != null && tokenPort != port) {
            continue
        }

        val normTokenHost = normalizeHost(tokenHost)
        if (normalizedHost == normTokenHost) {
            return true
        }

        if (normTokenHost.startsWith('.')) {
            val suffix = normTokenHost.substring(1)
            if (normalizedHost == suffix || normalizedHost.endsWith(".$suffix")) {
                return true
            }
        } else if (normalizedHost.endsWith(".$normTokenHost")) {
            return true
        }
    }

    return false
}

/**
 * Generate HTTP Basic auth header value.
 */
@OptIn(ExperimentalEncodingApi::class)
public fun basicAuthHeader(auth: ProxyAuth): String {
    val token = "${auth.username}:${auth.password}"
    val encoded = Base64.Default.encode(token.encodeToByteArray())
    return "Basic $encoded"
}

private fun parseProxyConfig(value: String): ProxyConfig {
    val trimmed = value.trim()
    val schemeIndex = trimmed.indexOf("://")
    if (schemeIndex == -1) {
        throw TungsteniteException.Url(UrlError.UnsupportedProxyScheme)
    }

    val schemeStr = trimmed.substring(0, schemeIndex).lowercase()
    val scheme =
        when (schemeStr) {
            "http" -> ProxyScheme.Http
            "socks5" -> ProxyScheme.Socks5
            "socks5h" -> ProxyScheme.Socks5h
            else -> throw TungsteniteException.Url(UrlError.UnsupportedProxyScheme)
        }

    val authorityAndPath = trimmed.substring(schemeIndex + 3)
    val pathIndex = authorityAndPath.indexOf('/')
    val authority = if (pathIndex != -1) authorityAndPath.substring(0, pathIndex) else authorityAndPath

    if (authority.isEmpty()) {
        throw TungsteniteException.Url(UrlError.InvalidProxyConfig(value))
    }

    val atIndex = authority.lastIndexOf('@')
    val (userinfo, hostport) =
        if (atIndex != -1) {
            Pair(authority.substring(0, atIndex), authority.substring(atIndex + 1))
        } else {
            Pair(null, authority)
        }

    val (hostRaw, portOpt) = splitHostPort(hostport)
    if (hostRaw.isEmpty()) {
        throw TungsteniteException.Url(UrlError.InvalidProxyConfig(value))
    }
    val host = normalizeHost(hostRaw)
    val port =
        portOpt ?: when (scheme) {
            ProxyScheme.Http -> 80
            ProxyScheme.Socks5, ProxyScheme.Socks5h -> 1080
        }

    val auth = userinfo?.let { parseUserinfo(it) }

    return ProxyConfig(scheme = scheme, host = host, port = port, auth = auth)
}

private fun parseUserinfo(userinfo: String): ProxyAuth {
    val colonIdx = userinfo.indexOf(':')
    val (user, pass) =
        if (colonIdx != -1) {
            Pair(userinfo.substring(0, colonIdx), userinfo.substring(colonIdx + 1))
        } else {
            Pair(userinfo, "")
        }
    val username = percentDecode(user)
    val password = percentDecode(pass)
    return ProxyAuth(username = username, password = password)
}

/**
 * Percent-decode a string value.
 */
public fun percentDecode(value: String): String {
    val bytes = ArrayList<Byte>(value.length)
    var i = 0
    val rawBytes = value.encodeToByteArray()
    while (i < rawBytes.size) {
        val b = rawBytes[i]
        if (b == '%'.code.toByte()) {
            if (i + 2 >= rawBytes.size) {
                throw TungsteniteException.Url(UrlError.InvalidProxyConfig(value))
            }
            val hi = fromHex(rawBytes[i + 1])
            val lo = fromHex(rawBytes[i + 2])
            bytes.add(((hi shl 4) or lo).toByte())
            i += 3
        } else {
            bytes.add(b)
            i += 1
        }
    }
    return try {
        bytes.toByteArray().decodeToString()
    } catch (_: Exception) {
        throw TungsteniteException.Url(UrlError.InvalidProxyConfig(value))
    }
}

private fun fromHex(byte: Byte): Int {
    val c = byte.toInt().toChar()
    return when (c) {
        in '0'..'9' -> c - '0'
        in 'a'..'f' -> c - 'a' + 10
        in 'A'..'F' -> c - 'A' + 10
        else -> throw TungsteniteException.Url(UrlError.InvalidProxyConfig("invalid percent-encoding"))
    }
}
