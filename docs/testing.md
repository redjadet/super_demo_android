# Testing

## Unit (`./gradlew testDebugUnitTest`)

| Test | Covers |
| --- | --- |
| `CachingFeedRepositoryTest` | Seed, remote write-through, offline stale cache, concurrent bookmarks |
| `FeedViewModelTest` | Local-first state, offline `showingLocalOnly` |
| `ItemsViewModelTest` | Blank-title validation; successful add |
| `RoomItemRepositoryTest` | Seed + CRUD |
| `AppDeepLinkTest` | Deep-link parsing (Robolectric) |

## Instrumentation (`./gradlew connectedDebugAndroidTest`)

Requires a running emulator/device (this Mac Mini: AVD `Small_Phone_3`, API 36).

| Test | Covers |
| --- | --- |
| `MainActivitySmokeTest` | Dashboard default, Items validation, Feed tabs, risks |
| `OfflineFeedInstrumentationTest` | Room SoT + stale banner text when remote fails |
| `FeedDeepLinkInstrumentationTest` | Warm `superdemo://feed/<missing>` → not-found detail |

Hilt test runner: `com.redjadet.superdemo.HiltTestRunner`.

Espresso / AndroidX Test alphas (`espresso-core` 3.8.0-alpha01) are pinned because
stable Espresso 3.6/3.7 still calls removed `InputManager.getInstance()` on API 36.

`./bin/checklist` clears **`com.redjadet.superdemo.debug`** (debug `applicationIdSuffix`)
before connected tests so Items seed-once leftovers on a reused AVD do not flake
the Items smoke test.

## Commands

See [build-and-run.md](build-and-run.md).
