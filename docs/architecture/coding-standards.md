# Coding Standards

This section defines the minimal but critical rules that development agents (AI or human) must follow. The goal is to enforce consistency and adhere to the architectural decisions made.

## Critical Full-stack Rules

1.  **Shared Types via `shared-types`**: All interfaces or types used in API DTOs must be defined in the `packages/shared-types` package. Both the frontend and backend **must** use this package to ensure contract consistency.
2.  **Domain Immutability**: Domain objects (`domain` layer) are immutable. Any modification to an aggregate must result in a new instance of that aggregate.
3.  **No Logic in Adapters**: REST controllers and other adapters must be as "thin" as possible. They only convert requests/events into application service calls and map the results. All business logic must reside in the `application` and `domain` layers.
4.  **Application Service Entry Points**: The frontend (via REST controllers) and other contexts (via events) must **never** interact directly with repositories or the domain. They must **always** go through the application services (the use case layer).
5.  **Environment Variables**: Never access environment variables directly (`process.env` or `System.getenv`). Use the configuration mechanisms provided by the frameworks (Quarkus or Remix) for clean dependency injection of configuration.

## Naming Conventions

| Element | Frontend (Remix/React) | Backend (Java/Quarkus) | Example |
| :--- | :--- | :--- | :--- |
| UI Components | `PascalCase` | N/A | `TrackCard.tsx` |
| Route Files | `kebab-case.tsx` | N/A | `producers.$id.tsx` |
| Service Classes | N/A | `PascalCase` | `ProducerService.java` |
| REST Endpoints | N/A | `kebab-case` | `/api/v1/producers` |
| DB Tables | `snake_case` | `snake_case` | `producers`, `tracks` |
| DB Columns | `snake_case` | N/A | `producer_code`, `artist_names` | 