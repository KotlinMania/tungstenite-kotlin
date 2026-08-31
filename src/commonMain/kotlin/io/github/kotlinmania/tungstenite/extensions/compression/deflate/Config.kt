// port-lint: source tungstenite/src/extensions/compression/deflate/config.rs
package io.github.kotlinmania.tungstenite.extensions.compression.deflate

import io.github.kotlinmania.tungstenite.extensions.headers.WebsocketExtensionParam
import io.github.kotlinmania.tungstenite.extensions.headers.WebsocketProtocolExtension
import io.github.kotlinmania.tungstenite.protocol.Role

public const val PER_MESSAGE_DEFLATE: String = "permessage-deflate"
public const val SERVER_NO_CONTEXT_TAKEOVER: String = "server_no_context_takeover"
public const val CLIENT_NO_CONTEXT_TAKEOVER: String = "client_no_context_takeover"
public const val SERVER_MAX_WINDOW_BITS: String = "server_max_window_bits"
public const val CLIENT_MAX_WINDOW_BITS: String = "client_max_window_bits"

public val ALLOWED_WINDOW_BITS: IntRange = 8..15
public val SUPPORTED_WINDOW_BITS: IntRange = 9..15

/**
 * Errors from permessage-deflate extension negotiation.
 */
public sealed class NegotiationError(
    message: String,
) : Exception(message) {
    public class InvalidServerMaxWindowBitsValue(
        public val value: Int,
    ) : NegotiationError("Invalid $SERVER_MAX_WINDOW_BITS value in a negotiation response: $value")

    public class MissingServerMaxWindowBitsValue : NegotiationError("Missing $SERVER_MAX_WINDOW_BITS value in a negotiation response")

    public class MissingServerNoContextTakeover : NegotiationError("Missing $SERVER_NO_CONTEXT_TAKEOVER value in a negotiation response")

    public class UnsupportedServerMaxWindowBitsValue(
        public val value: Int,
    ) : NegotiationError("Unsupported $SERVER_MAX_WINDOW_BITS value: $value")

    public class UnsupportedClientMaxWindowBitsValue(
        public val value: Int,
    ) : NegotiationError("Unsupported $CLIENT_MAX_WINDOW_BITS value: $value")
}

/**
 * Errors from parsing a single parameter in a permessage-deflate extension directive.
 */
public sealed class ParameterError(
    message: String,
) : Exception(message) {
    public class UnknownParameter(
        public val name: String,
    ) : ParameterError("Unknown parameter in a negotiation response: $name")

    public class DuplicateParameter(
        public val name: String,
    ) : ParameterError("Duplicate parameter in a negotiation response: $name")

    public class InvalidParameterValue(
        public val name: String,
        public val value: String,
    ) : ParameterError("Invalid value $value for parameter $name")
}

/**
 * Error type returned by [DeflateConfig.setMaxWindowBits].
 */
public class DeflateInvalidMaxWindowBits : Exception("this implementation supports max window bits in $SUPPORTED_WINDOW_BITS")

/**
 * Contents of a permessage-deflate Per-Message Compression Extension.
 */
