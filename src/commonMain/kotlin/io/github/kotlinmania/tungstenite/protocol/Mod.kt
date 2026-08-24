// port-lint: source protocol/mod.rs
package io.github.kotlinmania.tungstenite.protocol

import io.github.kotlinmania.tungstenite.CapacityError
import io.github.kotlinmania.tungstenite.ProtocolError
import io.github.kotlinmania.tungstenite.TungsteniteException
import io.github.kotlinmania.tungstenite.extensions.Extensions
import io.github.kotlinmania.tungstenite.extensions.ExtensionsConfig
import io.github.kotlinmania.tungstenite.protocol.frame.CloseFrame
import io.github.kotlinmania.tungstenite.protocol.frame.Frame
import io.github.kotlinmania.tungstenite.protocol.frame.FrameCodec
import io.github.kotlinmania.tungstenite.protocol.frame.OpCode
import io.github.kotlinmania.tungstenite.protocol.frame.Control as OpCtl
import io.github.kotlinmania.tungstenite.protocol.frame.Data as OpData

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
    public var extensions: ExtensionsConfig? = null,
) {
    public fun readBufferSize(size: Int): WebSocketConfig = apply { this.readBufferSize = size }

    public fun writeBufferSize(size: Int): WebSocketConfig = apply { this.writeBufferSize = size }

    public fun maxWriteBufferSize(size: Int): WebSocketConfig = apply { this.maxWriteBufferSize = size }

    public fun maxMessageSize(size: Long?): WebSocketConfig = apply { this.maxMessageSize = size }

    public fun maxFrameSize(size: Long?): WebSocketConfig = apply { this.maxFrameSize = size }

    public fun acceptUnmaskedFrames(accept: Boolean): WebSocketConfig = apply { this.acceptUnmaskedFrames = accept }

    public fun extensions(extensions: ExtensionsConfig?): WebSocketConfig = apply { this.extensions = extensions }

    public fun assertValid() {
        require(maxWriteBufferSize > writeBufferSize) {
            "WebSocketConfig: maxWriteBufferSize must be greater than writeBufferSize"
        }
    }

    public companion object {
        public fun default(): WebSocketConfig = WebSocketConfig()
    }
}

/**
 * WebSocket input-output stream manager.
 */
public class WebSocket<Stream>(
    public var socket: Stream,
    public val context: WebSocketContext,
) {
    public fun intoInner(): Stream = socket

    public fun getRef(): Stream = socket

    public fun getMut(): Stream = socket

    public fun setConfig(setFunc: (WebSocketConfig) -> Unit) {
        context.setConfig(setFunc)
    }

    public val config: WebSocketConfig
        get() = context.config

    public fun canRead(): Boolean = context.canRead()

    public fun canWrite(): Boolean = context.canWrite()

    public fun read(
        readFn: (ByteArray) -> Int,
        writeFn: ((ByteArray, Int, Int) -> Int)? = null,
        flushFn: (() -> Unit)? = null,
    ): Message? =
        context.read(readFn, writeFn, flushFn)

    public fun send(writeFn: (ByteArray, Int, Int) -> Int, flushFn: () -> Unit, message: Message) {
        context.write(writeFn, message)
        context.flush(writeFn, flushFn)
    }

    public fun write(writeFn: (ByteArray, Int, Int) -> Int, message: Message) {
        context.write(writeFn, message)
    }

    public fun flush(writeFn: (ByteArray, Int, Int) -> Int, flushFn: () -> Unit) {
        context.flush(writeFn, flushFn)
    }

    public fun close(writeFn: (ByteArray, Int, Int) -> Int, flushFn: () -> Unit, code: CloseFrame? = null) {
        context.close(writeFn, flushFn, code)
    }

    public companion object {
        public fun <Stream> fromRawSocket(
            stream: Stream,
            role: Role,
            config: WebSocketConfig? = null,
        ): WebSocket<Stream> =
            WebSocket(stream, WebSocketContext.new(role, config))

        public fun <Stream> fromPartiallyRead(
            stream: Stream,
            part: ByteArray,
            role: Role,
            config: WebSocketConfig? = null,
        ): WebSocket<Stream> =
            WebSocket(stream, WebSocketContext.fromPartiallyRead(part, role, config))
    }
}

/**
 * Context managing WebSocket state, frame coding, and control replies.
 */
