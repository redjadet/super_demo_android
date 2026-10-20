# Local emulator proof

Hosted **CI** (`ci.yml`) runs unit/lint/assemble. Hosted **Emulator**
(`emulator.yml`) runs connected tests on **API 34** (`aosp_atd`). The local
Mac gate is [`./bin/checklist`](../bin/checklist): unit tests, assemble, lint,
then `connectedDebugAndroidTest` on whatever device/emulator `adb` reports as
`device` (optional `ANDROID_SERIAL`) — often API 36 on this workstation.

## Latest recorded local gate

| Field | Value |
| --- | --- |
| Date | 2026-10-20 |
| Command | `./bin/checklist` |
| Unit | **20** tests, 0 failures |
| Connected | **7** tests, 0 failures (smoke + blank-title + offline Feed + missing deep-link) |
| Device | `emulator-5554` (AVD `Small_Phone_3`, API 36 / Android 16) |
| Result | **Checklist green** |
| Tip SHA | [`303cea1`](https://github.com/redjadet/super_demo_android/commit/303cea1) |
| Hosted unit CI | [run 39410313726](https://github.com/redjadet/super_demo_android/actions/runs/39410313726) |
| Hosted emulator | [run 39410313774](https://github.com/redjadet/super_demo_android/actions/runs/39410313774) (API 34 `aosp_atd`) |

## Notes

- Checklist clears `com.redjadet.superdemo.debug` before instrumentation so
  Items seed-once leftovers on a reused AVD do not flake.
- Lint excludes test sources (`checkTestSources = false`) due to an AGP FIR
  issue; see [ci.md](ci.md).
- Hosted emulator lane is API 34 for runner stability; local checklist may use
  a newer AVD. Same `androidTest` suite.
