# P2-6: Update REST controller to handle TrackNotFoundInExternalServiceException

Story: docs/stories/story-P2.md

## Description
Enhance the existing ProducerResource REST controller to catch and properly handle the new TrackNotFoundInExternalServiceException, returning appropriate HTTP 422 responses as specified in the acceptance criteria.

## Acceptance Criteria
- Given TrackNotFoundInExternalServiceException is thrown by ProducerService, when handling POST request, then controller returns HTTP 422 Unprocessable Entity
- Error response includes descriptive message indicating external service failure
- Existing error handling for other scenarios remains unchanged
- Response follows consistent error format used throughout the application
- Controller maintains clean separation of concerns (no business logic)

## Dependencies
- P2-5: ProducerService must throw TrackNotFoundInExternalServiceException
- P2-4: Exception class must be defined
- Story P1: Existing ProducerResource from previous story

## Estimate
- 1 pt

## Status
- Not Started

## Technical Details

### Files to modify:
- `apps/producer/producer-adapters/producer-adapter-rest/src/main/java/com/musichub/producer/adapter/rest/ProducerResource.java`

### Error handling enhancement:
```java
@POST
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public Response register(RegisterTrackRequest request) {
    try {
        // Existing validation logic...
        
        Producer producer = registerTrackUseCase.registerTrack(request.isrc);
        ProducerDto response = ProducerMapper.toDto(producer);
        
        return Response.accepted(response).build(); // 202
        
    } catch (IllegalArgumentException e) {
        // Existing 400 handling...
        
    } catch (TrackNotFoundInExternalServiceException e) {
        // NEW: Handle external service failure
        ErrorResponse error = new ErrorResponse(
            "TRACK_NOT_FOUND_EXTERNAL", 
            "The ISRC was valid, but we could not find metadata for it on external services."
        );
        return Response.status(422).entity(error).build();
        
    } catch (Exception e) {
        // Existing 500 handling...
    }
}
```

### Error response consistency:
```java
public class ErrorResponse {
    public String error;
    public String message;
    
    public ErrorResponse(String error, String message) {
        this.error = error;
        this.message = message;
    }
}
```

## Validation Steps
1. Add try-catch block for TrackNotFoundInExternalServiceException in ProducerResource
2. Implement 422 error response with descriptive message
3. Ensure error response format is consistent with existing error handling
4. Write unit tests for new error scenario:
   - Mock service to throw TrackNotFoundInExternalServiceException
   - Verify 422 status code is returned
   - Verify error message is descriptive and user-friendly
5. Test that existing error handling still works correctly
6. Verify no business logic leaks into controller layer
7. Test integration with actual service exception throwing

## Next Task
- P2-7: Update frontend Toast notifications for new error scenario

## Artifacts
- Modified: `ProducerResource.java` with enhanced error handling
- Created: Unit tests for 422 error scenario
- Updated: Integration tests to cover new error path