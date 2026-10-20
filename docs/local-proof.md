# Local emulator proof

Hosted CI does **not** run connected instrumentation. The local gate is
[`./bin/checklist`](../bin/checklist): unit tests, assemble, lint, then
`connectedDebugAndroidTest` on whatever device/emulator `adb` reports as
`device` (optional `ANDROID_SERIAL`).

## Latest recorded local gate

| Field | Value |
| --- | --- |
| Date | 2026-10-20 |
| Command | `./bin/checklist` |
| Unit | **15** tests, 0 failures |
| Connected | **6** tests, 0 failures (smoke + offline Feed + missing deep-link) |
| Device | `emulator-5554` (AVD `Small_Phone_3`, API 16 image / Android 16) |
| Result | **Checklist green** |

Tip SHA for this proof is the commit that lands this file on `main` (see git history).
Prefer also linking the matching hosted unit CI job in [EVIDENCE.md](EVIDENCE.md)
after that push’s Actions run completes.

## Notes

- Checklist clears `com.redjadet.superdemo.debug` before instrumentation so
  Items seed-once leftovers on a reused AVD do not flake.
- Lint excludes test sources (`checkTestSources = false`) due to an AGP FIR
  issue; see [ci.md](ci.md).
