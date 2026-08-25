# Immediate Actions - High-Value Files

Based on AST analysis, here are the concrete next steps.

## Summary

- **Files Present:** 27/50 (54.0%)
- **Function parity:** 281/430 matched (target 520) — 65.3%
- **Class/type parity:** 76/111 matched (target 240) — 68.5%
- **Combined symbol parity:** 357/541 matched (target 760) — 66.0%
- **Average inline-code cosine:** 0.41 (function body across 20 matched files)
- **Average documentation cosine:** 0.67 (doc text across 20 matched files)
- **Cheat-zeroed Files:** 9
- **Critical Issues:** 24 files with <0.60 function similarity

## Priority 1: Fix Incomplete High-Dependency Files

No incomplete high-dependency files detected.

## Priority 2: Port Missing High-Value Files

Critical missing files (>10 dependencies):

No missing high-value files detected.

## Detailed Work Items

Every matched file is listed below with function and type symbol parity.

### 1. error

- **Target:** `tungstenite.Error [ZERO] [PROVENANCE-FALLBACK]`
- **Similarity:** 0.00
- **Dependents:** 6
- **Priority Score:** 6021110.0
- **Functions:** 3/4 matched (target 54)
- **Missing functions:** `from`
- **Types:** 6/7 matched (target 69)
- **Missing types:** `Result`
- **Tests:** 3/3 matched
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `error.rs` vs expected `error.rs`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `tests:error.rs` vs expected `error.rs`
- **Proposed provenance header:** `// port-lint: source error.rs` (current: `// port-lint: source error.rs`)
- **Proposed provenance header:** `// port-lint: tests error.rs` (current: `// port-lint: tests error.rs`)
- **Lint issues:** 2

### 2. proxy

- **Target:** `tungstenite.Proxy [PROVENANCE-FALLBACK]`
- **Similarity:** 0.46
- **Dependents:** 1
- **Priority Score:** 1114305.4
- **Functions:** 28/39 matched (target 32)
- **Missing functions:** `from_env`, `connect_proxy_stream`, `proxy_from_env_for_host`, `get_env_first`, `connect_http_proxy`, `connect_socks5_proxy`, `connect_to_proxy`, `new`, `read`, `write`, `flush`
- **Types:** 4/4 matched (target 5)
- **Missing types:** _none_
- **Tests:** 9/13 matched
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `proxy.rs` vs expected `proxy.rs`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `tests:proxy.rs` vs expected `proxy.rs`
- **Proposed provenance header:** `// port-lint: source proxy.rs` (current: `// port-lint: source proxy.rs`)
- **Proposed provenance header:** `// port-lint: tests proxy.rs` (current: `// port-lint: tests proxy.rs`)
- **Lint issues:** 2

### 3. headers.sec_websocket_extensions

- **Target:** `headers.SecWebsocketExtensions [PROVENANCE-FALLBACK]`
- **Similarity:** 0.29
- **Dependents:** 1
- **Priority Score:** 1113007.1
- **Functions:** 14/21 matched (target 35)
- **Missing functions:** `name`, `params`, `value`, `from`, `from_iter`, `from_str`, `fmt`
- **Types:** 5/9 matched (target 6)
- **Missing types:** `Item`, `IntoIter`, `Err`, `WriteToDyn`
- **Tests:** 5/5 matched
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `extensions/headers/sec_websocket_extensions.rs` vs expected `extensions/headers/sec_websocket_extensions.rs`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `tests:extensions/headers/sec_websocket_extensions.rs` vs expected `extensions/headers/sec_websocket_extensions.rs`
- **Proposed provenance header:** `// port-lint: source extensions/headers/sec_websocket_extensions.rs` (current: `// port-lint: source extensions/headers/sec_websocket_extensions.rs`)
- **Proposed provenance header:** `// port-lint: tests extensions/headers/sec_websocket_extensions.rs` (current: `// port-lint: tests extensions/headers/sec_websocket_extensions.rs`)
- **Lint issues:** 2

### 4. deflate.config

