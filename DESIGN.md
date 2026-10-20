# Design notes

A quiet green Material 3 palette, clear reading hierarchy and content that stays
usable when connectivity fails. Theme implementation:
[Theme.kt](app/src/main/java/com/redjadet/superdemo/app/ui/theme/Theme.kt).

| Role | Use |
| --- | --- |
| `primary` / `primaryContainer` | Primary actions and dashboard introduction |
| `secondaryContainer` | Selected navigation |
| `surfaceContainer*` | Navigation and cards, with matching light/dark roles |
| `tertiaryContainer` / `onTertiaryContainer` | Refresh failure banner with readable paired colors |
| `error` | Form validation |
| `onSurface` / `onSurfaceVariant` | Primary and supporting text |

- Describe capabilities and user actions; keep engineering evidence in the docs.
- Use Material controls with labeled actions and decorative icons where text is present.
- Preserve saved content on refresh failure and offer a visible retry action.
- Cap content at 840dp when window width reaches 600dp, including details and scope.
- Let detail content scroll for larger text, long posts and smaller windows.
- Treat screenshots as captured runtime evidence; refresh them when presentation changes.
