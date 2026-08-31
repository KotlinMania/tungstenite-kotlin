# Immediate Actions - High-Value Files

Based on AST analysis, here are the concrete next steps.

## Summary

- **Files Present:** 27/27 (100.0%)
- **Function parity:** 287/357 matched (target 542) — 80.4%
- **Class/type parity:** 77/102 matched (target 242) — 75.5%
- **Combined symbol parity:** 364/459 matched (target 784) — 79.3%
- **Average inline-code cosine:** 0.43 (function body across 20 matched files)
- **Average documentation cosine:** 0.67 (doc text across 20 matched files)
- **Cheat-zeroed Files:** 8
- **Critical Issues:** 24 files with <0.60 function similarity

## Priority 1: Fix Incomplete High-Dependency Files

No incomplete high-dependency files detected.

## Priority 2: Port Missing High-Value Files

Critical missing files (>10 dependencies):

No missing high-value files detected.

## Detailed Work Items

Every matched file is listed below with function and type symbol parity.

### 1. error

- **Target:** `tungstenite.Error`
- **Similarity:** 0.34
- **Dependents:** 6
- **Priority Score:** 6001106.5
- **Functions:** 4/4 matched (target 60)
- **Missing functions:** _none_
- **Types:** 7/7 matched (target 71)
- **Missing types:** _none_
- **Tests:** 3/3 matched

### 2. headers.sec_websocket_extensions

- **Target:** `headers.SecWebsocketExtensions`
- **Similarity:** 0.30
- **Dependents:** 1
- **Priority Score:** 1103007.0
- **Functions:** 15/21 matched (target 38)
- **Missing functions:** `name`, `params`, `value`, `from`, `from_iter`, `fmt`
- **Types:** 5/9 matched (target 6)
- **Missing types:** `Item`, `IntoIter`, `Err`, `WriteToDyn`
- **Tests:** 5/5 matched

### 3. proxy

- **Target:** `tungstenite.Proxy`
- **Similarity:** 0.51
- **Dependents:** 1
- **Priority Score:** 1084304.9
- **Functions:** 31/39 matched (target 42)
- **Missing functions:** `connect_proxy_stream`, `connect_http_proxy`, `connect_socks5_proxy`, `connect_to_proxy`, `new`, `read`, `write`, `flush`
- **Types:** 4/4 matched (target 5)
- **Missing types:** _none_
- **Tests:** 9/13 matched

### 4. protocol.mod

- **Target:** `protocol.Mod [STUB]`
- **Similarity:** 0.00
- **Dependents:** 0
- **Priority Score:** 95110.0
- **Functions:** 36/44 matched (target 55)
- **Missing functions:** `from_raw_socket_with_extensions`, `from_partially_read_with_extensions`, `get_config`, `read_message`, `write_message`, `write_pending`, `check_connection_reset`, `make_message`
- **Types:** 6/7 matched
- **Missing types:** `CheckConnectionReset`
- **Tests:** 7/8 matched

### 5. deflate.config

- **Target:** `deflate.Config [ZERO]`
- **Similarity:** 0.00
- **Dependents:** 0
- **Priority Score:** 93610.0
- **Functions:** 22/29 matched (target 36)
- **Missing functions:** `apply`, `from`, `ordinal`, `name`, `make_config`, `parse_extensions`, `parse_deflates`
- **Types:** 5/7 matched (target 14)
- **Missing types:** `ParamName`, `Err`
- **Tests:** 10/13 matched

### 6. frame.utf8

- **Target:** `frame.Utf8`
- **Similarity:** 0.33
- **Dependents:** 0
- **Priority Score:** 81706.7
- **Functions:** 8/14 matched (target 22)
- **Missing functions:** `deref`, `hash`, `partial_cmp`, `cmp`, `eq`, `fmt`
- **Types:** 1/3 matched (target 2)
- **Missing types:** `Target`, `Error`
- **Tests:** 1/1 matched

### 7. handshake.server

- **Target:** `handshake.Server`
- **Similarity:** 0.52
- **Dependents:** 0
- **Priority Score:** 72004.8
- **Functions:** 10/11 matched
- **Missing functions:** `from_httparse`
- **Types:** 3/9 matched (target 5)
- **Missing types:** `Request`, `Response`, `ErrorResponse`, `IncomingData`, `InternalStream`, `FinalResult`
- **Tests:** 2/2 matched

### 8. client

- **Target:** `tungstenite.Client`
- **Similarity:** 0.27
- **Dependents:** 0
- **Priority Score:** 61507.3
- **Functions:** 7/13 matched (target 12)
- **Missing functions:** `connect_with_config`, `try_client_handshake`, `create_request`, `connect`, `connect_to_some`, `connect_stream`
- **Types:** 2/2 matched
- **Missing types:** _none_

### 9. stream

- **Target:** `tungstenite.Stream`
- **Similarity:** 0.00
- **Dependents:** 0
- **Priority Score:** 60910.0
- **Functions:** 0/5 matched (target 1)
- **Missing functions:** `set_nodelay`, `fmt`, `read`, `write`, `flush`
- **Types:** 3/4 matched (target 6)
- **Missing types:** `RustlsStreamDebug`

### 10. handshake.client

- **Target:** `handshake.Client`
- **Similarity:** 0.60
- **Dependents:** 0
- **Priority Score:** 52304.0
- **Functions:** 14/16 matched (target 24)
- **Missing functions:** `from_httparse`, `construct_expected`
- **Types:** 4/7 matched (target 6)
- **Missing types:** `IncomingData`, `InternalStream`, `FinalResult`
- **Tests:** 7/8 matched

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

### 12. compression.mod

