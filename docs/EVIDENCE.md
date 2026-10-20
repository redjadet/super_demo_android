# Engineering evidence for technical reviewers

Inspect one Feed cache race, its design decision, and regression tests; then
contribution, proof scope, and related cases. Sibling iOS evidence style:
[super_demo_ios EVIDENCE](https://github.com/redjadet/super_demo_ios/blob/main/superDemoApp/docs/EVIDENCE.md).

## Lead case: preserve bookmark toggles during refresh

**Problem:** a user toggles a bookmark while `refresh()` is waiting on the
network. A naïve “snapshot bookmarks before fetch, then clear + insert” can
overwrite the newer local intent when remote posts arrive.

| Evidence | Inspect |
| --- | --- |
| **Design decision** | Keep Feed posts **Room-driven** via `Flow`. After the network returns, read the latest bookmark map **inside** the same `withTransaction` as `clear` + `upsert` so a toggle after the response cannot race the replace. Propagate `CancellationException` (no stale success). Bookmarks are **local-only** in this Android sample (no outbox / remote bookmark API). |
| **Regression** | `CachingFeedRepositoryTest.refresh_preservesBookmarkToggledDuringNetworkWait` toggles id `1` inside the fake remote call and asserts the merged row keeps the mid-flight value. |
| **Related** | `refresh_writesRemoteIntoCacheTransactionally`, `refresh_returnsLocalCacheWhenRemoteFails`, `toggleBookmark_isAtomic`. |
| **Implementation** | [`CachingFeedRepository.kt`](../app/src/main/java/com/redjadet/superdemo/features/feed/data/CachingFeedRepository.kt) · [`FeedViewModel.kt`](../app/src/main/java/com/redjadet/superdemo/features/feed/presentation/FeedViewModel.kt). |

**Trade-off vs iOS:** iOS proves a durable **outbox** race with remote bookmark
sync. Android proves transactional local merge + Room SoT without claiming a
sync engine. Honesty: [production risks](portfolio.md#honesty) and Dashboard
risks screen.

## My contribution

I am **İlker Sevim**, owner of `redjadet/super_demo_android`. I own architecture,
requirements, acceptance criteria, and whether evidence is enough to accept a
change. Cursor / Codex assist with implementation drafts, tests, and docs.

| Responsibility | Contribution |
| --- | --- |
| **Designed** | Feature folders, Hilt composition, Room as Feed/Items SoT, UDF ViewModels |
| **Reviewed** | Offline degrade, cancellation, deep-link consume-once, CI honesty |
| **Validated** | Unit suite + `./bin/checklist` (emulator instrumentation on Mac Mini) |

## Verification and proof scope

| Evidence | Scope |
| --- | --- |
| **Unit** | `./gradlew testDebugUnitTest` — repository, ViewModel, Items, deep links |
| **Local full gate** | `./bin/checklist` — unit + assemble + lint + `connectedDebugAndroidTest` on AVD `Small_Phone_3` |
| **Hosted CI** | [`.github/workflows/ci.yml`](../.github/workflows/ci.yml) — unit, assembleDebug, lintDebug, assembleDebugAndroidTest. **Does not** run an emulator |

## More engineering cases

### 1. Offline Feed with failing remote (instrumentation)

| Evidence | Inspect |
| --- | --- |
| **Problem** | Online-only UI tests can pass without proving Room SoT. |
| **Decision** | Hilt test module replaces the remote with a failing source; assert seeded list + stale banner. |
| **Source** | [`OfflineNetworkTestModule.kt`](../app/src/androidTest/java/com/redjadet/superdemo/di/OfflineNetworkTestModule.kt) · [`OfflineFeedInstrumentationTest.kt`](../app/src/androidTest/java/com/redjadet/superdemo/OfflineFeedInstrumentationTest.kt) |

### 2. Cancellation during refresh

| Evidence | Inspect |
| --- | --- |
| **Problem** | A new refresh must cancel the prior job without treating cancellation as a hard Network error. |
| **Decision** | `refreshJob?.cancel()`; rethrow `CancellationException` in repository and ViewModel. |
| **Source** | `FeedViewModel.refresh` · `CachingFeedRepository.refresh` |

### 3. Items one-time seed + Room Flow

| Evidence | Inspect |
| --- | --- |
| **Problem** | Re-seeding after “delete all” contradicts “seeded once.” One-shot list reads go stale after detail edits. |
| **Decision** | `ItemSeedStore` flag + `observeItems(): Flow`. |
| **Regression** | `RoomItemRepositoryTest` |

## Reproduce

```bash
cd /Volumes/Lacie_Ssd/projects/super_demo_android
export JAVA_HOME="/Users/ilkersevim/Library/Java/JavaVirtualMachines/jbr-21.0.11/Contents/Home"
export ANDROID_HOME="$HOME/Library/Android/sdk"
export PATH="$JAVA_HOME/bin:$ANDROID_HOME/platform-tools:$ANDROID_HOME/emulator:$PATH"

./gradlew testDebugUnitTest
# Emulator must be online for the full gate:
./bin/checklist
```
