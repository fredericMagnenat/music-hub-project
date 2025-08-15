# P1-12: Frontend tests (Vitest/RTL)

Story: docs/stories/story-P1.md

## Status
Pending

## Description
Add tests for form, components, and API service: disabled/loading state, normalization, success (202), and distinct error handling for 400 vs 422.

## Acceptance Criteria
- [ ] All critical UX behaviors covered.
- [ ] Toasts emitted aux bons statuts.
- [ ] Validation ISRC: cas invalides/valides normalisés.

## Implementation Notes
- Cibler `app/routes/_index.tsx`, `app/lib/utils.ts`, `components/ui/{input,button,toast}`.
- Mock fetch pour `/api/v1/producers`.

## Dependencies
- P1-7..P1-9

## Estimate
- 2 pts
