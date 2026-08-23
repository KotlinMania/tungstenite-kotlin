# Immediate Actions - High-Value Files

Based on AST analysis, here are the concrete next steps.

## Summary

- **Files Present:** 22/50 (44.0%)
- **Function parity:** 177/399 matched (target 379) — 44.4%
- **Class/type parity:** 58/101 matched (target 202) — 57.4%
- **Combined symbol parity:** 235/500 matched (target 581) — 47.0%
- **Average inline-code cosine:** 0.30 (function body across 19 matched files)
- **Average documentation cosine:** 0.67 (doc text across 19 matched files)
- **Cheat-zeroed Files:** 6
- **Critical Issues:** 18 files with <0.60 function similarity

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
- **Priority Score:** 6051110.0
- **Functions:** 0/4 matched (target 51)
- **Missing functions:** `from`, `error_size`, `tls_error_size`, `protocol_error_size`
- **Types:** 6/7 matched (target 69)
- **Missing types:** `Result`
- **Tests:** 0/3 matched
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `error.rs` vs expected `error.rs`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `tests:error.rs` vs expected `error.rs`
- **Proposed provenance header:** `// port-lint: source error.rs` (current: `// port-lint: source error.rs`)
- **Proposed provenance header:** `// port-lint: tests error.rs` (current: `// port-lint: tests error.rs`)
- **Lint issues:** 2

### 2. headers.sec_websocket_extensions

- **Target:** `headers.SecWebsocketExtensions [PROVENANCE-FALLBACK]`
- **Similarity:** 0.07
- **Dependents:** 1
- **Priority Score:** 1233009.4
- **Functions:** 4/21 matched (target 17)
- **Missing functions:** `new`, `iter`, `value`, `decode`, `encode`, `from`, `from_iter`, `into_iter`, `from_str`, `fmt`, `encoded_len`, `write_with`, `test_decode`, `test_encode`, `parse_separate_headers`, `round_trip_complex`, `write_to_exact_encoded_len`
- **Types:** 3/9 matched (target 4)
- **Missing types:** `Item`, `IntoIter`, `Err`, `WriteTo`, `CommaDelimited`, `WriteToDyn`
- **Tests:** 0/5 matched
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `extensions/headers/sec_websocket_extensions.rs` vs expected `extensions/headers/sec_websocket_extensions.rs`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `tests:extensions/headers/sec_websocket_extensions.rs` vs expected `extensions/headers/sec_websocket_extensions.rs`
- **Proposed provenance header:** `// port-lint: source extensions/headers/sec_websocket_extensions.rs` (current: `// port-lint: source extensions/headers/sec_websocket_extensions.rs`)
- **Proposed provenance header:** `// port-lint: tests extensions/headers/sec_websocket_extensions.rs` (current: `// port-lint: tests extensions/headers/sec_websocket_extensions.rs`)
- **Lint issues:** 2

### 3. proxy

- **Target:** `tungstenite.Proxy [PROVENANCE-FALLBACK]`
- **Similarity:** 0.26
- **Dependents:** 1
- **Priority Score:** 1224307.4
- **Functions:** 18/39 matched (target 19)
- **Missing functions:** `from_env`, `connect_proxy_stream`, `proxy_from_env_for_host`, `get_env_first`, `split_userinfo`, `parse_host_port`, `connect_http_proxy`, `connect_socks5_proxy`, `connect_to_proxy`, `read_connect_response`, `http_connect`, `socks5_handshake`, `socks5_userpass_auth`, `send_socks5_connect`, `new`, `read`, `write`, `flush`, `http_connect_handshake_ok`, `socks5_handshake_no_auth`, `socks5_handshake_with_auth`
- **Types:** 3/4 matched
- **Missing types:** `MockStream`
- **Tests:** 6/13 matched
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `proxy.rs` vs expected `proxy.rs`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `tests:proxy.rs` vs expected `proxy.rs`
- **Proposed provenance header:** `// port-lint: source proxy.rs` (current: `// port-lint: source proxy.rs`)
- **Proposed provenance header:** `// port-lint: tests proxy.rs` (current: `// port-lint: tests proxy.rs`)
- **Lint issues:** 2

