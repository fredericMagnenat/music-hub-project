# P1-1: Implement ProducerId in producer-domain

Story: docs/stories/story-P1.md

Description
Implement `ProducerId` as a value object in `producer-domain`, generated deterministically (UUIDv5) from `ProducerCode`. Used by the `Producer` aggregate for idempotence.

Acceptance Criteria
- Given a `ProducerCode`, when creating a `ProducerId`, then the same code yields the same UUIDv5 across runs.
- Given two different `ProducerCode` values, then the `ProducerId` values differ.
- Equality and hash semantics implemented correctly.
- (If applicable) JSON serialization/deserialization preserves value.

Dependencies
- `ISRC` and `ProducerCode` in apps/shared-kernel

Estimate
- 2 pts