public data class PermessageDeflateConfig(
    public var serverNoContextTakeover: Boolean = false,
    public var clientNoContextTakeover: Boolean = false,
    public var serverMaxWindowBits: Int? = null,
    public var clientMaxWindowBits: Int? = null,
    public var clientMaxWindowBitsPresent: Boolean = clientMaxWindowBits != null,
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
            this.clientMaxWindowBitsPresent = true
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
        if (clientMaxWindowBitsPresent || clientMaxWindowBits != null) {
            if (clientMaxWindowBits != null) {
                params.add(WebsocketExtensionParam(CLIENT_MAX_WINDOW_BITS, clientMaxWindowBits.toString()))
            } else {
                params.add(WebsocketExtensionParam(CLIENT_MAX_WINDOW_BITS))
            }
        }
        return WebsocketProtocolExtension(PER_MESSAGE_DEFLATE, params)
    }

    /**
     * Turn config into offer WebsocketProtocolExtension.
     */
    public fun asOffer(): WebsocketProtocolExtension = intoExtension()

    public companion object {
        public fun new(): PermessageDeflateConfig = PermessageDeflateConfig()

        public fun default(): PermessageDeflateConfig = PermessageDeflateConfig()

        public fun isValidWindowBits(bits: Int): Boolean = bits in ALLOWED_WINDOW_BITS

        public fun fromStr(s: String): PermessageDeflateConfig =
            parseParams(WebsocketProtocolExtension.parse(s).params)

        public fun parseParams(params: List<WebsocketExtensionParam>): PermessageDeflateConfig {
            val config = PermessageDeflateConfig()
            val seen = HashSet<String>()
            for (param in params) {
                if (!seen.add(param.name)) {
                    throw ParameterError.DuplicateParameter(param.name)
                }
                when (param.name) {
                    SERVER_NO_CONTEXT_TAKEOVER -> {
                        if (param.value != null) {
                            throw ParameterError.InvalidParameterValue(SERVER_NO_CONTEXT_TAKEOVER, param.value)
                        }
                        config.serverNoContextTakeover = true
                    }
                    CLIENT_NO_CONTEXT_TAKEOVER -> {
                        if (param.value != null) {
                            throw ParameterError.InvalidParameterValue(CLIENT_NO_CONTEXT_TAKEOVER, param.value)
                        }
                        config.clientNoContextTakeover = true
                    }
                    SERVER_MAX_WINDOW_BITS -> {
                        val value = param.value ?: throw ParameterError.InvalidParameterValue(SERVER_MAX_WINDOW_BITS, "")
                        val bits = value.toIntOrNull() ?: throw ParameterError.InvalidParameterValue(SERVER_MAX_WINDOW_BITS, value)
                        if (bits !in ALLOWED_WINDOW_BITS) {
                            throw ParameterError.InvalidParameterValue(SERVER_MAX_WINDOW_BITS, value)
                        }
                        config.serverMaxWindowBits = bits
                    }
                    CLIENT_MAX_WINDOW_BITS -> {
                        config.clientMaxWindowBitsPresent = true
                        if (param.value != null) {
                            val bits =
                                param.value.toIntOrNull()
                                    ?: throw ParameterError.InvalidParameterValue(CLIENT_MAX_WINDOW_BITS, param.value)
                            if (bits !in ALLOWED_WINDOW_BITS) {
                                throw ParameterError.InvalidParameterValue(CLIENT_MAX_WINDOW_BITS, param.value)
                            }
                            config.clientMaxWindowBits = bits
                        } else {
                            config.clientMaxWindowBits = null
                        }
                    }
                    else -> throw ParameterError.UnknownParameter(param.name)
                }
            }
            return config
        }

        public fun parseParams(
            params: List<WebsocketExtensionParam>,
            @Suppress("UNUSED_PARAMETER") role: Role,
        ): PermessageDeflateConfig = parseParams(params)
    }
}

/**
 * Client/server configuration for permessage-deflate support.
 */
