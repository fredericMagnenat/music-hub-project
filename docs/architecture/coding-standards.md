# Coding Standards

This section defines the minimal but critical rules that development agents (AI or human) must follow. The goal is to enforce consistency and adhere to the architectural decisions made.

## Commit Message Format

All git commits **must** follow the [Conventional Commits](https://www.conventionalcommits.org/en/v1.0.0/) specification. This creates a clear and machine-readable commit history, which facilitates automated versioning and changelog generation.

*   **Example `feat`:** `feat(producer): allow track registration via ISRC`
*   **Example `fix`:** `fix(api): correct artist name serialization`
*   **Example `docs`:** `docs(architecture): add db migration strategy`

## Critical Full-stack Rules

1.  **Shared Types via `shared-types`**: All interfaces or types used in API DTOs **must** be defined in the `packages/shared-types` package. Both the frontend and backend **must** use this package to ensure contract consistency.
2.  **Domain Immutability**: Domain objects (`domain` layer) **must be** immutable. Any modification to an aggregate must result in a new instance of that aggregate.
3.  **Enforce Value Objects & Shared Kernel**:
    *   In the backend domain, **prefer creating Value Objects** (`ISRC`, `ProducerCode`, `ArtistName`, etc.) over using primitive types like `String` for any data that has intrinsic rules, format, or constraints.
    *   If a Value Object or other domain concept is used by more than one bounded context, it **must** be placed in the `shared-domain` module.
    *   If an event contract is used for communication between contexts, it **must** be placed in the `shared-events` module.
    *   This enforces domain invariants and creates a clear, reusable "Shared Kernel".
4.  **No Logic in Adapters**: REST controllers and other adapters **must be** as "thin" as possible. They only convert requests/events into application service calls and map the results. All business logic **must reside** in the `application` and `domain` layers.
5.  **Application Use Case Entry Points**: The frontend (via REST controllers) and other contexts (via events) **must never** interact directly with repositories or the domain. They **must always** go through the application layer's use case interfaces (e.g., `RegisterTrackUseCase.java`). The implementing classes are often called "Application Services".
6.  **Configuration over Environment Variables**: **Never** access environment variables directly (`process.env` or `System.getenv`). Use the configuration mechanisms provided by the frameworks (Quarkus or Remix) for clean dependency injection of configuration.
