# Unified Project Structure

This section outlines the concrete project structure within our monorepo, optimized for our chosen stack (Quarkus + Remix) and architectural decisions. It uses a multi-module Maven setup for the backend to enforce architectural boundaries.

## Monorepo Root Structure

```plaintext
music-hub-project/
├── apps/                       # Application packages (frontend + backend modules)
│   ├── webui/                  # Remix UI application
│   ├── bootstrap/              # Quarkus runtime (assembles contexts + REST)
│   ├── producer/               # Producer bounded context (multi-module)
│   ├── artist/                 # Artist bounded context (multi-module)
│   └── shared-kernel/          # Shared domain values/events
├── docs/                       # Architecture, PRD, stories, specs
├── docker-compose.yml          # Local services (e.g., PostgreSQL)
└── README.md
```

## Backend Modules Structure (`apps/`)

The backend is organized as a multi-module Maven project to strictly enforce the boundaries of our Hexagonal and Domain-Driven Design. Each context and layer is an independent module with explicitly defined dependencies. Module names are prefixed to ensure clarity and global uniqueness of artifacts. The parent aggregator is `apps/pom.xml`.

```plaintext
apps/
├── pom.xml                 # Parent POM declaring all backend modules
│
├── bootstrap/              # Module for application startup and configuration (Quarkus)
│   ├── pom.xml
│   └── src/
│
├── shared-kernel/          # SHARED KERNEL: Domain values and event contracts
│   └── pom.xml             # --- e.g., ISRC value object, TrackWasRegistered event
│
├── producer/               # Parent module for the 'producer' bounded context
│   ├── pom.xml
│   ├── producer-domain/
│   │   └── pom.xml         # --- CORE DOMAIN: Depends on shared-kernel
│   ├── producer-application/
│   │   └── pom.xml         # --- Depends on: producer-domain, shared-kernel
│   └── producer-adapters/    # Parent module for producer's adapters
│       ├── pom.xml
│       ├── producer-adapter-messaging/
│       │   └── pom.xml     # --- Listens for events (secondary adapter)
│       ├── producer-adapter-persistence/
│       │   └── pom.xml     # --- Implements persistence ports
│       ├── producer-adapter-rest/
│       │   └── pom.xml     # --- Implements REST API (primary adapter)
│       └── producer-adapter-spi/
│           └── pom.xml     # --- Implements external service clients (secondary adapter)
│
└── artist/                 # Parent module for the 'artist' bounded context
    ├── pom.xml
    ├── artist-domain/
    │   └── pom.xml         # --- CORE DOMAIN: Depends on shared-kernel
    ├── artist-application/
    │   └── pom.xml         # --- Depends on: artist-domain, shared-kernel
    └── artist-adapters/      # Parent module for artist's adapters
        ├── pom.xml
        ├── artist-adapter-persistence/
        │   └── pom.xml     # --- Implements persistence ports
        ├── artist-adapter-messaging/
        │   └── pom.xml     # --- Listens for events (secondary adapter)
        ├── artist-adapter-rest/
        │   └── pom.xml     # --- Implements REST API
        └── artist-adapter-spi/
            └── pom.xml     # --- Implements external service clients (secondary adapter)
```
