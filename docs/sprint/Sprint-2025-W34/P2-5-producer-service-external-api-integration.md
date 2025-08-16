# P2-5: Modify ProducerService to integrate external API call and event publishing

Story: docs/stories/story-P2.md

## Description
Enhance the existing ProducerService to fetch track metadata from external API, create Track objects, add them to Producer aggregates, and publish TrackWasRegistered events to the Vert.x event bus.

## Acceptance Criteria
- Given a valid ISRC, when ProducerService processes the request, then it calls external API to fetch track metadata
- Given successful API response, when track is added to producer, then TrackWasRegistered event is published to event bus
- Given external API failure, when processing request, then TrackNotFoundInExternalServiceException is thrown
- Service maintains idempotent behavior (duplicate tracks don't create duplicate events)
- Event is published only after successful aggregate save to database

## Dependencies
- P2-3: Producer.addTrack method must exist
- P2-4: MusicPlatformClient interface must be available
- P2-2: TrackWasRegistered event must be defined
- Story P1: Existing ProducerService from previous story

## Estimate
- 3 pts

## Status
- Not Started

## Technical Details

### Files to modify:
- `apps/producer/producer-application/src/main/java/com/musichub/producer/application/service/ProducerService.java`

### Enhanced service logic:
```java
@ApplicationScoped
public class ProducerService implements RegisterTrackUseCase {
    
    @Inject
    ProducerRepository repository;
    
    @Inject
    MusicPlatformClient musicPlatformClient;
    
    @Inject
    Event<TrackWasRegistered> trackRegisteredEvent;
    
    @Override
    @Transactional
    public Producer registerTrack(String isrcInput) {
        // 1. Existing logic: normalize ISRC, find/create Producer
        
        // 2. NEW: Fetch track metadata from external API
        TrackMetadataDto metadata = fetchTrackMetadata(isrcInput);
        
        // 3. NEW: Create Track domain object
        Track track = createTrackFromMetadata(metadata);
        
        // 4. NEW: Add track to producer (idempotent)
        boolean wasAdded = producer.addTrack(track);
        
        // 5. Save producer to database
        Producer savedProducer = repository.save(producer);
        
        // 6. NEW: Publish event only if track was actually added
        if (wasAdded) {
            publishTrackWasRegisteredEvent(track, savedProducer);
        }
        
        return savedProducer;
    }
    
    private TrackMetadataDto fetchTrackMetadata(String isrc) {
        // Handle external API call with exception wrapping
    }
    
    private void publishTrackWasRegisteredEvent(Track track, Producer producer) {
        // Publish to Vert.x event bus
    }
}
```

## Validation Steps
1. Inject MusicPlatformClient and Event<TrackWasRegistered> into existing ProducerService
2. Implement track metadata fetching with proper exception handling
3. Add Track creation logic from external metadata
4. Integrate with Producer.addTrack method
5. Implement event publishing after successful save
6. Write comprehensive unit tests:
   - Successful flow with API call and event publishing
   - API failure scenarios (404, 500, timeout)
   - Idempotent behavior (duplicate track, no duplicate event)
   - Event publishing only after successful save
7. Test transaction rollback scenarios
8. Verify integration with existing ProducerService functionality

## Next Task
- P2-6: Update REST controller to handle new error scenarios

## Artifacts
- Modified: `ProducerService.java` with external API integration
- Created: Unit tests for enhanced service functionality
- Updated: Service integration tests with WireMock