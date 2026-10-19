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
interfaces. Hilt binds implementations in `core/di`.

## Reference features

| Feature | Why it is a reference |
| --- | --- |
| **Items** | Simplest Room CRUD + seed |
| **Feed** | Offline-first cache, remote sync, stale honesty |
| **Dashboard** | Local portfolio metrics + risks route |
