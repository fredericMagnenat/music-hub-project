# P2-3: Add addTrack method to Producer aggregate

Story: docs/stories/story-P2.md

## Description
Extend the Producer aggregate with an `addTrack(Track track)` method that handles business rules and invariants for track addition while maintaining aggregate consistency.

## Acceptance Criteria
- Given a Producer aggregate, when addTrack is called with a valid Track, then the track is added to the producer's track collection
- Given a Producer aggregate, when addTrack is called with a duplicate track (same ISRC), then the operation is idempotent (no duplicate, returns false)
- Method validates that track's producer code matches the aggregate's producer code
- Method maintains aggregate consistency and business invariants
- Method returns boolean indicating if track was actually added (true) or was duplicate (false)

## Dependencies
- P2-1: Track entity must exist
- P2-2: TrackWasRegistered event must be defined
- Story P1: Producer aggregate must exist from previous story

## Estimate
- 2 pts

## Status
- Done

## Technical Details

### Files to modify:
- `apps/producer/producer-domain/src/main/java/com/musichub/producer/domain/model/Producer.java`

### Method signature:
```java
public class Producer {
    // Existing fields and methods...
    private final Set<Track> tracks = new HashSet<>();
    
    /**
     * Adds a track to this producer's collection.
     * @param track The track to add
     * @return true if track was added, false if it already existed (idempotent)
     * @throws IllegalArgumentException if track's producer code doesn't match this producer
     */
    public boolean addTrack(Track track) {
        // Validation logic
        // Business rules enforcement
        // Add to collection if not present
        // Return appropriate boolean
    }
    
    public Set<Track> getTracks() {
        return Collections.unmodifiableSet(tracks);
    }
}
```

### Business rules to implement:
1. Track's producer code must match this producer's code
2. No duplicate tracks (same ISRC)
3. Maintain aggregate consistency
4. Idempotent behavior

## Validation Steps
1. Modify Producer aggregate to add track management
2. Implement addTrack method with business validation
3. Write comprehensive unit tests:
   - Adding valid track succeeds
   - Adding duplicate track is idempotent
   - Adding track with wrong producer code fails
   - Track collection is immutable from outside
4. Test integration with existing Producer functionality
5. Verify aggregate consistency is maintained
6. Test edge cases (null track, invalid track data)

## Next Task
- P2-4: Create MusicPlatformClient interface in producer-adapter-spi

## Artifacts
- Modified: `Producer.java` with addTrack method
- Created: Unit tests for track management functionality