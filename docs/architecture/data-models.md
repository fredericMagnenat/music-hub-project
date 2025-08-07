# Data Models

This section defines the core business entities. It is a conceptual model that will serve as the basis for both the database schema and the shared API types.

### Technical Concerns

`createdAt` and `updatedAt` timestamps are considered technical concerns. They will be handled automatically by the persistence layer (e.g., database default values) and are thus excluded from these domain models.

### ID Generation Strategy

A hybrid approach will be used for primary key generation to combine robustness and idempotence:
*   **Producer:** IDs will be **deterministic UUIDv5**. They will be generated from a constant namespace and the immutable `producerCode`. This guarantees that the same producer code always results in the same ID, simplifying "upsert" logic.
*   **Artist:** IDs will be **random UUIDv4**. The artist's name is mutable, so a random UUID is used to decouple the entity's identity from its data, allowing for name changes without breaking references.

### Value Objects

To increase type safety and encapsulate validation logic within the domain, several attributes will be implemented as Value Objects, not primitive strings. While they are serialized as strings in the API, they are treated as rich objects in the backend code. Any Value Object that is shared between bounded contexts (like `ISRC`) **must** be defined in the `shared-domain` Maven module.
*   **`ProducerCode`**: A 5-character code identifying a producer.
*   **`ISRC`**: A 12-character International Standard Recording Code.
*   **`TrackTitle`**: A non-empty title for a track.
*   **`ArtistName`**: A non-empty name for an artist.

---

## Database Schema Management

Database schema changes will be managed through **Flyway**. Migration scripts will be stored in the backend source code (`src/main/resources/db/migration`). This ensures that schema changes are version-controlled, repeatable, and automatically applied upon application startup, which is critical for a reliable CI/CD pipeline.

## Model: Producer

*   **Purpose:** The main aggregate representing a rights holder. It owns and manages a collection of `Track` objects.
*   **Business Attributes:**
    *   `id`: `UUID` (Deterministic v5)
    *   `producerCode`: `ProducerCode`
    *   `name`: `String` (Optional)
    *   `tracks`: `Set<Track>`

### TypeScript Interface
```typescript
export interface Producer {
  id: string; // UUID
  producerCode: string;
  name?: string;
  tracks: Track[];
}
```

## Model: Track

*   **Purpose:** A child entity within the `Producer` aggregate, identified by its ISRC.
*   **Business Attributes:**
    *   `isrc`: `ISRC`
    *   `title`: `TrackTitle`
    *   `artistNames`: `Array<ArtistName>`
    *   `sources`: `Array<Source>`
    *   `status`: `'Provisional' | 'Verified'`

### TypeScript Interface
```typescript
export interface Source {
  sourceName: 'Spotify' | 'Tidal' | 'Deezer' | 'Apple Music' | 'Manual';
  sourceId: string;
}

export interface Track {
  isrc: string;
  title: string;
  artistNames: string[];
  sources: Source[];
  status: 'Provisional' | 'Verified';
}
```

## Model: Artist

*   **Purpose:** An aggregate representing an artist in its own bounded context. It holds references to tracks, but does not own them.
*   **Business Attributes:**
    *   `id`: `UUID` (Random v4)
    *   `name`: `ArtistName`
    *   `status`: `'Provisional' | 'Verified'`
    *   `sources`: `Array<Source>`
    *   `trackIds`: `Array<ISRC>`

### TypeScript Interface
```typescript
export interface Artist {
  id: string; // UUID
  name: string;
  status: 'Provisional' | 'Verified';
  sources: Source[];
  trackIds: string[]; // Set of ISRC codes
}
```
