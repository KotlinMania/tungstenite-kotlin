# Immediate Actions - High-Value Files

Based on AST analysis, here are the concrete next steps.

## Summary

- **Files Present:** 22/27 (81.5%)
- **Function parity:** 196/326 matched (target 396) — 60.1%
- **Class/type parity:** 60/92 matched (target 205) — 65.2%
- **Combined symbol parity:** 256/418 matched (target 601) — 61.2%
- **Average inline-code cosine:** 0.36 (function body across 19 matched files)
- **Average documentation cosine:** 0.65 (doc text across 19 matched files)
- **Cheat-zeroed Files:** 3
- **Critical Issues:** 19 files with <0.60 function similarity

## Priority 1: Fix Incomplete High-Dependency Files

No incomplete high-dependency files detected.

## Priority 2: Port Missing High-Value Files

Critical missing files (>10 dependencies):

No missing high-value files detected.

## Detailed Work Items

Every matched file is listed below with function and type symbol parity.

### 1. error

- **Target:** `tungstenite.Error`
- **Similarity:** 0.00
- **Dependents:** 6
- **Priority Score:** 6051110.0
- **Functions:** 0/4 matched (target 51)
- **Missing functions:** `from`, `error_size`, `tls_error_size`, `protocol_error_size`
- **Types:** 6/7 matched (target 69)
- **Missing types:** `Result`
- **Tests:** 0/3 matched

### 2. proxy

- **Target:** `tungstenite.Proxy`
- **Similarity:** 0.26
- **Dependents:** 1
- **Priority Score:** 1224307.4
- **Functions:** 18/39 matched (target 19)
- **Missing functions:** `from_env`, `connect_proxy_stream`, `proxy_from_env_for_host`, `get_env_first`, `split_userinfo`, `parse_host_port`, `connect_http_proxy`, `connect_socks5_proxy`, `connect_to_proxy`, `read_connect_response`, `http_connect`, `socks5_handshake`, `socks5_userpass_auth`, `send_socks5_connect`, `new`, `read`, `write`, `flush`, `http_connect_handshake_ok`, `socks5_handshake_no_auth`, `socks5_handshake_with_auth`
- **Types:** 3/4 matched
- **Missing types:** `MockStream`
- **Tests:** 6/13 matched

### 3. headers.sec_websocket_extensions

- **Target:** `headers.SecWebsocketExtensions`
- **Similarity:** 0.32
- **Dependents:** 1
- **Priority Score:** 1133006.8
- **Functions:** 12/21 matched (target 32)
- **Missing functions:** `decode`, `encode`, `from`, `from_iter`, `into_iter`, `from_str`, `fmt`, `test_decode`, `test_encode`
- **Types:** 5/9 matched (target 6)
- **Missing types:** `Item`, `IntoIter`, `Err`, `WriteToDyn`
- **Tests:** 3/5 matched

### 4. deflate.config

- **Target:** `deflate.Config`
- **Similarity:** 0.07
- **Dependents:** 0
- **Priority Score:** 293609.3
- **Functions:** 4/29 matched (target 10)
- **Missing functions:** `new`, `set_max_window_bits`, `set_no_context_takeover`, `as_offer`, `accept_offer`, `accept_response`, `as_extension`, `apply`, `from`, `from_str`, `ordinal`, `name`, `deflate_config_parse_params_valid`, `deflate_rejects_unknown_parameters`, `deflate_rejects_duplicate_parameters`, `deflate_config_minimal_client_offer`, `deflate_server_respects_offer_server_no_context_takeover`, `rejects_unsupported_client_max_window_bits_offer`, `interop`, `make_config`, `rejects_unsupported_client_max_window_bits_response`, `parse_extensions`, `parse_deflates`, `simplest`, `client_multiple_offers`
- **Types:** 3/7 matched (target 12)
- **Missing types:** `DeflateConfig`, `DeflateInvalidMaxWindowBits`, `ParamName`, `Err`
- **Tests:** 0/13 matched
- **Lint issues:** 1

### 5. handshake.client

