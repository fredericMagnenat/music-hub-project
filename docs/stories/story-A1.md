### User Story: A1 - Update an Artist Following an Event

> **As an** Artist Context, **when** I receive a `TrackWasRegistered` event, **I want** to find the corresponding artist in my catalog or create them if unknown, then link the track to them, **in order to** autonomously maintain up-to-date profiles.

### Pre-requisites
*   This story depends on the event contract for `TrackWasRegistered` defined in **Story P2**.

#### Backend Technical Tasks (`artist` context)

1.  **Module `artist-adapter-messaging`:**
    *   Create a `TrackEventHandler` class.
    *   Implement a method that listens (`@Observes`) for `TrackWasRegistered` events published on the Vert.x event bus.
    *   This method will act as an adapter, receiving the event and calling the appropriate application service.

2.  **Module `artist-domain`:**
    *   Create the `Artist` aggregate and its repository interface (`ArtistRepository`).
    *   Implement the random ID generation logic (UUIDv4) for the `Artist`.
    *   Add an `addTrackReference(ISRC isrc)` method to the `Artist` aggregate.

3.  **Module `artist-application`:**
    *   Create the `ArtistService` application service.
    *   Create a `handle(TrackWasRegistered event)` method.
    *   This method will contain the User Story's logic:
        *   For each artist name in the event:
        *   Search if an artist with that name exists via the `ArtistRepository`.
        *   If they don't exist, create a new one with "Provisional" status.
        *   Call the `addTrackReference()` method on the found or created artist.
        *   Save the artist.

4.  **Module `artist-adapter-persistence`:**
    *   Implement `ArtistRepositoryImpl` using JPA/Panache.
    *   Define the `ArtistEntity` JPA entity.

#### Tests to Plan

*   **Backend:**
    *   Integration test for the `ArtistService` application service. A fake `TrackWasRegistered` event will be manually published, and we will verify that the artist is correctly created or updated in the database. 