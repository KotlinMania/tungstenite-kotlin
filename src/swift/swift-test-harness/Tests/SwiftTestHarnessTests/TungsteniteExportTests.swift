import Testing
import Tungstenite

@Suite struct TungsteniteExportTests {
    @Test func handshakeHelpersAndConstantsWork() {
        #expect(READ_BUFFER_CHUNK_SIZE == 4096)
        #expect(handshake.versionAsStr(ver: "HTTP/1.1") == "HTTP/1.1")
        #expect(handshake.deriveAcceptKey(requestKey: "dGhlIHNhbXBsZSBub25jZQ==") == "s3pPLMBiTxaQ9kYGzzhZRbK+xOo=")
        #expect(handshake.generateKey().count == 24)
    }

    @Test func messageTypeCasesExposeAllVariants() {
        #expect(`protocol`.MessageType.allCases.count == 2)
    }

    @Test func proxyAndMessageHelpersWorkEndToEnd() {
        #expect(normalizeHost(host: "[::1]") == "::1")
        #expect(percentDecode(value: "user%40example.com") == "user@example.com")
        let auth = ProxyAuth.Companion.shared.new(username: "alice", password: "secret")
        #expect(basicAuthHeader(auth: auth) == "Basic YWxpY2U6c2VjcmV0")

        let msg = `protocol`.Message.Companion.shared.text(string: "hello websocket")
        #expect(msg.isText())
        #expect(!msg.isBinary())
        #expect(!msg.isEmpty())
        #expect(msg.len() == 15)
        #expect(msg.toText() == "hello websocket")
    }
}
