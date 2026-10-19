# Portfolio / reviewer map

Android counterpart to the iOS reviewer tour. Sibling reference:
[super_demo_ios](https://github.com/redjadet/super_demo_ios).

## 3-minute path

1. **Dashboard** — release-health metrics + engineering demos list.
2. **Feed** — local Room list; pull to sync; stale banner when offline after sync fail.
3. **Items** — seeded Room CRUD.
4. **Risks** — honesty boundaries (`dashboard/risks`).

## Screens vs iOS

| iOS | Android | Notes |
| --- | --- | --- |
| Dashboard / Production Readiness | Dashboard | Compose metrics + demos |
| Items (SwiftData) | Items (Room) | Same purpose |
| Feed + cache + stale | Feed + Room cache | Same purpose |
| Deep links `superdemo://` | Same scheme | Implemented |
| UIKit showcase | Not ported | Compose-first sample |
| watchOS / tvOS / Widget / StoreKit / Sign in with Apple / Flutter add-to-app | Not claimed | Apple / optional surfaces |

## Honesty

This is a portfolio sample, not a Play Store product. No production auth, no
paid billing, no Wear/TV companions in this repo.
