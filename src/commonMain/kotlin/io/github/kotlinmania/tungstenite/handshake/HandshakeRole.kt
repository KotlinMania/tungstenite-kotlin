package io.github.kotlinmania.tungstenite.handshake

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
