### Refactoring Story: 06 - Establish a Shared Kernel for Backend

> **As an** Architect, **I want** to introduce a Shared Kernel with `shared-domain` and `shared-events` modules and adopt a `UseCase` naming convention for application ports, **in order to** improve decoupling between bounded contexts, enforce a single source of truth for shared concepts, and clarify application entry points.

## Status
Done



### Context & Rationale

During the initial implementation and architectural review, several points of potential friction and tight coupling were identified. This refactoring addresses them proactively to establish a more robust and scalable architecture.

1.  **Decoupling Contexts:** Bounded contexts like `Artist` and `Producer` should not depend on each other directly. They should communicate via shared, public contracts (events).
2.  **Single Source of Truth:** Core concepts used by multiple contexts, like an `ISRC`, must have a single, canonical definition to avoid duplication and divergence.
3.  **Clarity of Intent:** Application entry points should clearly state the specific business use case they represent, rather than being generic "services".

### Technical Tasks

1.  [x] **Create `shared-kernel` Module:**
    *   A new Maven module `apps/shared-kernel` was created.
    *   **Purpose:** To house stable, shared domain concepts (Value Objects, etc.) used across multiple bounded contexts.
    *   The `ISRC` Value Object, with its validation logic, will be the first concept to be placed here.
    *   The `artist-domain` and `producer-domain` modules will now depend on `shared-domain`.

2.  [x] **Move Events into Shared Kernel:**
    *   Public event contracts used for inter-context communication are defined in `apps/shared-kernel` alongside shared values.
    *   The `TrackWasRegistered` event record was placed here.
    *   The `artist-application` and `producer-application` modules now depend on `shared-kernel`.

3.  [x] **Refactor Application Port to a Use Case:**
    *   The interface `ArtistServicePort` was renamed to `ArtistTrackRegistrationUseCase`.
    *   **Rationale:** This aligns with Clean Architecture principles, making the entry point's purpose explicit and adhering to the Interface Segregation Principle.
    *   The `ArtistService` class was updated to implement this new interface.

4.  [x] **Update Architecture Documentation:**
    *   All relevant architecture documents in `docs/architecture/` were updated to reflect the new module structure and naming conventions. This includes:
        *   `unified-project-structure.md`
        *   `high-level-architecture.md`
        *   `data-models.md`
        *   `tech-stack.md`
        *   `coding-standards.md`
        *   `naming-conventions.md`

### Validation

*   The primary validation for this refactoring is to ensure the entire backend application continues to build successfully and all existing tests pass.
*   Run `mvn clean install` from the `apps` directory to confirm.
