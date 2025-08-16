# Sprint 2025-W34

Goal: Implement Flyway Migration Generation Workflow (Story 10) to enable automated database schema evolution following documented migration strategy.

## Scope links:
- Story: docs/stories/10.flyway-migration-workflow.md
- Architecture: docs/architecture/database-migration-flyway.md
- Sprint Previous: docs/sprint/Sprint-2025-W33/

## Backlog (tickets):

### Story 10: Flyway Migration Workflow
- **FLYWAY-1**: Configure Hibernate Schema Export Plugin
- **FLYWAY-2**: Create dev-schema profile configurations  
- **FLYWAY-3**: Implement migration generation script core logic
- **FLYWAY-4**: Add version range validation and file generation
- **FLYWAY-5**: Integration testing and complete workflow validation

### Story P2: Integrate Track and Publish Event
- **P2-1**: Create Track entity and Source value object in producer-domain
- **P2-2**: Define TrackWasRegistered domain event
- **P2-3**: Add addTrack method to Producer aggregate
- **P2-4**: Create MusicPlatformClient interface in producer-adapter-spi
- **P2-5**: Modify ProducerService to integrate external API call and event publishing
- **P2-6**: Update REST controller to handle TrackNotFoundInExternalServiceException
- **P2-7**: Update frontend Toast notifications for enhanced API responses
- **P2-8**: Backend integration tests with WireMock for external API scenarios
- **P2-9**: Frontend tests for enhanced Toast scenarios

## Sequential Dependencies:
```
FLYWAY: FLYWAY-1 → FLYWAY-2 → FLYWAY-3 → FLYWAY-4 → FLYWAY-5

P2: P2-1 → P2-2 → P2-3 → P2-4 → P2-5 → P2-6 → P2-7 → P2-8 → P2-9
```

## Definition of Done

### Story 10: Flyway Migration Workflow
- [ ] Complete workflow: entity change → generate migration → apply migration works end-to-end  
- [ ] Version range validation prevents producer/artist context conflicts
- [ ] Both PostgreSQL and H2 schema generation profiles functional
- [ ] Generated migration files follow documented template and naming conventions
- [ ] Script handles all edge cases with clear error messages
- [ ] Documentation examples match actual script behavior

### Story P2: Integrate Track and Publish Event  
- [ ] All AC in story P2 verified by automated tests
- [ ] External API integration working with proper error handling (422 for external failures)
- [ ] TrackWasRegistered events published correctly after successful track addition
- [ ] Frontend Toast notifications show track details on success and external service errors on failure
- [ ] Idempotent behavior maintained (duplicate tracks don't create duplicate events)
- [ ] WireMock integration tests cover both success and failure scenarios

## Active Issues:
- BUG-webui-creation-404.md: WebUI 404 on creation request (parallel work)

## Estimated Total: 28 pts

### Story 10 (Flyway): 10 pts
- FLYWAY-1: 1 pt
- FLYWAY-2: 1 pt  
- FLYWAY-3: 2 pts
- FLYWAY-4: 3 pts
- FLYWAY-5: 3 pts

### Story P2 (Track Integration): 18 pts  
- P2-1: 2 pts
- P2-2: 1 pt
- P2-3: 2 pts
- P2-4: 2 pts
- P2-5: 3 pts
- P2-6: 1 pt
- P2-7: 2 pts
- P2-8: 3 pts
- P2-9: 2 pts