public class WebSocketContext(
    public val role: Role,
    public var config: WebSocketConfig = WebSocketConfig.default(),
) {
    public var state: WebSocketState = WebSocketState.Active
    public val frame: FrameCodec = FrameCodec.new(config.readBufferSize)
    public var additionalSend: Frame? = null
    public var unflushedAdditional: Boolean = false
    public var incomplete: IncompleteMessage? = null

    init {
        config.assertValid()
        frame.setMaxOutBufferLen(config.maxWriteBufferSize)
        frame.setOutBufferWriteLen(config.writeBufferSize)
    }

    public fun setConfig(setFunc: (WebSocketConfig) -> Unit) {
        setFunc(config)
        config.assertValid()
        frame.setMaxOutBufferLen(config.maxWriteBufferSize)
        frame.setOutBufferWriteLen(config.writeBufferSize)
    }

    public fun canRead(): Boolean = state.canRead()

    public fun canWrite(): Boolean = state.isActive()

    public fun read(
        readFn: (ByteArray) -> Int,
        writeFn: ((ByteArray, Int, Int) -> Int)? = null,
        flushFn: (() -> Unit)? = null,
    ): Message? {
        state.checkNotTerminated()

        while (true) {
            if ((additionalSend != null || unflushedAdditional) && writeFn != null && flushFn != null) {
                try {
                    flush(writeFn, flushFn)
                } catch (e: TungsteniteException.Io) {
                    if (e.message?.contains("WouldBlock", ignoreCase = true) == true) {
                        unflushedAdditional = true
                    } else {
                        throw e
                    }
                }
            } else if (role == Role.Server && !state.canRead()) {
                state = WebSocketState.Terminated
                throw TungsteniteException.ConnectionClosed()
            }

            val msg = readMessageFrame(readFn)
            if (msg != null) {
                return msg
            }
        }
    }

    public fun write(
        writeFn: (ByteArray, Int, Int) -> Int,
        message: Message,
    ) {
        state.checkNotTerminated()
        if (!state.isActive()) {
            throw TungsteniteException.ProtocolViolation(ProtocolError.SendAfterClosing)
        }

        val frameToSend =
            when (message) {
                is Message.Text -> Frame.message(message.utf8.asBytes(), OpCode.Data(OpData.Text), true)
                is Message.Binary -> Frame.message(message.data, OpCode.Data(OpData.Binary), true)
                is Message.Ping -> Frame.ping(message.data.asSlice())
                is Message.Pong -> {
                    setAdditional(Frame.pong(message.data.asSlice()))
                    return
                }
                is Message.Close -> {
                    close(writeFn, {}, message.frame)
                    return
                }
                is Message.FrameMsg -> message.frame
            }

        val shouldFlush = bufferFrame(writeFn, frameToSend)
        if (shouldFlush) {
            frame.writeOutBuffer(writeFn)
        }
    }

    public fun flush(writeFn: (ByteArray, Int, Int) -> Int, flushFn: () -> Unit) {
        if (additionalSend != null) {
            val msg = additionalSend
            additionalSend = null
            if (msg != null) {
                bufferFrame(writeFn, msg)
            }
        }
        frame.writeOutBuffer(writeFn)
        flushFn()
        unflushedAdditional = false
    }

    public fun close(
        writeFn: (ByteArray, Int, Int) -> Int,
        flushFn: () -> Unit,
        code: CloseFrame? = null,
    ) {
        if (state == WebSocketState.Active) {
            state = WebSocketState.ClosedByUs
            val closeFrame = Frame.close(code)
            bufferFrame(writeFn, closeFrame)
        }
        flush(writeFn, flushFn)
    }

    public fun readMessageFrame(
        readFn: (ByteArray) -> Int,
    ): Message? {
        val f =
            frame.readFrame(
                readFn = readFn,
                maxSize = config.maxFrameSize,
                unmask = (role == Role.Server),
                acceptUnmasked = config.acceptUnmaskedFrames,
            ) ?: return null

        if (!state.canRead()) {
            throw TungsteniteException.ProtocolViolation(ProtocolError.ReceivedAfterClosing)
        }

        val hdr = f.header
        if (hdr.rsv1 || hdr.rsv2 || hdr.rsv3) {
            throw TungsteniteException.ProtocolViolation(ProtocolError.NonZeroReservedBits)
        }

        if (role == Role.Client && f.isMasked()) {
            throw TungsteniteException.ProtocolViolation(ProtocolError.MaskedFrameFromServer)
        }

        return when (val op = hdr.opcode) {
            is OpCode.Control -> {
                when (val ctl = op.code) {
                    is OpCtl.Close -> {
                        val closeOpt = doClose(f.intoClose())
                        if (closeOpt != null) Message.Close(closeOpt) else null
                    }
                    is OpCtl.Ping -> {
                        val payload = f.intoPayload()
                        if (state.isActive()) {
                            setAdditional(Frame.pong(payload.asSlice()))
                        }
                        Message.Ping(payload)
                    }
                    is OpCtl.Pong -> Message.Pong(f.intoPayload())
                    is OpCtl.Reserved -> throw TungsteniteException.ProtocolViolation(ProtocolError.UnknownControlFrameType(ctl.value))
                }
            }
            is OpCode.Data -> {
                val fin = hdr.isFinal
                val payload = f.intoPayload().asSlice()
                when (val data = op.code) {
                    is OpData.Continue -> {
                        val inc =
                            incomplete
                                ?: throw TungsteniteException.ProtocolViolation(ProtocolError.UnexpectedContinueFrame)
                        inc.extend(payload, config.maxMessageSize)
                        if (fin) {
                            incomplete = null
                            inc.complete()
                        } else {
                            null
                        }
                    }
                    is OpData.Text -> {
                        if (incomplete != null) {
                            throw TungsteniteException.ProtocolViolation(ProtocolError.ExpectedFragment(OpData.Text))
                        }
                        if (fin) {
                            checkMaxSize(payload.size.toLong(), config.maxMessageSize)
                            Message.text(payload.decodeToString())
                        } else {
                            val inc = IncompleteMessage.new(MessageType.Text)
                            inc.extend(payload, config.maxMessageSize)
                            incomplete = inc
                            null
                        }
                    }
                    is OpData.Binary -> {
                        if (incomplete != null) {
                            throw TungsteniteException.ProtocolViolation(ProtocolError.ExpectedFragment(OpData.Binary))
                        }
                        if (fin) {
                            checkMaxSize(payload.size.toLong(), config.maxMessageSize)
                            Message.binary(payload)
                        } else {
                            val inc = IncompleteMessage.new(MessageType.Binary)
                            inc.extend(payload, config.maxMessageSize)
                            incomplete = inc
                            null
                        }
                    }
                    is OpData.Reserved -> throw TungsteniteException.ProtocolViolation(ProtocolError.UnknownDataFrameType(data.value))
                }
            }
        }
    }

    public fun doClose(close: CloseFrame?): CloseFrame? =
        when (state) {
            WebSocketState.Active -> {
                state = WebSocketState.ClosedByPeer
                val reply = Frame.close(close)
                setAdditional(reply)
                close
            }
            WebSocketState.ClosedByPeer, WebSocketState.CloseAcknowledged -> null
            WebSocketState.ClosedByUs -> {
                state = WebSocketState.CloseAcknowledged
                close
            }
            WebSocketState.Terminated -> null
        }

    private fun bufferFrame(writeFn: (ByteArray, Int, Int) -> Int, frameToSend: Frame): Boolean {
        var frame = frameToSend
        if (role == Role.Client) {
            frame.setRandomMask()
        }
        this.frame.bufferFrame(writeFn, frame)
        return true
    }

    private fun setAdditional(add: Frame) {
        val emptyOrPong =
            additionalSend == null ||
                additionalSend?.header?.opcode is OpCode.Control &&
                (additionalSend?.header?.opcode as OpCode.Control).code is OpCtl.Pong
        if (emptyOrPong) {
            additionalSend = add
        }
    }

    public companion object {
        public fun new(role: Role, config: WebSocketConfig? = null): WebSocketContext =
            WebSocketContext(role, config ?: WebSocketConfig.default())

        public fun fromPartiallyRead(
            part: ByteArray,
            role: Role,
            config: WebSocketConfig? = null,
        ): WebSocketContext {
            val conf = config ?: WebSocketConfig.default()
            val ctx = WebSocketContext(role, conf)
            for (b in part) {
                ctx.frame.inBuffer.add(b)
            }
            return ctx
        }
    }
}

/**
 * Check if the payload size exceeds the maximum allowed message size.
 */
public fun checkMaxSize(size: Long, maxSize: Long?) {
    if (maxSize != null && size > maxSize) {
        throw TungsteniteException.Capacity(CapacityError.MessageTooLong(size, maxSize))
    }
}