### 4. deflate.config

- **Target:** `deflate.Config [ZERO] [PROVENANCE-FALLBACK]`
- **Similarity:** 0.00
- **Dependents:** 0
- **Priority Score:** 293610.0
- **Functions:** 4/29 matched (target 10)
- **Missing functions:** `new`, `set_max_window_bits`, `set_no_context_takeover`, `as_offer`, `accept_offer`, `accept_response`, `as_extension`, `apply`, `from`, `from_str`, `ordinal`, `name`, `deflate_config_parse_params_valid`, `deflate_rejects_unknown_parameters`, `deflate_rejects_duplicate_parameters`, `deflate_config_minimal_client_offer`, `deflate_server_respects_offer_server_no_context_takeover`, `rejects_unsupported_client_max_window_bits_offer`, `interop`, `make_config`, `rejects_unsupported_client_max_window_bits_response`, `parse_extensions`, `parse_deflates`, `simplest`, `client_multiple_offers`
- **Types:** 3/7 matched (target 12)
- **Missing types:** `DeflateConfig`, `DeflateInvalidMaxWindowBits`, `ParamName`, `Err`
- **Tests:** 0/13 matched
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `extensions/compression/deflate/config.rs` vs expected `extensions/compression/deflate/config.rs`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `tests:extensions/compression/deflate/config.rs` vs expected `extensions/compression/deflate/config.rs`
- **Proposed provenance header:** `// port-lint: source extensions/compression/deflate/config.rs` (current: `// port-lint: source extensions/compression/deflate/config.rs`)
- **Proposed provenance header:** `// port-lint: tests extensions/compression/deflate/config.rs` (current: `// port-lint: tests extensions/compression/deflate/config.rs`)
- **Lint issues:** 3

### 5. protocol.mod

- **Target:** `protocol.Mod [STUB] [PROVENANCE-FALLBACK]`
- **Similarity:** 0.00
- **Dependents:** 0
- **Priority Score:** 175110.0
- **Functions:** 29/44 matched (target 42)
- **Missing functions:** `from_raw_socket_with_extensions`, `from_partially_read_with_extensions`, `get_config`, `read_message`, `write_message`, `write_pending`, `check_connection_reset`, `receive_messages`, `size_limiting_text_fragmented`, `size_limiting_binary`, `per_message_deflate_compression`, `per_message_deflate_decompression`, `per_message_compression_not_recognized`, `per_message_compression_decompress_respects_message_size_limit`, `make_message`
- **Types:** 5/7 matched (target 6)
- **Missing types:** `CheckConnectionReset`, `WriteMoc`
- **Tests:** 0/8 matched
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `protocol/mod.rs` vs expected `protocol/mod.rs`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `tests:protocol/mod.rs` vs expected `protocol/mod.rs`
- **Proposed provenance header:** `// port-lint: source protocol/mod.rs` (current: `// port-lint: source protocol/mod.rs`)
- **Proposed provenance header:** `// port-lint: tests protocol/mod.rs` (current: `// port-lint: tests protocol/mod.rs`)
- **Lint issues:** 2

### 6. handshake.client

- **Target:** `handshake.Client [PROVENANCE-FALLBACK]`
- **Similarity:** 0.14
- **Dependents:** 0
- **Priority Score:** 162308.6
- **Functions:** 4/16 matched (target 15)
- **Missing functions:** `generate_request`, `extract_subprotocols_from_request`, `verify_response`, `from_httparse`, `random_keys`, `construct_expected`, `request_formatting`, `request_formatting_with_host`, `request_formatting_with_at`, `request_with_compression`, `response_parsing`, `invalid_custom_request`
- **Types:** 3/7 matched (target 5)
- **Missing types:** `IncomingData`, `InternalStream`, `FinalResult`, `VerifyData`
- **Tests:** 0/8 matched
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `handshake/client.rs` vs expected `handshake/client.rs`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `tests:handshake/client.rs` vs expected `handshake/client.rs`
- **Proposed provenance header:** `// port-lint: source handshake/client.rs` (current: `// port-lint: source handshake/client.rs`)
- **Proposed provenance header:** `// port-lint: tests handshake/client.rs` (current: `// port-lint: tests handshake/client.rs`)
- **Lint issues:** 2

