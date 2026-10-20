# Portfolio tour — super_demo_android

Technical-reviewer guide for this **Kotlin / Compose / Room** sample.

**Evidence first:** [bookmark-during-refresh → design → regression](EVIDENCE.md#lead-case-preserve-bookmark-toggles-during-refresh),
then [contribution and proof scope](EVIDENCE.md#my-contribution).

## 3-minute path

1. **Dashboard** — release-health metrics + engineering demos list; open **Production risks**.
2. **Feed** — Room list first; pull to sync; stale banner + Retry when remote fails
   (instrumentation forces remote failure).
3. **Items** — one-time seeded Room CRUD; empty state after delete-all (no re-seed).
4. **Deep link** — `adb shell am start -a android.intent.action.VIEW -d "superdemo://feed/1"`
   (missing ids open a “Post not found.” detail).

First launch seeds Feed/Items into empty Room stores (offline-first).

## Reviewer map

| Quality theme | Path | Discussion | Proof |
| --- | --- | --- | --- |
| Layer boundaries | `features/*/`, [layers.md](layers.md) | Presentation → Domain ← Data; Hilt in `core/di` | Manual + unit tests |
| Offline Feed SoT | `CachingFeedRepository`, [offline-first.md](offline-first.md) | Seed → observe Room → transactional sync | Unit + `OfflineFeedInstrumentationTest` |
| Bookmark mid-flight | [EVIDENCE.md](EVIDENCE.md) | Merge bookmarks after network, inside transaction | concurrent `refresh_preservesBookmarkToggledDuringNetworkWait` |
| Cancellation | `FeedViewModel`, repository | Cancel prior refresh; rethrow cancel | `FeedViewModelTest` |
| Items Flow + seed-once | `ItemLocal`, `ItemSeedStore` | Emptying table does not re-seed | `RoomItemRepositoryTest` |
| Deep links | `AppDeepLink`, `AppRoot`, Feed detail | Consume pending id; missing → not found | `AppDeepLinkTest`, `FeedDeepLinkInstrumentationTest` |
| CI honesty | [ci.md](ci.md), `bin/checklist` | Hosted: unit/lint/assemble; emulator local | Workflow + [local-proof.md](local-proof.md) |
| Design tokens | [DESIGN.md](../DESIGN.md) | M3 + stale banner role | Theme sources |

## Honesty

This is a portfolio sample, **not** a Play Store product.

**In repo:** clean feature layers (domain free of Hilt imports), Room SoT,
Compose/M3, Hilt, Navigation Compose, deep links, unit + instrumentation,
GHA CI (no hosted emulator).

**Not claimed (intentional):** Apple multi-platform companions, bookmark
**outbox** sync to a real backend, production auth, minify/R8 release
hardening, background WorkManager sync, TTL-based cache expiry UI.

## Screenshots

In-repo captures (emulator, sample data):

| Dashboard | Items | Feed | Risks |
| --- | --- | --- | --- |
| ![Dashboard](screenshots/android/dashboard.png) | ![Items](screenshots/android/items.png) | ![Feed](screenshots/android/feed.png) | ![Risks](screenshots/android/risks.png) |
