package com.musichub.bootstrap.producer;

import com.musichub.shared.events.ArtistCreditInfo;
import com.musichub.shared.events.TrackWasRegistered;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkiverse.wiremock.devservice.ConnectWireMock;
import io.restassured.http.ContentType;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Singleton;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * Integration tests for Producer Registration with external API simulation using WireMock.
 * These tests verify the complete flow from REST endpoint through external API calls to event publishing.
 * 
 * This resolves CDI build failures by simulating external music platform APIs instead of requiring
 * real credentials for Tidal/Spotify services.
 */
@QuarkusTest
@ConnectWireMock
@TestMethodOrder(OrderAnnotation.class)
@DisplayName("Producer Registration with External API Integration Tests")
class ProducerRegistrationWithExternalApiIntegrationTest {

    @ConfigProperty(name = "quarkus.rest-client.music-platform-client.url")
    String musicPlatformUrl;

    private static final String TEST_ISRC = "FRLA12400001";
    private static final String TRACKS_ENDPOINT = "/tracks";

    /**
     * Event capture singleton to collect TrackWasRegistered events during tests.
     * This allows us to verify that events are properly published after successful track registration.
     */
    @Singleton
    public static class EventCapture {
        private final List<TrackWasRegistered> capturedEvents = new ArrayList<>();
        private CountDownLatch eventLatch = new CountDownLatch(1);

        public void onTrackWasRegistered(@Observes TrackWasRegistered event) {
            capturedEvents.add(event);
            eventLatch.countDown();
        }

        public List<TrackWasRegistered> getCapturedEvents() {
            return capturedEvents;
        }

        public void reset() {
            capturedEvents.clear();
            eventLatch = new CountDownLatch(1);
        }

        public boolean waitForEvent(long timeout, TimeUnit unit) throws InterruptedException {
            return eventLatch.await(timeout, unit);
        }
    }

    private EventCapture eventCapture = new EventCapture();

    @BeforeEach
    void setUp() {
        eventCapture.reset();
    }

    @Test
    @DisplayName("Should register track and publish event when external API returns success")
    void shouldRegisterTrackAndPublishEvent_whenExternalApiSucceeds() throws InterruptedException {
        // Given - WireMock successful response for Tidal JSON:API format
        configureWireMockSuccessResponse();

        // When - POST request to register track
        given()
            .contentType(ContentType.JSON)
            .body("""
                {
                    "isrc": "%s"
                }
                """.formatted(TEST_ISRC))
            .when()
            .post("/api/v1/producers")
            .then()
            .statusCode(202)  // Accepted
            .body("tracks.size()", equalTo(1))
            .body("producerCode", notNullValue())
            .body("id", notNullValue());

        // Then - verify event was published
        boolean eventReceived = eventCapture.waitForEvent(5, TimeUnit.SECONDS);
        assertThat(eventReceived).isTrue();
        
        List<TrackWasRegistered> events = eventCapture.getCapturedEvents();
        assertThat(events).hasSize(1);
        
        TrackWasRegistered event = events.get(0);
        assertThat(event.isrc().value()).isEqualTo(TEST_ISRC);
        assertThat(event.title()).isEqualTo("Bohemian Rhapsody");
        assertThat(event.artistCredits()).containsExactly(ArtistCreditInfo.withName("Queen"));
    }

    @Test
    @DisplayName("Should return 422 and publish no event when external API returns 404")
    void shouldReturn422AndPublishNoEvent_whenExternalApiReturns404() throws InterruptedException {
        // Given - WireMock 404 response (track not found)
        configureWireMock404Response();

        // When - POST request to register track
        given()
            .contentType(ContentType.JSON)
            .body("""
                {
                    "isrc": "%s"
                }
                """.formatted(TEST_ISRC))
            .when()
            .post("/api/v1/producers")
            .then()
            .statusCode(422)  // Unprocessable Entity
            .body("error", equalTo("TRACK_NOT_FOUND_EXTERNAL"))
            .body("message", containsString("could not find metadata"));

        // Then - verify no event was published
        boolean eventReceived = eventCapture.waitForEvent(2, TimeUnit.SECONDS);
        assertThat(eventReceived).isFalse();
        assertThat(eventCapture.getCapturedEvents()).isEmpty();
    }

    @Test
    @DisplayName("Should return 422 when external API returns 500 server error")
    void shouldReturn422_whenExternalApiReturns500() throws InterruptedException {
        // Given - WireMock 500 server error response
        configureWireMock500Response();

        // When - POST request to register track
        given()
            .contentType(ContentType.JSON)
            .body("""
                {
                    "isrc": "%s"
                }
                """.formatted(TEST_ISRC))
            .when()
            .post("/api/v1/producers")
            .then()
            .statusCode(422)  // Unprocessable Entity (external service error)
            .body("error", equalTo("TRACK_NOT_FOUND_EXTERNAL"))
            .body("message", containsString("could not find metadata"));

        // Then - verify no event was published
        boolean eventReceived = eventCapture.waitForEvent(2, TimeUnit.SECONDS);
        assertThat(eventReceived).isFalse();
        assertThat(eventCapture.getCapturedEvents()).isEmpty();
    }

    /**
     * Configures WireMock to simulate a successful Tidal API response.
     * Uses Tidal's actual JSON:API response structure.
     */
    private void configureWireMockSuccessResponse() {
        // Reset WireMock and configure successful response
        // Note: WireMock server is managed by Quarkus WireMock extension
        stubFor(get(urlMatching(TRACKS_ENDPOINT + "\\?.*filter\\[isrc\\]=" + TEST_ISRC + ".*"))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/vnd.api+json")
                .withBody("""
                    {
                        "data": [{
                            "id": "123456",
                            "type": "tracks",
                            "attributes": {
                                "isrc": "%s",
                                "title": "Bohemian Rhapsody"
                            },
                            "relationships": {
                                "artists": {
                                    "data": [{"id": "1", "type": "artists"}]
                                }
                            }
                        }],
                        "included": [{
                            "id": "1",
                            "type": "artists",
                            "attributes": {
                                "name": "Queen"
                            }
                        }]
                    }
                    """.formatted(TEST_ISRC))));
    }

    /**
     * Configures WireMock to simulate a 404 Not Found response from the external API.
     */
    private void configureWireMock404Response() {
        stubFor(get(urlMatching(TRACKS_ENDPOINT + "\\?.*filter\\[isrc\\]=" + TEST_ISRC + ".*"))
            .willReturn(aResponse()
                .withStatus(404)
                .withHeader("Content-Type", "application/vnd.api+json")
                .withBody("""
                    {
                        "errors": [{
                            "status": "404",
                            "title": "Not Found",
                            "detail": "Track not found"
                        }]
                    }
                    """)));
    }

    /**
     * Configures WireMock to simulate a 500 Internal Server Error from the external API.
     */
    private void configureWireMock500Response() {
        stubFor(get(urlMatching(TRACKS_ENDPOINT + "\\?.*filter\\[isrc\\]=" + TEST_ISRC + ".*"))
            .willReturn(aResponse()
                .withStatus(500)
                .withHeader("Content-Type", "application/vnd.api+json")
                .withBody("""
                    {
                        "errors": [{
                            "status": "500",
                            "title": "Internal Server Error",
                            "detail": "Service temporarily unavailable"
                        }]
                    }
                    """)));
    }
}