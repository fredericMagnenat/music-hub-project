# P1-4: Persistence adapter (entity + repo impl)

Story: docs/stories/story-P1.md

Description
Implement JPA/Panache repository and entity mapping for `Producer`. Ensure value objects are persisted correctly.

Acceptance Criteria
- `ProducerEntity` with correct mappings (id, code, tracks by ISRC).
- `ProducerRepositoryImpl` implements domain port; idempotent save semantics.
- Flyway migration scripts created and applied for required tables.

Dependencies
- P1-2 (aggregate/port)

Estimate
- 5 pts

Status
- Done on 2025-08-14 via commit `d68abce` (feat(producer-persistence): add ProducerEntity, Mapper, and RepositoryImpl (Panache))

Artifacts
- Entity: `apps/producer/producer-adapters/producer-adapter-persistence/src/main/java/com/musichub/producer/adapter/persistence/ProducerEntity.java`
- Repository: `apps/producer/producer-adapters/producer-adapter-persistence/src/main/java/com/musichub/producer/adapter/persistence/ProducerRepositoryImpl.java`
- Mapper: `apps/producer/producer-adapters/producer-adapter-persistence/src/main/java/com/musichub/producer/adapter/persistence/ProducerMapper.java`

Note
- Flyway migrations à ajouter ultérieurement (P1-4 AC3 non couvert ici).