- **Target:** `deflate.Config [ZERO] [PROVENANCE-FALLBACK]`
- **Similarity:** 0.00
- **Dependents:** 0
- **Priority Score:** 103610.0
- **Functions:** 21/29 matched (target 33)
- **Missing functions:** `apply`, `from`, `from_str`, `ordinal`, `name`, `make_config`, `parse_extensions`, `parse_deflates`
- **Types:** 5/7 matched (target 14)
- **Missing types:** `ParamName`, `Err`
- **Tests:** 10/13 matched
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `extensions/compression/deflate/config.rs` vs expected `extensions/compression/deflate/config.rs`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `tests:extensions/compression/deflate/config.rs` vs expected `extensions/compression/deflate/config.rs`
- **Proposed provenance header:** `// port-lint: source extensions/compression/deflate/config.rs` (current: `// port-lint: source extensions/compression/deflate/config.rs`)
- **Proposed provenance header:** `// port-lint: tests extensions/compression/deflate/config.rs` (current: `// port-lint: tests extensions/compression/deflate/config.rs`)
- **Lint issues:** 2

### 5. protocol.mod

- **Target:** `protocol.Mod [STUB] [PROVENANCE-FALLBACK]`
- **Similarity:** 0.00
- **Dependents:** 0
- **Priority Score:** 95110.0
- **Functions:** 36/44 matched (target 55)
- **Missing functions:** `from_raw_socket_with_extensions`, `from_partially_read_with_extensions`, `get_config`, `read_message`, `write_message`, `write_pending`, `check_connection_reset`, `make_message`
- **Types:** 6/7 matched
- **Missing types:** `CheckConnectionReset`
- **Tests:** 7/8 matched
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `protocol/mod.rs` vs expected `protocol/mod.rs`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `tests:protocol/mod.rs` vs expected `protocol/mod.rs`
- **Proposed provenance header:** `// port-lint: source protocol/mod.rs` (current: `// port-lint: source protocol/mod.rs`)
- **Proposed provenance header:** `// port-lint: tests protocol/mod.rs` (current: `// port-lint: tests protocol/mod.rs`)
- **Lint issues:** 2

### 6. frame.utf8

- **Target:** `frame.Utf8 [PROVENANCE-FALLBACK]`
- **Similarity:** 0.33
- **Dependents:** 0
- **Priority Score:** 81706.7
- **Functions:** 8/14 matched (target 22)
- **Missing functions:** `deref`, `hash`, `partial_cmp`, `cmp`, `eq`, `fmt`
- **Types:** 1/3 matched (target 2)
- **Missing types:** `Target`, `Error`
- **Tests:** 1/1 matched
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `protocol/frame/utf8.rs` vs expected `protocol/frame/utf8.rs`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `tests:protocol/frame/utf8.rs` vs expected `protocol/frame/utf8.rs`
- **Proposed provenance header:** `// port-lint: source protocol/frame/utf8.rs` (current: `// port-lint: source protocol/frame/utf8.rs`)
- **Proposed provenance header:** `// port-lint: tests protocol/frame/utf8.rs` (current: `// port-lint: tests protocol/frame/utf8.rs`)
- **Lint issues:** 2

### 7. handshake.server

- **Target:** `handshake.Server [PROVENANCE-FALLBACK]`
- **Similarity:** 0.52
- **Dependents:** 0
- **Priority Score:** 72004.8
- **Functions:** 10/11 matched
- **Missing functions:** `from_httparse`
- **Types:** 3/9 matched (target 5)
- **Missing types:** `Request`, `Response`, `ErrorResponse`, `IncomingData`, `InternalStream`, `FinalResult`
- **Tests:** 2/2 matched
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `handshake/server.rs` vs expected `handshake/server.rs`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `tests:handshake/server.rs` vs expected `handshake/server.rs`
- **Proposed provenance header:** `// port-lint: source handshake/server.rs` (current: `// port-lint: source handshake/server.rs`)
- **Proposed provenance header:** `// port-lint: tests handshake/server.rs` (current: `// port-lint: tests handshake/server.rs`)
- **Lint issues:** 2

