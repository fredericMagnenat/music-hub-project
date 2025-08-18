package com.musichub.producer.adapter.spi.auth;

import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.JsonbConfig;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for TidalTokenResponse DTO
 */
class TidalTokenResponseTest {

    private static Jsonb jsonb;

    @BeforeAll
    static void setup() {
        JsonbConfig config = new JsonbConfig()
                .withFormatting(true);
        jsonb = JsonbBuilder.create(config);
    }

    @AfterAll
    static void cleanup() throws Exception {
        if (jsonb != null) {
            jsonb.close();
        }
    }

    @Test
    void defaultConstructor_ShouldCreateEmptyInstance() {
        // When: Creating empty instance
        TidalTokenResponse response = new TidalTokenResponse();

        // Then: Should have null values
        assertNotNull(response);
        assertNull(response.getAccessToken());
        assertNull(response.getExpiresIn());
        assertFalse(response.isValid());
    }

    @Test
    void parameterizedConstructor_ShouldSetAllFields() {
        // When: Creating instance with parameters
        TidalTokenResponse response = new TidalTokenResponse("token123", 3600L);

        // Then: Should set all fields
        assertEquals("token123", response.getAccessToken());
        assertEquals(3600L, response.getExpiresIn());
        assertTrue(response.isValid());
    }

    @Test
    void jsonDeserialization_ShouldMapCorrectly() throws Exception {
        // Given: JSON response from Tidal auth API
        String json = """
            {
                "access_token": "eyJhbGciOiJIUzI1NiIs...",
                "expires_in": 3600
            }
            """;

        // When: Deserializing JSON
        TidalTokenResponse response = jsonb.fromJson(json, TidalTokenResponse.class);

        // Then: Should map correctly
        assertNotNull(response);
        assertEquals("eyJhbGciOiJIUzI1NiIs...", response.getAccessToken());
        assertEquals(3600L, response.getExpiresIn());
        assertTrue(response.isValid());
    }

    @Test
    void jsonSerialization_ShouldProduceCorrectJson() throws Exception {
        // Given: Token response instance
        TidalTokenResponse response = new TidalTokenResponse("token123", 3600L);

        // When: Serializing to JSON
        String json = jsonb.toJson(response);
        System.out.println("Generated JSON: " + json); // Pour déboguer

        // Then: Should produce correct JSON (avec espaces de JSON-B formaté)
        assertTrue(json.contains("\"access_token\""));
        assertTrue(json.contains("\"token123\""));
        assertTrue(json.contains("\"expires_in\""));
        assertTrue(json.contains("3600"));
    }

    @Test
    void isValid_WhenTokenExists_ShouldReturnTrue() {
        // Given: Response with token
        TidalTokenResponse response = new TidalTokenResponse("valid-token", 3600L);

        // When & Then: Should be valid
        assertTrue(response.isValid());
    }

    @Test
    void isValid_WhenTokenEmpty_ShouldReturnFalse() {
        // Given: Response with empty token
        TidalTokenResponse response = new TidalTokenResponse();
        response.setAccessToken("");

        // When & Then: Should be invalid
        assertFalse(response.isValid());
    }

    @Test
    void isValid_WhenTokenNull_ShouldReturnFalse() {
        // Given: Response with null token
        TidalTokenResponse response = new TidalTokenResponse();
        response.setAccessToken(null);

        // When & Then: Should be invalid
        assertFalse(response.isValid());
    }

    @Test
    void getAuthorizationHeader_WhenValidToken_ShouldReturnBearerHeader() {
        // Given: Valid token response
        TidalTokenResponse response = new TidalTokenResponse("token123", 3600L);

        // When: Getting authorization header
        String header = response.getAuthorizationHeader();

        // Then: Should return correct header
        assertEquals("Bearer token123", header);
    }

    @Test
    void getAuthorizationHeader_WhenInvalidToken_ShouldReturnNull() {
        // Given: Invalid token response
        TidalTokenResponse response = new TidalTokenResponse();
        response.setAccessToken(null);

        // When: Getting authorization header
        String header = response.getAuthorizationHeader();

        // Then: Should return null
        assertNull(header);
    }

    @Test 
    void expiresIn_WhenNull_ShouldBeHandledProperly() {
        // Given: Response with null expiresIn
        TidalTokenResponse response = new TidalTokenResponse("token123", null);

        // When & Then: Should still be valid token and handle null expiresIn
        assertTrue(response.isValid());
        assertNull(response.getExpiresIn());
        assertEquals("Bearer token123", response.getAuthorizationHeader());
    }
}