# P1-11: Backend integration tests (ProducerController)

Story: docs/stories/story-P1.md

Description
Add `@QuarkusTest` covering new/existing producer flows, invalid ISRC (400), unresolvable ISRC (422), idempotence, and input normalization.

Acceptance Criteria
- REST contract and error payload validated.

Dependencies
- P1-5 (REST endpoint)

Estimate
- 3 pts
 
 Status
 - Done on 2025-08-15 via commits `3d3edda`
 
 Artifacts
 - Integration tests (bootstrap):
   - `apps/bootstrap/src/test/java/com/musichub/bootstrap/producer/ProducerRegistrationIntegrationTest.java`
   - `apps/bootstrap/src/test/java/com/musichub/bootstrap/producer/ProducerResourceIntegrationErrorHandlingTest.java`
