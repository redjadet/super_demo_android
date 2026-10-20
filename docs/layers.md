# Feature layers

Mirror of the iOS feature-folder idea, adapted to Kotlin packages:

```
features/<Feature>/
  domain/        # pure models + repository interfaces + use cases
  data/          # Room / Retrofit / repository implementations
  presentation/  # Compose screens + ViewModels
```

## Dependency direction

`presentation → domain ← data`

Presentation must not import Room/Retrofit types. Data implements domain
interfaces. Domain types stay free of Android/Hilt imports; Hilt constructs
use cases and binds repositories in `core/di` (`AppModule` + `RepositoryBindings`).

## Reference features

| Feature | Why it is a reference |
| --- | --- |
| **Items** | Simplest Room CRUD + seed |
| **Feed** | Offline-first cache, remote sync, refresh failure fallback |
| **Dashboard** | Local portfolio metrics + risks route |