- **Target:** `handshake.Client`
- **Similarity:** 0.14
- **Dependents:** 0
- **Priority Score:** 162308.6
- **Functions:** 4/16 matched (target 12)
- **Missing functions:** `generate_request`, `extract_subprotocols_from_request`, `verify_response`, `from_httparse`, `random_keys`, `construct_expected`, `request_formatting`, `request_formatting_with_host`, `request_formatting_with_at`, `request_with_compression`, `response_parsing`, `invalid_custom_request`
- **Types:** 3/7 matched (target 4)
- **Missing types:** `IncomingData`, `InternalStream`, `FinalResult`, `VerifyData`
- **Tests:** 0/8 matched

### 6. protocol.mod

- **Target:** `protocol.Mod [STUB]`
- **Similarity:** 0.00
- **Dependents:** 0
- **Priority Score:** 135110.0
- **Functions:** 33/44 matched (target 47)
- **Missing functions:** `from_raw_socket_with_extensions`, `from_partially_read_with_extensions`, `get_config`, `read_message`, `write_message`, `write_pending`, `check_connection_reset`, `per_message_deflate_compression`, `per_message_deflate_decompression`, `per_message_compression_decompress_respects_message_size_limit`, `make_message`
- **Types:** 5/7 matched
- **Missing types:** `CheckConnectionReset`, `WriteMoc`
- **Tests:** 4/8 matched

### 7. handshake.server

- **Target:** `handshake.Server`
- **Similarity:** 0.15
- **Dependents:** 0
- **Priority Score:** 132008.5
- **Functions:** 4/11 matched (target 6)
- **Missing functions:** `create_parts`, `create_response_with_body`, `write_response`, `from_httparse`, `on_request`, `request_parsing`, `request_replying`
- **Types:** 3/9 matched (target 4)
- **Missing types:** `Request`, `Response`, `ErrorResponse`, `IncomingData`, `InternalStream`, `FinalResult`
- **Tests:** 0/2 matched

### 8. frame.utf8

- **Target:** `frame.Utf8`
- **Similarity:** 0.24
- **Dependents:** 0
- **Priority Score:** 101707.6
- **Functions:** 6/14 matched (target 16)
- **Missing functions:** `deref`, `as_ref`, `borrow`, `hash`, `partial_cmp`, `cmp`, `eq`, `fmt`
- **Types:** 1/3 matched (target 2)
- **Missing types:** `Target`, `Error`
- **Tests:** 1/1 matched

### 9. client

- **Target:** `tungstenite.Client`
- **Similarity:** 0.13
- **Dependents:** 0
- **Priority Score:** 91508.7
- **Functions:** 4/13 matched (target 6)
- **Missing functions:** `connect_with_config`, `try_client_handshake`, `create_request`, `connect`, `connect_to_some`, `connect_stream`, `new`, `with_header`, `with_sub_protocol`
- **Types:** 2/2 matched
- **Missing types:** _none_

### 10. stream

- **Target:** `tungstenite.Stream`
- **Similarity:** 0.00
- **Dependents:** 0
- **Priority Score:** 60910.0
- **Functions:** 0/5 matched (target 1)
- **Missing functions:** `set_nodelay`, `fmt`, `read`, `write`, `flush`
- **Types:** 3/4 matched (target 6)
- **Missing types:** `RustlsStreamDebug`

### 11. frame.frame

- **Target:** `frame.Frame`
- **Similarity:** 0.59
- **Dependents:** 0
- **Priority Score:** 43204.1
- **Functions:** 24/28 matched (target 37)
- **Missing functions:** `fmt`, `parse_internal`, `header`, `payload`
- **Types:** 4/4 matched (target 8)
- **Missing types:** _none_
- **Tests:** 1/1 matched

### 12. handshake.mod

- **Target:** `handshake.Mod [STUB]`
- **Similarity:** 0.00
- **Dependents:** 0
- **Priority Score:** 31210.0
- **Functions:** 5/8 matched (target 10)
- **Missing functions:** `handshake`, `fmt`, `from`
- **Types:** 4/4 matched (target 11)
- **Missing types:** _none_
- **Tests:** 1/1 matched

### 13. protocol.message

