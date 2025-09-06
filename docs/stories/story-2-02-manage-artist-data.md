### User Story: 2-02 - Refactor Artist Context to Align with Domain Charter

## Status
Ready for Development

> **As a** Developer, **when** implementing new features, **I want** the entire Artist bounded context (domain, persistence, SPI, and REST adapters) to be refactored, **so that** its implementation perfectly reflects the rich domain model, rules, and responsibilities defined in the `domain-charter.md`.

### Acceptance Criteria

1.  **Given** the `Artist` aggregate in the `artist-domain` module, **when** the code is reviewed, **then** it **must** correctly model an `Artist` with its full set of attributes (`status`, `sources`, `contributions`) and enforce all related business rules (like status transitions).

2.  **Given** the `artist-adapter-persistence` module, **when** an `Artist` aggregate is saved or retrieved, **then** it **must** correctly manage the entire object graph (Artist and its list of Contributions and Sources) in the database.

3.  **Given** the `artist-adapter-spi` module, **when** the artist reconciliation logic is triggered, **then** it **must** correctly call external services to fetch and enrich the `Artist` domain entity with an `externalId`.

4.  **Given** the `artist-adapter-rest` module, **when** a client calls the API, **then** it **must** correctly map the rich internal `Artist` domain model to the required API JSON contract, including the on-the-fly assembly of `producerIds`.

5.  **Given** all existing Artist-related functionality (like creation from events), **when** the refactoring is complete, **then** all existing integration tests **must** pass, ensuring no regressions.

### Tasks / Subtasks

- [ ] **Task 1: Refactor `artist-domain` (AC: 1)**
    - [ ] Update the `Artist` aggregate to hold its rich data collections (`Contribution`, `Source`).
    - [ ] Implement the business logic for status transitions (`PROVISIONAL` -> `VERIFIED`).
    - [ ] Implement the logic for applying the "Source of Truth Hierarchy" when updating artist data.

- [ ] **Task 2: Refactor `artist-adapter-persistence` (AC: 2)**
    - [ ] Update the `ArtistEntity` (JPA) to correctly map its relationships (`Contribution`, `Source`).
    - [ ] Modify the `ArtistRepository` implementation to handle saving and loading the full aggregate.

- [ ] **Task 3: Refactor `artist-adapter-spi` (AC: 3)**
    - [ ] Implement or update the client responsible for calling external APIs (e.g., Spotify) to find artist profiles and their `externalId`.
    - [ ] Ensure the mapping from the external API response to our `Artist` domain model is correct.

- [ ] **Task 4: Refactor `artist-adapter-rest` (AC: 4)**
    - [ ] Create internal DTOs (`ArtistResponse`, etc.) for the API contract.
    - [ ] Implement the logic to assemble the `producerIds` list for the API response.
    - [ ] Implement mapping from the `Artist` domain aggregate to the `ArtistResponse` DTO.

- [ ] **Task 5: Verify and Test (AC: 5)**
    - [ ] Update unit tests for all modified layers (`domain`, `persistence`, `spi`, `rest`).
    - [ ] Run all integration tests in the `bootstrap` module to guarantee no regressions.
    - [ ] Add tests to cover the new rich object mapping and business rules.

### Change Log

| Date | Version | Description | Author |
|------|---------|-------------|---------|
| 2025-09-05 | v1.0 | Initial story creation for Artist data management | Bob (Scrum Master) |
| 2025-09-06 | v2.0 | Complete refactor scope - Updated per Winston's architectural guidance to cover full Artist context refactoring | Bob (Scrum Master) |

### Dev Agent Record

*This section will be populated by the development agent during implementation*

### QA Results

*This section will be populated by the QA agent after story completion*