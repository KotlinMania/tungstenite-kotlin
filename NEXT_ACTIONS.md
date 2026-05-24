# Immediate Actions - High-Value Files

Based on AST analysis, here are the concrete next steps.

## Summary

- **Files Present:** 1/27 (3.7%)
- **Function parity:** 1/318 matched (target 26) — 0.3%
- **Class/type parity:** 4/78 matched (target 29) — 5.1%
- **Combined symbol parity:** 5/396 matched (target 55) — 1.3%
- **Average inline-code cosine:** 0.06 (function body across 1 matched files)
- **Average documentation cosine:** 1.00 (doc text across 1 matched files)
- **Cheat-zeroed Files:** 0
- **Critical Issues:** 1 files with <0.60 function similarity

## Priority 1: Fix Incomplete High-Dependency Files

No incomplete high-dependency files detected.

## Priority 2: Port Missing High-Value Files

Critical missing files (>10 dependencies):

No missing high-value files detected.

## Detailed Work Items

Every matched file is listed below with function and type symbol parity.

### 1. frame.coding

- **Target:** `frame.Coding`
- **Similarity:** 0.06
- **Dependents:** 0
- **Priority Score:** 61109.4
- **Functions:** 1/7 matched (target 26)
- **Missing functions:** `fmt`, `from`, `opcode_from_u8`, `opcode_into_u8`, `closecode_from_u16`, `closecode_into_u16`
- **Types:** 4/4 matched (target 29)
- **Missing types:** _none_
- **Tests:** 0/4 matched

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
