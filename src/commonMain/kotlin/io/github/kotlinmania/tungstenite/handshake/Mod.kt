// port-lint: source handshake/mod.rs
package io.github.kotlinmania.tungstenite.handshake

import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

/**
 * Stage processing result.
 */
public sealed class ProcessingResult<out Stream, out FinalResult> {
    public data class Continue<Stream>(
        public val machine: HandshakeMachine<Stream>,
    ) : ProcessingResult<Stream, Nothing>()

    public data class Done<FinalResult>(
        public val result: FinalResult,
    ) : ProcessingResult<Nothing, FinalResult>()
}

/**
 * Handshake role.
 */
public interface HandshakeRole<IncomingData, Stream, FinalResult> {
    public fun stageFinished(
        finish: StageResult<IncomingData, Stream>,
    ): Result<ProcessingResult<Stream, FinalResult>>
}

/**
 * A handshake error.
 */
public sealed class HandshakeError<out Role> {
    /** Handshake was interrupted (would block). */
    public data class Interrupted<Role>(
        public val midHandshake: MidHandshake<Role>,
    ) : HandshakeError<Role>()

    /** Handshake failed. */
    public data class Failure(
        public val error: Throwable,
    ) : HandshakeError<Nothing>()

    public fun fmt(): String =
        when (this) {
            is Interrupted -> "Interrupted handshake (WouldBlock)"
            is Failure -> error.message ?: error.toString()
        }

    public companion object {
        public fun from(err: Throwable): HandshakeError<Nothing> = Failure(err)
    }
}

/**
 * A WebSocket handshake in progress.
 */
public class MidHandshake<Role>(
    public val role: Role,
    public var machine: HandshakeMachine<*>,
) {
    /** Allow access to machine. */
    public fun getRef(): HandshakeMachine<*> = machine

    /** Allow mutable access to machine. */
    public fun getMut(): HandshakeMachine<*> = machine

    /** Restarts the handshake process. */
    public fun handshake(): Any? = null
}

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

internal object Sha1 {
    fun digest(data: ByteArray): ByteArray {
        var h0 = 0x67452301
        var h1 = 0xEFCDAB89.toInt()
        var h2 = 0x98BADCFE.toInt()
        var h3 = 0x10325476
        var h4 = 0xC3D2E1F0.toInt()

        val bitLen = data.size.toLong() * 8L
        val padLen = ((56 - ((data.size + 1) % 64) + 64) % 64)
        val padded = ByteArray(data.size + 1 + padLen + 8)
        data.copyInto(padded, 0, 0, data.size)
        padded[data.size] = 0x80.toByte()
        for (i in 0 until 8) {
            padded[padded.size - 8 + i] = ((bitLen ushr ((7 - i) * 8)) and 0xFF).toByte()
        }

        val w = IntArray(80)
        for (chunk in 0 until padded.size step 64) {
            for (i in 0 until 16) {
                val j = chunk + i * 4
                w[i] = ((padded[j].toInt() and 0xFF) shl 24) or
                    ((padded[j + 1].toInt() and 0xFF) shl 16) or
                    ((padded[j + 2].toInt() and 0xFF) shl 8) or
                    (padded[j + 3].toInt() and 0xFF)
            }
            for (i in 16 until 80) {
                val num = w[i - 3] xor w[i - 8] xor w[i - 14] xor w[i - 16]
                w[i] = (num shl 1) or (num ushr 31)
            }

            var a = h0
            var b = h1
            var c = h2
            var d = h3
            var e = h4

            for (i in 0 until 80) {
                val f: Int
                val k: Int
                when (i) {
                    in 0..19 -> {
                        f = (b and c) or ((b.inv()) and d)
                        k = 0x5A827999
                    }
                    in 20..39 -> {
                        f = b xor c xor d
                        k = 0x6ED9EBA1
                    }
                    in 40..59 -> {
                        f = (b and c) or (b and d) or (c and d)
                        k = 0x8F1BBCDC.toInt()
                    }
                    else -> {
                        f = b xor c xor d
                        k = 0xCA62C1D6.toInt()
                    }
                }
                val temp = ((a shl 5) or (a ushr 27)) + f + e + k + w[i]
                e = d
                d = c
                c = (b shl 30) or (b ushr 2)
                b = a
                a = temp
            }

            h0 += a
            h1 += b
            h2 += c
            h3 += d
            h4 += e
        }

        val out = ByteArray(20)

        fun putInt(v: Int, offset: Int) {
            out[offset] = (v ushr 24).toByte()
            out[offset + 1] = (v ushr 16).toByte()
            out[offset + 2] = (v ushr 8).toByte()
            out[offset + 3] = v.toByte()
        }
        putInt(h0, 0)
        putInt(h1, 4)
        putInt(h2, 8)
        putInt(h3, 12)
        putInt(h4, 16)
        return out
    }
}
