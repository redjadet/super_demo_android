# Offline-first

**Required** for this portfolio sample. Room is the local source of truth for
Feed and Items. Core tabs must open and stay usable with no network.

## Rules

1. **Local first** — UI observes Room (`Flow`) / seeded local data before remote success.
2. **Seed on empty** — `OfflineFeedSeed` (Feed) and Items DAO seed so first launch works offline.
3. **Sync on launch / pull** — `FeedRepository.refresh()` pulls JSONPlaceholder when the Feed opens or the user pulls to refresh (no connectivity observer / WorkManager / background sync in this sample).
4. **Graceful degrade** — remote failure returns local posts with `isStale` / `fromCache` meaning “latest foreground refresh failed while cache is usable” (not TTL age). UI shows a banner, not a blank error if cache exists.
5. **Dashboard** — fully local capability overview (no network).
6. **Bookmarks** — local Room flags only (no outbox / remote bookmark API).

## Code map

| Concern | Path |
| --- | --- |
| Feed contract | `features/feed/domain/FeedModels.kt` |
| Cache + sync | `features/feed/data/CachingFeedRepository.kt` |
| Offline seed | `features/feed/data/OfflineFeedSeed.kt` |
| Items Room | `features/items/data/ItemLocal.kt` |
| Proof tests | `CachingFeedRepositoryTest`, `FeedViewModelTest`, `OfflineFeedInstrumentationTest` |

## Invariants → proof

| ID | Invariant | Proof |
| --- | --- | --- |
| OF-01 | Empty Feed DB seeds local posts | `CachingFeedRepositoryTest.ensureSeededForOffline_*` |
| OF-02 | Remote success replaces cache in one transaction | `CachingFeedRepositoryTest.refresh_writesRemoteIntoCacheTransactionally` |
| OF-03 | Remote failure keeps local + stale | unit + `OfflineFeedInstrumentationTest` (forced failing remote) |
| OF-04 | Bookmark mid-refresh is preserved | `refresh_preservesBookmarkToggledDuringNetworkWait` |
| OF-05 | Items seed once | `RoomItemRepositoryTest.ensureSeeded_seedsOnceAndObserves` |

## Manual check

Enable airplane mode → open Feed and Items → list/CRUD still work → pull-to-refresh keeps local data and stale banner.
