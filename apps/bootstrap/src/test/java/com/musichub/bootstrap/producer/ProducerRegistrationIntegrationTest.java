package com.musichub.bootstrap.producer;

import io.quarkus.test.TestTransaction;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@QuarkusTest
@DisplayName("Producer Registration Integration Tests")
class ProducerRegistrationIntegrationTest {
    @Inject
    EntityManager entityManager;

    @BeforeEach
    @Transactional
    void cleanDatabase() {
        // Clean the database before each test
        entityManager.createQuery("DELETE FROM ProducerEntity").executeUpdate();
        entityManager.flush();
    }

    @Test
    @TestTransaction
    @DisplayName("Should register track successfully end-to-end")
    void shouldRegisterTrackEndToEnd() {
        given()
                .contentType(ContentType.JSON)
                .body("{\"isrc\":\"FRLA12400001\"}")
                .when()
                .post("/api/v1/producers")
                .then()
                .statusCode(202)
                .body("producerCode", equalTo("FRLA1"))
                .body("id", notNullValue())
                .body("name", nullValue())
                .body("tracks", hasSize(1))
                .body("tracks[0].isrc", equalTo("FRLA12400001"));
    }

    @Test
    @TestTransaction
    @DisplayName("Should register track with different producer code")
    void shouldRegisterTrackWithDifferentProducerCode() {
        given()
                .contentType(ContentType.JSON)
                .body("{\"isrc\":\"GBUM71505078\"}")
                .when()
                .post("/api/v1/producers")
                .then()
                .statusCode(202)
                .body("producerCode", equalTo("GBUM7"))
                .body("tracks", hasSize(1))
                .body("tracks[0]", equalTo("GBUM71505078"));
    }

    @Test
    @TestTransaction
    @DisplayName("Should handle multiple tracks for same producer")
    void shouldHandleMultipleTracksForSameProducer() {
        // First track
        given()
                .contentType(ContentType.JSON)
                .body("{\"isrc\":\"FRLA12400001\"}")
                .when()
                .post("/api/v1/producers")
                .then()
                .statusCode(202)
                .body("producerCode", equalTo("FRLA1"))
                .body("tracks", hasSize(1));

        // Second track for same producer
        given()
                .contentType(ContentType.JSON)
                .body("{\"isrc\":\"FRLA12400002\"}")
                .when()
                .post("/api/v1/producers")
                .then()
                .statusCode(202)
                .body("producerCode", equalTo("FRLA1"))
                .body("tracks", hasSize(2))
                .body("tracks", hasItems("FRLA12400001", "FRLA12400002"));
    }

    @Test
    @TestTransaction
    @DisplayName("Should be idempotent when registering same track twice")
    void shouldBeIdempotentWhenRegisteringSameTrackTwice() {
        // First registration
        given()
                .contentType(ContentType.JSON)
                .body("{\"isrc\":\"FRLA12400001\"}")
                .when()
                .post("/api/v1/producers")
                .then()
                .statusCode(202);

        // Second registration of same track
        given()
                .contentType(ContentType.JSON)
                .body("{\"isrc\":\"FRLA12400001\"}")
                .when()
                .post("/api/v1/producers")
                .then()
                .statusCode(202)
                .body("producerCode", equalTo("FRLA1"))
                .body("tracks", hasSize(1))
                .body("tracks[0]", equalTo("FRLA12400001"));
    }

    @Test
    @TestTransaction
    @DisplayName("Should be idempotent across differently formatted ISRC inputs (hyphens vs canonical)")
    void shouldBeIdempotentAcrossDifferentFormats() {
        // First registration with hyphens
        given()
                .contentType(ContentType.JSON)
                .body("{\"isrc\":\"FR-LA1-24-00001\"}")
                .when()
                .post("/api/v1/producers")
                .then()
                .statusCode(202)
                .body("producerCode", equalTo("FRLA1"))
                .body("tracks", hasSize(1))
                .body("tracks[0]", equalTo("FRLA12400001"));

        // Second registration with canonical value
        given()
                .contentType(ContentType.JSON)
                .body("{\"isrc\":\"FRLA12400001\"}")
                .when()
                .post("/api/v1/producers")
                .then()
                .statusCode(202)
                .body("producerCode", equalTo("FRLA1"))
                .body("tracks", hasSize(1))
                .body("tracks[0]", equalTo("FRLA12400001"));
    }

    @Test
    @TestTransaction
    @DisplayName("Should normalize ISRC with hyphens")
    void shouldNormalizeIsrcWithHyphens() {
        given()
                .contentType(ContentType.JSON)
                .body("{\"isrc\":\"FR-LA1-24-00001\"}")
                .when()
                .post("/api/v1/producers")
                .then()
                .statusCode(202)
                .body("producerCode", equalTo("FRLA1"))
                .body("tracks", hasSize(1))
                .body("tracks[0]", equalTo("FRLA12400001"));
    }

    // Tests sans @TestTransaction (ne modifient pas la DB)
    @Test
    @DisplayName("Should return 400 when request body is missing")
    void shouldReturn400WhenRequestBodyIsMissing() {
        given()
                .contentType(ContentType.JSON)
                .when()
                .post("/api/v1/producers")
                .then()
                .statusCode(400)
                .body("error", equalTo("InvalidISRCFormat"))
                .body("message", equalTo("Field 'isrc' is required"));
    }

