# Testing Strategy

The testing strategy combines multiple levels to ensure quality:

* **Unit Tests (Backend & Frontend)**: Validate the atomic logic of domain classes and React components in isolation.
* **Integration Tests (Backend)**: Validate complete flows, from the API to the database, using an in-memory database (H2) and mocks (WireMock) for external services.
* **Code Quality**: Code coverage is measured by **Jacoco** (80% target), and architectural rules are validated by **ArchUnit**.
* **Test Documentation**: JUnit 5's `@DisplayName` annotation is mandatory for all backend tests to ensure their readability.

-----
