# super_demo_android

[![CI](https://github.com/redjadet/super_demo_android/actions/workflows/ci.yml/badge.svg?branch=main)](https://github.com/redjadet/super_demo_android/actions/workflows/ci.yml)
[![Emulator](https://github.com/redjadet/super_demo_android/actions/workflows/emulator.yml/badge.svg?branch=main)](https://github.com/redjadet/super_demo_android/actions/workflows/emulator.yml)
![Kotlin](https://img.shields.io/badge/Kotlin-2.2.10-7F52FF?logo=kotlin&logoColor=white)
![AGP](https://img.shields.io/badge/AGP-9.4.1-3DDC84?logo=android&logoColor=white)
![Compose](https://img.shields.io/badge/Jetpack%20Compose-BOM%202025.04.01-4285F4?logo=jetpackcompose&logoColor=white)
![Room](https://img.shields.io/badge/Room-source%20of%20truth-4285F4?logo=android&logoColor=white)
![Hilt](https://img.shields.io/badge/DI-Hilt-8E44AD)
![Android](https://img.shields.io/badge/Platform-Android-3DDC84?logo=android&logoColor=white)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

**Reviewer links:** [3-minute path](docs/portfolio.md#3-minute-path) ·
[Evidence](docs/EVIDENCE.md) · [Architecture tour](docs/architecture-tour.md) ·
[Offline-first](docs/offline-first.md) · [Testing](docs/testing.md) ·
[Local gate](bin/checklist)

A **Kotlin / Jetpack Compose / Room** engineering portfolio demo — the Android
sibling of [super_demo_ios](https://github.com/redjadet/super_demo_ios). It shows
clean feature layers, offline-first Feed caching, Room-backed Items CRUD,
Navigation Compose deep links, and Hilt DI with UDF ViewModels.

This is a portfolio sample, **not** a Play Store product. Hosted **CI** runs
unit/lint/assemble; hosted **Emulator** ([`emulator.yml`](.github/workflows/emulator.yml))
runs connected tests on API 34 when the workflow is green. Local
[`./bin/checklist`](bin/checklist) remains the full Mac gate (often API 36).

Author: [İlker Sevim](https://redjadet.github.io/react-web-portfolio/).

## Engineering decisions and evidence

### Bookmark toggle during Feed refresh

Toggle a bookmark while refresh is in flight: merge the **latest** Room
bookmark map after the network returns, then replace posts inside one
transaction so mid-flight toggles are not overwritten.
[Problem, decision and regression](docs/EVIDENCE.md#lead-case-preserve-bookmark-toggles-during-refresh) ·
[Passing CI · tip `495c8e9`](https://github.com/redjadet/super_demo_android/actions/runs/39407809001).

**My contribution:** designed / reviewed / validated by me; AI-assisted
implementation. [Responsibilities](docs/EVIDENCE.md#my-contribution).

- [Architecture tour](docs/architecture-tour.md) — layers, DI, Feed/Items paths
- [Offline-first](docs/offline-first.md) — Room SoT, stale honesty
- [Portfolio / reviewer map](docs/portfolio.md) — 3-minute path and honesty table

## Run the demo

```bash
cd /path/to/super_demo_android
export JAVA_HOME="${JAVA_HOME:-$(/usr/libexec/java_home -v 21 2>/dev/null)}"
export ANDROID_HOME="${ANDROID_HOME:-$HOME/Library/Android/sdk}"
export PATH="$JAVA_HOME/bin:$ANDROID_HOME/platform-tools:$ANDROID_HOME/emulator:$PATH"

./gradlew :app:installDebug
# Full local gate (needs a booted emulator/device):
./bin/checklist
```

Or open the project in Android Studio and run the `app` configuration.

- [3-minute reviewer path](docs/portfolio.md#3-minute-path)
- [Build & run](docs/build-and-run.md) · [Testing](docs/testing.md) · [CI map](docs/ci.md)
- Deep links: `superdemo://feed/1` · `superdemo://items` · `superdemo://dashboard/risks`

[Documentation index](docs/README.md) · [CODEMAP](CODEMAP.md) · [DESIGN](DESIGN.md) · [MIT License](LICENSE)

## Screenshots

Emulator captures with sample / seeded Room data (first-launch offline seed).

| Dashboard | Items | Feed |
| --- | --- | --- |
| <img src="docs/screenshots/android/dashboard.png" width="220" alt="Dashboard with release-health metrics"> | <img src="docs/screenshots/android/items.png" width="220" alt="Items with seeded Room rows"> | <img src="docs/screenshots/android/feed.png" width="220" alt="Feed list with bookmarks"> |

More: [portfolio screenshots](docs/portfolio.md#screenshots) · Production risks UI in the same folder.

## Stack

| Area | Choice |
| --- | --- |
| Language | Kotlin 2.1 |
| UI | Jetpack Compose + Material 3 |
| Architecture | Single-Activity, UDF ViewModels, feature folders |
| Offline | Room source of truth + optional remote sync |
| Navigation | Navigation Compose + `superdemo://` deep links |
| DI | Hilt |
| Async | Coroutines + Flow / StateFlow |
| Networking | Retrofit + OkHttp + kotlinx.serialization (JSONPlaceholder) |
| Build | Gradle KTS + version catalog · `minSdk 26` · `targetSdk`/`compileSdk` 36 |

## License

MIT — see [LICENSE](LICENSE).
