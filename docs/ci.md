# Continuous integration

| Workflow | Validation | Artifacts |
| --- | --- | --- |
| [CI](../.github/workflows/ci.yml) | Unit tests, debug app assembly, Android Lint and instrumentation assembly | Unit report, lint report, debug APK |
| [Emulator](../.github/workflows/emulator.yml) | Connected instrumentation on API 34 (`aosp_atd`, x86_64) | Connected test report and results |

Both run for pull requests and pushes to `main`; the emulator lane also supports
manual dispatch. Jobs use JDK 21 and the checked-in Gradle wrapper.

## Download the demo

Open a successful [CI run](https://github.com/redjadet/super_demo_android/actions/workflows/ci.yml)
and download **super-demo-debug** from **Artifacts**. Extract and install the APK
on an API 26+ test device. GitHub may require sign-in to download run artifacts.
The debug package is `com.redjadet.superdemo.debug`; artifacts are kept for 14 days.

## Proof boundaries

- Hosted connected tests run on **API 34**. Local runs record their actual device
  and API level separately in [local verification](local-proof.md).
- `compileSdk` and `targetSdk` are **37**. Building against that SDK does not prove
  runtime behavior on every supported API level.
- Android Lint checks app sources. `checkTestSources = false` avoids the AGP FIR
  failure previously observed on Robolectric test sources.
- A green debug build does not establish signed release or Play Store readiness.

## Local equivalent

[`./bin/checklist`](../bin/checklist) runs unit tests, app and instrumentation
assembly, lint and connected tests. It clears debug app data before connected
tests, matching the hosted emulator’s deterministic seed setup.