    @Test
    @DisplayName("Should return 400 when ISRC field is missing")
    void shouldReturn400WhenIsrcFieldIsMissing() {
        given()
                .contentType(ContentType.JSON)
                .body("{}")
                .when()
                .post("/api/v1/producers")
                .then()
                .statusCode(400)
                .body("error", equalTo("InvalidISRCFormat"))
                .body("message", equalTo("Field 'isrc' is required"));
    }

    @Test
    @DisplayName("Should return 400 when ISRC is empty")
    void shouldReturn400WhenIsrcIsEmpty() {
        given()
                .contentType(ContentType.JSON)
                .body("{\"isrc\":\"\"}")
                .when()
                .post("/api/v1/producers")
                .then()
                .statusCode(400)
                .body("error", equalTo("InvalidISRCFormat"))
                .body("message", equalTo("Field 'isrc' is required"));
    }

    @Test
    @DisplayName("Should return 400 when ISRC is blank")
    void shouldReturn400WhenIsrcIsBlank() {
        given()
                .contentType(ContentType.JSON)
                .body("{\"isrc\":\"   \"}")
                .when()
                .post("/api/v1/producers")
                .then()
                .statusCode(400)
                .body("error", equalTo("InvalidISRCFormat"))
                .body("message", equalTo("Field 'isrc' is required"));
    }

    @Test
    @DisplayName("Should return 400 when ISRC format is invalid")
    void shouldReturn400WhenIsrcFormatIsInvalid() {
        given()
                .contentType(ContentType.JSON)
                .body("{\"isrc\":\"INVALID\"}")
                .when()
                .post("/api/v1/producers")
                .then()
                .statusCode(400)
                .body("error", equalTo("InvalidISRCFormat"))
                .body("message", containsString("ISRC value 'INVALID' is invalid"));
    }

    @Test
    @DisplayName("Should return 400 when ISRC is too short")
    void shouldReturn400WhenIsrcIsTooShort() {
        given()
                .contentType(ContentType.JSON)
                .body("{\"isrc\":\"FR123\"}")
                .when()
                .post("/api/v1/producers")
                .then()
                .statusCode(400)
                .body("error", equalTo("InvalidISRCFormat"))
                .body("message", containsString("ISRC value 'FR123' is invalid"));
    }

    @Test
    @DisplayName("Should return 400 when producer code is invalid")
    void shouldReturn400WhenProducerCodeIsInvalid() {
        given()
                .contentType(ContentType.JSON)
                .body("{\"isrc\":\"1234512400001\"}")
                .when()
                .post("/api/v1/producers")
                .then()
                .statusCode(400)
                .body("error", equalTo("InvalidISRCFormat"))
                .body("message", containsString("ISRC value '1234512400001' is invalid"));
    }

    @Test
    @DisplayName("Should accept application/json content type")
    void shouldAcceptApplicationJsonContentType() {
        given()
                .contentType("application/json")
                .body("{\"isrc\":\"FRLA12400999\"}")  // ISRC unique
                .when()
                .post("/api/v1/producers")
                .then()
                .statusCode(202);
    }

    @Test
    @DisplayName("Should return proper content type in response")
    void shouldReturnProperContentTypeInResponse() {
        given()
                .contentType(ContentType.JSON)
                .body("{\"isrc\":\"FRLA12400998\"}")  // ISRC unique
                .when()
                .post("/api/v1/producers")
                .then()
                .statusCode(202)
                .contentType(ContentType.JSON);
    }

    @Test
    @TestTransaction
    @DisplayName("Should handle ISRC with lowercase letters")
    void shouldHandleIsrcWithLowercaseLetters() {
        given()
                .contentType(ContentType.JSON)
                .body("{\"isrc\":\"frla12400001\"}")
                .when()
                .post("/api/v1/producers")
                .then()
                .statusCode(202)
                .body("producerCode", equalTo("FRLA1"))
                .body("tracks", hasSize(1))
                .body("tracks[0]", equalTo("FRLA12400001"));
    }

    @Test
    @TestTransaction
    @DisplayName("Should handle ISRC with mixed case")
    void shouldHandleIsrcWithMixedCase() {
        given()
                .contentType(ContentType.JSON)
                .body("{\"isrc\":\"FrLa12400001\"}")
                .when()
                .post("/api/v1/producers")
                .then()
                .statusCode(202)
                .body("producerCode", equalTo("FRLA1"))
                .body("tracks", hasSize(1))
                .body("tracks[0]", equalTo("FRLA12400001"));
    }

    @Test
    @TestTransaction
    @DisplayName("Should handle ISRC with leading/trailing whitespace")
    void shouldHandleIsrcWithWhitespace() {
        given()
                .contentType(ContentType.JSON)
                .body("{\"isrc\":\"  FRLA12400001  \"}")
                .when()
                .post("/api/v1/producers")
                .then()
                .statusCode(202)
                .body("producerCode", equalTo("FRLA1"))
                .body("tracks", hasSize(1))
                .body("tracks[0]", equalTo("FRLA12400001"));
    }
}