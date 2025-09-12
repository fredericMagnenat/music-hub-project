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
import java.util.UUID;
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
        UUID artistId = UUID.randomUUID();
        configureWireMockSuccessResponse(artistId);

        // When - POST request to register track
        given()
            .contentType(ContentType.JSON)
            .body(String.format("{\"isrc\":\"%s\"}", TEST_ISRC))
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
        assertThat(event.artistCredits()).containsExactly(new ArtistCreditInfo("Queen", artistId.toString()));
    }

    /**
     * Configures WireMock to simulate a successful Tidal API response.
     * Uses Tidal's actual JSON:API response structure.
     */
    private void configureWireMockSuccessResponse(UUID artistId) {
        String urlPattern = TRACKS_ENDPOINT + "\\?.*filter\\[isrc\\]=" + TEST_ISRC + ".*";
        String jsonBody = String.format(
            "{\"isrc\":\"%s\",\"title\":\"Bohemian Rhapsody\",\"platform\":\"tidal\",\"artists\":[{\"id\":\"%s\",\"name\":\"Queen\"}]}",
            TEST_ISRC, artistId
        );

        stubFor(get(urlMatching(urlPattern))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBody(jsonBody)));
    }
}
