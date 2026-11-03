// port-lint: source stream.rs
package io.github.kotlinmania.tungstenite

/** Stream mode, either plain TCP or TLS. */
public enum class Mode {
    /** Plain mode (`ws://` URL). */
    Plain,

    /** TLS mode (`wss://` URL). */
    Tls,
}

/** Trait / interface to switch TCP_NODELAY. */
public interface NoDelay {
    /** Set the TCP_NODELAY option to the given value. */
    public fun setNoDelay(nodelay: Boolean)
}

/**
 * A stream that might be protected with TLS.
 */
public sealed class MaybeTlsStream<S> : NoDelay {
    /** Unencrypted socket stream. */
    public data class Plain<S>(
        public val stream: S,
    ) : MaybeTlsStream<S>()

    /** Encrypted socket stream using native TLS. */
    public data class NativeTls<S>(
        public val stream: S,
    ) : MaybeTlsStream<S>()

    /** Encrypted socket stream using rustls. */
    public data class Rustls<S>(
        public val stream: S,
    ) : MaybeTlsStream<S>()

    /** Get the underlying stream. */
    public fun get(): S =
        when (this) {
            is Plain -> stream
            is NativeTls -> stream
            is Rustls -> stream
        }

    override fun setNoDelay(nodelay: Boolean) {
        val s = get()
        if (s is NoDelay) {
            s.setNoDelay(nodelay)
        }
    }
}

/**
 * Debug representation of a Rustls stream.
 */
public data class RustlsStreamDebug<S>(
    public val stream: S,
)
