### User Story: P2 - Integrate a Track and Publish an Event

> **As a** Producer (user), **when** I validate the addition of a track, **I want** the track to be added to the correct "Producer" aggregate and a `TrackWasRegistered` event to be published, **in order to** secure the track's registration and inform the rest of the system.

### Acceptance Criteria

1.  **Given** a valid ISRC is processed (from Story P1)
    **When** the external music API is called and returns a success with track metadata
    **Then** a `Track` entity is created and added to the `Producer` aggregate
    **And** a `TrackWasRegistered` event containing the track's ISRC, title, and artist names is published on the event bus
    **And** the `Producer` aggregate is saved to the database.

2.  **Given** a valid ISRC is processed
    **When** the external music API call fails or returns no data
    **Then** no `Track` is added to the aggregate
    **And** no event is published
    **And** the API call (`POST /producers`) returns a `422 Unprocessable Entity` error.

### Backend Technical Tasks (`producer` context)

1.  **Module `producer-adapter-spi`:**
    *   Create an HTTP client interface (e.g., `MusicPlatformClient`) using the Quarkus REST Client.
    *   This client will have a method like `getTrackByIsrc(String isrc)`.
    *   The implementation will handle secrets (API keys) as defined in the architecture's "Security and Secrets Management" section.

2.  **Module `producer-domain`:**
    *   Create the `Track` entity and the `Source` value object as defined in our architecture.
    *   Add an `addTrack(Track track)` method to the `Producer` aggregate. This method will contain the logic for adding a track while ensuring business rules (invariants) are respected.
    *   Define the `TrackWasRegistered` domain event class. This class is a simple DTO that will be serialized on the event bus.
        ```java
        // Example structure for the event
        public class TrackWasRegistered {
            private final String isrc;
            private final String title;
            private final List<String> artistNames;

            // Constructor, getters...
        }
        ```

3.  **Module `producer-application`:**
    *   Modify the `ProducerService`: after finding/creating the `Producer`, it will use the SPI client to fetch the track metadata.
    *   If the call is successful, it will create the `Track` object and call the `producer.addTrack()` method.
    *   After saving the `Producer` via the repository, it will instantiate and publish the `TrackWasRegistered` event to the Vert.x event bus.
    *   If the SPI call fails, it should throw a specific exception (e.g., `TrackNotFoundInExternalServiceException`) to be handled by the REST adapter.

### Frontend Technical Tasks (`apps/frontend`)

1.  **State Management & UI Feedback:**
    *   On a successful `202 Accepted` response from `POST /producers`, the UI should optimistically update.
    *   Display a `Toast` notification (using `shadcn/ui`) with a success message, for example: `"Track 'Bohemian Rhapsody' by 'Queen' has been successfully registered and is being processed."`
    *   On a `422 Unprocessable Entity` error, display a `Toast` with a descriptive error message: `"The ISRC was valid, but we could not find metadata for it on external services."`

### Tests to Plan

*   **Backend:**
    *   Integration test for the `ProducerService` application service:
        *   Use WireMock to simulate a **successful** response from the external API and verify that the `TrackWasRegistered` event is correctly published.
        -   Use WireMock to simulate a **failed** (e.g., 404) response from the external API and verify that no event is published and an exception is thrown.
*   **Frontend:**
    *   Tests for the UI state after an API call, verifying that the correct success or error `Toast` notification is displayed based on the API response. 