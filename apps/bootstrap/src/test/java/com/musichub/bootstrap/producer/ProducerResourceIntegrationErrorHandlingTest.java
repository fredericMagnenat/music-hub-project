package com.musichub.bootstrap.producer;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.InjectMock;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.musichub.producer.application.ports.in.RegisterTrackUseCase;

import static io.restassured.RestAssured.given;
import static org.mockito.Mockito.when;

@QuarkusTest
@DisplayName("ProducerResource Integration Error Handling Tests")
class ProducerResourceIntegrationErrorHandlingTest {

    @InjectMock
    RegisterTrackUseCase registerTrackUseCase;

    @Test
    @DisplayName("Should map RuntimeException from use case to 422 with error payload")
    void shouldMapRuntimeExceptionTo422() {
        // Given
        when(registerTrackUseCase.registerTrack("FRLA12409999"))
            .thenThrow(new RuntimeException("ISRC not found upstream"));

        // When / Then
        given()
            .contentType(ContentType.JSON)
            .body("{\"isrc\":\"FRLA12409999\"}")
            .when()
            .post("/api/v1/producers")
            .then()
            .statusCode(422)
            .contentType(ContentType.JSON)
            .body("error", org.hamcrest.Matchers.equalTo("UnresolvableISRC"))
            .body("message", org.hamcrest.Matchers.equalTo("ISRC not found upstream"));
    }
}
