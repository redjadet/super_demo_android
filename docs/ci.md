# CI

Two GitHub Actions workflows:

| Workflow | What it proves |
| --- | --- |
| [`ci.yml`](../.github/workflows/ci.yml) | Unit tests, `assembleDebug`, `lintDebug`, `assembleDebugAndroidTest` |
| [`emulator.yml`](../.github/workflows/emulator.yml) | `connectedDebugAndroidTest` on **API 34** (ubuntu + KVM) |

## Hosted CI (`ci.yml`)

On `push` / `pull_request` to `main`:

| Step | Command / action |
| --- | --- |
| JDK 21 | `actions/setup-java` (Temurin) |
| Unit tests | `./gradlew testDebugUnitTest` |
| Debug assemble | `./gradlew :app:assembleDebug` |
| Android Lint | `./gradlew :app:lintDebug` |
| Compile androidTest | `./gradlew :app:assembleDebugAndroidTest` |

## Hosted emulator (`emulator.yml`)

Runs connected instrumentation on API **34** (`google_apis` / x86_64) via
`reactivecircus/android-emulator-runner`. Clears `com.redjadet.superdemo.debug`
before tests (same seed-once determinism as the local checklist).

**Honesty:** hosted emulator is API 34 for runner stability. Local Mac
[`./bin/checklist`](../bin/checklist) may use a newer AVD (e.g. API 36). Both
lanes exercise the same `androidTest` suite; neither claims the other API level.

Also: Android Lint sets `checkTestSources = false` to avoid an AGP FIR crash on
Robolectric unit-test sources; app production sources are still linted.

## Local gate

Full local proof (unit + assemble + lint + connected): [`./bin/checklist`](../bin/checklist) ·
[local-proof.md](local-proof.md).
