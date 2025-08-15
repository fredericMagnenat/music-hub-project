# P1-9: WebUI components (ISRCInputField, SubmitButton, Toast)

Story: docs/stories/story-P1.md

## Status
Done

## Description
Build reusable components using shadcn-style with client-side validation, loading state, and toasts for success/error.

## Acceptance Criteria
- [x] `ISRCInputField` normalizes and validates input (via `Input` + logique route).
- [x] `SubmitButton` shows spinner when loading (via `Button`).
- [x] Toasts: success on 202, error messages differentiated for 400 vs 422.

## Implementation Notes
- Composants: `~/components/ui/input`, `~/components/ui/button`, `~/components/ui/toast` (provider + hook + toaster).
- Tokens Tailwind: `--hub-*` définis dans `app/tailwind.css`; utilisés par `Button`/`Input`.

## Files
- `apps/webui/app/components/ui/input.tsx`
- `apps/webui/app/components/ui/button.tsx`
- `apps/webui/app/components/ui/toast.tsx`
- `apps/webui/app/root.tsx`
- `apps/webui/app/routes/_index.tsx`

## Dependencies
- P1-8 (API service)

## Estimate
- 2 pts
