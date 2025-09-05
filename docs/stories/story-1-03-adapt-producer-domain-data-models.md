### User Story: 1-03 - Adapt Producer Domain to Data Models

## Status
Draft

> **As a** System, **when** the Producer domain implementation needs to align with the official data models specification, **I want** the Producer entity and related components to strictly follow the TypeScript interface defined in `docs/architecture/data-models.md`, **so that** the backend and frontend maintain consistent type contracts and the API responses match the expected structure.

### Acceptance Criteria

1. **Given** the Producer entity exists in the domain layer
   **When** it is accessed through the API
   **Then** the JSON response must exactly match the Producer TypeScript interface structure: `{ id: string, producerCode: string, name?: string | null, tracks: string[] }`

2. **Given** a Producer is persisted to the database
   **When** it is retrieved and mapped to domain objects
   **Then** all fields must be correctly mapped according to the data model specification

3. **Given** the shared-types package exists
   **When** Producer-related types are needed in frontend or backend
   **Then** they must use the exact TypeScript interface from the shared-types package, not duplicate definitions

4. **Given** existing functionality is working (Producer registration, track addition)
   **When** domain adaptation is completed
   **Then** all existing functionality must continue to work without regression

### Tasks / Subtasks

- [ ] **Task 1: Review Current Producer Domain Implementation (AC: 1, 2, 4)**
  - [ ] Examine current `Producer` aggregate in `apps/producer/producer-domain/`
  - [ ] Identify discrepancies with the data model specification
  - [ ] Document current vs expected structure

- [ ] **Task 2: Update Shared-Types Package (AC: 3)**
  - [ ] Ensure `Producer` interface in `packages/shared-types/src/` matches exactly `docs/architecture/data-models.md`
  - [ ] Add related interfaces if missing: `Track`, `Source`, `ArtistCredit`
  - [ ] Export all interfaces properly for consumption

- [ ] **Task 3: Adapt Domain Model (AC: 1, 2)**
  - [ ] Update `Producer` aggregate to align with TypeScript interface structure
  - [ ] Ensure `tracks` field contains array of ISRC strings (not Track objects)
  - [ ] Verify `name` field is optional and can be null
  - [ ] Ensure `id` is string UUID format

- [ ] **Task 4: Update Persistence Layer (AC: 2)**
  - [ ] Review `ProducerEntity` JPA entity mapping
  - [ ] Ensure database schema aligns with domain model
  - [ ] Update `ProducerMapper` if needed for proper domain/entity conversion
  - [ ] Verify JSON serialization produces correct API response format

- [ ] **Task 5: Update Application Layer (AC: 4)**
  - [ ] Review `RegisterTrackService` and other application services
  - [ ] Ensure they work with updated domain model
  - [ ] Verify business logic remains intact

- [ ] **Task 6: Update REST Adapter (AC: 1)**
  - [ ] Review `ProducerResource` controller
  - [ ] Ensure API responses use correct data structure
  - [ ] Import Producer interface from shared-types package
  - [ ] Verify JSON serialization matches TypeScript interface

- [ ] **Task 7: Comprehensive Testing (AC: 4)**
  - [ ] Run existing tests to ensure no regression
  - [ ] Update tests that may be affected by structure changes
  - [ ] Add integration tests verifying JSON response structure
  - [ ] Verify frontend can consume API responses correctly

### Dev Notes

#### Data Model Requirements
[Source: docs/architecture/data-models.md]

The Producer interface must strictly follow:
```typescript
interface Producer {
  id: string; // UUID
  producerCode: string;
  name?: string | null;
  tracks: string[]; // List of track ISRC codes
}
```

**Key Requirements:**
- `id`: Must be a UUID string format
- `producerCode`: String representing the 5-character producer code (e.g., "FRLA1")
- `name`: Optional field, can be string or null
- `tracks`: Array of ISRC strings, NOT Track objects

#### Architecture Context
[Source: docs/architecture/source-tree.md]

**Relevant Modules:**
- `packages/shared-types/`: TypeScript interfaces shared between frontend/backend
- `apps/producer/producer-domain/`: Core domain model
- `apps/producer/producer-application/`: Application services
- `apps/producer/producer-adapter-persistence/`: JPA entities and repository implementation
- `apps/producer/producer-adapter-rest/`: REST controllers

#### Previous Story Context
From Stories P1 and P2 (both Done):
- Producer aggregate already exists with basic functionality
- Track registration workflow is implemented
- ISRC normalization and ProducerCode extraction working
- Repository and REST endpoints operational

**Critical Implementation Detail:** The current implementation may be storing Track objects or references rather than simple ISRC strings in the tracks collection, which needs correction.

#### Coding Standards
[Source: docs/architecture/coding-standards.md]

- **Shared Types Rule**: All API DTOs must use `packages/shared-types` interfaces
- **Domain Immutability**: Domain objects must remain immutable
- **Value Objects**: Continue using `ISRC`, `ProducerCode` from shared-kernel
- **No Logic in Adapters**: Keep controllers thin, logic in application layer

#### Testing Requirements
[Source: docs/architecture/testing-best-practices.md]

**Domain Layer Testing:**
- Location: `apps/producer/producer-domain/src/test/java/`
- Use pure unit tests with no framework dependencies
- Test business logic in isolation
- Use `@DisplayName` and nested test classes

**Application Layer Testing:**
- Location: `apps/producer/producer-application/src/test/java/`
- Mock repository ports
- Focus on orchestration logic
- Use `@ExtendWith(MockitoExtension.class)`

**Integration Testing:**
- Location: `apps/bootstrap/src/test/java/`
- Use `@QuarkusTest` with real infrastructure
- Test complete HTTP-to-database flows
- Verify JSON response structure matches TypeScript interface

**Test Data Setup:**
- Clean database state with `@Transactional` in `@BeforeEach`
- Use `@TestTransaction` for test data isolation

#### Technical Constraints
[Source: docs/architecture/tech-stack.md]

- Java 21 with Quarkus 3.25.3
- Hibernate ORM + Panache for persistence
- Jackson for JSON serialization
- REST API style for frontend communication
- PostgreSQL database

### Change Log

| Date | Version | Description | Author |
|------|---------|-------------|---------|
| 2025-09-05 | v1.0 | Initial story creation for Producer domain data model adaptation | Bob (Scrum Master) |

### Dev Agent Record

*This section will be populated by the development agent during implementation*

### QA Results

*This section will be populated by the QA agent after story completion*