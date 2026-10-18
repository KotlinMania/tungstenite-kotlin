# Immediate Actions - High-Value Files

Based on AST analysis, here are the concrete next steps.

## Summary

- **Files Present:** 19/27 (70.4%)
- **Function parity:** 243/325 matched (target 429) — 74.8%
- **Class/type parity:** 60/78 matched (target 205) — 76.9%
- **Combined symbol parity:** 303/403 matched (target 634) — 75.2%
- **Average inline-code cosine:** 0.13 (function body across 19 matched files)
- **Average documentation cosine:** 0.73 (doc text across 19 matched files)
- **Cheat-zeroed Files:** 1
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
- **Priority Score:** 6052009.0
- **Functions:** 9/13 matched (target 61)
- **Missing functions:** `from`, `from`, `from`, `from`
- **Types:** 6/7 matched (target 70)
- **Missing types:** `Error`
- **Tests:** 3/3 matched

### 2. headers.sec_websocket_extensions

- **Target:** `headers.SecWebsocketExtensions`
- **Similarity:** 0.08
- **Dependents:** 1
- **Priority Score:** 1144509.1
- **Functions:** 25/36 matched (target 41)
- **Missing functions:** `name`, `params`, `name`, `value`, `name`, `into_iter`, `fmt`, `encoded_len`, `write_with`, `encoded_len`, `write_with`
- **Types:** 6/9 matched
- **Missing types:** `Item`, `IntoIter`, `Err`
- **Tests:** 5/7 matched

### 3. proxy

- **Target:** `tungstenite.Proxy`
- **Similarity:** 0.14
- **Dependents:** 1
- **Priority Score:** 1084308.5
- **Functions:** 31/39 matched (target 42)
- **Missing functions:** `connect_proxy_stream`, `connect_http_proxy`, `connect_socks5_proxy`, `connect_to_proxy`, `new`, `read`, `write`, `flush`
- **Types:** 4/4 matched (target 5)
- **Missing types:** _none_
- **Tests:** 9/13 matched

### 4. client

- **Target:** `tungstenite.Client`
- **Similarity:** 0.09
- **Dependents:** 0
- **Priority Score:** 142409.1
- **Functions:** 8/22 matched (target 12)
- **Missing functions:** `connect_with_config`, `try_client_handshake`, `create_request`, `connect`, `connect_to_some`, `connect_stream`, `into_client_request`, `into_client_request`, `into_client_request`, `into_client_request`, `into_client_request`, `into_client_request`, `into_client_request`, `into_client_request`
- **Types:** 2/2 matched
- **Missing types:** _none_

### 5. frame.coding

- **Target:** `frame.Coding`
- **Similarity:** 0.07
- **Dependents:** 0
- **Priority Score:** 91809.3
- **Functions:** 5/14 matched (target 26)
- **Missing functions:** `fmt`, `fmt`, `fmt`, `from`, `from`, `fmt`, `from`, `from`, `from`
- **Types:** 4/4 matched (target 29)
- **Missing types:** _none_
- **Tests:** 4/4 matched

### 6. stream

- **Target:** `tungstenite.Stream`
- **Similarity:** 0.02
- **Dependents:** 0
- **Priority Score:** 81309.8
- **Functions:** 1/9 matched (target 2)
- **Missing functions:** `set_nodelay`, `set_nodelay`, `fmt`, `fmt`, `read`, `write`, `flush`, `set_nodelay`
- **Types:** 4/4 matched (target 7)
- **Missing types:** _none_

### 7. deflate.config

- **Target:** `deflate.Config [ZERO]`
- **Similarity:** 0.00
- **Dependents:** 0
- **Priority Score:** 73710.0
- **Functions:** 24/30 matched (target 39)
- **Missing functions:** `apply`, `from`, `from`, `make_config`, `parse_extensions`, `parse_deflates`
- **Types:** 6/7 matched (target 17)
- **Missing types:** `Err`
- **Tests:** 10/13 matched

### 8. frame.utf8

