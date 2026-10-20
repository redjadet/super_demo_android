# Navigation

- **Shell:** Material 3 `NavigationBar` with three tabs — Dashboard, Items, Feed
- **Library:** Navigation Compose (`NavHost` in `AppRoot`)
- **Deep links:** custom scheme `superdemo://` (see `AppDeepLink`)

| URL | Behavior |
| --- | --- |
| `superdemo://dashboard` | Dashboard tab |
| `superdemo://dashboard/risks` | Demo scope screen (route retained) |
| `superdemo://items` | Items tab |
| `superdemo://feed` | Feed tab |
| `superdemo://feed/<id>` | Feed tab + post detail when present |

```bash
adb shell am start -a android.intent.action.VIEW -d "superdemo://feed/1" -p com.redjadet.superdemo.debug
```
