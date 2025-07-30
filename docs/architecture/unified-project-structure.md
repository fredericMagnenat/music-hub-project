# Unified Project Structure

This section outlines the concrete project structure within our monorepo, optimized for our chosen stack (Quarkus + Remix) and architectural decisions. It uses a multi-module Maven setup for the backend to enforce architectural boundaries.

## Monorepo Root Structure

```plaintext
music-data-hub/
├── .github/                    # CI/CD Workflows (GitHub Actions)
│   └── workflows/
│       └── ci.yaml
├── apps/                       # Deployable application packages
│   ├── frontend/               # The Remix UI application
│   └── backend/                # The Quarkus API application (Maven Parent)
├── packages/                   # Shared local packages
│   └── shared-types/           # Shared TypeScript interfaces
├── infrastructure/             # Infrastructure as Code
│   └── terraform/
├── .gitignore
├── package.json                # Root package.json for monorepo scripts
├── pnpm-workspace.yaml         # PNPM workspace configuration
└── README.md
```

## Backend Multi-Module Structure (`apps/backend/`)

The backend is organized as a multi-module Maven project to strictly enforce the boundaries of our Hexagonal and Domain-Driven Design. Each context and layer is an independent module with explicitly defined dependencies. Module names are prefixed to ensure clarity and global uniqueness of artifacts.

```plaintext
backend/
├── pom.xml                 # Parent POM declaring all backend modules
│
├── bootstrap/              # Module for application startup and configuration
│   ├── pom.xml
│   └── src/
│
├── producer/               # Parent module for the 'producer' bounded context
│   ├── pom.xml
│   ├── producer-domain/
│   │   └── pom.xml         # --- CORE DOMAIN: No external dependencies
│   ├── producer-application/
│   │   └── pom.xml         # --- Depends on: producer-domain
│   ├── producer-adapters/    # Parent module for producer's adapters
│   │   ├── pom.xml
│   │   ├── producer-adapter-messaging/
│   │   │   └── pom.xml     # --- Listens for events (secondary adapter)
│   │   ├── producer-adapter-persistence/
│   │   │   └── pom.xml     # --- Implements persistence ports
│   │   ├── producer-adapter-rest/
│   │   │   └── pom.xml     # --- Implements REST API (primary adapter)
│   │   └── producer-adapter-spi/
│   │       └── pom.xml     # --- Implements external service clients (secondary adapter)
│   └── producer-wiring/
│       └── pom.xml         # --- Assembles producer components
│
└── artist/                 # Parent module for the 'artist' bounded context
    ├── pom.xml
    ├── artist-domain/
    │   └── pom.xml         # --- CORE DOMAIN: No external dependencies
    ├── artist-application/
    │   └── pom.xml         # --- Depends on: artist-domain
    ├── artist-adapters/      # Parent module for artist's adapters
    │   ├── pom.xml
    │   ├── artist-adapter-persistence/
    │   │   └── pom.xml     # --- Implements persistence ports
    │   ├── artist-adapter-messaging/
    │   │   └── pom.xml     # --- Listens for events (secondary adapter)
    │   ├── artist-adapter-rest/
    │   │   └── pom.xml     # --- Implements REST API
    │   └── artist-adapter-spi/
    │       └── pom.xml     # --- Implements external service clients (secondary adapter)
    └── artist-wiring/
        └── pom.xml         # --- Assembles artist components
```
