// port-lint: source extensions/compression/deflate/config.rs
package io.github.kotlinmania.tungstenite.extensions.compression.deflate

import io.github.kotlinmania.tungstenite.extensions.headers.WebsocketExtensionParam
import io.github.kotlinmania.tungstenite.extensions.headers.WebsocketProtocolExtension
import io.github.kotlinmania.tungstenite.protocol.Role

public const val PER_MESSAGE_DEFLATE: String = "permessage-deflate"
public const val SERVER_NO_CONTEXT_TAKEOVER: String = "server_no_context_takeover"
public const val CLIENT_NO_CONTEXT_TAKEOVER: String = "client_no_context_takeover"
public const val SERVER_MAX_WINDOW_BITS: String = "server_max_window_bits"
public const val CLIENT_MAX_WINDOW_BITS: String = "client_max_window_bits"

public val SUPPORTED_WINDOW_BITS: IntRange = 9..15

/**
 * Errors from permessage-deflate extension negotiation.
 */
public sealed class NegotiationError(
    message: String,
) : Exception(message) {
    public class InvalidServerMaxWindowBitsValue(
        val value: Int,
    ) : NegotiationError("Invalid $SERVER_MAX_WINDOW_BITS value in a negotiation response: $value")

    public class MissingServerMaxWindowBitsValue : NegotiationError("Missing $SERVER_MAX_WINDOW_BITS value in a negotiation response")

    public class MissingServerNoContextTakeover : NegotiationError("Missing $SERVER_NO_CONTEXT_TAKEOVER value in a negotiation response")

    public class UnsupportedServerMaxWindowBitsValue(
        val value: Int,
    ) : NegotiationError("Unsupported $SERVER_MAX_WINDOW_BITS value: $value")

    public class UnsupportedClientMaxWindowBitsValue(
        val value: Int,
    ) : NegotiationError("Unsupported $CLIENT_MAX_WINDOW_BITS value: $value")
}

/**
 * Errors from parsing a single parameter in a permessage-deflate extension directive.
 */
public sealed class ParameterError(
    message: String,
) : Exception(message) {
    public class UnknownParameter(
        val name: String,
    ) : ParameterError("Unknown parameter in a negotiation response: $name")

    public class DuplicateParameter(
        val name: String,
    ) : ParameterError("Duplicate parameter in a negotiation response: $name")

    public class InvalidParameterValue(
        val name: String,
        val value: String,
    ) : ParameterError("Invalid value $value for parameter $name")
}

/**
 * Contents of a permessage-deflate Per-Message Compression Extension.
 */
public data class PermessageDeflateConfig(
    public var serverNoContextTakeover: Boolean = false,
    public var clientNoContextTakeover: Boolean = false,
    public var serverMaxWindowBits: Int? = null,
    public var clientMaxWindowBits: Int? = null,
    public var compressionLevel: Int = 6,
) {
    /**
     * Set serverNoContextTakeover.
     */
    public fun serverNoContextTakeover(enable: Boolean): PermessageDeflateConfig =
        apply {
            this.serverNoContextTakeover = enable
        }

    /**
     * Set clientNoContextTakeover.
     */
    public fun clientNoContextTakeover(enable: Boolean): PermessageDeflateConfig =
        apply {
            this.clientNoContextTakeover = enable
        }

    /**
     * Set serverMaxWindowBits.
     */
    public fun serverMaxWindowBits(bits: Int?): PermessageDeflateConfig =
        apply {
            this.serverMaxWindowBits = bits
        }

    /**
     * Set clientMaxWindowBits.
     */
    public fun clientMaxWindowBits(bits: Int?): PermessageDeflateConfig =
        apply {
            this.clientMaxWindowBits = bits
        }

    /**
     * Turn config into WebsocketProtocolExtension.
     */
    public fun intoExtension(): WebsocketProtocolExtension {
        val params = ArrayList<WebsocketExtensionParam>()
        if (serverNoContextTakeover) {
            params.add(WebsocketExtensionParam(SERVER_NO_CONTEXT_TAKEOVER))
        }
        if (clientNoContextTakeover) {
            params.add(WebsocketExtensionParam(CLIENT_NO_CONTEXT_TAKEOVER))
        }
        if (serverMaxWindowBits != null) {
            params.add(WebsocketExtensionParam(SERVER_MAX_WINDOW_BITS, serverMaxWindowBits.toString()))
        }
        if (clientMaxWindowBits != null) {
            params.add(WebsocketExtensionParam(CLIENT_MAX_WINDOW_BITS, clientMaxWindowBits.toString()))
        }
        return WebsocketProtocolExtension(PER_MESSAGE_DEFLATE, params)
    }

    public companion object {
        public fun default(): PermessageDeflateConfig = PermessageDeflateConfig()

        public fun parseParams(
            params: List<WebsocketExtensionParam>,
            role: Role,
        ): PermessageDeflateConfig {
            val config = PermessageDeflateConfig()
            for (param in params) {
                when (param.name) {
                    SERVER_NO_CONTEXT_TAKEOVER -> {
                        config.serverNoContextTakeover = true
                    }
                    CLIENT_NO_CONTEXT_TAKEOVER -> {
                        config.clientNoContextTakeover = true
                    }
                    SERVER_MAX_WINDOW_BITS -> {
                        val bits =
                            param.value?.toIntOrNull()
                                ?: throw ParameterError.InvalidParameterValue(SERVER_MAX_WINDOW_BITS, param.value ?: "")
                        if (bits !in SUPPORTED_WINDOW_BITS) {
                            throw NegotiationError.UnsupportedServerMaxWindowBitsValue(bits)
                        }
                        config.serverMaxWindowBits = bits
                    }
                    CLIENT_MAX_WINDOW_BITS -> {
                        val bits = param.value?.toIntOrNull()
                        if (bits != null && bits !in SUPPORTED_WINDOW_BITS) {
                            throw NegotiationError.UnsupportedClientMaxWindowBitsValue(bits)
                        }
                        config.clientMaxWindowBits = bits
                    }
                    else -> throw ParameterError.UnknownParameter(param.name)
                }
            }
            return config
        }
    }
}
