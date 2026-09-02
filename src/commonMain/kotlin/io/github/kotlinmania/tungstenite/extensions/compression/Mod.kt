// port-lint: source extensions/compression/mod.rs
package io.github.kotlinmania.tungstenite.extensions.compression

import io.github.kotlinmania.tungstenite.extensions.compression.deflate.DeflateContext
import io.github.kotlinmania.tungstenite.extensions.compression.deflate.DeflateError

/**
 * Active context for performing per-message compression.
 */
public sealed class PerMessageCompressionContext {
    /**
     * Context for compressing/decompressing with `permessage-deflate`.
     */
    public data class Deflate(
        public val context: DeflateContext,
    ) : PerMessageCompressionContext()

    /**
     * Compress the given payload.
     */
    public fun compress(payload: ByteArray): ByteArray =
        when (this) {
            is Deflate -> context.compress(payload)
        }

    /**
     * Decompress the given payload.
     */
    public fun decompress(
        payload: ByteArray,
        isFinal: Boolean,
        sizeLimit: Int = Int.MAX_VALUE,
    ): ByteArray =
        when (this) {
            is Deflate -> context.decompress(payload, isFinal, sizeLimit)
        }
}

/**
 * Error encountered while compressing or decompressing.
 */
public sealed class CompressionError(
    message: String,
    cause: Throwable? = null,
) : Exception(message, cause) {
    /** Error encountered while deflating or inflating. */
    public class Deflate(
        public val deflate: DeflateError,
    ) : CompressionError("Deflate error: $deflate", deflate)
}

/**
 * Error encountered while decompressing.
 */
public sealed class DecompressionError(
    message: String,
    cause: Throwable? = null,
) : Exception(message, cause) {
    /** The decompressed frame is larger than the configured limit. */
    public object SizeLimitReached : DecompressionError("decompressed data is too large")

    /** An error was encountered while decompressing. */
    public class Decompression(
        public val error: Any?,
    ) : DecompressionError("$error", error as? Throwable)
}
