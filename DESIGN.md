# Design tokens — super_demo_android

Lean Material 3 roles used by the portfolio UI. Implementation:
[`app/src/main/java/com/redjadet/superdemo/app/ui/theme/Theme.kt`](app/src/main/java/com/redjadet/superdemo/app/ui/theme/Theme.kt).

| Role | Usage |
| :--- | :--- |
| `primary` / `primaryContainer` | Ready chips, selected nav, FAB |
| `secondaryContainer` | Partial status |
| `error` / `errorContainer` | Risk chips, form errors |
| `onSurface` / `onSurfaceVariant` | Body / supporting text |
| `Stale` (custom) | Offline / sync-failed Feed banner wash |

## Principles

- Prefer M3 components (`Scaffold`, `TopAppBar`, `PullToRefreshBox`, `NavigationBar`)
- Empty, error, and stale states are first-class (not silent blanks)
- No Play Store marketing chrome — sample data and honesty copy
