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
) {
    public companion object {
        public fun new(username: String, password: String): ProxyAuth = ProxyAuth(username, password)
    }
}

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
        public fun new(
            scheme: ProxyScheme,
            host: String,
            port: Int,
            auth: ProxyAuth? = null,
        ): ProxyConfig = ProxyConfig(scheme, host, port, auth)

        /** Parse a proxy configuration from a proxy URL string. */
        public fun parse(value: String): ProxyConfig = parseProxyConfig(value)

        /** Resolve proxy configuration from environment for host, port, and TLS mode. */
        public fun fromEnv(host: String, port: Int, isTls: Boolean = false): ProxyConfig? =
            proxyFromEnvForHost(host, port, isTls)

        /** Resolve proxy configuration from environment for a given URI string. */
        public fun fromEnv(uri: String): ProxyConfig? {
            val isTls = uri.startsWith("wss://", ignoreCase = true) || uri.startsWith("https://", ignoreCase = true)
            val schemeEnd = uri.indexOf("://")
            val remainder = if (schemeEnd != -1) uri.substring(schemeEnd + 3) else uri
            val pathStart = remainder.indexOfAny(charArrayOf('/', '?', '#'))
            val hostPortStr = if (pathStart != -1) remainder.substring(0, pathStart) else remainder
            val atIdx = hostPortStr.lastIndexOf('@')
            val actualHostPort = if (atIdx != -1) hostPortStr.substring(atIdx + 1) else hostPortStr
            val (host, portOpt) = splitHostPort(actualHostPort)
            if (host.isEmpty()) return null
            val port = portOpt ?: if (isTls) 443 else 80
            return proxyFromEnvForHost(host, port, isTls)
        }
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

private val PROXY_ENV: MutableMap<String, String> = mutableMapOf()

public fun setProxyEnvVar(name: String, value: String) {
    PROXY_ENV[name] = value
}

public fun removeProxyEnvVar(name: String) {
    PROXY_ENV.remove(name)
}

public fun getEnvFirst(keys: List<String>): String? {
    for (k in keys) {
        val v = PROXY_ENV[k]
        if (!v.isNullOrEmpty()) {
            return v
        }
    }
    return null
}

public fun getEnvFirst(vararg keys: String): String? = getEnvFirst(keys.toList())

public fun proxyFromEnvForHost(
    host: String,
    port: Int,
    isTls: Boolean = false,
): ProxyConfig? {
    if (shouldBypassProxy(host, port)) {
        return null
    }

    val proxy =
        if (isTls) {
            getEnvFirst("HTTPS_PROXY", "https_proxy")
                ?: getEnvFirst("HTTP_PROXY", "http_proxy")
        } else {
            getEnvFirst("HTTP_PROXY", "http_proxy")
        } ?: getEnvFirst("ALL_PROXY", "all_proxy") ?: return null

    return parseProxyConfig(proxy)
}

/**
 * Check if the host and port should bypass proxy using configured environment variables.
 */
public fun shouldBypassProxy(host: String, port: Int): Boolean {
    val noProxy = getEnvFirst("NO_PROXY", "no_proxy") ?: return false
    return shouldBypassProxy(host, port, noProxy)
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

    val (userinfo, hostport) = splitUserinfo(authority)
    val (host, port) = parseHostPort(hostport, scheme)
    val auth = userinfo?.let { parseUserinfo(it) }

    return ProxyConfig(scheme = scheme, host = host, port = port, auth = auth)
}

/**
 * Split authority into userinfo and hostport.
 */
public fun splitUserinfo(authority: String): Pair<String?, String> {
    val atIndex = authority.lastIndexOf('@')
    return if (atIndex != -1) {
        Pair(authority.substring(0, atIndex), authority.substring(atIndex + 1))
    } else {
        Pair(null, authority)
    }
}

/**
 * Parse host and port from a hostport token.
 */
public fun parseHostPort(
    hostport: String,
    scheme: ProxyScheme,
): Pair<String, Int> {
    val (hostRaw, portOpt) = splitHostPort(hostport)
    if (hostRaw.isEmpty()) {
        throw TungsteniteException.Url(UrlError.InvalidProxyConfig(hostport))
    }
    val host = normalizeHost(hostRaw)
    val port =
        portOpt ?: when (scheme) {
            ProxyScheme.Http -> 80
            ProxyScheme.Socks5, ProxyScheme.Socks5h -> 1080
        }
    return Pair(host, port)
}

/**
 * Read HTTP CONNECT response using a read function.
 */
public fun readConnectResponse(readFn: (ByteArray) -> Int): ByteArray {
    val buf = ArrayList<Byte>()
    val chunk = ByteArray(512)
    while (true) {
        if (buf.size >= MAX_CONNECT_RESPONSE_SIZE) {
            throw TungsteniteException.Url(UrlError.ProxyConnect("HTTP CONNECT response too large"))
        }
        val read = readFn(chunk)
        if (read <= 0) break
        for (i in 0 until read) {
            buf.add(chunk[i])
        }
        if (buf.size >= 4) {
            var found = false
            for (i in 0..buf.size - 4) {
                if (buf[i] == '\r'.code.toByte() &&
                    buf[i + 1] == '\n'.code.toByte() &&
                    buf[i + 2] == '\r'.code.toByte() &&
                    buf[i + 3] == '\n'.code.toByte()
                ) {
                    found = true
                    break
                }
            }
            if (found) break
        }
    }
    return buf.toByteArray()
}

