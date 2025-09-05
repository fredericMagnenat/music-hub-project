### User Story: 2-02 - Manage Artist Data

## Status
Draft

> **As a** Producer (user), **I want** to be able to view and potentially update artist information (e.g., stage name, country, etc.), **so that** I can ensure the quality of data in my artist catalog.

### Acceptance Criteria

1. **Given** an Artist exists in the system with a unique ID
   **When** a Producer queries the artist by ID
   **Then** the system returns the complete Artist profile including all fields from the data model: `{ id, name, status, country, sources, contributions, submissionDate, producerIds }`

2. **Given** a Producer views an Artist profile
   **When** the Artist has "PROVISIONAL" status (auto-created from track events)
   **Then** the Producer should be able to update editable fields like name, country, and status
   **And** the system should validate and save the changes

3. **Given** a Producer attempts to update an Artist
   **When** they provide valid data for editable fields
   **Then** the system updates the Artist aggregate and persists the changes
   **And** the updated timestamp is reflected in the response

4. **Given** a Producer queries for Artists
   **When** they request artists associated with their producer code
   **Then** the system returns a filtered list of artists who have tracks linked to that producer

5. **Given** an Artist profile is displayed
   **When** the Artist has contributions (tracks)
   **Then** the track list should show title, ISRC, and track ID for each contribution
   **And** clicking on a track should navigate to track details (if available)

### Tasks / Subtasks

- [ ] **Task 1: Create Artist REST API Endpoints (AC: 1, 2, 4)**
  - [ ] Create `GET /api/v1/artists/{id}` endpoint for single artist retrieval
  - [ ] Create `GET /api/v1/artists?producerCode={code}` endpoint for producer's artists
  - [ ] Create `PUT /api/v1/artists/{id}` endpoint for artist updates
  - [ ] Add proper error handling for 404 (artist not found), 400 (validation), 422 (business rules)

- [ ] **Task 2: Implement Artist Application Services (AC: 2, 3)**
  - [ ] Create `ArtistQueryService` for read operations
  - [ ] Create `ArtistManagementService` for update operations
  - [ ] Add validation logic for editable fields (name, country, status transitions)
  - [ ] Implement business rules for status changes (PROVISIONAL → VERIFIED)

- [ ] **Task 3: Update Artist Domain Model (AC: 3)**
  - [ ] Ensure Artist aggregate supports field updates while maintaining immutability
  - [ ] Add validation for country codes (ISO standard if applicable)
  - [ ] Add business rules for valid status transitions
  - [ ] Implement update timestamp tracking

- [ ] **Task 4: Create Artist DTOs in Shared-Types (AC: 1)**
  - [ ] Define `ArtistResponse` interface matching the data model exactly
  - [ ] Define `ArtistUpdateRequest` interface for editable fields
  - [ ] Export interfaces for frontend consumption

- [ ] **Task 5: Frontend Artist Management UI (AC: 1, 2, 5)**
  - [ ] Create Artist Profile page/component (`/artists/{id}`)
  - [ ] Create Artist List page for producer's artists
  - [ ] Add edit form for updating artist information
  - [ ] Implement track contributions display with navigation
  - [ ] Add proper loading states and error handling

- [ ] **Task 6: Artist API Service (Frontend) (AC: 1, 2, 4)**
  - [ ] Create `artist.service.ts` with CRUD operations
  - [ ] Implement `getArtist(id)`, `getArtistsByProducer(code)`, `updateArtist(id, data)`
  - [ ] Add proper TypeScript types from shared-types
  - [ ] Handle API errors and map to user-friendly messages

- [ ] **Task 7: Comprehensive Testing (AC: All)**
  - [ ] Unit tests for Artist domain updates and validation
  - [ ] Application service tests with mocked repositories
  - [ ] Integration tests for complete API workflows
  - [ ] Frontend component tests for Artist UI
  - [ ] End-to-end tests for artist management workflow

### Dev Notes

#### Data Model Requirements
[Source: docs/architecture/data-models.md]

