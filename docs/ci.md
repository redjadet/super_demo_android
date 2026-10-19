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

Local full gate (includes emulator instrumentation): `./bin/checklist`.

## Honesty boundaries

- **Instrumentation / emulator tests are not on CI** (heavy, flaky for this sample).
  They are required on the Mac Mini local AVD (`Small_Phone_3`) — see
  [testing.md](testing.md) and [build-and-run.md](build-and-run.md).
- Detekt is not required yet; Android Lint covers the static lane for v1.
