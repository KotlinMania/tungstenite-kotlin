# Immediate Actions - High-Value Files

Based on AST analysis, here are the concrete next steps.

## Summary

- **Files Present:** 19/27 (70.4%)
- **Function parity:** 185/325 matched (target 344) — 56.9%
- **Class/type parity:** 59/78 matched (target 190) — 75.6%
- **Combined symbol parity:** 244/403 matched (target 534) — 60.5%
- **Average inline-code cosine:** 0.11 (function body across 19 matched files)
- **Average documentation cosine:** 0.72 (doc text across 19 matched files)
- **Cheat-zeroed Files:** 0
- **Critical Issues:** 19 files with <0.60 function similarity
- **Needs Review:** 0 files with 0.60-0.84 function similarity
- **Excellent:** 0 files with >=0.85 function similarity

## Priority 1: Fix Incomplete High-Dependency Files

No incomplete high-dependency files detected.

## Priority 2: Port Missing High-Value Files

Critical missing files (>10 dependencies):

No missing high-value files detected.

## Detailed Work Items

Every matched file is listed below with function and type symbol parity.

### 1. error

- **Target:** `tungstenite.Error`
- **Similarity:** 0.10
- **Dependents:** 6
- **Priority Score:** 6082009.0
- **Functions:** 6/13 matched (target 57)
- **Missing functions:** `from`, `from`, `from`, `from`, `error_size`, `tls_error_size`, `protocol_error_size`
- **Types:** 6/7 matched (target 69)
- **Missing types:** `Error`
- **Tests:** 0/3 matched

### 2. headers.sec_websocket_extensions

- **Target:** `headers.SecWebsocketExtensions`
- **Similarity:** 0.07
- **Dependents:** 1
- **Priority Score:** 1194509.4
- **Functions:** 20/36 matched (target 30)
- **Missing functions:** `name`, `params`, `name`, `value`, `name`, `into_iter`, `fmt`, `encoded_len`, `write_with`, `test_decode`, `test_encode`, `parse_separate_headers`, `round_trip_complex`, `write_to_exact_encoded_len`, `encoded_len`, `write_with`
- **Types:** 6/9 matched (target 8)
- **Missing types:** `Item`, `IntoIter`, `Err`
- **Tests:** 0/7 matched

### 3. proxy

- **Target:** `tungstenite.Proxy`
- **Similarity:** 0.10
- **Dependents:** 1
- **Priority Score:** 1184309.0
- **Functions:** 22/39 matched (target 30)
- **Missing functions:** `connect_proxy_stream`, `connect_http_proxy`, `connect_socks5_proxy`, `connect_to_proxy`, `new`, `read`, `write`, `flush`, `split_host_port_basic`, `split_host_port_ipv6`, `no_proxy_star`, `no_proxy_suffix`, `http_connect_request_with_auth`, `http_connect_parse_ok`, `http_connect_handshake_ok`, `socks5_handshake_no_auth`, `socks5_handshake_with_auth`
- **Types:** 3/4 matched (target 3)
- **Missing types:** `MockStream`
- **Tests:** 0/13 matched

### 4. deflate.config

- **Target:** `deflate.Config`
- **Similarity:** 0.06
- **Dependents:** 0
- **Priority Score:** 173709.4
- **Functions:** 14/30 matched (target 23)
- **Missing functions:** `apply`, `from`, `from`, `deflate_config_parse_params_valid`, `deflate_rejects_unknown_parameters`, `deflate_rejects_duplicate_parameters`, `deflate_config_minimal_client_offer`, `deflate_server_respects_offer_server_no_context_takeover`, `rejects_unsupported_client_max_window_bits_offer`, `interop`, `make_config`, `rejects_unsupported_client_max_window_bits_response`, `parse_extensions`, `parse_deflates`, `simplest`, `client_multiple_offers`
- **Types:** 6/7 matched (target 16)
- **Missing types:** `Err`
- **Tests:** 0/13 matched

### 5. client

- **Target:** `tungstenite.Client`
- **Similarity:** 0.09
- **Dependents:** 0
- **Priority Score:** 142409.1
- **Functions:** 8/22 matched (target 12)
- **Missing functions:** `connect_with_config`, `try_client_handshake`, `create_request`, `connect`, `connect_to_some`, `connect_stream`, `into_client_request`, `into_client_request`, `into_client_request`, `into_client_request`, `into_client_request`, `into_client_request`, `into_client_request`, `into_client_request`
- **Types:** 2/2 matched
- **Missing types:** _none_

### 6. frame.coding

- **Target:** `frame.Coding`
- **Similarity:** 0.00
- **Dependents:** 0
- **Priority Score:** 131810.0
- **Functions:** 1/14 matched (target 20)
- **Missing functions:** `fmt`, `fmt`, `fmt`, `from`, `from`, `fmt`, `from`, `from`, `from`, `opcode_from_u8`, `opcode_into_u8`, `closecode_from_u16`, `closecode_into_u16`
- **Types:** 4/4 matched (target 28)
- **Missing types:** _none_
- **Tests:** 0/4 matched

### 7. handshake.client

- **Target:** `handshake.Client`
- **Similarity:** 0.05
- **Dependents:** 0
- **Priority Score:** 122309.5
- **Functions:** 7/16 matched
- **Missing functions:** `from_httparse`, `random_keys`, `construct_expected`, `request_formatting`, `request_formatting_with_host`, `request_formatting_with_at`, `request_with_compression`, `response_parsing`, `invalid_custom_request`
- **Types:** 4/7 matched
- **Missing types:** `IncomingData`, `InternalStream`, `FinalResult`
- **Tests:** 0/8 matched

