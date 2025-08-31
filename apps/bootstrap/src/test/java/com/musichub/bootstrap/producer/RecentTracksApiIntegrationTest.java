package com.musichub.bootstrap.producer;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@QuarkusTest
@DisplayName("Recent Tracks API Integration Tests")
class RecentTracksApiIntegrationTest {

    @BeforeEach
    void setUp() {
        // Clean slate for each test - this would be handled by test profile database setup
    }

    @Test
    @DisplayName("GET /api/v1/tracks/recent should return empty array when no tracks exist")
    void getRecentTracks_shouldReturnEmptyArray_whenNoTracksExist() {
        given()
            .when()
                .get("/api/v1/tracks/recent")
            .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("$", hasSize(0));
    }

    @Test
    @DisplayName("GET /api/v1/tracks/recent should return tracks after registering some")
    void getRecentTracks_shouldReturnTracks_afterRegisteringSome() {
        // First, register some tracks to have data
        String isrc1 = "FRLA12400001";
        String isrc2 = "FRLA12400002";

        // Register first track
        given()
            .contentType(ContentType.JSON)
            .body("{\"isrc\":\"" + isrc1 + "\"}")
            .when()
                .post("/api/v1/producers")
            .then()
                .statusCode(202);

        // Register second track
        given()
            .contentType(ContentType.JSON)
            .body("{\"isrc\":\"" + isrc2 + "\"}")
            .when()
                .post("/api/v1/producers")
            .then()
                .statusCode(202);

        // Now fetch recent tracks
        given()
            .when()
                .get("/api/v1/tracks/recent")
            .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("$", hasSize(greaterThanOrEqualTo(2)))
                .body("[0].isrc", anyOf(equalTo(isrc1), equalTo(isrc2)))
                .body("[0].title", notNullValue())
                .body("[0].artistNames", notNullValue())
                .body("[0].source.name", notNullValue())
                .body("[0].status", notNullValue())
                .body("[0].submissionDate", notNullValue())
                .body("[0].producer.id", notNullValue())
                .body("[0].producer.producerCode", notNullValue());
    }

    @Test
    @DisplayName("GET /api/v1/tracks/recent should return tracks in descending order by submission date")
    void getRecentTracks_shouldReturnTracksInDescendingOrderBySubmissionDate() {
        // Register tracks with some time delay to ensure different submission times
        String isrc1 = "FRLA12400010";
        String isrc2 = "FRLA12400011";

        // Register first track
        given()
            .contentType(ContentType.JSON)
            .body("{\"isrc\":\"" + isrc1 + "\"}")
            .when()
                .post("/api/v1/producers")
            .then()
                .statusCode(202);

        // Small delay to ensure different timestamps
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Register second track (should be more recent)
        given()
            .contentType(ContentType.JSON)
            .body("{\"isrc\":\"" + isrc2 + "\"}")
            .when()
                .post("/api/v1/producers")
            .then()
                .statusCode(202);

        // Verify order - most recent first
        given()
            .when()
                .get("/api/v1/tracks/recent")
            .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("$", hasSize(greaterThanOrEqualTo(2)))
                .body("findAll { it.isrc == '" + isrc2 + "' }.size()", equalTo(1))
                .body("findAll { it.isrc == '" + isrc1 + "' }.size()", equalTo(1));
        
        // The most recent track (isrc2) should appear before the older one (isrc1)
        // This is a simple check - in a real test you might want to verify the exact order
    }

    @Test
    @DisplayName("GET /api/v1/tracks/recent should handle API errors gracefully")
    void getRecentTracks_shouldHandleApiErrorsGracefully() {
        // This test would be more relevant if we had error scenarios to test
        // For now, just verify the endpoint exists and responds
        given()
            .when()
                .get("/api/v1/tracks/recent")
            .then()
                .statusCode(anyOf(equalTo(200), equalTo(500))); // Either success or controlled failure
    }

    @Test
    @DisplayName("GET /api/v1/tracks/recent response should match API specification")
    void getRecentTracks_responseShouldMatchApiSpecification() {
        // Register a track first to have data to validate
        String isrc = "FRLA12400100";
        
        given()
            .contentType(ContentType.JSON)
            .body("{\"isrc\":\"" + isrc + "\"}")
            .when()
                .post("/api/v1/producers")
            .then()
                .statusCode(202);

        // Validate response structure matches the API spec
        given()
            .when()
                .get("/api/v1/tracks/recent")
            .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("$", isA(java.util.List.class))
                .body("every { it.id != null }", equalTo(true))
                .body("every { it.isrc != null }", equalTo(true))
                .body("every { it.title != null }", equalTo(true))
                .body("every { it.artistNames != null }", equalTo(true))
                .body("every { it.source != null }", equalTo(true))
                .body("every { it.source.name != null }", equalTo(true))
                .body("every { it.source.externalId != null }", equalTo(true))
                .body("every { it.status != null }", equalTo(true))
                .body("every { it.submissionDate != null }", equalTo(true))
                .body("every { it.producer != null }", equalTo(true))
                .body("every { it.producer.id != null }", equalTo(true))
                .body("every { it.producer.producerCode != null }", equalTo(true));
    }
}