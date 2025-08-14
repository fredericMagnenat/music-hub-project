# P1-5: REST endpoint POST /api/v1/producers

Story: docs/stories/story-P1.md

Description
Create `ProducerController` exposing `POST /api/v1/producers`, consume `RegisterTrackRequest`, map domain errors to `400/422`, emit `202` with current Producer representation.

Acceptance Criteria
- Endpoint available at `/api/v1/producers`.
- Returns `202` with Producer payload; maps `InvalidISRCFormat` to 400 and `UnresolvableISRC` to 422 with standardized error body.
- Input normalization (trim/uppercase/remove dashes).

Dependencies
- P1-3 (application service)

Estimate
- 3 pts

Status
- Done on 2025-08-14 via commits `b3b3956`, `199654d`

Artifacts
- Resource: `apps/producer/producer-adapters/producer-adapter-rest/src/main/java/com/musichub/producer/adapter/rest/resource/ProducerResource.java`
- Tests (unit): `apps/producer/producer-adapters/producer-adapter-rest/src/test/java/com/musichub/producer/adapter/rest/ProducerResourceTest.java`
- Tests (integration bootstrap): `apps/bootstrap/src/test/java/com/musichub/bootstrap/producer/ProducerRegistrationIntegrationTest.java`
