# P2-2: Define TrackWasRegistered domain event

Story: docs/stories/story-P2.md

## Description
Create the `TrackWasRegistered` domain event class in producer-domain that will be published to the event bus when a track is successfully added to a producer aggregate.

## Acceptance Criteria
- Given a TrackWasRegistered event, when created, then it contains ISRC, title, and artist names
- Event class is immutable and serializable for event bus transport
- Event follows domain event patterns with proper constructor validation
- Event can be JSON serialized/deserialized correctly
- Event class is located in producer-domain module (not shared-kernel yet)

## Dependencies
- P2-1: Track entity must exist (to understand track structure)

## Estimate
- 1 pt

## Status
- Not Started

## Technical Details

### Files to create:
- `apps/producer/producer-domain/src/main/java/com/musichub/producer/domain/events/TrackWasRegistered.java`

### Event structure:
```java
public class TrackWasRegistered {
    private final String isrc;
    private final String title;
    private final List<String> artistNames;
    private final String producerCode;
    private final Instant registeredAt;
    
    // Constructor with validation
    // Factory method: TrackWasRegistered.from(Track, Producer)
    // Getters
    // JSON serialization support
    // equals/hashCode/toString
}
```

### Design considerations:
- Event should contain data, not domain object references
- Timestamp for event ordering/debugging
- Producer context for event consumers
- Immutable design for thread safety

## Validation Steps
1. Create TrackWasRegistered event class
2. Add factory method to create from Track and Producer
3. Write unit tests for event creation and validation
4. Test JSON serialization/deserialization
5. Verify event is immutable (no setters, final fields)
6. Test factory method with various Track/Producer combinations
7. Validate timestamp is set correctly on creation

## Next Task
- P2-3: Add addTrack method to Producer aggregate

## Artifacts
- Created: `TrackWasRegistered.java` domain event
- Created: Unit tests for event class