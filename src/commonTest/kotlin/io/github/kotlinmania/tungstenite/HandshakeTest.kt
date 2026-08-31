// port-lint: tests tungstenite/src/../tests/handshake.rs
package io.github.kotlinmania.tungstenite

import io.github.kotlinmania.tungstenite.handshake.Response
import io.github.kotlinmania.tungstenite.handshake.VerifyData
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

public class HandshakeTest {
    private fun createResponse(subprotocol: String?): Response {
        val headers =
            mutableMapOf(
                "Upgrade" to "websocket",
                "Connection" to "Upgrade",
                "Sec-WebSocket-Accept" to "s3pPLMBiTxaQ9kYGzzhZRbK+xOo=",
            )
        if (subprotocol != null) {
            headers["Sec-WebSocket-Protocol"] = subprotocol
        }
        return Response(statusCode = 101, headers = headers)
    }

    @Test
    public fun testServerSendNoSubprotocol() {
        val verify =
            VerifyData(
                acceptKey = "s3pPLMBiTxaQ9kYGzzhZRbK+xOo=",
                subprotocols = listOf("my-sub-protocol"),
            )
        val response = createResponse(null)
        val ex =
            assertFailsWith<TungsteniteException.ProtocolViolation> {
                verify.verifyResponse(response)
            }
        assertEquals(ProtocolError.SecWebSocketSubProtocol(SubProtocolError.NoSubProtocol), ex.error)
    }

    @Test
    public fun testServerSentSubprotocolNoneRequested() {
        val verify =
            VerifyData(
                acceptKey = "s3pPLMBiTxaQ9kYGzzhZRbK+xOo=",
                subprotocols = null,
            )
        val response = createResponse("my-sub-protocol")
        val ex =
            assertFailsWith<TungsteniteException.ProtocolViolation> {
                verify.verifyResponse(response)
            }
        assertEquals(
            ProtocolError.SecWebSocketSubProtocol(SubProtocolError.ServerSentSubProtocolNoneRequested),
            ex.error,
        )
    }

    @Test
    public fun testInvalidSubprotocol() {
        val verify =
            VerifyData(
                acceptKey = "s3pPLMBiTxaQ9kYGzzhZRbK+xOo=",
                subprotocols = listOf("my-sub-protocol"),
            )
        val response = createResponse("invalid-sub-protocol")
        val ex =
            assertFailsWith<TungsteniteException.ProtocolViolation> {
                verify.verifyResponse(response)
            }
        assertEquals(ProtocolError.SecWebSocketSubProtocol(SubProtocolError.InvalidSubProtocol), ex.error)
    }

    @Test
    public fun testRequestMultipleSubprotocols() {
        val verify =
            VerifyData(
                acceptKey = "s3pPLMBiTxaQ9kYGzzhZRbK+xOo=",
                subprotocols = listOf("my-sub-protocol", "my-sub-protocol-1", "my-sub-protocol-2"),
            )
        val response = createResponse("my-sub-protocol")
        val (verifiedResponse, _) = verify.verifyResponse(response)
        assertEquals("my-sub-protocol", verifiedResponse.headers["Sec-WebSocket-Protocol"])
    }

    @Test
    public fun testRequestMultipleSubprotocolsWithInitialUnknown() {
        val verify =
            VerifyData(
                acceptKey = "s3pPLMBiTxaQ9kYGzzhZRbK+xOo=",
                subprotocols = listOf("protocol-unknown-to-server", "my-sub-protocol"),
            )
        val response = createResponse("my-sub-protocol")
        val (verifiedResponse, _) = verify.verifyResponse(response)
        assertEquals("my-sub-protocol", verifiedResponse.headers["Sec-WebSocket-Protocol"])
    }

    @Test
    public fun testRequestSingleSubprotocol() {
        val verify =
            VerifyData(
                acceptKey = "s3pPLMBiTxaQ9kYGzzhZRbK+xOo=",
                subprotocols = listOf("my-sub-protocol"),
            )
        val response = createResponse("my-sub-protocol")
        val (verifiedResponse, _) = verify.verifyResponse(response)
        assertEquals("my-sub-protocol", verifiedResponse.headers["Sec-WebSocket-Protocol"])
    }
}