- **Target:** `compression.Mod [STUB]`
- **Similarity:** 0.00
- **Dependents:** 0
- **Priority Score:** 40710.0
- **Functions:** 0/4 matched (target 2)
- **Missing functions:** `compressor`, `decompressor`, `map`, `from`
- **Types:** 3/3 matched (target 6)
- **Missing types:** _none_

### 13. handshake.mod

- **Target:** `handshake.Mod [STUB]`
- **Similarity:** 0.00
- **Dependents:** 0
- **Priority Score:** 31210.0
- **Functions:** 5/8 matched (target 10)
- **Missing functions:** `handshake`, `fmt`, `from`
- **Types:** 4/4 matched (target 11)
- **Missing types:** _none_
- **Tests:** 1/1 matched

### 14. protocol.message

- **Target:** `protocol.Message`
- **Similarity:** 0.57
- **Dependents:** 0
- **Priority Score:** 23104.3
- **Functions:** 25/26 matched (target 44)
- **Missing functions:** `fmt`
- **Types:** 4/5 matched (target 11)
- **Missing types:** `IncompleteMessageCollector`
- **Tests:** 6/6 matched

### 15. deflate.mod

- **Target:** `deflate.Mod [STUB]`
- **Similarity:** 0.00
- **Dependents:** 0
- **Priority Score:** 21910.0
- **Functions:** 13/15 matched (target 25)
- **Missing functions:** `from`, `make_frames`
- **Types:** 4/4 matched (target 12)
- **Missing types:** _none_
- **Tests:** 10/11 matched

### 16. buffer

- **Target:** `tungstenite.Buffer`
- **Similarity:** 0.41
- **Dependents:** 0
- **Priority Score:** 21505.9
- **Functions:** 12/14 matched (target 17)
- **Missing functions:** `as_cursor`, `as_cursor_mut`
- **Types:** 1/1 matched (target 2)
- **Missing types:** _none_
- **Tests:** 2/2 matched

### 17. frame.coding

- **Target:** `frame.Coding`
- **Similarity:** 0.28
- **Dependents:** 0
- **Priority Score:** 21107.2
- **Functions:** 5/7 matched (target 26)
- **Missing functions:** `fmt`, `from`
- **Types:** 4/4 matched (target 29)
- **Missing types:** _none_
- **Tests:** 4/4 matched

### 18. handshake.headers

- **Target:** `handshake.Headers`
- **Similarity:** 0.40
- **Dependents:** 0
- **Priority Score:** 20606.0
- **Functions:** 4/5 matched (target 12)
- **Missing functions:** `from_httparse`
- **Types:** 0/1 matched (target 2)
- **Missing types:** `FromHttparse`
- **Tests:** 3/3 matched

### 19. util

- **Target:** `tungstenite.Util`
- **Similarity:** 0.15
- **Dependents:** 0
- **Priority Score:** 20508.5
- **Functions:** 2/2 matched (target 6)
- **Missing functions:** _none_
- **Types:** 1/3 matched (target 2)
- **Missing types:** `NonBlockingResult`, `Result`

### 20. tls

- **Target:** `tungstenite.Tls`
- **Similarity:** 0.30
- **Dependents:** 0
- **Priority Score:** 20507.0
- **Functions:** 2/3 matched (target 2)
- **Missing functions:** `wrap_stream`
- **Types:** 1/2 matched (target 4)
- **Missing types:** `TlsHandshakeError`

### 21. handshake.machine

- **Target:** `handshake.Machine`
- **Similarity:** 0.66
- **Dependents:** 0
- **Priority Score:** 11303.4
- **Functions:** 6/7 matched (target 10)
- **Missing functions:** `single_round`
- **Types:** 6/6 matched (target 14)
- **Missing types:** _none_

### 22. lib

- **Target:** `tungstenite.Lib`
- **Similarity:** 1.00
- **Dependents:** 0
- **Priority Score:** 10100.0
- **Functions:** 0/0 matched
- **Missing functions:** _none_
- **Types:** 0/1 matched (target 4)
- **Missing types:** `ReadBuffer`

### 23. frame.mod

- **Target:** `frame.Mod [STUB]`
- **Similarity:** 0.00
- **Dependents:** 0
- **Priority Score:** 2110.0
- **Functions:** 19/19 matched (target 24)
- **Missing functions:** _none_
- **Types:** 2/2 matched (target 5)
- **Missing types:** _none_
- **Tests:** 4/4 matched

### 24. extensions.mod

- **Target:** `extensions.Mod [STUB]`
- **Similarity:** 0.00
- **Dependents:** 0
- **Priority Score:** 1510.0
- **Functions:** 12/12 matched (target 14)
- **Missing functions:** _none_
- **Types:** 3/3 matched (target 7)
- **Missing types:** _none_
- **Tests:** 6/6 matched

### 25. frame.mask

- **Target:** `frame.Mask`
- **Similarity:** 0.52
- **Dependents:** 0
- **Priority Score:** 504.8
- **Functions:** 5/5 matched (target 6)
- **Missing functions:** _none_
- **Types:** 0/0 matched (target 1)
- **Missing types:** _none_
- **Tests:** 1/1 matched

### 26. server

- **Target:** `tungstenite.Server`
- **Similarity:** 0.91
- **Dependents:** 0
- **Priority Score:** 400.9
- **Functions:** 4/4 matched
- **Missing functions:** _none_
- **Types:** 0/0 matched
- **Missing types:** _none_

### 27. headers.mod

- **Target:** `headers.Mod [STUB]`
- **Similarity:** 0.00
- **Dependents:** 0
- **Priority Score:** 210.0
- **Functions:** 2/2 matched
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

