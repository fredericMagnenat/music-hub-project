# P1-2: Producer aggregate and repository port

Story: docs/stories/story-P1.md

Description
Create `Producer` aggregate in `producer-domain` using `ProducerId` and `ProducerCode`. Define `ProducerRepository` port with methods to find by id/code and to save.

Acceptance Criteria
- Aggregate encapsulates list of `Track` (by ISRC) and supports idempotent add/attach.
- Business invariants enforced (unique ISRC per producer, normalization on insert).
- Repository port defined with clear abstractions (no persistence details).

Dependencies
- P1-1 (ProducerId)
- shared-kernel (`ISRC`, `ProducerCode`)

Estimate
- 5 pts

Status
- Done on 2025-08-14 via commit `a896237` (feat(producer-domain): add Producer aggregate and repository port)

Artifacts
- Code: `apps/producer/producer-domain/src/main/java/com/musichub/producer/domain/model/Producer.java`
- Port: `apps/producer/producer-domain/src/main/java/com/musichub/producer/domain/port/ProducerRepository.java`
- Tests: `apps/producer/producer-domain/src/test/java/com/musichub/producer/domain/model/ProducerTest.java`
