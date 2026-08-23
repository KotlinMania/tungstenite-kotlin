# Immediate Actions - High-Value Files

Based on AST analysis, here are the concrete next steps.

## Summary

- **Files Present:** 13/27 (48.1%)
- **Function parity:** 117/356 matched (target 271) — 32.9%
- **Class/type parity:** 35/91 matched (target 157) — 38.5%
- **Combined symbol parity:** 152/447 matched (target 428) — 34.0%
- **Average inline-code cosine:** 0.31 (function body across 10 matched files)
- **Average documentation cosine:** 0.73 (doc text across 10 matched files)
- **Cheat-zeroed Files:** 5
- **Critical Issues:** 11 files with <0.60 function similarity

## Priority 1: Fix Incomplete High-Dependency Files

No incomplete high-dependency files detected.

## Priority 2: Port Missing High-Value Files

Critical missing files (>10 dependencies):

No missing high-value files detected.

## Detailed Work Items

Every matched file is listed below with function and type symbol parity.

### 1. error

- **Target:** `tungstenite.Error [ZERO]`
- **Similarity:** 0.00
- **Dependents:** 6
- **Priority Score:** 6061110.0
- **Functions:** 0/4 matched (target 51)
- **Missing functions:** `from`, `error_size`, `tls_error_size`, `protocol_error_size`
- **Types:** 5/7 matched (target 68)
- **Missing types:** `Result`, `Error`
- **Tests:** 0/3 matched

### 2. protocol.mod

- **Target:** `protocol.Mod [STUB]`
- **Similarity:** 0.00
- **Dependents:** 0
- **Priority Score:** 375110.0
- **Functions:** 11/44 matched (target 12)
- **Missing functions:** `from_raw_socket`, `from_raw_socket_with_extensions`, `from_partially_read`, `from_partially_read_with_extensions`, `into_inner`, `get_ref`, `get_mut`, `set_config`, `get_config`, `can_write`, `read`, `send`, `write`, `flush`, `close`, `read_message`, `write_message`, `write_pending`, `new`, `read_message_frame`, `do_close`, `buffer_frame`, `set_additional`, `check_max_size`, `check_connection_reset`, `receive_messages`, `size_limiting_text_fragmented`, `size_limiting_binary`, `per_message_deflate_compression`, `per_message_deflate_decompression`, `per_message_compression_not_recognized`, `per_message_compression_decompress_respects_message_size_limit`, `make_message`
- **Types:** 3/7 matched (target 3)
- **Missing types:** `WebSocket`, `WebSocketContext`, `CheckConnectionReset`, `WriteMoc`
- **Tests:** 0/8 matched

### 3. frame.utf8

- **Target:** `frame.Utf8`
- **Similarity:** 0.24
- **Dependents:** 0
- **Priority Score:** 101707.6
- **Functions:** 6/14 matched (target 16)
- **Missing functions:** `deref`, `as_ref`, `borrow`, `hash`, `partial_cmp`, `cmp`, `eq`, `fmt`
- **Types:** 1/3 matched (target 2)
- **Missing types:** `Target`, `Error`
- **Tests:** 1/1 matched

### 4. frame.coding

- **Target:** `frame.Coding`
- **Similarity:** 0.06
- **Dependents:** 0
- **Priority Score:** 61109.4
- **Functions:** 1/7 matched (target 26)
- **Missing functions:** `fmt`, `from`, `opcode_from_u8`, `opcode_into_u8`, `closecode_from_u16`, `closecode_into_u16`
- **Types:** 4/4 matched (target 29)
- **Missing types:** _none_
- **Tests:** 0/4 matched

### 5. frame.mod

- **Target:** `frame.Mod [STUB]`
- **Similarity:** 0.00
- **Dependents:** 0
- **Priority Score:** 52110.0
- **Functions:** 14/19 matched (target 23)
- **Missing functions:** `read_in`, `read_frames`, `write_frames`, `parse_overflow`, `size_limit_hit`
- **Types:** 2/2 matched (target 5)
- **Missing types:** _none_
- **Tests:** 0/4 matched

### 6. handshake.mod

- **Target:** `handshake.Mod [STUB]`
- **Similarity:** 0.00
- **Dependents:** 0
- **Priority Score:** 31210.0
- **Functions:** 5/8 matched (target 9)
- **Missing functions:** `handshake`, `fmt`, `from`
- **Types:** 4/4 matched (target 10)
- **Missing types:** _none_
- **Tests:** 1/1 matched

### 7. frame.frame

- **Target:** `frame.Frame`
- **Similarity:** 0.64
- **Dependents:** 0
- **Priority Score:** 23203.6
- **Functions:** 26/28 matched (target 39)
- **Missing functions:** `fmt`, `parse_internal`
- **Types:** 4/4 matched (target 8)
- **Missing types:** _none_
- **Tests:** 1/1 matched

### 8. protocol.message

- **Target:** `protocol.Message`
- **Similarity:** 0.57
- **Dependents:** 0
- **Priority Score:** 23104.3
- **Functions:** 25/26 matched (target 44)
- **Missing functions:** `fmt`
- **Types:** 4/5 matched (target 11)
- **Missing types:** `IncompleteMessageCollector`
- **Tests:** 6/6 matched

### 9. buffer

- **Target:** `tungstenite.Buffer`
- **Similarity:** 0.41
- **Dependents:** 0
- **Priority Score:** 21505.9
- **Functions:** 12/14 matched (target 17)
- **Missing functions:** `as_cursor`, `as_cursor_mut`
- **Types:** 1/1 matched (target 2)
- **Missing types:** _none_
- **Tests:** 2/2 matched

### 10. handshake.headers

- **Target:** `handshake.Headers`
- **Similarity:** 0.40
- **Dependents:** 0
- **Priority Score:** 20606.0
- **Functions:** 4/5 matched (target 12)
- **Missing functions:** `from_httparse`
- **Types:** 0/1 matched (target 2)
- **Missing types:** `FromHttparse`
- **Tests:** 3/3 matched

### 11. util

- **Target:** `tungstenite.Util`
- **Similarity:** 0.15
- **Dependents:** 0
- **Priority Score:** 20508.5
- **Functions:** 2/2 matched (target 6)
- **Missing functions:** _none_
- **Types:** 1/3 matched (target 2)
- **Missing types:** `NonBlockingResult`, `Result`

### 12. handshake.machine

- **Target:** `handshake.Machine`
- **Similarity:** 0.66
- **Dependents:** 0
- **Priority Score:** 11303.4
- **Functions:** 6/7 matched (target 10)
- **Missing functions:** `single_round`
- **Types:** 6/6 matched (target 14)
- **Missing types:** _none_

### 13. frame.mask

- **Target:** `frame.Mask [ZERO]`
- **Similarity:** 0.00
- **Dependents:** 0
- **Priority Score:** 510.0
- **Functions:** 5/5 matched (target 6)
- **Missing functions:** _none_
- **Types:** 0/0 matched (target 1)
- **Missing types:** _none_
- **Tests:** 1/1 matched

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
| `lib` | `Lib` | 0 | `lib.rs` | `Lib.kt` |

