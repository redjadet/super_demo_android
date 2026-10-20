# Portfolio tour — super_demo_android

Technical-reviewer guide for this **Kotlin / Compose / Room** sample. Sibling
iOS tour: [super_demo_ios portfolio](https://github.com/redjadet/super_demo_ios/blob/main/superDemoApp/docs/portfolio.md).

**Evidence first:** [bookmark-during-refresh → design → regression](EVIDENCE.md#lead-case-preserve-bookmark-toggles-during-refresh),
then [contribution and proof scope](EVIDENCE.md#my-contribution).

## 3-minute path

1. **Dashboard** — release-health metrics + engineering demos list; open **Production risks**.
2. **Feed** — Room list first; pull to sync; stale banner + Retry when remote fails
   (instrumentation forces remote failure).
3. **Items** — one-time seeded Room CRUD; empty state after delete-all (no re-seed).
4. **Deep link** — `adb shell am start -a android.intent.action.VIEW -d "superdemo://feed/1"`.

First launch seeds Feed/Items into empty Room stores (offline-first). Debug
builds also set `BuildConfig.REVIEWER_DEMO_MODE=true` for future demo gates;
seeding itself does not depend on that flag.

## Reviewer map

| Quality theme | Path | Discussion | Proof |
| --- | --- | --- | --- |
| Layer boundaries | `features/*/`, [layers.md](layers.md) | Presentation → Domain ← Data; Hilt in `core/di` | Manual + unit tests |
| Offline Feed SoT | `CachingFeedRepository`, [offline-first.md](offline-first.md) | Seed → observe Room → transactional sync | Unit + `OfflineFeedInstrumentationTest` |
| Bookmark mid-flight | [EVIDENCE.md](EVIDENCE.md) | Merge bookmarks after network, inside transaction | `refresh_preservesBookmarkToggledDuringNetworkWait` |
| Cancellation | `FeedViewModel`, repository | Cancel prior refresh; rethrow cancel | `FeedViewModelTest.refresh_cancelKeepsLocalPostsWithoutNetworkError` |
| Items Flow + seed-once | `ItemLocal`, `ItemSeedStore` | Emptying table does not re-seed | `RoomItemRepositoryTest` |
| Deep links | `AppDeepLink`, `AppRoot` | Consume pending post id once | `AppDeepLinkTest`, smoke UI |
| CI honesty | [ci.md](ci.md), `bin/checklist` | Hosted: unit/lint/assemble; emulator local | Workflow + checklist |
| Design tokens | [DESIGN.md](../DESIGN.md) | M3 + stale banner role | Theme sources |

## Honesty

This is a portfolio sample, **not** a Play Store product.

**In repo:** clean layers, Room SoT, Compose/M3, Hilt, Navigation Compose,
deep links, unit + instrumentation, GHA CI (no hosted emulator).

**Not claimed (intentional):** watchOS / tvOS / Widget companions, Sign in with
Apple, StoreKit, Flutter add-to-app, UIKit showcase, bookmark **outbox** sync to
a real backend, production auth, minify/R8 release hardening.

## Screens vs iOS

| iOS | Android | Notes |
| --- | --- | --- |
| Dashboard / Production Readiness | Dashboard | Compose metrics + demos |
| Items (SwiftData) | Items (Room) | Same purpose; seed-once |
| Feed + cache + stale | Feed + Room cache | Same purpose |
| Bookmark outbox | Local bookmark flag | Documented trade-off |
| Deep links `superdemo://` | Same scheme | Implemented |
| Apple multi-platform extras | Not ported | See honesty |

## Screenshots

In-repo captures (emulator, sample data):

| Dashboard | Items | Feed | Risks |
| --- | --- | --- | --- |
| ![Dashboard](screenshots/android/dashboard.png) | ![Items](screenshots/android/items.png) | ![Feed](screenshots/android/feed.png) | ![Risks](screenshots/android/risks.png) |
