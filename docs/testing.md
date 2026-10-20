# Testing

## Unit tests

```bash
./gradlew testDebugUnitTest
```

| Test | Coverage |
| --- | --- |
| `CachingFeedRepositoryTest` | Offline seed, transactional refresh, failed refresh fallback and concurrent bookmarks |
| `FeedViewModelTest` | Room-driven state, local-only refresh and cancellation |
| `ItemsViewModelTest` | Blank-title rejection and successful creation |
| `ItemDetailViewModelTest` | Blank edits preserve stored data; corrected edits save and clear the error |
| `RoomItemRepositoryTest` | CRUD and seed-once behavior |
| `AppDeepLinkTest` | Route parsing with Robolectric |
| `ReviewerContentWidthTest` | Phone / wide-window breakpoint |

## Connected instrumentation

```bash
./gradlew connectedDebugAndroidTest
```

Requires an online emulator or attached test device. Hosted tests use API 34;
[recorded local checks](local-proof.md) name their own device and API level.

| Test | Coverage |
| --- | --- |
| `MainActivitySmokeTest` | Dashboard, Items seed list, add/edit validation and save recovery, Feed and scope navigation |
| `OfflineFeedInstrumentationTest` | Room-seeded posts and retry banner with forced remote failure |
| `FeedDeepLinkInstrumentationTest` | Warm missing-post deep link reaches a not-found detail |

Hilt test runner: `com.redjadet.superdemo.HiltTestRunner`. The test module replaces
the remote data source with a failure, so offline tests do not depend on the
public API being available.

AndroidX Test alpha versions are pinned for the API 36 local test environment;
see the [version catalog](../gradle/libs.versions.toml). App-source lint runs in
both the local checklist and hosted CI. Test-source lint is disabled due to the
AGP FIR issue recorded in the build configuration.

## Full local gate

```bash
./bin/checklist
```

Runs unit tests, debug and instrumentation assembly, Android Lint, then connected
tests. It clears `com.redjadet.superdemo.debug` before instrumentation, so seed-once
state left on a reused emulator does not affect the suite.

[Setup](build-and-run.md) · [CI lanes](ci.md) · [Engineering cases](EVIDENCE.md)
