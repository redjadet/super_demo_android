# Architecture

Single-Activity Jetpack Compose app with clean feature layers and Hilt
composition. Inspired by [`super_demo_ios`](https://github.com/redjadet/super_demo_ios)
but implemented with Android idioms (ViewModel + StateFlow UDF, Room, Navigation
Compose) rather than Swift Observation / SwiftData.

## Shell

- `MainActivity` — edge-to-edge host; deep-link intake
- `AppRoot` — Material 3 bottom navigation (Dashboard / Items / Feed) + nested routes
- Theme — Material 3 (`app/ui/theme`)

## Feature slices

Each feature under `features/<name>/` owns:

| Layer | Responsibility | May import |
| --- | --- | --- |
| **Domain** | Entities, repository interfaces, use cases | Kotlin / coroutines only |
| **Data** | Room, Retrofit, mappers, repository impls | Domain + AndroidX / network |
| **Presentation** | Compose UI + `@HiltViewModel` UDF | Domain (+ UI toolkit) |

Composition / DI wiring lives in `core/di/AppModule.kt` (and Hilt entry points).

## UDF

ViewModels expose immutable `StateFlow` UI state. Side effects (refresh, CRUD)
are methods; Compose collects with `collectAsStateWithLifecycle`.

## Related

- [Offline-first](offline-first.md)
- [Layers](layers.md)
- [Navigation](navigation.md)
