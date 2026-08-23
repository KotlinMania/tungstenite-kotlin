# Immediate Actions - High-Value Files

Based on AST analysis, here are the concrete next steps.

## Summary

- **Files Present:** 8/27 (29.6%)
- **Function parity:** 69/349 matched (target 185) — 19.8%
- **Class/type parity:** 21/87 matched (target 122) — 24.1%
- **Combined symbol parity:** 90/436 matched (target 307) — 20.6%
- **Average inline-code cosine:** 0.23 (function body across 6 matched files)
- **Average documentation cosine:** 0.76 (doc text across 6 matched files)
- **Cheat-zeroed Files:** 4
- **Critical Issues:** 8 files with <0.60 function similarity

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
- **Functions:** 0/4 matched (target 49)
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

### 3. frame.mod

- **Target:** `frame.Mod [STUB]`
- **Similarity:** 0.00
- **Dependents:** 0
- **Priority Score:** 212110.0
- **Functions:** 0/19 matched (target 0)
- **Missing functions:** `new`, `from_partially_read`, `into_inner`, `get_ref`, `get_mut`, `read`, `send`, `write`, `flush`, `set_max_out_buffer_len`, `set_out_buffer_write_len`, `read_frame`, `read_in`, `buffer_frame`, `write_out_buffer`, `read_frames`, `write_frames`, `parse_overflow`, `size_limit_hit`
- **Types:** 0/2 matched (target 0)
- **Missing types:** `FrameSocket`, `FrameCodec`
- **Tests:** 0/4 matched

### 4. frame.utf8

- **Target:** `frame.Utf8`
- **Similarity:** 0.24
- **Dependents:** 0
- **Priority Score:** 101707.6
- **Functions:** 6/14 matched (target 16)
- **Missing functions:** `deref`, `as_ref`, `borrow`, `hash`, `partial_cmp`, `cmp`, `eq`, `fmt`
- **Types:** 1/3 matched (target 2)
- **Missing types:** `Target`, `Error`
- **Tests:** 1/1 matched

### 5. frame.coding

- **Target:** `frame.Coding`
- **Similarity:** 0.06
- **Dependents:** 0
- **Priority Score:** 61109.4
- **Functions:** 1/7 matched (target 26)
- **Missing functions:** `fmt`, `from`, `opcode_from_u8`, `opcode_into_u8`, `closecode_from_u16`, `closecode_into_u16`
- **Types:** 4/4 matched (target 29)
- **Missing types:** _none_
- **Tests:** 0/4 matched

### 6. frame.frame

- **Target:** `frame.Frame`
- **Similarity:** 0.57
- **Dependents:** 0
- **Priority Score:** 53204.3
- **Functions:** 23/28 matched (target 34)
- **Missing functions:** `fmt`, `default`, `parse_internal`, `header_mut`, `compressed_message`
- **Types:** 4/4 matched (target 8)
- **Missing types:** _none_
- **Tests:** 1/1 matched

### 7. protocol.message

- **Target:** `protocol.Message`
- **Similarity:** 0.53
- **Dependents:** 0
- **Priority Score:** 43104.7
- **Functions:** 23/26 matched (target 42)
- **Missing functions:** `new`, `new_compressed`, `fmt`
- **Types:** 4/5 matched (target 11)
- **Missing types:** `IncompleteMessageCollector`
- **Tests:** 6/6 matched

### 8. frame.mask

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
| `handshake.mod` | `handshake.Mod` | 0 | `handshake/mod.rs` | `handshake/Mod.kt` |
| `lib` | `Lib` | 0 | `lib.rs` | `Lib.kt` |

