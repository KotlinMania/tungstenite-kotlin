// port-lint: source handshake/machine.rs
package io.github.kotlinmania.tungstenite.handshake

import io.github.kotlinmania.tungstenite.ReadBuffer
import io.github.kotlinmania.tungstenite.TungsteniteException

/**
 * The parseable object trait for handshake headers / data.
 */
public interface TryParse<T> {
    /**
     * Return Ok(null) if incomplete, Err on syntax error, or Ok(Pair(consumedBytes, parsedObject)).
     */
    public fun tryParse(data: ByteArray): Result<Pair<Int, T>?>
}

/**
 * Attack mitigation. Contains counters needed to prevent DoS attacks
 * and reject valid but useless headers.
 */
public class AttackCheck(
    public var numberOfPackets: Int = 0,
    public var numberOfBytes: Int = 0,
) {
    /**
     * Check the size of an incoming packet. To be called immediately after `read()`
     * passing its returned bytes count as `size`.
     */
    public fun checkIncomingPacketSize(size: Int): Result<Unit> {
        numberOfPackets += 1
        numberOfBytes += size

        if (numberOfBytes > MAX_BYTES) {
            return Result.failure(TungsteniteException.AttackAttempt())
        }
        if (numberOfPackets > MAX_PACKETS) {
            return Result.failure(TungsteniteException.AttackAttempt())
        }
        if (numberOfPackets > MIN_PACKET_CHECK_THRESHOLD &&
            numberOfPackets * MIN_PACKET_SIZE > numberOfBytes
        ) {
            return Result.failure(TungsteniteException.AttackAttempt())
        }
        return Result.success(Unit)
    }

    public companion object {
        public const val MAX_BYTES: Int = 65536
        public const val MAX_PACKETS: Int = 512
        public const val MIN_PACKET_SIZE: Int = 128
        public const val MIN_PACKET_CHECK_THRESHOLD: Int = 64
    }
}

/**
 * The result of a single handshake round.
 */
public sealed class RoundResult<out Obj, out Stream> {
    /** Round not done, I/O would block. */
    public data class WouldBlock<Stream>(
        public val machine: HandshakeMachine<Stream>,
    ) : RoundResult<Nothing, Stream>()

    /** Round done, state unchanged. */
    public data class Incomplete<Stream>(
        public val machine: HandshakeMachine<Stream>,
    ) : RoundResult<Nothing, Stream>()

    /** Stage complete. */
    public data class StageFinished<Obj, Stream>(
        public val result: StageResult<Obj, Stream>,
    ) : RoundResult<Obj, Stream>()
}

/**
 * The result of a stage.
 */
public sealed class StageResult<out Obj, out Stream> {
    /** Reading round finished. */
    public data class DoneReading<Obj, Stream>(
        public val result: Obj,
        public val stream: Stream,
        public val tail: ByteArray,
    ) : StageResult<Obj, Stream>() {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is DoneReading<*, *>) return false
            if (result != other.result) return false
            if (stream != other.stream) return false
            return tail.contentEquals(other.tail)
        }

        override fun hashCode(): Int {
            var res = result?.hashCode() ?: 0
            res = 31 * res + (stream?.hashCode() ?: 0)
            res = 31 * res + tail.contentHashCode()
            return res
        }
    }

    /** Writing round finished. */
    public data class DoneWriting<Stream>(
        public val stream: Stream,
    ) : StageResult<Nothing, Stream>()
}

/**
 * The handshake state.
 */
public sealed class HandshakeState {
    public data class Reading(
        val buffer: ReadBuffer,
        val attackCheck: AttackCheck,
    ) : HandshakeState()

    public data class Writing(
        val data: ByteArray,
        var position: Int = 0,
    ) : HandshakeState() {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is Writing) return false
            return position == other.position && data.contentEquals(other.data)
        }

        override fun hashCode(): Int = 31 * data.contentHashCode() + position
    }

    public object Flushing : HandshakeState()
}

/**
 * A generic handshake state machine.
 */
public class HandshakeMachine<Stream>(
    public val stream: Stream,
    public var state: HandshakeState,
) {
    public companion object {
        /**
         * Start reading data from the peer.
         */
        public fun <Stream> startRead(stream: Stream): HandshakeMachine<Stream> =
            HandshakeMachine(stream, HandshakeState.Reading(ReadBuffer.new(), AttackCheck()))

        /**
         * Start writing data to the peer.
         */
        public fun <Stream> startWrite(stream: Stream, data: ByteArray): HandshakeMachine<Stream> =
            HandshakeMachine(stream, HandshakeState.Writing(data.copyOf()))
    }
}