The Artist interface must strictly follow:
```typescript
interface Artist {
  id: string; // UUID
  name: string;
  status: 'PROVISIONAL' | 'VERIFIED';
  country?: string;
  sources: Source[]; // Platforms where the artist is identified
  contributions: Contribution[]; // The tracks to which the artist has contributed
  submissionDate: string; // ISO 8601 Date String
  producerIds: string[]; // List of associated producer UUIDs (assembled on the fly)
}

interface Contribution {
  trackId: string; // The track's UUID
  title: string;
  isrc: string;
}
```

**Editable Fields for Producers:**
- `name`: Artist stage name (required)
- `country`: ISO country code (optional)
- `status`: Can change from PROVISIONAL to VERIFIED

**Read-Only Fields:**
- `id`, `sources`, `contributions`, `submissionDate`, `producerIds` (computed)

#### Architecture Context
[Source: docs/architecture/source-tree.md]

**Relevant Modules:**
- `packages/shared-types/`: TypeScript interfaces for Artist DTOs
- `apps/artist/artist-domain/`: Artist aggregate and business logic
- `apps/artist/artist-application/`: Query and management services
- `apps/artist/artist-adapter-persistence/`: Repository implementation
- `apps/artist/artist-adapter-rest/`: REST controllers for artist endpoints
- `apps/webui/app/`: Frontend pages and components

#### Previous Story Context
From Story A1 (2-01) - Done:
- Artist aggregate exists with basic event handling
- Artists can be created automatically from `TrackWasRegistered` events
- Track references are properly linked to artists
- Basic repository operations are implemented

**Key Implementation Details:**
- Artists start with "PROVISIONAL" status when auto-created
- Track contributions are stored as `Contribution` objects with trackId, title, ISRC
- `producerIds` are assembled on-the-fly based on track ownership

#### Coding Standards
[Source: docs/architecture/coding-standards.md]

- **Shared Types Rule**: All API DTOs must use `packages/shared-types` interfaces
- **Domain Immutability**: Artist updates must create new instances, not mutate existing
- **Value Objects**: Use domain-specific types for country codes, status enums
- **No Logic in Adapters**: Keep REST controllers thin, business logic in application layer

#### Business Rules
- Status transitions: PROVISIONAL → VERIFIED (allowed), VERIFIED → PROVISIONAL (not allowed)
- Name updates: Required field, must be non-empty after trim
- Country codes: Should follow ISO standards if provided
- Only producers associated with artist's tracks can update artist data

#### API Design
**Endpoints:**
- `GET /api/v1/artists/{id}` - Returns Artist with full data model
- `GET /api/v1/artists?producerCode={code}` - Returns artists associated with producer
- `PUT /api/v1/artists/{id}` - Updates editable fields only

**Error Handling:**
- 404: Artist not found
- 400: Invalid request data or validation errors
- 403: Producer not authorized to update this artist
- 422: Business rule violations (e.g., invalid status transition)

#### Testing Requirements
[Source: docs/architecture/testing-best-practices.md]

**Domain Layer Testing:**
- Location: `apps/artist/artist-domain/src/test/java/`
- Test artist field updates and validation logic
- Test status transition business rules
- Use pure unit tests with no framework dependencies

**Application Layer Testing:**
- Location: `apps/artist/artist-application/src/test/java/`
- Mock ArtistRepository for service tests
- Test query and update orchestration
- Verify error handling and business rule enforcement

**Integration Testing:**
- Location: `apps/bootstrap/src/test/java/`
- Test complete HTTP-to-database artist management flows
- Verify JSON response structure matches TypeScript interfaces
- Test producer authorization for artist updates

**Frontend Testing:**
- Location: `apps/webui/app/routes/artists/`
- Component tests for artist profile and edit forms
- API service tests with mocked HTTP calls
- User interaction tests for form validation

#### Technical Constraints
[Source: docs/architecture/tech-stack.md]

- Java 21 with Quarkus 3.25.3 for backend
- Hibernate ORM + Panache for persistence
- Jackson for JSON serialization
- Remix + TypeScript for frontend
- REST API communication pattern

### Change Log

| Date | Version | Description | Author |
|------|---------|-------------|---------|
| 2025-09-05 | v1.0 | Initial story creation for Artist data management | Bob (Scrum Master) |

### Dev Agent Record

*This section will be populated by the development agent during implementation*

### QA Results

*This section will be populated by the QA agent after story completion*