### 8. client

- **Target:** `tungstenite.Client [PROVENANCE-FALLBACK]`
- **Similarity:** 0.27
- **Dependents:** 0
- **Priority Score:** 61507.3
- **Functions:** 7/13 matched (target 12)
- **Missing functions:** `connect_with_config`, `try_client_handshake`, `create_request`, `connect`, `connect_to_some`, `connect_stream`
- **Types:** 2/2 matched
- **Missing types:** _none_
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `client.rs` vs expected `client.rs`
- **Proposed provenance header:** `// port-lint: source client.rs` (current: `// port-lint: source client.rs`)
- **Lint issues:** 1

### 9. stream

- **Target:** `tungstenite.Stream [PROVENANCE-FALLBACK]`
- **Similarity:** 0.00
- **Dependents:** 0
- **Priority Score:** 60910.0
- **Functions:** 0/5 matched (target 1)
- **Missing functions:** `set_nodelay`, `fmt`, `read`, `write`, `flush`
- **Types:** 3/4 matched (target 6)
- **Missing types:** `RustlsStreamDebug`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `stream.rs` vs expected `stream.rs`
- **Proposed provenance header:** `// port-lint: source stream.rs` (current: `// port-lint: source stream.rs`)
- **Lint issues:** 1

### 10. handshake.client

- **Target:** `handshake.Client [PROVENANCE-FALLBACK]`
- **Similarity:** 0.60
- **Dependents:** 0
- **Priority Score:** 52304.0
- **Functions:** 14/16 matched (target 24)
- **Missing functions:** `from_httparse`, `construct_expected`
- **Types:** 4/7 matched (target 6)
- **Missing types:** `IncomingData`, `InternalStream`, `FinalResult`
- **Tests:** 7/8 matched
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `handshake/client.rs` vs expected `handshake/client.rs`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `tests:handshake/client.rs` vs expected `handshake/client.rs`
- **Proposed provenance header:** `// port-lint: source handshake/client.rs` (current: `// port-lint: source handshake/client.rs`)
- **Proposed provenance header:** `// port-lint: tests handshake/client.rs` (current: `// port-lint: tests handshake/client.rs`)
- **Lint issues:** 2

### 11. frame.frame

- **Target:** `frame.Frame [PROVENANCE-FALLBACK]`
- **Similarity:** 0.59
- **Dependents:** 0
- **Priority Score:** 43204.1
- **Functions:** 24/28 matched (target 37)
- **Missing functions:** `fmt`, `parse_internal`, `header`, `payload`
- **Types:** 4/4 matched (target 8)
- **Missing types:** _none_
- **Tests:** 1/1 matched
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `protocol/frame/frame.rs` vs expected `protocol/frame/frame.rs`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `tests:protocol/frame/frame.rs` vs expected `protocol/frame/frame.rs`
- **Proposed provenance header:** `// port-lint: source protocol/frame/frame.rs` (current: `// port-lint: source protocol/frame/frame.rs`)
- **Proposed provenance header:** `// port-lint: tests protocol/frame/frame.rs` (current: `// port-lint: tests protocol/frame/frame.rs`)
- **Lint issues:** 2

### 12. compression.mod

- **Target:** `compression.Mod [STUB] [PROVENANCE-FALLBACK]`
- **Similarity:** 0.00
- **Dependents:** 0
- **Priority Score:** 40710.0
- **Functions:** 0/4 matched (target 2)
- **Missing functions:** `compressor`, `decompressor`, `map`, `from`
- **Types:** 3/3 matched (target 6)
- **Missing types:** _none_
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `extensions/compression/mod.rs` vs expected `extensions/compression/mod.rs`
- **Proposed provenance header:** `// port-lint: source extensions/compression/mod.rs` (current: `// port-lint: source extensions/compression/mod.rs`)
- **Lint issues:** 1

### 13. handshake.mod

