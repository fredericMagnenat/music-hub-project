# Tech Stack

This section defines the definitive list of technologies, libraries, and tools for the project. The choices below are based on the PRD and the decisions made so far.

| Category | Technology | Version | Purpose | Rationale |
| :--- | :--- | :--- | :--- | :--- |
| Frontend Language | TypeScript | ~5.x | Static typing for the frontend | Ensures robustness, self-documentation, and maintainability. |
| Frontend Framework | Remix | latest | Full-stack framework for React | Chosen in the PRD. Enables rich user experiences and optimized performance. |
| UI Component Library | shadcn/ui | latest | Composable and accessible UI components | Provides unstyled, copy-paste components for full control and customizability. Aligns with a modern Tailwind CSS workflow. |
| Backend Language | Java | 21 (LTS) | Core backend language | Specified in the PRD. Robust, performant, and widely used in enterprise. |
| Backend Framework | Quarkus | latest | Cloud-native Java framework | Specified in the PRD. Offers fast startup times and low memory consumption. |
| Code Generation | Lombok | latest | Reduce boilerplate Java code | Keeps domain classes (entities, value objects) clean by auto-generating getters, setters, constructors, etc. |
| API Style | REST | - | Frontend/Backend Communication | The simplest and most standard approach for this type of application. |
| Event Bus (In-Memory) | Quarkus Vert.x Events | - | Manage internal, in-process events | Leverages the built-in, lightweight Vert.x event bus for asynchronous communication between contexts (e.g., `TrackWasRegistered`). |
| Database | PostgreSQL | 16.x | Relational data storage | Specified in the PRD. Powerful, open-source, and reliable. |
| Frontend Testing | Vitest & RTL | latest | Unit/component testing | Specified in the PRD. The standard ecosystem for testing React/Vite applications. |
| Backend Testing | JUnit 5 & Mockito | latest | Unit/integration testing | Specified in the PRD. The standard ecosystem for testing in Java/Quarkus. |
| Code Coverage | Jacoco | latest | Measure backend test coverage | Required by the PRD (NFR3: 80% coverage). Provides a clear metric for code quality and testing thoroughness. |
| Architecture Testing | ArchUnit | latest | Enforce architectural rules | Automatically verifies that our hexagonal architecture rules (e.g., dependencies between modules) are not violated. |
| HTTP Service Mocking | io.quarkiverse.wiremock:quarkus-wiremock | latest | Mock external APIs in tests | Allows for reliable and fast integration tests by simulating external HTTP services (e.g., the Music Platform API). |
| Object Mapping | MapStruct | latest | Map between DTOs and domain objects | Generates type-safe, performant mapping code, keeping conversion logic separate from business logic. |
| Monitoring | io.quarkiverse.micrometer.registry:quarkus-micrometer-registry-otlp | latest | Export application metrics | Industry standard (Micrometer + OpenTelemetry) for maximum observability and compatibility with various backends. |
| Build & Deployment | Docker Containers | latest | Application packaging | Industry standard for deployment. Allows running applications consistently anywhere. |
| IaC (Infra as Code) | Terraform | latest | Managing AWS infrastructure | Enables versioned, reproducible, and automated infrastructure management. |
| CI/CD | GitHub Actions | - | Build/test/deploy automation | Natively integrated with GitHub, easy to set up for a project hosted on the platform. |
