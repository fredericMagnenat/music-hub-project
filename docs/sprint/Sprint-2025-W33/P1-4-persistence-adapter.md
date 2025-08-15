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
- Completed on 2025-08-15: AC3 (Flyway) implemented and validated. See commits: `dbd9331` (enable Flyway, add producer V1), `2a14320` (add artist V100), `ccba12b` (fix duplicate version), `e308aa2` (dev repair).

Artifacts
- Entity: `apps/producer/producer-adapters/producer-adapter-persistence/src/main/java/com/musichub/producer/adapter/persistence/ProducerEntity.java`
- Repository: `apps/producer/producer-adapters/producer-adapter-persistence/src/main/java/com/musichub/producer/adapter/persistence/ProducerRepositoryImpl.java`
- Mapper: `apps/producer/producer-adapters/producer-adapter-persistence/src/main/java/com/musichub/producer/adapter/persistence/ProducerMapper.java`
 - Flyway Migrations:
   - `apps/producer/producer-adapters/producer-adapter-persistence/src/main/resources/db/migration/producer/V1__init_producer.sql`
   - `apps/artist/artist-adapters/artist-adapter-persistence/src/main/resources/db/migration/artist/V100__init_artist.sql`

Note
- Flyway configured in `apps/bootstrap` with locations active in `%dev` only; tests use `%test` profile without locations. Global versioning ranges documented in `docs/architecture/coding-standards.md`.
