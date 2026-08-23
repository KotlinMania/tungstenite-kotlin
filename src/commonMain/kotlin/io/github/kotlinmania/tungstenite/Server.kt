// port-lint: source server.rs
package io.github.kotlinmania.tungstenite

import io.github.kotlinmania.tungstenite.handshake.Callback
import io.github.kotlinmania.tungstenite.handshake.MidHandshake
import io.github.kotlinmania.tungstenite.handshake.NoCallback
import io.github.kotlinmania.tungstenite.handshake.ServerHandshake
import io.github.kotlinmania.tungstenite.protocol.WebSocketConfig

/**
 * Accept the given Stream as a WebSocket with default configuration.
 */
public fun <Stream> accept(
    stream: Stream,
): MidHandshake<ServerHandshake<Stream, NoCallback>> =
    acceptWithConfig(stream, null)

/**
 * Accept the given Stream as a WebSocket with custom configuration.
 */
public fun <Stream> acceptWithConfig(
    stream: Stream,
    config: WebSocketConfig? = null,
): MidHandshake<ServerHandshake<Stream, NoCallback>> =
    acceptHdrWithConfig(stream, NoCallback(), config)

/**
 * Accept the given Stream as a WebSocket with a custom callback for header inspection.
 */
public fun <Stream, C : Callback> acceptHdr(
    stream: Stream,
    callback: C,
): MidHandshake<ServerHandshake<Stream, C>> =
    acceptHdrWithConfig(stream, callback, null)

/**
 * Accept the given Stream as a WebSocket with a custom callback and configuration.
 */
public fun <Stream, C : Callback> acceptHdrWithConfig(
    stream: Stream,
    callback: C,
    config: WebSocketConfig? = null,
): MidHandshake<ServerHandshake<Stream, C>> =
    ServerHandshake.start(stream, callback, config)
