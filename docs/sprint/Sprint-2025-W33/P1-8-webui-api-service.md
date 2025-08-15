# P1-8: WebUI API service

Story: docs/stories/story-P1.md

## Status
Done

## Description
Implement `registerTrack(isrc: string)`, calling `POST /api/v1/producers`. Handle `202`, `400`, `422` distinctly.

## Acceptance Criteria
- [x] Service returns typed results.
- [x] Distinct handling for `400` vs `422`.

## Implementation Notes
- File: `apps/webui/app/lib/utils.ts` → `registerTrack()` renvoie `{ ok, status, data? }`.
- 202: parse JSON si présent, sinon ok sans data; 400/422: retourne `ok:false` et status.
- UI consomme ces statuts pour toasts/messages.

## Files
- `apps/webui/app/lib/utils.ts`

## Dependencies
- P1-5 (REST contract)

## Estimate
- 2 pts
