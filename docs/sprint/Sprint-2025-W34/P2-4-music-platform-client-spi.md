# P2-4: Create MusicPlatformClient interface in producer-adapter-spi

Story: docs/stories/story-P2.md

## Description
Create an HTTP client interface for external music platform API integration using Quarkus REST Client, with proper error handling and secret management for API keys.

## Acceptance Criteria
- Given a MusicPlatformClient interface, when getTrackByIsrc is called, then it returns track metadata or appropriate error response
- Interface uses Quarkus REST Client annotations for HTTP integration
- Client handles API authentication via configuration (API keys)
- Interface defines proper exception handling for external service failures
- Client configuration supports different environments (dev/test/prod)

## Dependencies
- P2-1: Track entity must exist (for return type understanding)
- Architecture: Security and Secrets Management section guidance

## Estimate
- 2 pts

## Status
- Not Started

## Technical Details

### Files to create:
- `apps/producer/producer-adapters/producer-adapter-spi/src/main/java/com/musichub/producer/adapter/spi/MusicPlatformClient.java`
- `apps/producer/producer-adapters/producer-adapter-spi/src/main/java/com/musichub/producer/adapter/spi/dto/TrackMetadataDto.java`
- `apps/producer/producer-adapters/producer-adapter-spi/src/main/java/com/musichub/producer/adapter/spi/exception/TrackNotFoundInExternalServiceException.java`

### MusicPlatformClient interface:
```java
@RegisterRestClient
@Path("/api/v1")
public interface MusicPlatformClient {
    
    @GET
    @Path("/tracks/{isrc}")
    @Produces(MediaType.APPLICATION_JSON)
    TrackMetadataDto getTrackByIsrc(@PathParam("isrc") String isrc);
}
```

### Configuration properties:
```properties
# External music platform API
quarkus.rest-client.music-platform-client.url=${MUSIC_PLATFORM_API_URL:http://localhost:9090}
quarkus.rest-client.music-platform-client.scope=javax.inject.Singleton

# API authentication
music-platform.api.key=${MUSIC_PLATFORM_API_KEY:}
```

### TrackMetadataDto structure:
```java
public class TrackMetadataDto {
    public String isrc;
    public String title;
    public List<String> artistNames;
    public String platform;
    // JSON mapping annotations
}
```

## Validation Steps
1. Create MusicPlatformClient interface with Quarkus REST Client annotations
2. Create TrackMetadataDto for API response mapping
3. Create custom exception for service failures
4. Add configuration properties for API URL and authentication
5. Write unit tests with mock HTTP responses
6. Test integration with Quarkus REST Client configuration
7. Verify proper exception handling for 404, 500, timeout scenarios
8. Test API key configuration and injection

## Next Task
- P2-5: Modify ProducerService to integrate external API call

## Artifacts
- Created: `MusicPlatformClient.java` interface
- Created: `TrackMetadataDto.java` DTO
- Created: `TrackNotFoundInExternalServiceException.java`
- Updated: Configuration properties for external API