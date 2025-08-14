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
