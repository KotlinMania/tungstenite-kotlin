# Immediate Actions - High-Value Files

Based on AST analysis, here are the concrete next steps.

## Summary

- **Files Present:** 27/27 (100.0%)
- **Function parity:** 227/357 matched (target 450) — 63.6%
- **Class/type parity:** 73/102 matched (target 235) — 71.6%
- **Combined symbol parity:** 300/459 matched (target 685) — 65.4%
- **Average inline-code cosine:** 0.35 (function body across 20 matched files)
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
- **Similarity:** 0.00
- **Dependents:** 6
- **Priority Score:** 6051110.0
- **Functions:** 0/4 matched (target 51)
- **Missing functions:** `from`, `error_size`, `tls_error_size`, `protocol_error_size`
- **Types:** 6/7 matched (target 69)
- **Missing types:** `Result`
- **Tests:** 0/3 matched

### 2. headers.sec_websocket_extensions

- **Target:** `headers.SecWebsocketExtensions`
- **Similarity:** 0.23
- **Dependents:** 1
- **Priority Score:** 1163007.8
- **Functions:** 9/21 matched (target 28)
- **Missing functions:** `name`, `params`, `value`, `decode`, `encode`, `from`, `from_iter`, `into_iter`, `from_str`, `fmt`, `test_decode`, `test_encode`
- **Types:** 5/9 matched (target 6)
- **Missing types:** `Item`, `IntoIter`, `Err`, `WriteToDyn`
- **Tests:** 3/5 matched

### 3. proxy

- **Target:** `tungstenite.Proxy`
- **Similarity:** 0.37
- **Dependents:** 1
- **Priority Score:** 1154306.2
- **Functions:** 25/39 matched (target 27)
- **Missing functions:** `from_env`, `connect_proxy_stream`, `proxy_from_env_for_host`, `get_env_first`, `connect_http_proxy`, `connect_socks5_proxy`, `connect_to_proxy`, `new`, `read`, `write`, `flush`, `http_connect_handshake_ok`, `socks5_handshake_no_auth`, `socks5_handshake_with_auth`
- **Types:** 3/4 matched
- **Missing types:** `MockStream`
- **Tests:** 6/13 matched

### 4. deflate.config

- **Target:** `deflate.Config [ZERO]`
- **Similarity:** 0.00
- **Dependents:** 0
- **Priority Score:** 173610.0
- **Functions:** 14/29 matched (target 28)
- **Missing functions:** `apply`, `from`, `from_str`, `ordinal`, `name`, `deflate_config_parse_params_valid`, `deflate_rejects_unknown_parameters`, `deflate_rejects_duplicate_parameters`, `interop`, `make_config`, `rejects_unsupported_client_max_window_bits_response`, `parse_extensions`, `parse_deflates`, `simplest`, `client_multiple_offers`
- **Types:** 5/7 matched (target 14)
- **Missing types:** `ParamName`, `Err`
- **Tests:** 3/13 matched

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

### 8. deflate.mod

- **Target:** `deflate.Mod [STUB]`
- **Similarity:** 0.00
- **Dependents:** 0
- **Priority Score:** 121910.0
- **Functions:** 3/15 matched (target 14)
- **Missing functions:** `from`, `interop`, `large_message_compression`, `decompression_limits_applied`, `compressible_payload_prefixes`, `make_frames`, `large_message_decompression`, `decompress_multiple_messages_that_each_set_bfinal`, `one_block`, `sharing_sliding_window`, `deflate_block_with_bfinal_set`, `two_deflate_blocks`
- **Types:** 4/4 matched (target 11)
- **Missing types:** _none_
- **Tests:** 0/11 matched

### 9. frame.utf8

- **Target:** `frame.Utf8`
- **Similarity:** 0.24
- **Dependents:** 0
- **Priority Score:** 101707.6
- **Functions:** 6/14 matched (target 16)
- **Missing functions:** `deref`, `as_ref`, `borrow`, `hash`, `partial_cmp`, `cmp`, `eq`, `fmt`
- **Types:** 1/3 matched (target 2)
- **Missing types:** `Target`, `Error`
- **Tests:** 1/1 matched

### 10. client

- **Target:** `tungstenite.Client`
- **Similarity:** 0.13
- **Dependents:** 0
- **Priority Score:** 91508.7
- **Functions:** 4/13 matched (target 6)
- **Missing functions:** `connect_with_config`, `try_client_handshake`, `create_request`, `connect`, `connect_to_some`, `connect_stream`, `new`, `with_header`, `with_sub_protocol`
- **Types:** 2/2 matched
- **Missing types:** _none_

### 11. stream

- **Target:** `tungstenite.Stream`
- **Similarity:** 0.00
- **Dependents:** 0
- **Priority Score:** 60910.0
- **Functions:** 0/5 matched (target 1)
- **Missing functions:** `set_nodelay`, `fmt`, `read`, `write`, `flush`
- **Types:** 3/4 matched (target 6)
- **Missing types:** `RustlsStreamDebug`

