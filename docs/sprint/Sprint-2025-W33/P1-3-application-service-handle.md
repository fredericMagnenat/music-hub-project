# P1-3: ProducerService.handle(RegisterTrackRequest)

Story: docs/stories/story-P1.md

Description
Implement application service orchestrating: normalize ISRC, derive `ProducerCode`, find/create `Producer`, idempotently attach track, map domain errors.

Acceptance Criteria
- Valid ISRC flows create or reuse Producer and return domain result.
- Invalid ISRC format raises domain error mapped later to 400.
- Unresolvable ISRC (valid format but not found upstream) raises domain error mapped to 422.

Dependencies
- P1-2 (aggregate/port)

Estimate
- 3 pts
