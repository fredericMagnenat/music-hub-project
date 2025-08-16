# P2-1: Create Track entity and Source value object in producer-domain

Story: docs/stories/story-P2.md

## Description
Create the `Track` entity and `Source` value object in the producer-domain module as foundational domain objects for track management functionality.

## Acceptance Criteria
- Given a Track entity, when created with ISRC, title, and artist names, then all required fields are validated and stored correctly
- Given a Source value object, when created, then it represents the origin/platform of track metadata
- Track entity implements proper equality and hashCode based on ISRC
- Source value object is immutable with proper validation
- Both classes follow domain layer coding standards (immutable, no framework dependencies)

## Dependencies
- Story P1 must be complete (Producer aggregate exists)
- ISRC value object must exist in shared-kernel

## Estimate
- 2 pts

## Status
- Not Started

## Technical Details

### Files to create:
- `apps/producer/producer-domain/src/main/java/com/musichub/producer/domain/model/Track.java`
- `apps/producer/producer-domain/src/main/java/com/musichub/producer/domain/values/Source.java`

### Track entity structure:
```java
public class Track {
    private final ISRC isrc;
    private final String title;
    private final List<String> artistNames;
    private final Source source;
    
    // Constructor with validation
    // Getters
    // equals/hashCode based on ISRC
    // toString for debugging
}
```

### Source value object structure:
```java
public class Source {
    private final String platform;
    private final String apiVersion;
    
    // Factory methods: Source.of(platform, apiVersion)
    // Validation logic
    // Immutable implementation
}
```

## Validation Steps
1. Create Track entity with proper constructor validation
2. Create Source value object with immutable design
3. Write unit tests for both classes
4. Verify Track equality works correctly with same ISRC
5. Verify Source validation rejects invalid platforms
6. Test integration with existing ISRC value object
7. Ensure no framework dependencies in domain layer

## Next Task
- P2-2: Define TrackWasRegistered domain event

## Artifacts
- Created: `Track.java` entity
- Created: `Source.java` value object  
- Created: Unit tests for both classes