- **Target:** `handshake.Mod [STUB] [PROVENANCE-FALLBACK]`
- **Similarity:** 0.00
- **Dependents:** 0
- **Priority Score:** 31210.0
- **Functions:** 5/8 matched (target 10)
- **Missing functions:** `handshake`, `fmt`, `from`
- **Types:** 4/4 matched (target 11)
- **Missing types:** _none_
- **Tests:** 1/1 matched
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `handshake/mod.rs` vs expected `handshake/mod.rs`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `tests:handshake/mod.rs` vs expected `handshake/mod.rs`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `tests:handshake/mod.rs` vs expected `handshake/mod.rs`
- **Proposed provenance header:** `// port-lint: source handshake/mod.rs` (current: `// port-lint: source handshake/mod.rs`)
- **Proposed provenance header:** `// port-lint: tests handshake/mod.rs` (current: `// port-lint: tests handshake/mod.rs`)
- **Proposed provenance header:** `// port-lint: tests handshake/mod.rs` (current: `// port-lint: tests handshake/mod.rs`)
- **Lint issues:** 3

### 14. protocol.message

- **Target:** `protocol.Message [PROVENANCE-FALLBACK]`
- **Similarity:** 0.57
- **Dependents:** 0
- **Priority Score:** 23104.3
- **Functions:** 25/26 matched (target 44)
- **Missing functions:** `fmt`
- **Types:** 4/5 matched (target 11)
- **Missing types:** `IncompleteMessageCollector`
- **Tests:** 6/6 matched
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `protocol/message.rs` vs expected `protocol/message.rs`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `tests:protocol/message.rs` vs expected `protocol/message.rs`
- **Proposed provenance header:** `// port-lint: source protocol/message.rs` (current: `// port-lint: source protocol/message.rs`)
- **Proposed provenance header:** `// port-lint: tests protocol/message.rs` (current: `// port-lint: tests protocol/message.rs`)
- **Lint issues:** 2

### 15. deflate.mod

- **Target:** `deflate.Mod [STUB] [PROVENANCE-FALLBACK]`
- **Similarity:** 0.00
- **Dependents:** 0
- **Priority Score:** 21910.0
- **Functions:** 13/15 matched (target 25)
- **Missing functions:** `from`, `make_frames`
- **Types:** 4/4 matched (target 12)
- **Missing types:** _none_
- **Tests:** 10/11 matched
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `extensions/compression/deflate/mod.rs` vs expected `extensions/compression/deflate/mod.rs`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `tests:extensions/compression/deflate/mod.rs` vs expected `extensions/compression/deflate/mod.rs`
- **Proposed provenance header:** `// port-lint: source extensions/compression/deflate/mod.rs` (current: `// port-lint: source extensions/compression/deflate/mod.rs`)
- **Proposed provenance header:** `// port-lint: tests extensions/compression/deflate/mod.rs` (current: `// port-lint: tests extensions/compression/deflate/mod.rs`)
- **Lint issues:** 2

### 16. buffer

- **Target:** `tungstenite.Buffer [PROVENANCE-FALLBACK]`
- **Similarity:** 0.41
- **Dependents:** 0
- **Priority Score:** 21505.9
- **Functions:** 12/14 matched (target 17)
- **Missing functions:** `as_cursor`, `as_cursor_mut`
- **Types:** 1/1 matched (target 2)
- **Missing types:** _none_
- **Tests:** 2/2 matched
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `buffer.rs` vs expected `buffer.rs`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `tests:buffer.rs` vs expected `buffer.rs`
- **Proposed provenance header:** `// port-lint: source buffer.rs` (current: `// port-lint: source buffer.rs`)
- **Proposed provenance header:** `// port-lint: tests buffer.rs` (current: `// port-lint: tests buffer.rs`)
- **Lint issues:** 2

### 17. frame.coding

