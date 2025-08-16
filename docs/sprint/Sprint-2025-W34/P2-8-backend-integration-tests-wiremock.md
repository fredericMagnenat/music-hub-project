# P2-8: Backend integration tests with WireMock for external API scenarios

Story: docs/stories/story-P2.md

## Description
Create comprehensive integration tests for the ProducerService using WireMock to simulate external music platform API responses, covering both successful track retrieval and failure scenarios with proper event verification.

## Acceptance Criteria
- Given WireMock simulates successful API response, when registering track, then TrackWasRegistered event is published and can be verified
- Given WireMock simulates 404 API response, when registering track, then TrackNotFoundInExternalServiceException is thrown and no event is published
- Integration tests run in Quarkus test environment with real database and event bus
- Tests verify complete flow from REST endpoint through to event publishing
- WireMock configuration is properly isolated and cleaned up between tests

## Dependencies
- P2-7: Complete frontend implementation (for full flow understanding)
- P2-5: ProducerService with external API integration
- All previous P2 tasks must be complete for integration testing

## Estimate
- 3 pts

## Status
- Not Started

## Technical Details

### Files to create:
- `apps/bootstrap/src/test/java/com/musichub/bootstrap/producer/ProducerRegistrationWithExternalApiIntegrationTest.java`

### WireMock test structure:
```java
@QuarkusTest
@DisplayName("Producer Registration with External API Integration Tests")
class ProducerRegistrationWithExternalApiIntegrationTest {
    
    @Inject
    EntityManager entityManager;
    
    @ConfigProperty(name = "quarkus.rest-client.music-platform-client.url")
    String musicPlatformUrl;
    
    private WireMockServer wireMockServer;
    private List<TrackWasRegistered> capturedEvents = new ArrayList<>();
    
    @BeforeEach
    void setUp() {
        // Setup WireMock server
        // Clean database
        // Setup event capture
    }
    
    @Test
    @TestTransaction
    @DisplayName("Should register track and publish event when external API returns success")
    void shouldRegisterTrackAndPublishEvent_whenExternalApiSucceeds() {
        // Given - WireMock successful response
        wireMockServer.stubFor(get(urlEqualTo("/api/v1/tracks/FRLA12400001"))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBody("""
                    {
                        "isrc": "FRLA12400001",
                        "title": "Bohemian Rhapsody",
                        "artistNames": ["Queen"],
                        "platform": "spotify"
                    }
                    """)));
        
        // When
        given()
            .contentType(ContentType.JSON)
            .body("{\"isrc\":\"FRLA12400001\"}")
            .when()
            .post("/api/v1/producers")
            .then()
            .statusCode(202)
            .body("tracks", hasSize(1))
            .body("tracks[0].title", equalTo("Bohemian Rhapsody"));
            
        // Then - verify event was published
        await().untilAsserted(() -> {
            assertThat(capturedEvents).hasSize(1);
            TrackWasRegistered event = capturedEvents.get(0);
            assertThat(event.isrc()).isEqualTo("FRLA12400001");
            assertThat(event.title()).isEqualTo("Bohemian Rhapsody");
            assertThat(event.artistNames()).containsExactly("Queen");
        });
    }
    
    @Test
    @TestTransaction
    @DisplayName("Should return 422 and publish no event when external API returns 404")
    void shouldReturn422AndPublishNoEvent_whenExternalApiReturns404() {
        // Given - WireMock 404 response
        // When - POST request
        // Then - verify 422 response and no event published
    }
}
```

### Event capture mechanism:
```java
void onTrackWasRegistered(@ObservesAsync TrackWasRegistered event) {
    capturedEvents.add(event);
}
```

## Validation Steps
1. Set up WireMock server with proper lifecycle management
2. Configure WireMock to simulate external music platform API
3. Implement event capture mechanism for TrackWasRegistered events
4. Create test for successful API response:
   - Mock external API success response with track metadata
   - Verify 202 response with track details
   - Verify TrackWasRegistered event is published with correct data
   - Verify database state is updated correctly
5. Create test for API failure scenario:
   - Mock external API 404 response
   - Verify 422 response is returned
   - Verify no event is published
   - Verify no track is added to database
6. Test idempotent behavior with external API
7. Test various API failure scenarios (500, timeout)
8. Verify WireMock cleanup between tests
9. Test integration with real Quarkus event bus

## Next Task
- P2-9: Frontend tests for enhanced Toast scenarios

## Artifacts
- Created: Integration test class with WireMock setup
- Created: Event capture utilities for testing
- Created: WireMock response templates for various scenarios
- Updated: Test configuration for external API integration