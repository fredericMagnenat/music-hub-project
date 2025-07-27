### User Story: P1 - Validate and Create a Producer

> **As a** System, **when** a new ISRC is submitted, **I want** to validate that the "Producer Code" (the first 5 characters) exists, and create it if it's unknown, **in order to** ensure that each track has a referenced owner.

### Acceptance Criteria

1.  **Given** an ISRC for a **new** Producer is submitted
    **When** the `POST /producers` endpoint is called
    **Then** a new `Producer` record is created in the database with the correct `producerCode`
    **And** the system returns a `202 Accepted` status with the newly created Producer data.

2.  **Given** an ISRC for an **existing** Producer is submitted
    **When** the `POST /producers` endpoint is called
    **Then** no new `Producer` record is created
    **And** the system returns a `202 Accepted` status with the existing Producer data.

3.  **Given** an ISRC with an **invalid format** is submitted
    **When** the `POST /producers` endpoint is called
    **Then** the system returns a `400 Bad Request` error.

### Backend Technical Tasks (`producer` context)

1.  **Module `shared-types` (clarification):**
    *   Create a `RegisterTrackRequest.ts` file in `packages/shared-types/src/` defining the DTO. This ensures it's shared between frontend and backend.
        ```typescript
        export interface RegisterTrackRequest {
          isrc: string;
        }
        ```

2.  **Module `producer-adapter-rest`:**
    *   Create the `ProducerController` REST controller with a `POST /producers` endpoint.
    *   It will consume the `RegisterTrackRequest` DTO.
    *   Implement error handling to map domain exceptions (e.g., `InvalidISRCFormat`) to a `400 Bad Request` HTTP status.

3.  **Module `producer-application`:**
    *   Create the `ProducerService` application service.
    *   Create the `handle(RegisterTrackRequest request)` method in this service.
    *   This method will orchestrate the flow: extract the producer code, find/create the producer, etc.

4.  **Module `producer-domain`:**
    *   Create the `ISRC` and `ProducerCode` Value Objects with their validation logic.
    *   Create the `Producer` aggregate and its repository interface (`ProducerRepository`).
    *   Implement the deterministic ID generation logic (UUIDv5) for the `Producer`.
    *   Create a `ProducerFactory` to handle the creation of new `Producer` instances.

5.  **Module `producer-adapter-persistence`:**
    *   Implement `ProducerRepositoryImpl` using JPA/Panache.
    *   Define the `ProducerEntity` JPA entity and its mapping to the domain model.

### Frontend Technical Tasks (`apps/frontend`)

1.  **Page/Route:**
    *   Create a new page (e.g., `/`) containing a simple form with an input field for the ISRC and an "Add" button.
    *   The "Add" button should be disabled while the API request is in progress.

2.  **API Service:**
    *   In `app/services/`, create a `producer.service.ts`.
    *   Implement a `registerTrack(isrc: string)` function that calls the `POST /api/v1/producers` endpoint, importing `RegisterTrackRequest` from `@repo/shared-types`.
    *   The function must handle both success and error responses from the API.

3.  **Components (`app/components/`):**
    *   Create an `ISRCInputField` component using `shadcn/ui` with client-side validation for the ISRC format.
    *   Create a `SubmitButton` component that can display a loading state (spinner).
    *   Use a `Toast` component (`shadcn/ui`) to display success or error messages to the user after the API call.

### Tests to Plan

*   **Backend:**
    *   Unit tests for Value Objects (`ISRC`, `ProducerCode`).
    *   Unit tests for the `Producer` aggregate.
    *   Integration test (`@QuarkusTest`) for the `ProducerController`, covering success cases (new/existing producer) and a failure case (invalid ISRC format).
*   **Frontend:**
    *   Unit tests for the form and components with Vitest and RTL, including testing the disabled/loading state of the button.
    *   Tests for the API service to mock and verify both success and error handling. 