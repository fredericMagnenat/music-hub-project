# P1-12: Frontend tests (Vitest/RTL)

Story: docs/stories/story-P1.md

## Status
Done

## Description
Add tests for form, components, and API service: disabled/loading state, normalization, success (202), and distinct error handling for 400 vs 422.

## Acceptance Criteria
- [x] All critical UX behaviors covered.
- [x] Toasts émis aux bons statuts (202/400/422).
- [x] Validation ISRC: cas invalides/valides normalisés.

## Implementation Notes
- Tests déplacés hors de `app/` pour éviter l'exécution en dev: `apps/webui/tests/_index.test.tsx`.
- Vitest en `jsdom` avec jest-dom; plugin Remix désactivé en mode test.
- Ciblé: `app/routes/_index.tsx`, `app/lib/utils.ts`, `components/ui/{input,button,toast}`.
- Mock: `registerTrack()` pour simuler 202/400/422.

## Dependencies
- P1-7..P1-9

## Estimate
- 2 pts
