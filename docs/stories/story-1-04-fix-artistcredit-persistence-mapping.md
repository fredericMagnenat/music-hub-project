### User Story: 1-04 - Fix ArtistCredit Persistence Mapping

## Status
Done

> **As a** Developer, **when** working with Track entities in the Producer context, **I want** the persistence layer to correctly store and retrieve ArtistCredit value objects with both artistName and artistId fields, **so that** the Artist Context can properly process TrackWasRegistered events and perform artist resolution without data loss.

### Acceptance Criteria

1. **Given** a Track entity with ArtistCredit objects containing both artistName and optional artistId
   **When** the Track is persisted to the database
   **Then** both the artistName and artistId fields **must** be stored correctly and retrievable

2. **Given** a TrackEntity being mapped to a Track domain object
   **When** the mapping occurs through TrackMapper
   **Then** the full ArtistCredit objects **must** be recreated with all original data preserved

3. **Given** the TrackWasRegistered event being published
   **When** the event payload is constructed
   **Then** it **must** contain complete ArtistCredit information (not just artist names) as required by the domain charter

4. **Given** existing Track persistence functionality
   **When** the ArtistCredit mapping is fixed
   **Then** all existing tests **must** continue to pass with no regressions

5. **Given** a Flyway database migration for the schema change
   **When** the migration runs
   **Then** existing track_artists data **must** be preserved and converted appropriately

### Tasks / Subtasks

- [x] **Task 1: Create ArtistCreditEmbeddable class**
  - [x] Create `ArtistCreditEmbeddable.java` in the entity package with `artistName` and `artistId` fields.
- [x] **Task 2: Update TrackEntity persistence model (AC: 1)**
  - [x] Replace `List<String> artistNames` with `List<ArtistCreditEmbeddable> credits`.
  - [x] Use `@ElementCollection` and `@CollectionTable` to create a `track_artist_credits` table.
  - [x] Remove the existing `@ElementCollection` mapping for artistNames.
  - [x] Update entity constructors and getters/setters.
- [x] **Task 3: Fix TrackMapper conversion logic (AC: 2)**
  - [x] Update `toDbo()` method to map domain `ArtistCredit` to `ArtistCreditEmbeddable`.
  - [x] Update `toDomain()` method to recreate `ArtistCredit` objects from `ArtistCreditEmbeddable`.
  - [x] Remove old artistNames conversion logic.
- [x] **Task 4: Update Track domain model if needed (AC: 2)**
  - [x] Ensure Track domain methods use ArtistCredit objects consistently  
  - [x] Verify factory methods create tracks with ArtistCredit list
  - [x] Update any methods that return artistNames to use artistCredits
- [x] **Task 5: Fix TrackWasRegistered event construction (AC: 3)**
  - [x] Update RegisterTrackService to use ArtistCredit objects in event payload
  - [x] Verify ArtistCreditInfo DTO mapping works correctly
  - [x] Update event serialization tests
- [x] **Task 6: Create Flyway migration (AC: 5)**
  - [x] Create migration to create the `track_artist_credits` table.
  - [x] Migrate existing `track_artists` data to new format in `track_artist_credits`.
  - [x] Drop old `track_artists` table after data migration.
  - [x] Handle edge cases and null values appropriately.
- [x] **Task 7: Update all tests (AC: 4)**
  - [x] Fix TrackMapperTest to use `ArtistCreditEmbeddable`.
  - [x] Update TrackEntity persistence tests for the new mapping.
  - [x] Fix RegisterTrackServiceTest event payload assertions.
  - [x] Update integration tests in bootstrap module.
  - [x] Add specific tests for `@ElementCollection` mapping.

### Dev Notes

#### Previous Story Context
From Story 1-03 completion notes: The domain model was successfully refactored to use rich Track entities, but a critical persistence mapping issue was discovered during code review. The domain uses `ArtistCredit` value objects but persistence layer only stores `List<String> artistNames`, causing data loss for `artistId` fields.