- **Target:** `protocol.Message`
- **Similarity:** 0.57
- **Dependents:** 0
- **Priority Score:** 23104.3
- **Functions:** 25/26 matched (target 44)
- **Missing functions:** `fmt`
- **Types:** 4/5 matched (target 11)
- **Missing types:** `IncompleteMessageCollector`
- **Tests:** 6/6 matched

### 14. buffer

- **Target:** `tungstenite.Buffer`
- **Similarity:** 0.41
- **Dependents:** 0
- **Priority Score:** 21505.9
- **Functions:** 12/14 matched (target 17)
- **Missing functions:** `as_cursor`, `as_cursor_mut`
- **Types:** 1/1 matched (target 2)
- **Missing types:** _none_
- **Tests:** 2/2 matched

### 15. frame.coding

- **Target:** `frame.Coding`
- **Similarity:** 0.28
- **Dependents:** 0
- **Priority Score:** 21107.2
- **Functions:** 5/7 matched (target 26)
- **Missing functions:** `fmt`, `from`
- **Types:** 4/4 matched (target 29)
- **Missing types:** _none_
- **Tests:** 4/4 matched

### 16. handshake.headers

- **Target:** `handshake.Headers`
- **Similarity:** 0.40
- **Dependents:** 0
- **Priority Score:** 20606.0
- **Functions:** 4/5 matched (target 12)
- **Missing functions:** `from_httparse`
- **Types:** 0/1 matched (target 2)
- **Missing types:** `FromHttparse`
- **Tests:** 3/3 matched

### 17. util

- **Target:** `tungstenite.Util`
- **Similarity:** 0.15
- **Dependents:** 0
- **Priority Score:** 20508.5
- **Functions:** 2/2 matched (target 6)
- **Missing functions:** _none_
- **Types:** 1/3 matched (target 2)
- **Missing types:** `NonBlockingResult`, `Result`

### 18. handshake.machine

- **Target:** `handshake.Machine`
- **Similarity:** 0.66
- **Dependents:** 0
- **Priority Score:** 11303.4
- **Functions:** 6/7 matched (target 10)
- **Missing functions:** `single_round`
- **Types:** 6/6 matched (target 14)
- **Missing types:** _none_

### 19. lib

- **Target:** `tungstenite.Lib`
- **Similarity:** 1.00
- **Dependents:** 0
- **Priority Score:** 10100.0
- **Functions:** 0/0 matched
- **Missing functions:** _none_
- **Types:** 0/1 matched (target 4)
- **Missing types:** `ReadBuffer`

### 20. frame.mod

- **Target:** `frame.Mod [STUB]`
- **Similarity:** 0.00
- **Dependents:** 0
- **Priority Score:** 2110.0
- **Functions:** 19/19 matched (target 24)
- **Missing functions:** _none_
- **Types:** 2/2 matched (target 5)
- **Missing types:** _none_
- **Tests:** 4/4 matched

### 21. frame.mask

- **Target:** `frame.Mask`
- **Similarity:** 0.52
- **Dependents:** 0
- **Priority Score:** 504.8
- **Functions:** 5/5 matched (target 6)
- **Missing functions:** _none_
- **Types:** 0/0 matched (target 1)
- **Missing types:** _none_
- **Tests:** 1/1 matched

### 22. server

- **Target:** `tungstenite.Server`
- **Similarity:** 0.91
- **Dependents:** 0
- **Priority Score:** 400.9
- **Functions:** 4/4 matched
- **Missing functions:** _none_
- **Types:** 0/0 matched
- **Missing types:** _none_

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
| `deflate.mod` | `extensions.compression.deflate.Mod` | 0 | `extensions/compression/deflate/mod.rs` | `extensions/compression/deflate/Mod.kt` |
| `compression.mod` | `extensions.compression.Mod` | 0 | `extensions/compression/mod.rs` | `extensions/compression/Mod.kt` |
| `headers.mod` | `extensions.headers.Mod` | 0 | `extensions/headers/mod.rs` | `extensions/headers/Mod.kt` |
| `extensions.mod` | `extensions.Mod` | 0 | `extensions/mod.rs` | `extensions/Mod.kt` |