- **Target:** `frame.Coding [PROVENANCE-FALLBACK]`
- **Similarity:** 0.28
- **Dependents:** 0
- **Priority Score:** 21107.2
- **Functions:** 5/7 matched (target 26)
- **Missing functions:** `fmt`, `from`
- **Types:** 4/4 matched (target 29)
- **Missing types:** _none_
- **Tests:** 4/4 matched
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `protocol/frame/coding.rs` vs expected `protocol/frame/coding.rs`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `tests:protocol/frame/coding.rs` vs expected `protocol/frame/coding.rs`
- **Proposed provenance header:** `// port-lint: source protocol/frame/coding.rs` (current: `// port-lint: source protocol/frame/coding.rs`)
- **Proposed provenance header:** `// port-lint: tests protocol/frame/coding.rs` (current: `// port-lint: tests protocol/frame/coding.rs`)
- **Lint issues:** 2

### 18. handshake.headers

- **Target:** `handshake.Headers [PROVENANCE-FALLBACK]`
- **Similarity:** 0.40
- **Dependents:** 0
- **Priority Score:** 20606.0
- **Functions:** 4/5 matched (target 12)
- **Missing functions:** `from_httparse`
- **Types:** 0/1 matched (target 2)
- **Missing types:** `FromHttparse`
- **Tests:** 3/3 matched
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `handshake/headers.rs` vs expected `handshake/headers.rs`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `tests:handshake/headers.rs` vs expected `handshake/headers.rs`
- **Proposed provenance header:** `// port-lint: source handshake/headers.rs` (current: `// port-lint: source handshake/headers.rs`)
- **Proposed provenance header:** `// port-lint: tests handshake/headers.rs` (current: `// port-lint: tests handshake/headers.rs`)
- **Lint issues:** 2

### 19. util

- **Target:** `tungstenite.Util [PROVENANCE-FALLBACK]`
- **Similarity:** 0.15
- **Dependents:** 0
- **Priority Score:** 20508.5
- **Functions:** 2/2 matched (target 6)
- **Missing functions:** _none_
- **Types:** 1/3 matched (target 2)
- **Missing types:** `NonBlockingResult`, `Result`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `util.rs` vs expected `util.rs`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `tests:util.rs` vs expected `util.rs`
- **Proposed provenance header:** `// port-lint: source util.rs` (current: `// port-lint: source util.rs`)
- **Proposed provenance header:** `// port-lint: tests util.rs` (current: `// port-lint: tests util.rs`)
- **Lint issues:** 2

### 20. tls

- **Target:** `tungstenite.Tls [PROVENANCE-FALLBACK]`
- **Similarity:** 0.30
- **Dependents:** 0
- **Priority Score:** 20507.0
- **Functions:** 2/3 matched (target 2)
- **Missing functions:** `wrap_stream`
- **Types:** 1/2 matched (target 4)
- **Missing types:** `TlsHandshakeError`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `tls.rs` vs expected `tls.rs`
- **Proposed provenance header:** `// port-lint: source tls.rs` (current: `// port-lint: source tls.rs`)
- **Lint issues:** 1

### 21. handshake.machine

- **Target:** `handshake.Machine [PROVENANCE-FALLBACK]`
- **Similarity:** 0.66
- **Dependents:** 0
- **Priority Score:** 11303.4
- **Functions:** 6/7 matched (target 10)
- **Missing functions:** `single_round`
- **Types:** 6/6 matched (target 14)
- **Missing types:** _none_
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `handshake/machine.rs` vs expected `handshake/machine.rs`
- **Proposed provenance header:** `// port-lint: source handshake/machine.rs` (current: `// port-lint: source handshake/machine.rs`)
- **Lint issues:** 1

### 22. lib

- **Target:** `tungstenite.Lib [PROVENANCE-FALLBACK]`
- **Similarity:** 1.00
- **Dependents:** 0
- **Priority Score:** 10100.0
- **Functions:** 0/0 matched
- **Missing functions:** _none_
- **Types:** 0/1 matched (target 4)
- **Missing types:** `ReadBuffer`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `lib.rs` vs expected `lib.rs`
- **Proposed provenance header:** `// port-lint: source lib.rs` (current: `// port-lint: source lib.rs`)
- **Lint issues:** 1

