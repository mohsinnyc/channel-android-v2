# Channel (Android v2)

Ground-up rebuild of the Channel Android client, architected against the real
`channel-service` API contract from day one instead of growing alongside it.

## Stack

- Kotlin + Jetpack Compose, MVVM, Hilt
- **Ktor + kotlinx.serialization** for networking (not Retrofit/Gson) — both are
  JetBrains-official and coroutine-native, and this keeps the data layer
  structurally ready for Kotlin Multiplatform if/when an iOS target is added,
  without taking on KMP tooling now.
- KSP for annotation processing (Hilt), not kapt — faster builds.

## Conventions

- Navigation vocabulary is called `Destination`, matching the old app's naming.
- One top-level declaration per file.
- No `popUpTo(0)`-style NavController hacks to fake top-level mode switches —
  the root composable switches directly on an app-wide destination state
  (`AppDestination`), and each mode (Auth / Main / etc.) owns its own NavHost.

## Current state

Only the app shell exists so far: cold-start routing based on token presence
and a single `GET /auth/status` call, landing on one of Loading / Auth /
Banned / EmailVerification / Onboarding(step) / Main / Error. Every
destination past that point is a placeholder pending its feature module.
