// port-lint: source tungstenite/src/extensions/mod.rs
package io.github.kotlinmania.tungstenite.extensions

import io.github.kotlinmania.tungstenite.extensions.compression.PerMessageCompressionContext
import io.github.kotlinmania.tungstenite.extensions.compression.deflate.DeflateConfig
import io.github.kotlinmania.tungstenite.extensions.compression.deflate.DeflateContext
import io.github.kotlinmania.tungstenite.extensions.compression.deflate.DeflateParameterError
import io.github.kotlinmania.tungstenite.extensions.compression.deflate.EXTENSION_NAME
import io.github.kotlinmania.tungstenite.extensions.compression.deflate.PermessageDeflateConfig
import io.github.kotlinmania.tungstenite.extensions.headers.SecWebsocketExtensions
import io.github.kotlinmania.tungstenite.extensions.headers.WebsocketProtocolExtension
import io.github.kotlinmania.tungstenite.protocol.Role

/**
 * Container for configured extensions for a connection.
 */
public class Extensions(
    public var perMessageCompression: PerMessageCompressionContext? = null,
) {
    /**
     * Returns a function that, if present, compresses a message payload.
     */
    public fun perMessageCompressor(): ((ByteArray) -> ByteArray)? {
        val pmc = perMessageCompression ?: return null
        return { payload -> pmc.compress(payload) }
    }

    /**
     * Returns a function that, if present, decompresses a frame payload.
     */
    public fun perMessageDecompressor(): ((ByteArray, Boolean, Int) -> ByteArray)? {
        val pmc = perMessageCompression ?: return null
        return { payload, isFinal, sizeLimit -> pmc.decompress(payload, isFinal, sizeLimit) }
    }
}

/**
 * Configuration for extensions for a connection.
 */
public data class ExtensionsConfig(
    public var permessageDeflate: DeflateConfig? = null,
) {
    /**
     * Generate extension offer list for handshake requests.
     */
    public fun generateOffers(): List<WebsocketProtocolExtension> {
        val offers = ArrayList<WebsocketProtocolExtension>()
        val deflate = permessageDeflate
        if (deflate != null) {
            offers.add(deflate.asOffer().intoExtension())
        }
        return offers
    }

    /**
     * Checks that the given extensions are compatible with the given config.
     */
    public fun verifyAgreedOn(agreed: SecWebsocketExtensions): Extensions {
        var perMessageCompression: PerMessageCompressionContext? = null

        for (extension in agreed) {
            when (extension.name) {
                EXTENSION_NAME -> {
                    if (perMessageCompression != null) {
                        throw ExtensionsError.ExtensionConflict(EXTENSION_NAME)
                    }

                    val deflate =
                        permessageDeflate
                            ?: throw ExtensionsError.InvalidExtension(EXTENSION_NAME)

                    val parsedConfig =
                        try {
                            PermessageDeflateConfig.parseParams(extension.params)
                        } catch (_: DeflateParameterError) {
                            throw ExtensionsError.MalformedExtension(EXTENSION_NAME)
                        }

                    val negotiatedConfig =
                        try {
                            deflate.acceptResponse(parsedConfig)
                        } catch (e: Exception) {
                            throw ExtensionsError.InvalidExtension("$EXTENSION_NAME: ${e.message}")
                        }

                    perMessageCompression =
                        PerMessageCompressionContext.Deflate(
                            DeflateContext.new(Role.Client, negotiatedConfig),
                        )
                }
                else -> throw ExtensionsError.InvalidExtension(extension.name)
            }
        }

        return Extensions(perMessageCompression)
    }

    /**
     * Checks whether the given extension headers are compatible with the given config.
     */
    public fun acceptOffers(extensions: SecWebsocketExtensions): Pair<Extensions, SecWebsocketExtensions?> {
        var negotiatedPmc: Pair<PerMessageCompressionContext, WebsocketProtocolExtension>? = null

        for (extension in extensions) {
            when (extension.name) {
                EXTENSION_NAME -> {
                    val deflate = permessageDeflate ?: continue

                    val parsedConfig =
                        try {
                            PermessageDeflateConfig.parseParams(extension.params)
                        } catch (_: Exception) {
                            continue
                        }

                    if (negotiatedPmc != null) {
                        continue
                    }

                    val result = deflate.acceptOffer(parsedConfig)
                    if (result != null) {
                        val (config, response) = result
                        val context =
                            PerMessageCompressionContext.Deflate(
                                DeflateContext.new(Role.Server, config),
                            )
                        negotiatedPmc = Pair(context, response.intoExtension())
                    }
                }
                else -> {}
            }
        }

        val extensionsResult = Extensions(negotiatedPmc?.first)
        val responseHeaders = negotiatedPmc?.second?.let { SecWebsocketExtensions(listOf(it)) }

        return Pair(extensionsResult, responseHeaders)
    }

    /**
     * Bypasses negotiation of extension parameters and enables those that have been configured.
     */
    public fun intoUnnegotiatedContext(role: Role): Extensions {
        val deflate = permessageDeflate
        val perMessageCompression =
            deflate?.let {
                PerMessageCompressionContext.Deflate(DeflateContext.new(role, it))
            }
        return Extensions(perMessageCompression)
    }

    public companion object {
        public fun default(): ExtensionsConfig = ExtensionsConfig()
    }
}

/**
 * Error encountered while handling extensions.
 */
public sealed class ExtensionsError(
    message: String,
) : Exception(message) {
    /** The header included an invalid extension. */
    public class InvalidExtension(
        public val extension: String,
    ) : ExtensionsError("Extension header had invalid extension: $extension")

    /** The negotiation response included an extension more than once. */
    public class ExtensionConflict(
        public val extension: String,
    ) : ExtensionsError("Extension negotiation response had conflicting extension: $extension")

    /** The header included an unparsable extension. */
    public class MalformedExtension(
        public val extension: String,
    ) : ExtensionsError("Extension negotiation response had malformed extension: $extension")
}