### 23. frame.mod

- **Target:** `frame.Mod [STUB] [PROVENANCE-FALLBACK]`
- **Similarity:** 0.00
- **Dependents:** 0
- **Priority Score:** 2110.0
- **Functions:** 19/19 matched (target 24)
- **Missing functions:** _none_
- **Types:** 2/2 matched (target 5)
- **Missing types:** _none_
- **Tests:** 4/4 matched
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `protocol/frame/mod.rs` vs expected `protocol/frame/mod.rs`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `tests:protocol/frame/mod.rs` vs expected `protocol/frame/mod.rs`
- **Proposed provenance header:** `// port-lint: source protocol/frame/mod.rs` (current: `// port-lint: source protocol/frame/mod.rs`)
- **Proposed provenance header:** `// port-lint: tests protocol/frame/mod.rs` (current: `// port-lint: tests protocol/frame/mod.rs`)
- **Lint issues:** 2

### 24. extensions.mod

- **Target:** `extensions.Mod [STUB] [PROVENANCE-FALLBACK]`
- **Similarity:** 0.00
- **Dependents:** 0
- **Priority Score:** 1510.0
- **Functions:** 12/12 matched (target 14)
- **Missing functions:** _none_
- **Types:** 3/3 matched (target 7)
- **Missing types:** _none_
- **Tests:** 6/6 matched
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `extensions/mod.rs` vs expected `extensions/mod.rs`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `tests:extensions/mod.rs` vs expected `extensions/mod.rs`
- **Proposed provenance header:** `// port-lint: source extensions/mod.rs` (current: `// port-lint: source extensions/mod.rs`)
- **Proposed provenance header:** `// port-lint: tests extensions/mod.rs` (current: `// port-lint: tests extensions/mod.rs`)
- **Lint issues:** 2

### 25. frame.mask

- **Target:** `frame.Mask [PROVENANCE-FALLBACK]`
- **Similarity:** 0.52
- **Dependents:** 0
- **Priority Score:** 504.8
- **Functions:** 5/5 matched (target 6)
- **Missing functions:** _none_
- **Types:** 0/0 matched (target 1)
- **Missing types:** _none_
- **Tests:** 1/1 matched
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `protocol/frame/mask.rs` vs expected `protocol/frame/mask.rs`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `tests:protocol/frame/mask.rs` vs expected `protocol/frame/mask.rs`
- **Proposed provenance header:** `// port-lint: source protocol/frame/mask.rs` (current: `// port-lint: source protocol/frame/mask.rs`)
- **Proposed provenance header:** `// port-lint: tests protocol/frame/mask.rs` (current: `// port-lint: tests protocol/frame/mask.rs`)
- **Lint issues:** 2

### 26. server

- **Target:** `tungstenite.Server [PROVENANCE-FALLBACK]`
- **Similarity:** 0.91
- **Dependents:** 0
- **Priority Score:** 400.9
- **Functions:** 4/4 matched
- **Missing functions:** _none_
- **Types:** 0/0 matched
- **Missing types:** _none_
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `server.rs` vs expected `server.rs`
- **Proposed provenance header:** `// port-lint: source server.rs` (current: `// port-lint: source server.rs`)
- **Lint issues:** 1

### 27. headers.mod

- **Target:** `headers.Mod [STUB] [PROVENANCE-FALLBACK]`
- **Similarity:** 0.00
- **Dependents:** 0
- **Priority Score:** 210.0
- **Functions:** 2/2 matched
- **Missing functions:** _none_
- **Types:** 0/0 matched
- **Missing types:** _none_
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `extensions/headers/mod.rs` vs expected `extensions/headers/mod.rs`
- **Proposed provenance header:** `// port-lint: source extensions/headers/mod.rs` (current: `// port-lint: source extensions/headers/mod.rs`)
- **Lint issues:** 1

## Success Criteria

For each file to be considered "complete":
- **Similarity ≥ 0.85** (Excellent threshold)
- All public APIs ported
- All tests ported
- Documentation ported
- port-lint header present

