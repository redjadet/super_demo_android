# Architecture

Single-Activity Jetpack Compose app with clean feature layers and Hilt
composition. Android idioms: ViewModel + StateFlow UDF, Room, Navigation
Compose.

**Start here for a timed walk:** [architecture-tour.md](architecture-tour.md)

| Concern | Doc |
| --- | --- |
| Feature folders / dependency direction | [layers.md](layers.md) |
| Room source of truth + refresh | [offline-first.md](offline-first.md) |
| Tabs + `superdemo://` | [navigation.md](navigation.md) |
| DI | `core/di/AppModule.kt`, `RepositoryBindings.kt` |

Domain types stay free of Android/Hilt imports; Hilt constructs use cases and
binds repositories in `core/di`.