- **Target:** `frame.Utf8`
- **Similarity:** 0.15
- **Dependents:** 0
- **Priority Score:** 72408.5
- **Functions:** 16/21 matched (target 28)
- **Missing functions:** `as_ref`, `as_ref`, `try_from`, `from`, `from`
- **Types:** 1/3 matched
- **Missing types:** `Target`, `Error`
- **Tests:** 1/1 matched

### 9. handshake.server

- **Target:** `handshake.Server`
- **Similarity:** 0.14
- **Dependents:** 0
- **Priority Score:** 72108.6
- **Functions:** 10/12 matched (target 11)
- **Missing functions:** `from_httparse`, `on_request`
- **Types:** 4/9 matched (target 8)
- **Missing types:** `Request`, `Response`, `IncomingData`, `InternalStream`, `FinalResult`
- **Tests:** 2/2 matched

### 10. protocol.message

- **Target:** `protocol.Message`
- **Similarity:** 0.17
- **Dependents:** 0
- **Priority Score:** 54008.3
- **Functions:** 30/35 matched (target 44)
- **Missing functions:** `new`, `from`, `from`, `from`, `fmt`
- **Types:** 5/5 matched (target 12)
- **Missing types:** _none_
- **Tests:** 6/6 matched

### 11. handshake.client

- **Target:** `handshake.Client`
- **Similarity:** 0.15
- **Dependents:** 0
- **Priority Score:** 52308.5
- **Functions:** 14/16 matched (target 24)
- **Missing functions:** `from_httparse`, `construct_expected`
- **Types:** 4/7 matched (target 8)
- **Missing types:** `IncomingData`, `InternalStream`, `FinalResult`
- **Tests:** 7/8 matched

### 12. frame.frame

- **Target:** `frame.Frame`
- **Similarity:** 0.19
- **Dependents:** 0
- **Priority Score:** 43908.1
- **Functions:** 31/35 matched (target 38)
- **Missing functions:** `fmt`, `header`, `payload`, `fmt`
- **Types:** 4/4 matched (target 8)
- **Missing types:** _none_
- **Tests:** 4/4 matched

### 13. util

- **Target:** `tungstenite.Util`
- **Similarity:** 0.01
- **Dependents:** 0
- **Priority Score:** 30609.9
- **Functions:** 2/3 matched (target 6)
- **Missing functions:** `into_non_blocking`
- **Types:** 1/3 matched (target 2)
- **Missing types:** `NonBlockingResult`, `Result`

### 14. tls

- **Target:** `tungstenite.Tls`
- **Similarity:** 0.22
- **Dependents:** 0
- **Priority Score:** 20707.8
- **Functions:** 3/5 matched (target 3)
- **Missing functions:** `wrap_stream`, `wrap_stream`
- **Types:** 2/2 matched (target 6)
- **Missing types:** _none_

### 15. handshake.headers

- **Target:** `handshake.Headers`
- **Similarity:** 0.08
- **Dependents:** 0
- **Priority Score:** 20609.2
- **Functions:** 4/5 matched (target 12)
- **Missing functions:** `from_httparse`
- **Types:** 0/1 matched (target 2)
- **Missing types:** `FromHttparse`
- **Tests:** 3/3 matched

### 16. buffer

- **Target:** `tungstenite.Buffer`
- **Similarity:** 0.06
- **Dependents:** 0
- **Priority Score:** 1509.4
- **Functions:** 14/14 matched (target 19)
- **Missing functions:** _none_
- **Types:** 1/1 matched (target 2)
- **Missing types:** _none_
- **Tests:** 2/2 matched

### 17. handshake.machine

- **Target:** `handshake.Machine`
- **Similarity:** 0.22
- **Dependents:** 0
- **Priority Score:** 1307.8
- **Functions:** 7/7 matched (target 11)
- **Missing functions:** _none_
- **Types:** 6/6 matched (target 14)
- **Missing types:** _none_

### 18. frame.mask

- **Target:** `frame.Mask`
- **Similarity:** 0.12
- **Dependents:** 0
- **Priority Score:** 508.8
- **Functions:** 5/5 matched (target 6)
- **Missing functions:** _none_
- **Types:** 0/0 matched (target 1)
- **Missing types:** _none_
- **Tests:** 1/1 matched

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

