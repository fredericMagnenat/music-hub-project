# P1-10: Backend unit tests (VOs, aggregate)

Story: docs/stories/story-P1.md

Description
Add tests for `ISRC`, `ProducerCode` (shared-kernel), `ProducerId` (producer-domain), and `Producer` aggregate.

Acceptance Criteria
- Validation, equality, and idempotence behavior covered.

Dependencies
- P1-1, P1-2

Estimate
- 3 pts
 
 Status
 - Done on 2025-08-15 via commit `4eb563c`
 
 Artifacts
 - Tests (shared-kernel):
   - `apps/shared-kernel/src/test/java/com/musichub/shared/domain/values/ISRCTest.java`
   - `apps/shared-kernel/src/test/java/com/musichub/shared/domain/values/ProducerCodeTest.java`
 - Tests (producer-domain):
   - `apps/producer/producer-domain/src/test/java/com/musichub/producer/domain/values/ProducerIdTest.java`
   - `apps/producer/producer-domain/src/test/java/com/musichub/producer/domain/model/ProducerTest.java`