### 7. handshake.server

- **Target:** `handshake.Server [PROVENANCE-FALLBACK]`
- **Similarity:** 0.15
- **Dependents:** 0
- **Priority Score:** 132008.5
- **Functions:** 4/11 matched (target 6)
- **Missing functions:** `create_parts`, `create_response_with_body`, `write_response`, `from_httparse`, `on_request`, `request_parsing`, `request_replying`
- **Types:** 3/9 matched (target 4)
- **Missing types:** `Request`, `Response`, `ErrorResponse`, `IncomingData`, `InternalStream`, `FinalResult`
- **Tests:** 0/2 matched
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `handshake/server.rs` vs expected `handshake/server.rs`
- **Proposed provenance header:** `// port-lint: source handshake/server.rs` (current: `// port-lint: source handshake/server.rs`)
- **Lint issues:** 1

### 8. frame.utf8

- **Target:** `frame.Utf8 [PROVENANCE-FALLBACK]`
- **Similarity:** 0.24
- **Dependents:** 0
- **Priority Score:** 101707.6
- **Functions:** 6/14 matched (target 16)
- **Missing functions:** `deref`, `as_ref`, `borrow`, `hash`, `partial_cmp`, `cmp`, `eq`, `fmt`
- **Types:** 1/3 matched (target 2)
- **Missing types:** `Target`, `Error`
- **Tests:** 1/1 matched
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `protocol/frame/utf8.rs` vs expected `protocol/frame/utf8.rs`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `tests:protocol/frame/utf8.rs` vs expected `protocol/frame/utf8.rs`
- **Proposed provenance header:** `// port-lint: source protocol/frame/utf8.rs` (current: `// port-lint: source protocol/frame/utf8.rs`)
- **Proposed provenance header:** `// port-lint: tests protocol/frame/utf8.rs` (current: `// port-lint: tests protocol/frame/utf8.rs`)
- **Lint issues:** 2

### 9. client

- **Target:** `tungstenite.Client [PROVENANCE-FALLBACK]`
- **Similarity:** 0.13
- **Dependents:** 0
- **Priority Score:** 91508.7
- **Functions:** 4/13 matched (target 6)
- **Missing functions:** `connect_with_config`, `try_client_handshake`, `create_request`, `connect`, `connect_to_some`, `connect_stream`, `new`, `with_header`, `with_sub_protocol`
- **Types:** 2/2 matched
- **Missing types:** _none_
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `client.rs` vs expected `client.rs`
- **Proposed provenance header:** `// port-lint: source client.rs` (current: `// port-lint: source client.rs`)
- **Lint issues:** 1

### 10. frame.coding

- **Target:** `frame.Coding [PROVENANCE-FALLBACK]`
- **Similarity:** 0.06
- **Dependents:** 0
- **Priority Score:** 61109.4
- **Functions:** 1/7 matched (target 26)
- **Missing functions:** `fmt`, `from`, `opcode_from_u8`, `opcode_into_u8`, `closecode_from_u16`, `closecode_into_u16`
- **Types:** 4/4 matched (target 29)
- **Missing types:** _none_
- **Tests:** 0/4 matched
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `protocol/frame/coding.rs` vs expected `protocol/frame/coding.rs`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `tests:protocol/frame/coding.rs` vs expected `protocol/frame/coding.rs`
- **Proposed provenance header:** `// port-lint: source protocol/frame/coding.rs` (current: `// port-lint: source protocol/frame/coding.rs`)
- **Proposed provenance header:** `// port-lint: tests protocol/frame/coding.rs` (current: `// port-lint: tests protocol/frame/coding.rs`)
- **Lint issues:** 2

### 11. stream

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

### 12. frame.mod

