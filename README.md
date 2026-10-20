# super_demo_android

[![3-minute path](https://img.shields.io/badge/3--minute-reviewer%20path-3DDC84)](docs/portfolio.md#3-minute-path)
[![Evidence](https://img.shields.io/badge/Evidence-technical%20reviewer-3DDC84)](docs/EVIDENCE.md)
[![Architecture tour](https://img.shields.io/badge/Architecture-tour-0A7A3E)](docs/architecture-tour.md)
[![Offline-first](https://img.shields.io/badge/Data-Offline--first-16A34A)](docs/offline-first.md)
[![Testing](https://img.shields.io/badge/Testing-Unit%20%7C%20Instrumentation-2E7D32)](docs/testing.md)
[![Local gate](https://img.shields.io/badge/Gate-%2Fbin%2Fchecklist-1B5E20)](bin/checklist)

[![CI](https://github.com/redjadet/super_demo_android/actions/workflows/ci.yml/badge.svg?branch=main)](https://github.com/redjadet/super_demo_android/actions/workflows/ci.yml)

![Kotlin](https://img.shields.io/badge/Kotlin-2.1.20-7F52FF?logo=kotlin&logoColor=white)
![Compose](https://img.shields.io/badge/Jetpack%20Compose-BOM%202025.04.01-4285F4?logo=jetpackcompose&logoColor=white)
![Material 3](https://img.shields.io/badge/Material%203-UI-6750A4?logo=materialdesign&logoColor=white)
![Room](https://img.shields.io/badge/Room-source%20of%20truth-4285F4?logo=android&logoColor=white)
![Hilt](https://img.shields.io/badge/DI-Hilt-8E44AD)
![Coroutines](https://img.shields.io/badge/Async-Coroutines%20%7C%20Flow-E76F00)
![Nav](https://img.shields.io/badge/Navigation-Compose-0F9D58)
![Networking](https://img.shields.io/badge/Networking-Retrofit%20%7C%20OkHttp-0EA5E9)

![Platform](https://img.shields.io/badge/Platform-Android-3DDC84?logo=android&logoColor=white)
![minSdk](https://img.shields.io/badge/minSdk-26-6E6E73?logo=android&logoColor=white)
![targetSdk](https://img.shields.io/badge/targetSdk%20%2F%20compileSdk-36-6E6E73?logo=android&logoColor=white)
![AGP](https://img.shields.io/badge/AGP-8.9.1-3DDC84?logo=android&logoColor=white)
![Architecture](https://img.shields.io/badge/Architecture-Clean%20layers%20%7C%20UDF-0A7A3E)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

A **Kotlin / Jetpack Compose / Room** engineering portfolio demo — the Android
sibling of [super_demo_ios](https://github.com/redjadet/super_demo_ios). It shows
clean feature layers, offline-first Feed caching, Room-backed Items CRUD,
Navigation Compose deep links, and Hilt DI with UDF ViewModels.

This is a portfolio sample, **not** a Play Store product. Hosted CI runs unit
tests, assemble, lint, and androidTest compile; connected emulator proof is
local via [`./bin/checklist`](bin/checklist).

Author: [İlker Sevim](https://redjadet.github.io/react-web-portfolio/).

## Engineering decisions and evidence

### Bookmark toggle during Feed refresh

Toggle a bookmark while refresh is in flight: merge the **latest** Room
bookmark map after the network returns, then replace posts inside one
transaction so mid-flight toggles are not overwritten.
[Problem, decision and regression](docs/EVIDENCE.md#lead-case-preserve-bookmark-toggles-during-refresh) ·
[Passing CI · tip `f75bea2`](https://github.com/redjadet/super_demo_android/actions/runs/39389215172/job/118516393286).

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
