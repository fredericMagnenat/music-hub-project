# P1-8: WebUI API service

Story: docs/stories/story-P1.md

Description
Implement `producer.service.ts` with `registerTrack(isrc: string)`, calling `POST /api/v1/producers`. Handle `202`, `400`, `422` distinctly.

Acceptance Criteria
- Service returns typed results using `@repo/shared-types`.
- Distinct error handling for `400` vs `422`.

Dependencies
- P1-5 (REST contract)
- packages/shared-types

Estimate
- 2 pts
