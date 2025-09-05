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
    participant Tidal as External API (Tidal)
    participant Spotify as External API (Spotify)
    participant DB as Database (PostgreSQL)
    participant EventBus
    
    User->>+FE: 1. Enters ISRC and clicks "Validate"
    FE->>+BE: 2. POST /api/producers (isrc)
    
    BE-->>-FE: 3. Immediate HTTP 202 Accepted response
    Note right of FE: Displays "Processing..." notification
    
    par Parallel Calls
        BE->>+Tidal: 4a. GET /tracks?filter[isrc]=...
        Tidal-->>-BE: 5a. 200 OK response with metadata
    and
        BE->>+Spotify: 4b. GET /search?q=isrc:...
        Spotify-->>-BE: 5b. 200 OK response
    end
    
    BE->>+DB: 6. Saves (INSERT/UPDATE) the Producer aggregate and its Track
    DB-->>-BE: 7. Save confirmation
    
    BE->>EventBus: 8. Publishes [TrackWasRegistered] event
    
    Note right of BE: Initial request processing is complete.
    
    EventBus-->>BE: 9. Notifies Artist Context (asynchronous)
    
    BE->>+DB: 10. Artist Context reads and/or writes the Artist entity
    DB-->>-BE: 11. Save confirmation
````

-----
