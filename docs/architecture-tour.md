# Architecture tour (~10 minutes)

Timed walk for reviewers. Paths are under `app/src/main/java/com/redjadet/superdemo/`.

| Minute | Look at | Why |
| --- | --- | --- |
| 0–2 | [`CODEMAP.md`](../CODEMAP.md), [`layers.md`](layers.md) | Feature folders: `domain` / `data` / `presentation` |
| 2–4 | `core/di/AppModule.kt`, `NetworkModule.kt` | Hilt binds Room, repositories, Retrofit |
| 4–6 | `features/feed/data/CachingFeedRepository.kt` | Room SoT, transactional refresh, cancel, stale result |
| 6–8 | `features/feed/presentation/FeedViewModel.kt` | Posts from `observePosts()` only; sync metadata separate |
| 8–9 | `features/items/data/ItemLocal.kt`, `ItemSeedStore.kt` | Flow list + one-time seed |
| 9–10 | `app/navigation/AppRoot.kt`, `AppDeepLink.kt` | Tabs + `superdemo://` consume-once |

**Proof:** [EVIDENCE.md](EVIDENCE.md) · [offline-first.md](offline-first.md) · [testing.md](testing.md)