- **Target:** `frame.Mod [STUB] [PROVENANCE-FALLBACK]`
- **Similarity:** 0.00
- **Dependents:** 0
- **Priority Score:** 52110.0
- **Functions:** 14/19 matched (target 23)
- **Missing functions:** `read_in`, `read_frames`, `write_frames`, `parse_overflow`, `size_limit_hit`
- **Types:** 2/2 matched (target 5)
- **Missing types:** _none_
- **Tests:** 0/4 matched
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `protocol/frame/mod.rs` vs expected `protocol/frame/mod.rs`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `tests:protocol/frame/mod.rs` vs expected `protocol/frame/mod.rs`
- **Proposed provenance header:** `// port-lint: source protocol/frame/mod.rs` (current: `// port-lint: source protocol/frame/mod.rs`)
- **Proposed provenance header:** `// port-lint: tests protocol/frame/mod.rs` (current: `// port-lint: tests protocol/frame/mod.rs`)
- **Lint issues:** 2

### 13. handshake.mod

- **Target:** `handshake.Mod [STUB] [PROVENANCE-FALLBACK]`
- **Similarity:** 0.00
- **Dependents:** 0
- **Priority Score:** 31210.0
- **Functions:** 5/8 matched (target 9)
- **Missing functions:** `handshake`, `fmt`, `from`
- **Types:** 4/4 matched (target 10)
- **Missing types:** _none_
- **Tests:** 1/1 matched
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `handshake/mod.rs` vs expected `handshake/mod.rs`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `tests:handshake/mod.rs` vs expected `handshake/mod.rs`
- **Proposed provenance header:** `// port-lint: source handshake/mod.rs` (current: `// port-lint: source handshake/mod.rs`)
- **Proposed provenance header:** `// port-lint: tests handshake/mod.rs` (current: `// port-lint: tests handshake/mod.rs`)
- **Lint issues:** 2

### 14. frame.frame

- **Target:** `frame.Frame [PROVENANCE-FALLBACK]`
- **Similarity:** 0.64
- **Dependents:** 0
- **Priority Score:** 23203.6
- **Functions:** 26/28 matched (target 39)
- **Missing functions:** `fmt`, `parse_internal`
- **Types:** 4/4 matched (target 8)
- **Missing types:** _none_
- **Tests:** 1/1 matched
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `protocol/frame/frame.rs` vs expected `protocol/frame/frame.rs`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `tests:protocol/frame/frame.rs` vs expected `protocol/frame/frame.rs`
- **Proposed provenance header:** `// port-lint: source protocol/frame/frame.rs` (current: `// port-lint: source protocol/frame/frame.rs`)
- **Proposed provenance header:** `// port-lint: tests protocol/frame/frame.rs` (current: `// port-lint: tests protocol/frame/frame.rs`)
- **Lint issues:** 2

### 15. protocol.message

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

### 17. handshake.headers

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

### 18. util

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

### 19. handshake.machine

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

### 20. lib

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

### 21. frame.mask

- **Target:** `frame.Mask [ZERO] [PROVENANCE-FALLBACK]`
- **Similarity:** 0.00
- **Dependents:** 0
- **Priority Score:** 510.0
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

### 22. server

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

## Success Criteria

For each file to be considered "complete":
- **Similarity ≥ 0.85** (Excellent threshold)
- All public APIs ported
- All tests ported
- Documentation ported
- port-lint header present

## Reexport / Wiring Modules

These files match `reexport_modules` patterns in `.ast_distance_config.json`. They are filtered out of
normal priority and missing-file ladders because they are wiring
modules, not direct logic ports. Consult them for call-site routing;
do not treat them as the next implementation target by default.

### Missing

| Source | Expected target | Deps | Source path | Expected path |
|--------|-----------------|------|-------------|---------------|
| `deflate.mod` | `extensions.compression.deflate.Mod` | 0 | `src/extensions/compression/deflate/mod.rs` | `extensions/compression/deflate/Mod.kt` |
| `compression.mod` | `extensions.compression.Mod` | 0 | `src/extensions/compression/mod.rs` | `extensions/compression/Mod.kt` |
| `headers.mod` | `extensions.headers.Mod` | 0 | `src/extensions/headers/mod.rs` | `extensions/headers/Mod.kt` |
| `extensions.mod` | `extensions.Mod` | 0 | `src/extensions/mod.rs` | `extensions/Mod.kt` |

