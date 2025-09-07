### User Story: 0-14 - Adapt Frontend UI to API Contracts and Architecture

## Status
**BLOCKED** - Dependencies Incomplete

**DEPENDENCY BLOCKER**: Stories 1-03 and 2-02 must be completed (currently "Ready for Development"). Frontend cannot adapt to API contracts that don't exist yet.

> **As a** Developer, **I want** the entire frontend codebase (`webui`) to be aligned with the final API contracts and frontend architecture, **so that** the UI can correctly consume backend data and is structured for maintainable future development.

### Acceptance Criteria

1. **Given** the refactored backend APIs (from stories 1-03 and 2-02)
   **When** the frontend makes API calls
   **Then** it **must** correctly consume the new API contracts without errors

2. **Given** the new frontend architecture structure
   **When** TypeScript interfaces are needed
   **Then** they **must** be properly organized in `app/types/` directory following the defined conventions

3. **Given** the Producer and Artist API responses
   **When** the UI displays this data
   **Then** it **must** correctly map and render all fields according to the API contracts

4. **Given** the existing frontend functionality
   **When** the adaptation is complete
   **Then** all current features **must** continue to work without regression

5. **Given** the updated frontend architecture
   **When** new components or services are created
   **Then** they **must** follow the established patterns and structure

### Tasks / Subtasks

- [ ] **Task 1: Update TypeScript Interfaces (AC: 2)**
  - [ ] Review and update interfaces in `app/types/` to match backend API contracts
  - [ ] Ensure Producer and Artist types align with refactored backend models
  - [ ] Remove any outdated or unused type definitions

- [ ] **Task 2: Update API Service Layer (AC: 1, 3)**
  - [ ] Update Producer API service to consume new backend endpoints
  - [ ] Update Artist API service to consume new backend endpoints  
  - [ ] Ensure proper error handling for new API responses

- [ ] **Task 3: Update UI Components (AC: 3)**
  - [ ] Update Producer-related components to handle new data structure
  - [ ] Update Artist-related components to handle new data structure
  - [ ] Ensure proper data mapping and rendering

- [ ] **Task 4: Frontend Architecture Compliance (AC: 2, 5)**
  - [ ] Verify all code follows the defined frontend architecture patterns
  - [ ] Ensure proper separation of concerns (types, services, components)
  - [ ] Update any non-compliant code to match architecture standards

- [ ] **Task 5: Testing and Verification (AC: 4)**
  - [ ] Run existing frontend tests to ensure no regressions
  - [ ] Update tests to match new API contracts and data structures
  - [ ] Add integration tests for new API consumption patterns

### Dev Notes

#### Context
This story follows the backend refactoring completed in:
- Story 1-03: Producer context refactoring  
- Story 2-02: Artist context refactoring

The frontend must now be adapted to work with the updated backend APIs and follow the established frontend architecture.

#### Frontend Architecture Requirements

**Directory Structure**: Frontend follows Remix conventions with dedicated `types` directory:
```
webui/app/
├── components/ui/          # Base components (shadcn/ui style)
├── components/             # Business components  
├── lib/utils.ts           # Centralized API calls
├── routes/                # Remix routes
├── types/                 # DEDICATED DIRECTORY FOR TYPES
│   ├── index.ts          # Exports all types
│   ├── artist.ts         # Artist interface
│   ├── track.ts          # Track interface  
│   └── producer.ts       # Producer interface
└── root.tsx
```

**Component Philosophy**: Two-tiered approach:
- **Base Components** (`app/components/ui/`): Reusable primitives with no business logic
- **Business Components** (`app/components/`): Composite components for specific features

**API Integration**: Centralized in `app/lib/utils.ts` with development proxy configured in `vite.config.ts`

**State Management**: Local to components using React hooks (`useState`, `useEffect`) - no global state management

#### API Contract Updates

**CRITICAL DEPENDENCY STATUS**: Stories 1-03 and 2-02 are **Ready for Development** - NOT completed. Frontend adaptation cannot proceed until backend refactoring is complete.

**Expected API Changes** (based on backend stories):
- **Producer API** (`/api/producers`): Rich Track entities instead of ISRC strings
- **Artist API** (`/api/artists`): Rich Artist model with status, sources, contributions
- **Track API** (`/api/tracks/recent`): Enhanced metadata from external services

**API Endpoints**:
- `POST /api/producers` - Register track via ISRC 
- `GET /api/tracks/recent` - Recent tracks with full metadata
- `GET /api/artists` - Artists with rich domain data

**Response Structure Changes**: Backend refactoring will provide detailed DTOs mapping from rich domain models to simplified JSON contracts.

#### File Structure Requirements

**Monorepo Structure**:
```
music-hub-project/
├── apps/webui/            # Remix UI application
│   ├── app/
│   │   ├── components/ui/ # Base components
│   │   ├── components/    # Business components
│   │   ├── lib/utils.ts   # API calls
│   │   ├── routes/        # Remix routes
│   │   ├── types/         # TypeScript interfaces
│   │   └── root.tsx
│   ├── tests/             # Frontend tests
│   ├── vite.config.ts     # Vite config with API proxy
│   └── package.json
└── apps/bootstrap/        # Backend Quarkus runtime
```

**Key Files to Update**:
- `app/types/*.ts` - TypeScript interfaces for API contracts
- `app/lib/utils.ts` - API service functions 
- `app/components/RecentTracksList.tsx` - UI components consuming data
- `app/routes/_index.tsx` - Main route using updated APIs

#### Testing Requirements

**Frontend Testing Strategy**:
- **Tooling**: Vitest + React Testing Library
- **Location**: Co-located with Remix code in `apps/webui/app/`
- **API Mocking**: Mock `~/lib/utils` module for component tests
- **Global Setup**: `apps/webui/vitest.setup.ts`
- **Configuration**: `apps/webui/vite.config.ts` (Vite/Vitest config)

**Testing Standards**:
- Unit tests for React components in isolation
- Integration tests for new API consumption patterns
- Mock external API calls during testing
- Ensure no regressions in existing functionality
- Test data mapping and rendering of new API responses

**Required Tests**:
- Update existing tests for new data structures
- Add tests for TypeScript interface compliance
- Test error handling for new API responses
- Verify proper data mapping from rich backend models

### Testing

**Testing Approach**:
- **Unit Tests**: Component-level testing with mocked API calls
- **Integration Tests**: End-to-end API consumption testing
- **Regression Tests**: Ensure existing features continue working

**Test Files to Update**:
- `tests/_index.test.tsx` - Main route tests
- `tests/api.test.tsx` - API layer tests
- Component-specific test files for updated components

**Testing Requirements**:
- Mock `~/lib/utils` module for API calls
- Test new TypeScript interfaces
- Verify data mapping from backend APIs
- Ensure error handling for new API contracts

### Change Log

| Date | Version | Description | Author |
|------|---------|-------------|---------|
| 2025-09-06 | v1.0 | Initial story creation for frontend adaptation per Winston's request | Bob (Scrum Master) |
| 2025-09-06 | v1.1 | Populated Dev Notes with architecture details, added Testing section, fixed story format | Sarah (PO) |
| 2025-09-06 | v1.2 | Updated status to BLOCKED - Dependencies 1-03 and 2-02 must complete first | Sarah (PO) |

### Dev Agent Record

*This section will be populated by the development agent during implementation*

### QA Results

*This section will be populated by the QA agent after story completion*