public data class DeflateConfig(
    public var compressionLevel: Int = 6,
    public var serverNoContextTakeover: Boolean = false,
    public var clientNoContextTakeover: Boolean = false,
    public var serverMaxWindowBits: Int = 15,
    public var clientMaxWindowBits: Int = 15,
) {
    /**
     * Limits the maximum number of window bits used by a peer during compression.
     */
    public fun setMaxWindowBits(role: Role, bits: Int): DeflateConfig =
        apply {
            if (bits !in SUPPORTED_WINDOW_BITS) {
                throw DeflateInvalidMaxWindowBits()
            }
            when (role) {
                Role.Server -> serverMaxWindowBits = bits
                Role.Client -> clientMaxWindowBits = bits
            }
        }

    /**
     * Sets server_no_context_takeover or client_no_context_takeover.
     */
    public fun setNoContextTakeover(role: Role, noContextTakeover: Boolean): DeflateConfig =
        apply {
            when (role) {
                Role.Server -> serverNoContextTakeover = noContextTakeover
                Role.Client -> clientNoContextTakeover = noContextTakeover
            }
        }

    /**
     * Produces a [PermessageDeflateConfig] to send as a client offer to a server.
     */
    public fun asOffer(): PermessageDeflateConfig {
        val serverMax = if (serverMaxWindowBits != 15) serverMaxWindowBits else null
        val clientMax = if (clientMaxWindowBits != 15) clientMaxWindowBits else null
        return PermessageDeflateConfig(
            serverNoContextTakeover = serverNoContextTakeover,
            clientNoContextTakeover = clientNoContextTakeover,
            serverMaxWindowBits = serverMax,
            clientMaxWindowBits = clientMax,
            clientMaxWindowBitsPresent = true,
            compressionLevel = compressionLevel,
        )
    }

    /**
     * Receives a negotiation offer from the client and computes the agreed-upon parameters.
     */
    public fun acceptOffer(client: PermessageDeflateConfig): Pair<DeflateConfig, PermessageDeflateConfig>? {
        val serverNoCtx = serverNoContextTakeover || client.serverNoContextTakeover
        val clientNoCtx = clientNoContextTakeover || client.clientNoContextTakeover

        val (serverMax, responseServerMax) =
            when (val requestedMax = client.serverMaxWindowBits) {
                null -> Pair(serverMaxWindowBits, null)
                else -> {
                    if (requestedMax !in SUPPORTED_WINDOW_BITS) {
                        return null
                    }
                    val bits = minOf(requestedMax, serverMaxWindowBits)
                    Pair(bits, bits)
                }
            }

        val clientMax =
            if (!client.clientMaxWindowBitsPresent) {
                if (clientMaxWindowBits != 15) {
                    return null
                }
                clientMaxWindowBits
            } else if (client.clientMaxWindowBits == null) {
                clientMaxWindowBits
            } else {
                val req = client.clientMaxWindowBits!!
                if (req !in SUPPORTED_WINDOW_BITS) {
                    return null
                }
                minOf(req, clientMaxWindowBits)
            }

        val connConfig =
            DeflateConfig(
                compressionLevel = compressionLevel,
                serverNoContextTakeover = serverNoCtx,
                clientNoContextTakeover = clientNoCtx,
                serverMaxWindowBits = serverMax,
                clientMaxWindowBits = clientMax,
            )

        val offerResp =
            PermessageDeflateConfig(
                serverNoContextTakeover = serverNoCtx,
                clientNoContextTakeover = clientNoCtx,
                serverMaxWindowBits = responseServerMax,
                clientMaxWindowBits = if (clientMax != 15) clientMax else null,
                clientMaxWindowBitsPresent = clientMax != 15,
                compressionLevel = compressionLevel,
            )

        return Pair(connConfig, offerResp)
    }

    /**
     * Receives a response from the server and checks it against the requested context.
     */
    public fun acceptResponse(server: PermessageDeflateConfig): DeflateConfig {
        val serverNoCtx =
            if (serverNoContextTakeover && !server.serverNoContextTakeover) {
                throw NegotiationError.MissingServerNoContextTakeover()
            } else {
                server.serverNoContextTakeover
            }

        val clientNoCtx = clientNoContextTakeover || server.clientNoContextTakeover

        val serverMax =
            run {
                val defaultServerMaxBits = if (serverMaxWindowBits == 15) 15 else null
                val received =
                    server.serverMaxWindowBits
                        ?: defaultServerMaxBits
                        ?: throw NegotiationError.MissingServerMaxWindowBitsValue()

                if (received > serverMaxWindowBits) {
                    throw NegotiationError.InvalidServerMaxWindowBitsValue(received)
                }
                if (received !in SUPPORTED_WINDOW_BITS) {
                    throw NegotiationError.UnsupportedServerMaxWindowBitsValue(received)
                }
                received
            }

        val clientMax =
            if (!server.clientMaxWindowBitsPresent) {
                if (clientMaxWindowBits != 15) {
                    throw NegotiationError.UnsupportedClientMaxWindowBitsValue(clientMaxWindowBits)
                }
                clientMaxWindowBits
            } else if (server.clientMaxWindowBits == null) {
                clientMaxWindowBits
            } else {
                val received = server.clientMaxWindowBits!!
                if (received > clientMaxWindowBits) {
                    throw NegotiationError.UnsupportedClientMaxWindowBitsValue(received)
                }
                if (received !in SUPPORTED_WINDOW_BITS) {
                    throw NegotiationError.UnsupportedClientMaxWindowBitsValue(received)
                }
                received
            }

        return DeflateConfig(
            compressionLevel = compressionLevel,
            serverNoContextTakeover = serverNoCtx,
            clientNoContextTakeover = clientNoCtx,
            serverMaxWindowBits = serverMax,
            clientMaxWindowBits = clientMax,
        )
    }

    public fun asExtension(): WebsocketProtocolExtension = asOffer().intoExtension()

    public companion object {
        public fun new(): DeflateConfig = DeflateConfig()

        public fun default(): DeflateConfig = new()
    }
}
