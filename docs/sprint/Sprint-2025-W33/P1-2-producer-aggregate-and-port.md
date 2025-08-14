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