/**
 * Perform HTTP CONNECT handshake over given read/write functions.
 */
public fun httpConnect(
    readFn: (ByteArray) -> Int,
    writeFn: (ByteArray) -> Unit,
    host: String,
    port: Int,
    auth: ProxyAuth? = null,
) {
    val authority = "$host:$port"
    val request = buildHttpConnectRequest(authority, auth)
    writeFn(request)

    val response = readConnectResponse(readFn)
    val status = parseHttpConnectResponse(response)
    if (status !in 200..299) {
        throw TungsteniteException.Url(
            UrlError.ProxyConnect("HTTP CONNECT failed with status $status"),
        )
    }
}

/**
 * Perform SOCKS5 handshake over given read/write functions.
 */
public fun socks5Handshake(
    readFn: (ByteArray) -> Int,
    writeFn: (ByteArray) -> Unit,
    host: String,
    port: Int,
    auth: ProxyAuth? = null,
) {
    val methods =
        if (auth != null) {
            byteArrayOf(0x05, 0x02, 0x00, 0x02)
        } else {
            byteArrayOf(0x05, 0x01, 0x00)
        }
    writeFn(methods)

    val choice = readExactBytes(readFn, 2)
    if (choice[0] != 0x05.toByte()) {
        throw TungsteniteException.Url(UrlError.ProxyConnect("SOCKS5: invalid response version"))
    }

    when (choice[1].toInt() and 0xFF) {
        0x00 -> {}
        0x02 -> {
            val authCreds =
                auth
                    ?: throw TungsteniteException.Url(
                        UrlError.ProxyConnect("SOCKS5: proxy requested auth, but none provided"),
                    )
            socks5UserpassAuth(readFn, writeFn, authCreds)
        }
        0xFF -> {
            throw TungsteniteException.Url(
                UrlError.ProxyConnect("SOCKS5: no acceptable authentication method"),
            )
        }
        else -> {
            throw TungsteniteException.Url(
                UrlError.ProxyConnect("SOCKS5: unsupported authentication method"),
            )
        }
    }

    sendSocks5Connect(readFn, writeFn, host, port)
}

/**
 * Perform SOCKS5 username/password auth.
 */
public fun socks5UserpassAuth(
    readFn: (ByteArray) -> Int,
    writeFn: (ByteArray) -> Unit,
    auth: ProxyAuth,
) {
    val username = auth.username.encodeToByteArray()
    val password = auth.password.encodeToByteArray()

    if (username.size > 255 || password.size > 255) {
        throw TungsteniteException.Url(UrlError.ProxyConnect("SOCKS5 auth credentials too long"))
    }

    val buf = ByteArray(3 + username.size + password.size)
    buf[0] = 0x01
    buf[1] = username.size.toByte()
    username.copyInto(buf, 2)
    buf[2 + username.size] = password.size.toByte()
    password.copyInto(buf, 3 + username.size)

    writeFn(buf)

    val response = readExactBytes(readFn, 2)
    if (response[0] != 0x01.toByte() || response[1] != 0x00.toByte()) {
        throw TungsteniteException.Url(UrlError.ProxyConnect("SOCKS5 authentication failed"))
    }
}

/**
 * Send SOCKS5 CONNECT command.
 */
public fun sendSocks5Connect(
    readFn: (ByteArray) -> Int,
    writeFn: (ByteArray) -> Unit,
    host: String,
    port: Int,
) {
    val hostBytes = host.encodeToByteArray()
    if (hostBytes.size > 255) {
        throw TungsteniteException.Url(UrlError.ProxyConnect("SOCKS5 domain name too long"))
    }

    val req = ByteArray(4 + 1 + hostBytes.size + 2)
    req[0] = 0x05
    req[1] = 0x01
    req[2] = 0x00
    req[3] = 0x03
    req[4] = hostBytes.size.toByte()
    hostBytes.copyInto(req, 5)
    val portOffset = 5 + hostBytes.size
    req[portOffset] = ((port ushr 8) and 0xFF).toByte()
    req[portOffset + 1] = (port and 0xFF).toByte()

    writeFn(req)

    val header = readExactBytes(readFn, 4)
    if (header[0] != 0x05.toByte()) {
        throw TungsteniteException.Url(UrlError.ProxyConnect("SOCKS5: invalid response version"))
    }
    if (header[1] != 0x00.toByte()) {
        throw TungsteniteException.Url(
            UrlError.ProxyConnect("SOCKS5: connection failed with code ${header[1]}"),
        )
    }

    val addrLen =
        when (header[3].toInt() and 0xFF) {
            0x01 -> 4
            0x03 -> {
                val lenByte = readExactBytes(readFn, 1)
                lenByte[0].toInt() and 0xFF
            }
            0x04 -> 16
            else -> throw TungsteniteException.Url(UrlError.ProxyConnect("SOCKS5: invalid address type"))
        }

    val discard = readExactBytes(readFn, addrLen + 2)
    discard.size // read completed
}

private fun readExactBytes(
    readFn: (ByteArray) -> Int,
    count: Int,
): ByteArray {
    val result = ByteArray(count)
    var readTotal = 0
    val single = ByteArray(1)
    while (readTotal < count) {
        val n = readFn(single)
        if (n <= 0) {
            throw TungsteniteException.Url(UrlError.ProxyConnect("Unexpected EOF during proxy handshake"))
        }
        result[readTotal++] = single[0]
    }
    return result
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
