# Technical Assumptions

* **Repository Structure**: Monorepo.
* **Service Architecture**: Hexagonal, Domain-Driven Design (DDD), Event-Driven.
* **Testing Requirements**:
    * **Back-end (Quarkus)**: Unit and integration tests (JUnit). Code coverage measured with Jacoco (80% target).
    * **Front-end (Remix)**: Unit and component tests with Vitest and React Testing Library (80% target).
    * **End-to-End Tests**: Out of scope for the PoC.
* **Additional Technical Assumptions**:
    * **Stack**: Java (Quarkus) for the back-end, TypeScript (Remix) for the front-end.
    * **Database**: PostgreSQL.
    * **Deployment**: Docker containers.
    * **API Style**: REST.
    * **Event Bus**: Internal to the Quarkus application.
    * **Authentication**: Out of scope for the PoC.
* **Non-Functional Requirements**:
    * **NFR1**: Adherence to Hexagonal architecture principles.
    * **NFR2**: Response time < 5 seconds for ISRC search.
    * **NFR3**: 80% test coverage (back-end and front-end).
    * **NFR4**: Domain implementation according to DDD patterns.
    * **NFR5**: Use of an Event-Driven approach.
    * **NFR6**: Structured logging (JSON) for back-end errors.
    * **NFR7**: Exposure of a `/health` API endpoint for monitoring.