### 8. protocol.message

- **Target:** `protocol.Message`
- **Similarity:** 0.11
- **Dependents:** 0
- **Priority Score:** 114008.9
- **Functions:** 24/35 matched (target 38)
- **Missing functions:** `new`, `from`, `from`, `from`, `fmt`, `display`, `binary_convert`, `binary_convert_bytes`, `binary_convert_vec`, `binary_convert_into_bytes`, `text_convert`
- **Types:** 5/5 matched (target 11)
- **Missing types:** _none_
- **Tests:** 0/6 matched

### 9. handshake.server

- **Target:** `handshake.Server`
- **Similarity:** 0.12
- **Dependents:** 0
- **Priority Score:** 92108.8
- **Functions:** 8/12 matched (target 9)
- **Missing functions:** `from_httparse`, `on_request`, `request_parsing`, `request_replying`
- **Types:** 4/9 matched (target 7)
- **Missing types:** `Request`, `Response`, `IncomingData`, `InternalStream`, `FinalResult`
- **Tests:** 0/2 matched

### 10. frame.frame

- **Target:** `frame.Frame`
- **Similarity:** 0.15
- **Dependents:** 0
- **Priority Score:** 83908.5
- **Functions:** 27/35 matched (target 34)
- **Missing functions:** `fmt`, `header`, `payload`, `fmt`, `parse`, `format`, `format_into_buf`, `display`
- **Types:** 4/4 matched (target 7)
- **Missing types:** _none_
- **Tests:** 0/4 matched

### 11. frame.utf8

- **Target:** `frame.Utf8`
- **Similarity:** 0.15
- **Dependents:** 0
- **Priority Score:** 82408.5
- **Functions:** 15/21 matched (target 26)
- **Missing functions:** `as_ref`, `as_ref`, `try_from`, `from`, `from`, `hash_consistency`
- **Types:** 1/3 matched (target 2)
- **Missing types:** `Target`, `Error`
- **Tests:** 0/1 matched

### 12. stream

- **Target:** `tungstenite.Stream`
- **Similarity:** 0.02
- **Dependents:** 0
- **Priority Score:** 81309.8
- **Functions:** 1/9 matched (target 2)
- **Missing functions:** `set_nodelay`, `set_nodelay`, `fmt`, `fmt`, `read`, `write`, `flush`, `set_nodelay`
- **Types:** 4/4 matched (target 7)
- **Missing types:** _none_

### 13. handshake.headers

- **Target:** `handshake.Headers`
- **Similarity:** 0.00
- **Dependents:** 0
- **Priority Score:** 50610.0
- **Functions:** 1/5 matched (target 9)
- **Missing functions:** `from_httparse`, `headers`, `headers_iter`, `headers_incomplete`
- **Types:** 0/1 matched
- **Missing types:** `FromHttparse`
- **Tests:** 0/3 matched

### 14. util

- **Target:** `tungstenite.Util`
- **Similarity:** 0.01
- **Dependents:** 0
- **Priority Score:** 40609.9
- **Functions:** 1/3 matched (target 1)
- **Missing functions:** `into_non_blocking`, `into_non_blocking`
- **Types:** 1/3 matched (target 1)
- **Missing types:** `NonBlockingResult`, `Result`

### 15. buffer

- **Target:** `tungstenite.Buffer`
- **Similarity:** 0.04
- **Dependents:** 0
- **Priority Score:** 21509.6
- **Functions:** 12/14 matched (target 15)
- **Missing functions:** `simple_reading`, `reading_in_chunks`
- **Types:** 1/1 matched
- **Missing types:** _none_
- **Tests:** 0/2 matched

### 16. tls

- **Target:** `tungstenite.Tls`
- **Similarity:** 0.22
- **Dependents:** 0
- **Priority Score:** 20707.8
- **Functions:** 3/5 matched (target 3)
- **Missing functions:** `wrap_stream`, `wrap_stream`
- **Types:** 2/2 matched (target 6)
- **Missing types:** _none_

### 17. frame.mask

- **Target:** `frame.Mask`
- **Similarity:** 0.08
- **Dependents:** 0
- **Priority Score:** 10509.2
- **Functions:** 4/5 matched (target 4)
- **Missing functions:** `test_apply_mask`
- **Types:** 0/0 matched
- **Missing types:** _none_
- **Tests:** 0/1 matched

### 18. handshake.machine

- **Target:** `handshake.Machine`
- **Similarity:** 0.22
- **Dependents:** 0
- **Priority Score:** 1307.8
- **Functions:** 7/7 matched (target 11)
- **Missing functions:** _none_
- **Types:** 6/6 matched (target 14)
- **Missing types:** _none_

### 19. server

- **Target:** `tungstenite.Server`
- **Similarity:** 0.53
- **Dependents:** 0
- **Priority Score:** 404.7
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
| `handshake.mod` | `handshake.Mod` | 0 | `handshake/mod.rs` | `handshake/Mod.kt` |
| `lib` | `Lib` | 0 | `lib.rs` | `Lib.kt` |
| `frame.mod` | `protocol.frame.Mod` | 0 | `protocol/frame/mod.rs` | `protocol/frame/Mod.kt` |
| `protocol.mod` | `protocol.Mod` | 0 | `protocol/mod.rs` | `protocol/Mod.kt` |

