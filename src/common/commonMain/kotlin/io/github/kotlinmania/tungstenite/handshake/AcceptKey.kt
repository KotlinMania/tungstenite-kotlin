package io.github.kotlinmania.tungstenite.handshake

import io.github.kotlinmania.sha1.Sha1
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

/**
 * Convert HTTP version string to canonical representation or validate it.
 */
public fun versionAsStr(ver: String): String =
    when (ver) {
        "HTTP/0.9" -> "HTTP/0.9"
        "HTTP/1.0" -> "HTTP/1.0"
        "HTTP/1.1" -> "HTTP/1.1"
        else -> throw io.github.kotlinmania.tungstenite.TungsteniteException.ProtocolViolation(
            io.github.kotlinmania.tungstenite.ProtocolError.WrongHttpVersion,
        )
    }

/**
 * Derive the `Sec-WebSocket-Accept` response header from a `Sec-WebSocket-Key` request header.
 *
 * This function can be used to perform a handshake before passing a raw stream.
 */
@OptIn(ExperimentalEncodingApi::class)
public fun deriveAcceptKey(requestKey: ByteArray): String {
    val wsGuid = "258EAFA5-E914-47DA-95CA-C5AB0DC85B11".encodeToByteArray()
    val combined = ByteArray(requestKey.size + wsGuid.size)
    requestKey.copyInto(combined, 0, 0, requestKey.size)
    wsGuid.copyInto(combined, requestKey.size, 0, wsGuid.size)
    val digest = Sha1.digest(combined)
    return Base64.Default.encode(digest)
}

/**
 * Derive the `Sec-WebSocket-Accept` response header from a `Sec-WebSocket-Key` string.
 */
public fun deriveAcceptKey(requestKey: String): String =
    deriveAcceptKey(requestKey.encodeToByteArray())
