# Design tokens — super_demo_android

Lean Material 3 roles used by the portfolio UI. Implementation:
`app/.../app/ui/theme/`.

| Role | Usage |
| --- | --- |
| `primary` / `primaryContainer` | Ready chips, selected nav, FAB |
| `secondaryContainer` | Partial status |
| `error` / `errorContainer` | Risk chips, form errors |
| `onSurface` / `onSurfaceVariant` | Body / supporting text |
| `Stale` (custom) | Offline / sync-failed Feed banner wash |

**Principles**

- Prefer M3 components (`Scaffold`, `TopAppBar`, `PullToRefreshBox`, nav bar)
- Empty, error, and stale states are first-class (not silent blanks)
- No Play Store marketing chrome — sample data and honesty copy
