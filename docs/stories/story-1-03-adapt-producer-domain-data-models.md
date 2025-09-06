### User Story: 1-03 - Refactor Producer Context to Align with Domain Charter

## Status
Ready for Development

> **As a** Developer, **when** implementing new features, **I want** the entire Producer bounded context (domain, persistence, SPI, and REST adapters) to be refactored, **so that** its implementation perfectly reflects the rich domain model, rules, and responsibilities defined in the `domain-charter.md`.

### Acceptance Criteria

1.  **Given** the `Producer` aggregate in the `producer-domain` module
    **When** the code is reviewed
    **Then** it **must** correctly model a `Producer` owning a collection of rich `Track` entities (not just ISRC strings) and enforce all related business rules as per the domain object model.

2.  **Given** the `producer-adapter-persistence` module
    **When** a `Producer` aggregate is saved or retrieved
    **Then** it **must** correctly manage the entire object graph (Producer and its Tracks) in the database.

3.  **Given** the `producer-adapter-spi` module
    **When** fetching data from external services (Tidal, Spotify)
    **Then** it **must** correctly populate the `Track` entities with the retrieved metadata.

4.  **Given** the `producer-adapter-rest` module
    **When** a client calls the API
    **Then** it **must** correctly map the rich internal `Producer` domain model to the simplified JSON contract required by the frontend.

5.  **Given** the `TrackWasRegistered` event is published
    **When** its payload is inspected
    **Then** it **must** contain all the required data (`isrc`, `title`, `producerId`, `artistCredits`, `sources`) as specified in the `domain-charter.md`.

6.  **Given** all existing Producer-related functionality
    **When** the refactoring is complete
    **Then** all existing integration tests **must** pass, ensuring no regressions.

### Tasks / Subtasks

- [ ] **Task 1: Refactor `producer-domain` (AC: 1)**
  - [ ] Update the `Producer` aggregate to hold a list of `Track` entities.
  - [ ] Ensure the `Track` entity within this context contains all necessary fields (title, credits, sources, etc.).
  - [ ] Implement the business logic for applying the "Source of Truth Hierarchy" when updating track data.

- [ ] **Task 2: Refactor `producer-adapter-persistence` (AC: 2)**
  - [ ] Update the `ProducerEntity` (JPA) to correctly map the relationship with `TrackEntity`.
  - [ ] Modify the `ProducerRepository` implementation to handle saving and loading the full aggregate.

- [ ] **Task 3: Refactor `producer-adapter-spi` (AC: 3)**
  - [ ] Adjust the external API clients to map incoming data to the rich `Track` domain entity.

- [ ] **Task 4: Refactor `producer-adapter-rest` (AC: 4, 5)**
  - [ ] Create a `ProducerResponse` DTO for the API contract.
  - [ ] Implement mapping from the `Producer` domain aggregate to the `ProducerResponse` DTO.
  - [ ] Ensure the `TrackWasRegistered` event is built with the complete, correct payload after a track is registered.

- [ ] **Task 5: Verify and Test (AC: 6)**
  - [ ] Update unit tests for all modified layers.
  - [ ] Run all integration tests in the `bootstrap` module to guarantee no regressions.
  - [ ] Add tests if necessary to cover the new rich object mapping.

### Change Log

| Date | Version | Description | Author |
|------|---------|-------------|---------|
| 2025-09-05 | v1.0 | Initial story creation for Producer domain data model adaptation | Bob (Scrum Master) |
| 2025-09-06 | v2.0 | Complete refactor scope - Updated per Winston's architectural guidance to cover full Producer context refactoring | Bob (Scrum Master) |

### Dev Agent Record

*This section will be populated by the development agent during implementation*

### QA Results

*This section will be populated by the QA agent after story completion*