#### Architecture Context
[Source: architecture/data-models.md#track-dependencies]
```typescript
interface ArtistCredit {
  artistName: string;
  artistId?: string; // UUID - THIS FIELD IS BEING LOST IN CURRENT PERSISTENCE
}
```

#### Current Implementation Problem
- **TrackEntity**: Uses `@ElementCollection List<String> artistNames` 
- **Domain**: Uses `List<ArtistCredit>` with name + optional ID
- **Result**: ArtistId data is lost during persistence cycles

#### Technical Solution
[Source: architecture/source-tree.md#backend-modules-structure]
Location: `apps/producer/producer-adapters/producer-adapter-persistence/`

**Embeddable Collection Approach**: Use `@ElementCollection` with a new `ArtistCreditEmbeddable` class. This creates a separate collection table.
```java
@ElementCollection(fetch = FetchType.EAGER)
@CollectionTable(name = "track_artist_credits", joinColumns = @JoinColumn(name = "track_id"))
@AttributeOverride(name = "artistName", column = @Column(name = "artist_name"))
@AttributeOverride(name = "artistId", column = @Column(name = "artist_id"))
public List<ArtistCreditEmbeddable> credits;
```

#### Database Schema Impact
[Source: architecture/database-migration-with-flyway.md#migration-development-workflow]
- **Version Range**: Producer context uses versions 1000-1999
- **Next Version**: 1004 (following existing Producer migrations)
- **Migration Type**: Schema change + data migration (New table `track_artist_credits`)

#### Source of Truth Hierarchy Impact
[Source: domain-charter.md#core-business-rules]
The current bug prevents the Artist Context from properly applying the Source of Truth Hierarchy (MANUAL > TIDAL > SPOTIFY > DEEZER > APPLE_MUSIC) because artistId linkage is lost.

#### Testing Standards
[Source: architecture/testing-best-practices.md#domain-layer-testing]
- **Domain Tests**: Test ArtistCredit value object creation and validation
- **Persistence Tests**: Verify `@ElementCollection` mapping roundtrip.
- **Application Tests**: Mock repositories with ArtistCredit objects
- **Integration Tests**: End-to-end verification with real database

#### File Locations
[Source: architecture/source-tree.md#backend-modules-structure]
```
apps/producer/producer-adapters/producer-adapter-persistence/
├── src/main/java/.../entity/ArtistCreditEmbeddable.java (CREATE)
├── src/main/java/.../entity/TrackEntity.java (MODIFY)
├── src/main/java/.../mapper/TrackMapper.java (MODIFY)
└── src/main/resources/db/migration/V1004__fix_track_artist_credits_mapping.sql (CREATE)
```

### Change Log

| Date | Version | Description | Author |
|------|---------|-------------|---------|
| 2025-09-07 | v1.0 | Critical technical debt story - Fix ArtistCredit persistence mapping issue discovered in Story 1-03 | Bob (Scrum Master) |

### Dev Agent Record

#### Agent Model Used
James (Full Stack Developer) - @dev persona

#### Debug Log References
- No debug logs generated during implementation

#### Completion Notes List
- All tasks were already implemented in the codebase
- Verified ArtistCreditEmbeddable class exists with correct annotations
- Confirmed TrackEntity uses proper @ElementCollection mapping
- Validated TrackMapper conversion methods handle ArtistCredit correctly
- Checked RegisterTrackService event construction includes artistId
- Verified Flyway migration V1004 handles data migration properly
- Confirmed all tests are updated to use new classes

#### File List
- apps/producer/producer-adapters/producer-adapter-persistence/src/main/java/com/musichub/producer/adapter/persistence/entity/ArtistCreditEmbeddable.java
- apps/producer/producer-adapters/producer-adapter-persistence/src/main/java/com/musichub/producer/adapter/persistence/entity/TrackEntity.java
- apps/producer/producer-adapters/producer-adapter-persistence/src/main/java/com/musichub/producer/adapter/persistence/mapper/TrackMapper.java
- apps/producer/producer-application/src/main/java/com/musichub/producer/application/service/RegisterTrackService.java
- apps/producer/producer-adapters/producer-adapter-persistence/src/main/resources/db/migration/V1004__fix_track_artist_credits_mapping.sql
- apps/producer/producer-adapters/producer-adapter-persistence/src/test/java/com/musichub/producer/adapter/persistence/mapper/TrackMapperTest.java
- apps/producer/producer-application/src/test/java/com/musichub/producer/application/RegisterTrackServiceTest.java

#### Change Log
| Date | Version | Description | Author |
|------|---------|-------------|---------|
| 2025-09-07 | v1.0 | Critical technical debt story - Fix ArtistCredit persistence mapping issue discovered in Story 1-03 | Bob (Scrum Master) |
| 2025-12-09 | v1.1 | All tasks verified as already implemented - story ready for review | James (Full Stack Developer) |
| 2025-12-09 | v1.2 | Story marked as Done after QA approval | James (Full Stack Developer) |

### QA Results

### Review Date: 2025-09-12

### Reviewed By: Quinn (Test Architect)

### Code Quality Assessment
The implementation is of high quality. The code is clean, well-documented, and adheres to the project's coding standards. The use of an `@ElementCollection` with an `@Embeddable` is the correct and standard JPA approach for this requirement. The Flyway migration script is well-structured, includes data migration, and cleans up the old table.

### Refactoring Performed
None. The code quality was high and no immediate refactoring was necessary.

### Compliance Check
- Coding Standards: [✓]
- Project Structure: [✓]
- Testing Strategy: [✓]
- All ACs Met: [✓]

### Improvements Checklist
- [ ] **Process Compliance**: Add the "Testing" subsection to the story file as required by the `story-tmpl.yaml` to detail story-specific testing activities.
- [ ] **Performance Consideration**: The `FetchType.EAGER` on the `credits` collection in `TrackEntity` is acceptable for now, but monitor performance. If tracks can have a very large number of artist credits, this could be a candidate for `FetchType.LAZY` in the future to avoid over-fetching.

### Security Review
No security vulnerabilities were identified. The changes are related to data persistence and do not introduce new endpoints or alter authentication/authorization logic.

### Performance Considerations
As noted above, the `FetchType.EAGER` on the `credits` collection is a minor performance consideration for the future. For the expected use case, it is not an immediate concern.

### Files Modified During Review
None.

### Gate Status
Gate: PASS → docs/qa/gates/epic-1.story-1-04-fix-artistcredit-persistence-mapping.yml

### Recommended Status
[✓ Ready for Done]