### 12. frame.frame

- **Target:** `frame.Frame`
- **Similarity:** 0.59
- **Dependents:** 0
- **Priority Score:** 43204.1
- **Functions:** 24/28 matched (target 37)
- **Missing functions:** `fmt`, `parse_internal`, `header`, `payload`
- **Types:** 4/4 matched (target 8)
- **Missing types:** _none_
- **Tests:** 1/1 matched

### 13. compression.mod

- **Target:** `compression.Mod [STUB]`
- **Similarity:** 0.00
- **Dependents:** 0
- **Priority Score:** 40710.0
- **Functions:** 0/4 matched (target 2)
- **Missing functions:** `compressor`, `decompressor`, `map`, `from`
- **Types:** 3/3 matched (target 6)
- **Missing types:** _none_

### 14. handshake.mod

- **Target:** `handshake.Mod [STUB]`
- **Similarity:** 0.00
- **Dependents:** 0
- **Priority Score:** 31210.0
- **Functions:** 5/8 matched (target 10)
- **Missing functions:** `handshake`, `fmt`, `from`
- **Types:** 4/4 matched (target 11)
- **Missing types:** _none_
- **Tests:** 1/1 matched

### 15. protocol.message

- **Target:** `protocol.Message`
- **Similarity:** 0.57
- **Dependents:** 0
- **Priority Score:** 23104.3
- **Functions:** 25/26 matched (target 44)
- **Missing functions:** `fmt`
- **Types:** 4/5 matched (target 11)
- **Missing types:** `IncompleteMessageCollector`
- **Tests:** 6/6 matched

### 16. extensions.mod

- **Target:** `extensions.Mod [STUB]`
- **Similarity:** 0.00
- **Dependents:** 0
- **Priority Score:** 21510.0
- **Functions:** 10/12 matched
- **Missing functions:** `accept_offers_with_deflate_disabled`, `accept_offers_picks_first_acceptable_offer`
- **Types:** 3/3 matched (target 7)
- **Missing types:** _none_
- **Tests:** 4/6 matched

### 17. buffer

- **Target:** `tungstenite.Buffer`
- **Similarity:** 0.41
- **Dependents:** 0
- **Priority Score:** 21505.9
- **Functions:** 12/14 matched (target 17)
- **Missing functions:** `as_cursor`, `as_cursor_mut`
- **Types:** 1/1 matched (target 2)
- **Missing types:** _none_
- **Tests:** 2/2 matched

### 18. frame.coding

- **Target:** `frame.Coding`
- **Similarity:** 0.28
- **Dependents:** 0
- **Priority Score:** 21107.2
- **Functions:** 5/7 matched (target 26)
- **Missing functions:** `fmt`, `from`
- **Types:** 4/4 matched (target 29)
- **Missing types:** _none_
- **Tests:** 4/4 matched

### 19. handshake.headers

- **Target:** `handshake.Headers`
- **Similarity:** 0.40
- **Dependents:** 0
- **Priority Score:** 20606.0
- **Functions:** 4/5 matched (target 12)
- **Missing functions:** `from_httparse`
- **Types:** 0/1 matched (target 2)
- **Missing types:** `FromHttparse`
- **Tests:** 3/3 matched

### 20. util

- **Target:** `tungstenite.Util`
- **Similarity:** 0.15
- **Dependents:** 0
- **Priority Score:** 20508.5
- **Functions:** 2/2 matched (target 6)
- **Missing functions:** _none_
- **Types:** 1/3 matched (target 2)
- **Missing types:** `NonBlockingResult`, `Result`

### 21. tls

- **Target:** `tungstenite.Tls`
- **Similarity:** 0.30
- **Dependents:** 0
- **Priority Score:** 20507.0
- **Functions:** 2/3 matched (target 2)
- **Missing functions:** `wrap_stream`
- **Types:** 1/2 matched (target 4)
- **Missing types:** `TlsHandshakeError`

### 22. handshake.machine

- **Target:** `handshake.Machine`
- **Similarity:** 0.66
- **Dependents:** 0
- **Priority Score:** 11303.4
- **Functions:** 6/7 matched (target 10)
- **Missing functions:** `single_round`
- **Types:** 6/6 matched (target 14)
- **Missing types:** _none_

### 23. lib

- **Target:** `tungstenite.Lib`
- **Similarity:** 1.00
- **Dependents:** 0
- **Priority Score:** 10100.0
- **Functions:** 0/0 matched
- **Missing functions:** _none_
- **Types:** 0/1 matched (target 4)
- **Missing types:** `ReadBuffer`

### 24. frame.mod

- **Target:** `frame.Mod [STUB]`
- **Similarity:** 0.00
- **Dependents:** 0
- **Priority Score:** 2110.0
- **Functions:** 19/19 matched (target 24)
- **Missing functions:** _none_
- **Types:** 2/2 matched (target 5)
- **Missing types:** _none_
- **Tests:** 4/4 matched

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

