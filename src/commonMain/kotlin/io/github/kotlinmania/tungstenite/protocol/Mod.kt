// port-lint: source protocol/mod.rs
package io.github.kotlinmania.tungstenite.protocol

import io.github.kotlinmania.tungstenite.TungsteniteException

/** Indicates a Client or Server role of the websocket. */
public enum class Role {
    /** This socket is a server. */
    Server,

    /** This socket is a client. */
    Client,
}

/** The state of processing, either "active", "closing", or "terminated". */
public enum class WebSocketState {
    Active,
    ClosedByUs,
    ClosedByPeer,
    CloseAcknowledged,
    Terminated,
    ;

    public fun isTerminated(): Boolean = this == Terminated

    public fun canRead(): Boolean =
        this == Active || this == ClosedByUs

    public fun isActive(): Boolean = this == Active

    public fun checkNotTerminated() {
        if (isTerminated()) {
            throw TungsteniteException.AlreadyClosed()
        }
    }
}

/** The configuration for WebSocket connection. */
public data class WebSocketConfig(
    public var readBufferSize: Int = 128 * 1024,
    public var writeBufferSize: Int = 128 * 1024,
    public var maxWriteBufferSize: Int = Int.MAX_VALUE,
    public var maxMessageSize: Long? = 64L shl 20,
    public var maxFrameSize: Long? = 16L shl 20,
    public var acceptUnmaskedFrames: Boolean = false,
) {
    public fun readBufferSize(size: Int): WebSocketConfig = apply { this.readBufferSize = size }

    public fun writeBufferSize(size: Int): WebSocketConfig = apply { this.writeBufferSize = size }

    public fun maxWriteBufferSize(size: Int): WebSocketConfig = apply { this.maxWriteBufferSize = size }

    public fun maxMessageSize(size: Long?): WebSocketConfig = apply { this.maxMessageSize = size }

    public fun maxFrameSize(size: Long?): WebSocketConfig = apply { this.maxFrameSize = size }

    public fun acceptUnmaskedFrames(accept: Boolean): WebSocketConfig = apply { this.acceptUnmaskedFrames = accept }

    public fun assertValid() {
        require(maxWriteBufferSize > writeBufferSize) {
            "WebSocketConfig: maxWriteBufferSize must be greater than writeBufferSize"
        }
    }

    public companion object {
        public fun default(): WebSocketConfig = WebSocketConfig()
    }
}
