# CI

Lean GitHub Actions workflow adapted from the iOS repo’s “build + unit + lint”
idea — without Xcode / simulator shards.

## Workflow

[`.github/workflows/ci.yml`](../.github/workflows/ci.yml) on `push` / `pull_request`:

| Step | Command / action |
| --- | --- |
| JDK 21 | `actions/setup-java` (Temurin) |
| Unit tests | `./gradlew testDebugUnitTest` |
| Debug assemble | `./gradlew :app:assembleDebug` |
| Android Lint | `./gradlew :app:lintDebug` |
| Compile androidTest | `./gradlew :app:assembleDebugAndroidTest` |

Artifacts: lint report (always), unit-test HTML report (on failure).

Local full gate (includes emulator instrumentation): [`./bin/checklist`](../bin/checklist).

## Honesty boundaries

- **Instrumentation / emulator tests are not on CI** (heavy for this sample).
  Run them locally on any booted emulator/device via `./bin/checklist`
  (see [testing.md](testing.md) and [local-proof.md](local-proof.md)).
- Android Lint sets `checkTestSources = false` to avoid an AGP FIR crash on
  Robolectric unit-test sources; app production sources are still linted.
- Detekt is not required yet; Android Lint covers the static lane for v1.
