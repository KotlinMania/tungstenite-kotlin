import Testing
import Tungstenite

@Suite struct TungsteniteExportTests {
    @Test func handshakeHelpersAndConstantsWork() {
        #expect(READ_BUFFER_CHUNK_SIZE == 4096)
        #expect(handshake.versionAsStr(ver: "HTTP/1.1") == "HTTP/1.1")
        #expect(handshake.deriveAcceptKey(requestKey: "dGhlIHNhbXBsZSBub25jZQ==") == "s3pPLMBiTxaQ9kYGzzhZRbK+xOo=")
    }

    @Test func messageTypeCasesExposeAllVariants() {
        #expect(`protocol`.MessageType.allCases.count == 2)
    }
}
