# Sprint 2025-W33

Goal: Deliver Story P1 (Validate and Create a Producer) end-to-end across backend and frontend, with tests and basic observability.

Scope links:
- Story: docs/stories/story-P1.md
- Architecture: docs/architecture/index.md

Backlog (tickets):
- P1-1: ProducerId in producer-domain
- P1-2: Producer aggregate + port
- P1-3: ProducerService.handle
- P1-4: Persistence adapter (entity + repo impl)
- P1-5: REST endpoint POST /api/v1/producers + error mapping
- P1-6: Bootstrap base path + metrics
- P1-7: WebUI page (form ISRC)
- P1-8: WebUI API service
- P1-9: WebUI components (ISRCInputField, SubmitButton, Toast)
- P1-10: Backend unit tests (VOs, aggregate)
- P1-11: Backend integration tests (ProducerController)
- P1-12: Frontend tests (Vitest/RTL)

Definition of Done
- All AC in story P1 verified by automated tests
- No duplicate tracks on repeats (idempotence)
- Basic metrics exposed (400/422 counters)
- Docs updated where needed
