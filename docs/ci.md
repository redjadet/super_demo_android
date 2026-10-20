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

Runs connected instrumentation on API **34** (`aosp_atd` / x86_64) via
`reactivecircus/android-emulator-runner`. Clears `com.redjadet.superdemo.debug`
before tests (same seed-once determinism as the local checklist).

**Honesty:** hosted emulator is API 34 for runner stability. Local Mac
[`./bin/checklist`](../bin/checklist) may use a newer AVD (e.g. API 36). Both
lanes exercise the same `androidTest` suite; neither claims the other API level.

Also: Android Lint sets `checkTestSources = false` to avoid an AGP FIR crash on
Robolectric unit-test sources; app production sources are still linted.

## SDK / dependency honesty

- `compileSdk` / `targetSdk` **37** (Android 16). The IDE `EditedTargetSdkVersion`
  inspection is a one-time editor nudge after the bump; behaviors that change at
  37 were reviewed for this sample (no special photo-picker / bg-restriction paths).
- AGP Lint `NewerVersionAvailable` still reports Kotlin **2.4**, Retrofit **3**,
  OkHttp **5**, and kotlinx-serialization **1.12**. Those are deferred majors
  (language / breaking HTTP stack) until a dedicated upgrade pass — not ignored
  in lint config.

## Local gate

Full local proof (unit + assemble + lint + connected): [`./bin/checklist`](../bin/checklist) ·
[local-proof.md](local-proof.md).
