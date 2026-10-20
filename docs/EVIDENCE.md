# Engineering evidence for technical reviewers

Inspect one Feed cache race, its design decision, and regression tests; then
contribution, proof scope, and related cases.

## Lead case: preserve bookmark toggles during refresh

**Problem:** a user toggles a bookmark while `refresh()` is waiting on the
network. A naïve “snapshot bookmarks before fetch, then clear + insert” can
overwrite the newer local intent when remote posts arrive.

| Evidence | Inspect |
| --- | --- |
| **Design decision** | Keep Feed posts **Room-driven** via `Flow`. After the network returns, read the latest bookmark map **inside** the same `withTransaction` as `clear` + `upsert` so a toggle after the response cannot race the replace. Propagate `CancellationException` (no stale success). Bookmarks are **local-only** in this Android sample (no outbox / remote bookmark API). |
| **Regression** | `CachingFeedRepositoryTest.refresh_preservesBookmarkToggledDuringNetworkWait` suspends the fake remote, toggles id `1` on a concurrent coroutine, then completes the fetch and asserts the merged row keeps the mid-flight value. |
| **Passing run** | [Unit · lint · assemble](https://github.com/redjadet/super_demo_android/actions/runs/39417272605) + [Emulator · API 34](https://github.com/redjadet/super_demo_android/actions/runs/39417272616) on **2026-10-20** at [`2d09e86`](https://github.com/redjadet/super_demo_android/commit/2d09e86): **21 unit + 8 connected tests**. Local API 36 gate: [`./bin/checklist`](../bin/checklist) / [local-proof.md](local-proof.md). |
| **Related** | `refresh_writesRemoteIntoCacheTransactionally`, `refresh_returnsLocalCacheWhenRemoteFails`, `toggleBookmark_isAtomic`. |
| **Implementation** | [`CachingFeedRepository.kt`](../app/src/main/java/com/redjadet/superdemo/features/feed/data/CachingFeedRepository.kt) · [`FeedViewModel.kt`](../app/src/main/java/com/redjadet/superdemo/features/feed/presentation/FeedViewModel.kt). |

**Trade-off:** bookmarks remain local to this device. The remote API supplies
posts; it does not synchronize bookmark intent across devices. See
[demo scope](portfolio.md#demo-scope).

## My contribution

I am **İlker Sevim**, owner of `redjadet/super_demo_android`. I designed,
reviewed, and validated the architecture and acceptance criteria. Cursor / Codex
assist with implementation drafts, tests, and docs.

## Verification and proof scope

| Evidence | Commit and scope |
| --- | --- |
| **Recorded hosted run** | [CI 39417272605](https://github.com/redjadet/super_demo_android/actions/runs/39417272605) + [Emulator 39417272616](https://github.com/redjadet/super_demo_android/actions/runs/39417272616), **2026-10-20**, `2d09e86` (AGP 9.4.1 / Gradle 9.8.2 / SDK 37). Includes the presentation and Item-edit validation changes; PR checks validate later revisions. |
| **Unit** | `./gradlew testDebugUnitTest` — repository (incl. concurrent bookmark race), ViewModel, Items, deep links, tablet breakpoint |
| **Local full gate** | `./bin/checklist` — unit + assemble + lint + `connectedDebugAndroidTest` on a booted device/emulator |
| **Hosted CI** | [`.github/workflows/ci.yml`](../.github/workflows/ci.yml) — unit, assembleDebug, lintDebug, assembleDebugAndroidTest |
| **Hosted emulator** | [`.github/workflows/emulator.yml`](../.github/workflows/emulator.yml) — `connectedDebugAndroidTest` on API 34 `aosp_atd` |
| **Newer main runs** | [Actions · branch `main`](https://github.com/redjadet/super_demo_android/actions?query=branch%3Amain). Check the commit SHA when using a run as evidence. |

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

### 3. Missing deep-link post id

| Evidence | Inspect |
| --- | --- |
| **Problem** | A `superdemo://feed/<missing>` deep link must not stay pending forever after a successful load. |
| **Decision** | After Feed finishes loading, navigate to detail; detail shows “Post not found.” (`feedPostMissing`). |
| **Proof** | `FeedDeepLinkInstrumentationTest.warmDeepLink_missingPost_showsNotFoundDetail` |

### 4. Item edit validation

Blank edits must not erase an existing title. `ItemDetailViewModelTest`
reproduces the missing validation, checks that the stored item stays unchanged,
then verifies that a corrected edit saves and clears the error.
`MainActivitySmokeTest.itemEdit_rejectsBlankTitle_thenSavesCorrectedTitle` exercises
the same recovery through the Compose form and confirms the list receives the edit.

### 5. Items one-time seed + Room Flow

| Evidence | Inspect |
| --- | --- |
| **Problem** | Re-seeding after “delete all” contradicts “seeded once.” |
| **Decision** | `ItemSeedStore` flag + `observeItems(): Flow`. |
| **Regression** | `RoomItemRepositoryTest` |

## Reproduce

```bash
cd /path/to/super_demo_android
export JAVA_HOME="$(/usr/libexec/java_home -v 21)"
export ANDROID_HOME="${ANDROID_HOME:-$HOME/Library/Android/sdk}"
export PATH="$JAVA_HOME/bin:$ANDROID_HOME/platform-tools:$ANDROID_HOME/emulator:$PATH"

./gradlew testDebugUnitTest
# Emulator/device must be online for the full gate:
./bin/checklist
```
