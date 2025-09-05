# Tech Stack

This table is the single source of truth for all technologies, frameworks, and versions to be used.

| Category | Technology | Version | Role | Rationale |
| :--- | :--- | :--- | :--- | :--- |
| **Backend Language** | Java | 21 | Primary language for business logic. | Stability (LTS), performance, and mature ecosystem. |
| **Backend Framework**| Quarkus | 3.25.3 | Main framework for the backend and application. | High performance, developer experience, cloud-native. |
| | **SmallRye Mutiny** | Integrated with Quarkus | Asynchronous and reactive programming. | For non-blocking, end-to-end processing and increased scalability. |
| **Frontend Language**| TypeScript | 5.9.2 | Primary language for the user interface. | Static typing for robustness and maintainability. |
| **Frontend Framework**| Remix | 2.9.2 | Framework for building the user interface. | Modern web architecture, performance, good integration with Vite. |
| **Database** | PostgreSQL | 16+ | Primary data storage. | Powerful, reliable, open-source, and well-supported by Hibernate. |
| **Data Access** | Hibernate ORM + Panache | Managed by Quarkus BOM | Data access layer for the backend. | Standard Quarkus integration, simplifies CRUD operations. |
| **DB Migrations** | Flyway | Managed by Quarkus BOM | Database schema change management. | Standard, versioned, and reliable approach for SQL migrations. |
| **API Style** | REST | - | Communication protocol between frontend and backend. | Industry standard, simple to use and well-understood. |
| **Event Bus**| Vert.x EventBus | Managed by Quarkus BOM | Internal asynchronous communication between contexts. | Integrated with Quarkus, lightweight and performant for a reactive architecture. |
| **Build & Dependencies**| Maven (Backend), npm (Frontend)| - | Build and dependency management. | Standard tools for their respective ecosystems. |
| **Styling & UI** | Tailwind CSS | 4.1.11 | CSS framework for the user interface. | Utility-first approach for rapid and consistent development. |
| | **shadcn/ui** | N/A (via CLI) | Base of reusable UI components. | Pragmatic approach to build a custom UI without heavy external dependencies. |
| **Testing & Quality** | JUnit 5, Mockito | Managed by Quarkus BOM | Foundation for unit and integration tests. | Standard testing stack for the Java/Quarkus ecosystem. |
| | **AssertJ** | 3.24.2 | Fluent assertions for tests. | Improves the clarity and readability of tests. |
| | **WireMock** | 1.5.1 | Mocking external services in tests. | Allows testing integration with external APIs in isolation. |
| | **ArchUnit** | 1.3.0 | Validation of architectural rules in code. | Ensures compliance with hexagonal architecture constraints. |
| | **Jacoco** | 0.8.13 | Code coverage measurement. | Ensures the 80% coverage target (from PRD) is tracked. |
| | **Vitest, React Testing Library**| 3.2.4+ | Component testing for the frontend. | Modern and efficient ecosystem for testing React components. |
| **Dev Tools** | **Lombok** | 1.18.30 | Boilerplate code reduction. | Improves code readability and maintainability. |
| | **MapStruct** | 1.5.5.Final | Code generation for object mapping. | Standardizes and secures conversion between layers. |
| **Deployment & CI/CD**| **Docker Containers** | - | Deployment artifact. | Standardizes the execution environment for portability. |
| | **GitHub Actions** | - | Continuous Integration and Deployment (CI/CD). | Automation of builds, tests, and deployments. |
| **Monitoring** | Micrometer, Prometheus, OTLP | - | Exposing and sending application metrics. | Observability via open standards (OpenTelemetry). |
| **Authentication**| - | - | - | **Out of scope** for the PoC, as defined in the PRD. |

-----
