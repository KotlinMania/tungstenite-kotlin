// port-lint: source lib.rs
package io.github.kotlinmania.tungstenite

import io.github.kotlinmania.tungstenite.protocol.Message
import io.github.kotlinmania.tungstenite.protocol.frame.CloseFrame
import io.github.kotlinmania.tungstenite.protocol.frame.Utf8Bytes

public const val READ_BUFFER_CHUNK_SIZE: Int = 4096

public typealias DefaultReadBuffer = io.github.kotlinmania.tungstenite.ReadBuffer
public typealias WsMessage = Message
public typealias WsCloseFrame = CloseFrame
public typealias WsUtf8Bytes = Utf8Bytes
