# High-Level Architecture

## Technical Summary

The project's architecture is designed as a **full-stack monorepo** deployed as a single artifact. The backend is a **Quarkus (Java)** application built on the principles of **Hexagonal Architecture**, **Domain-Driven Design (DDD)**, and internal **Event-Driven** communication. It exposes a versionless REST API (`/api/*`). The frontend is a **Single-Page Application (SPA) in Remix (TypeScript)** that is integrated and served by the backend via **Quarkus Quinoa**.

## Platform and Infrastructure Choice

The target platform for the PoC deployment is a **containerized IaaS/PaaS (e.g., Scaleway, DigitalOcean)**. This option offers the best balance of cost, management simplicity for a single container, and performance. The application will be deployed as a single Docker container with a managed PostgreSQL database from the same provider.

## High-Level Architecture Diagram

```mermaid
sequenceDiagram
    participant User
    participant FE as Frontend (Remix SPA)
    participant BE as Backend (Quarkus)
    participant ExtServices as External APIs (Spotify, Tidal...)
    participant DB as Database (PostgreSQL)
    participant EventBus as Internal Event Bus

    User->>+FE: 1. Enters ISRC and clicks "Validate"
    FE->>+BE: 2. POST /api/producers (isrc)

    BE-->>-FE: 3. Immediate HTTP 202 Accepted response

    par Parallel Calls to External APIs
        BE->>+ExtServices: 4. Fetch track metadata
        ExtServices-->>-BE: 5. Response with metadata
    end

    BE->>+DB: 6. Save Producer & Track aggregates
    DB-->>-BE: 7. Confirmation

    BE->>EventBus: 8. Publish [TrackWasRegistered] event

    EventBus-->>BE: 9. Notify Artist Context (async)

    Note over BE,ExtServices: Artist Context Reconciliation
    BE->>+ExtServices: 10. (Optional) Fetch artist details to enrich profile
    ExtServices-->>-BE: 11. Response with artist data

    BE->>+DB: 12. Save/Update Artist aggregate
    DB-->>-BE: 13. Confirmation
```

-----
