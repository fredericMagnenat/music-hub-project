# P1-7: WebUI page (form ISRC)

Story: docs/stories/story-P1.md

## Status
Done

## Description
Create a simple page with ISRC input and a Validate button; disable the Validate button while the request is in progress.

## Acceptance Criteria
- [x] Input with client-side validation and normalization.
- [x] Button loading/disabled state during request.

## Implementation Notes
- Route: `apps/webui/app/routes/_index.tsx`
- Validation: normalisation (uppercase, suppression séparateurs) + regex `^[A-Z]{2}[A-Z0-9]{3}\d{7}$`.
- UI: composants shadcn-style `Input`/`Button`, focus ring conforme au token `--hub-primary`.
- Feedback: toasts ajoutés (succès/erreur) en complément des messages inline.

## Files
- `apps/webui/app/routes/_index.tsx`
- `apps/webui/app/components/ui/input.tsx`
- `apps/webui/app/components/ui/button.tsx`

## Dependencies
- P1-8 (API service)

## Estimate
- 2 pts
