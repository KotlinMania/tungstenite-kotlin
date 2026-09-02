// port-lint: source buffer.rs
package io.github.kotlinmania.tungstenite

import io.github.kotlinmania.bytes.buf.Buf

/**
 * A buffer for reading data from the network.
 *
 * The `ReadBuffer` is a buffer of bytes similar to a first-in, first-out queue.
 * It is filled by reading from a stream supporting read operations and is then
 * accessible as a cursor for reading bytes.
 */
public class ReadBuffer(
    public val chunkSize: Int = DEFAULT_CHUNK_SIZE,
    initialData: ByteArray = ByteArray(0),
) : Buf {
    private var storage: ByteArray = initialData.copyOf()
    private var position: Int = 0
    private val chunkBuffer: ByteArray = ByteArray(chunkSize)

    /**
     * Consume the `ReadBuffer` and get the internal storage.
     */
    public fun intoByteArray(): ByteArray {
        cleanUp()
        val result = storage
        storage = ByteArray(0)
        position = 0
        return result
    }

    /**
     * Consume the `ReadBuffer` and get the internal storage (alias for [intoByteArray]).
     */
    public fun intoVec(): ByteArray = intoByteArray()

    /**
     * Read next portion of data using the given read callback.
     *
     * The callback is passed the internal chunk buffer and returns the number
     * of bytes read into it.
     */
    public fun readFrom(readFn: (ByteArray) -> Int): Int {
        cleanUp()
        val size = readFn(chunkBuffer)
        if (size > 0) {
            val oldSize = storage.size
            val newStorage = ByteArray(oldSize + size)
            storage.copyInto(newStorage, 0, 0, oldSize)
            chunkBuffer.copyInto(newStorage, oldSize, 0, size)
            storage = newStorage
        }
        return size
    }

    /**
     * Read next portion of data from the given [source] ByteArray.
     */
    public fun readFrom(source: ByteArray, offset: Int = 0, length: Int = source.size - offset): Int {
        cleanUp()
        val toRead = minOf(chunkSize, length)
        if (toRead > 0) {
            val oldSize = storage.size
            val newStorage = ByteArray(oldSize + toRead)
            storage.copyInto(newStorage, 0, 0, oldSize)
            source.copyInto(newStorage, oldSize, offset, offset + toRead)
            storage = newStorage
        }
        return toRead
    }

    /**
     * Cleans up the part of the buffer that has been already read by the cursor.
     */
    private fun cleanUp() {
        if (position > 0) {
            val remainingLen = storage.size - position
            val newStorage = ByteArray(remainingLen)
            storage.copyInto(newStorage, 0, position, storage.size)
            storage = newStorage
            position = 0
        }
    }

    override fun remaining(): Int = storage.size - position

    override fun chunk(): ByteArray {
        if (remaining() == 0) return ByteArray(0)
        return storage.copyOfRange(position, storage.size)
    }

    override fun advance(cnt: Int) {
        if (cnt < 0 || position + cnt > storage.size) {
            throw IllegalArgumentException("cannot advance $cnt bytes, only ${remaining()} remaining")
        }
        position += cnt
    }

    /**
     * Get a cursor / slice to the remaining data storage.
     */
    public fun asCursor(): ByteArray = chunk()

    /**
     * Get a mutable view / slice to the data storage.
     */
    public fun asCursorMut(): ByteArray = chunk()

    override fun toString(): String =
        "ReadBuffer(chunkSize=$chunkSize, remaining=${remaining()}, position=$position)"

    public companion object {
        public const val DEFAULT_CHUNK_SIZE: Int = 4096

        /**
         * Create a default empty input buffer.
         */
        public fun default(): ReadBuffer = new()

        /**
         * Create a new empty input buffer.
         */
        public fun new(chunkSize: Int = DEFAULT_CHUNK_SIZE): ReadBuffer =
            ReadBuffer(chunkSize)

        /**
         * Create a new empty input buffer with a given `capacity`.
         */
        public fun withCapacity(capacity: Int, chunkSize: Int = DEFAULT_CHUNK_SIZE): ReadBuffer =
            ReadBuffer(chunkSize, initialData = ByteArray(0))

        /**
         * Create an input buffer filled with previously read data.
         */
        public fun fromPartiallyRead(part: ByteArray, chunkSize: Int = DEFAULT_CHUNK_SIZE): ReadBuffer =
            ReadBuffer(chunkSize, initialData = part)
    }
}
