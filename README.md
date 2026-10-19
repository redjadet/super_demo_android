# super_demo_android

Kotlin / Jetpack Compose portfolio demo mirroring the structure of
[super_demo_ios](https://github.com/redjadet/super_demo_ios): **Dashboard**,
**Items**, and **Feed** tabs with clean layers, offline-capable Feed caching,
Room-backed Items, Navigation Compose, and Hilt DI.

Author: [İlker Sevim](https://redjadet.github.io/react-web-portfolio/).

**Docs:** [CODEMAP](CODEMAP.md) · [docs/](docs/README.md) · [architecture](docs/architecture.md) · [offline-first](docs/offline-first.md) · [testing](docs/testing.md) · [build/run](docs/build-and-run.md) · [portfolio map](docs/portfolio.md)

## Offline-first (required)

Room is the **local source of truth** for Feed and Items. Core tabs must open and
remain usable with no network:

1. **Seed locally** on first launch (`OfflineFeedSeed` for Feed, Room seed for Items).
2. **Observe Room** via `Flow` in the Feed ViewModel — UI binds to local data first.
3. **Sync when online** through `FeedRepository.refresh()` (JSONPlaceholder). On
   success, Room is replaced with remote posts (bookmarks preserved by id).
4. **Degrade gracefully offline** — remote failures return the local cache with
   `isStale` / `fromCache` so the UI shows a stale banner instead of a hard error.
5. **Dashboard** is fully local sample data (no network dependency).

Airplane-mode check: open Feed → list + banner still render; Items CRUD still works.

## Stack

| Area | Choice |
| --- | --- |
| Language | Kotlin 2.1 |
| UI | Jetpack Compose + Material 3 |
| Architecture | Single-Activity, UDF ViewModels, feature folders (domain / data / presentation) |
| Offline | Room source of truth + optional remote sync |
| Navigation | Navigation Compose + bottom tabs + `superdemo://` deep links |
| DI | Hilt |
| Async | Coroutines + Flow / StateFlow |
| Persistence | Room (Feed cache + Items) |
| Networking | Retrofit + OkHttp + kotlinx.serialization (JSONPlaceholder) |
| Build | Gradle Kotlin DSL + version catalog |
| SDK | `minSdk 26`, `targetSdk` / `compileSdk` 36 |

## Screens vs iOS reference

| iOS (`super_demo_ios`) | Android (this repo) |
| --- | --- |
| Tab: Dashboard (Production Readiness) | Tab: Dashboard + metrics + engineering demos |
| Production risks route | `superdemo://dashboard/risks` |
| Tab: Items (SwiftData CRUD) | Tab: Items (Room CRUD, seeded) |
| Tab: Feed (JSONPlaceholder + cache + stale banner) | Same purpose via `CachingFeedRepository` |
| Bookmark toggle | Local bookmark flag on cached posts |
| Deep links `superdemo://…` | Same custom scheme |
| watchOS / tvOS / Widget / Sign in with Apple / StoreKit / Flutter add-to-app | **Not ported** (Apple / optional surfaces) |

## Open / build / run

Requirements on this Mac Mini layout:

- Android SDK at `~/Library/Android/sdk` (or set `sdk.dir` in `local.properties`)
- JDK 17+ (JDK 21 recommended; Android Studio JBR works)
- Optional: emulator AVD `Small_Phone_3`

```bash
cd /Volumes/Lacie_Ssd/projects/super_demo_android
export JAVA_HOME="/Users/ilkersevim/Library/Java/JavaVirtualMachines/jbr-21.0.11/Contents/Home"
export ANDROID_HOME="$HOME/Library/Android/sdk"
export PATH="$JAVA_HOME/bin:$ANDROID_HOME/platform-tools:$ANDROID_HOME/emulator:$PATH"

# Unit tests
./gradlew testDebugUnitTest

# Install debug APK
./gradlew :app:installDebug

# Full local gate (unit + assemble + lint + connected instrumentation)
# Requires AVD Small_Phone_3 online (API 36; headless -no-window is fine)
./bin/checklist
```

CI (GitHub Actions) runs unit tests, `assembleDebug`, `lintDebug`, and
`assembleDebugAndroidTest`. Connected emulator tests stay local via
`./bin/checklist` on this Mac Mini.

Android Studio: **Open** `/Volumes/Lacie_Ssd/projects/super_demo_android`, run the
`app` configuration on an emulator or device.

### Deep links

```bash
adb shell am start -a android.intent.action.VIEW -d "superdemo://feed/1"
adb shell am start -a android.intent.action.VIEW -d "superdemo://items"
adb shell am start -a android.intent.action.VIEW -d "superdemo://dashboard/risks"
```

## Tests

- **Unit:** `CachingFeedRepositoryTest`, `FeedViewModelTest`, `RoomItemRepositoryTest`, `AppDeepLinkTest`
- **Instrumentation (Mac Mini emulator):** `MainActivitySmokeTest`, `OfflineFeedInstrumentationTest` — AVD `Small_Phone_3`
- Details: [docs/testing.md](docs/testing.md)

## CI + local checklist

GitHub Actions [`.github/workflows/ci.yml`](.github/workflows/ci.yml) runs on
`main` / PRs: **unit tests**, **assembleDebug**, **Android Lint**,
**assembleDebugAndroidTest**. Emulator instrumentation stays a **local** proof
via `./bin/checklist` (not claimed on hosted CI). See [docs/ci.md](docs/ci.md).

## License

MIT — see [LICENSE](LICENSE).
