package com.musichub.producer.adapter.spi.auth;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for TidalTokenResponse DTO
 */
class TidalTokenResponseTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void defaultConstructor_ShouldCreateEmptyInstance() {
        // When: Creating empty instance
        TidalTokenResponse response = new TidalTokenResponse();

        // Then: Should have null values
        assertNotNull(response);
        assertNull(response.accessToken);
        assertNull(response.tokenType);
        assertNull(response.expiresIn);
        assertFalse(response.isValid());
    }

    @Test
    void parameterizedConstructor_ShouldSetAllFields() {
        // When: Creating instance with parameters
        TidalTokenResponse response = new TidalTokenResponse("token123", "Bearer", 3600);

        // Then: Should set all fields
        assertEquals("token123", response.accessToken);
        assertEquals("Bearer", response.tokenType);
        assertEquals(3600, response.expiresIn);
        assertTrue(response.isValid());
    }

    @Test
    void jsonDeserialization_ShouldMapCorrectly() throws JsonProcessingException {
        // Given: JSON response from Tidal auth API
        String json = """
            {
                "access_token": "eyJhbGciOiJIUzI1NiIs...",
                "token_type": "Bearer",
                "expires_in": 3600,
                "scope": "r_usr"
            }
            """;

        // When: Deserializing JSON
        TidalTokenResponse response = objectMapper.readValue(json, TidalTokenResponse.class);

        // Then: Should map correctly
        assertNotNull(response);
        assertEquals("eyJhbGciOiJIUzI1NiIs...", response.accessToken);
        assertEquals("Bearer", response.tokenType);
        assertEquals(3600, response.expiresIn);
        assertEquals("r_usr", response.scope);
        assertTrue(response.isValid());
    }

    @Test
    void jsonSerialization_ShouldProduceCorrectJson() throws JsonProcessingException {
        // Given: Token response instance
        TidalTokenResponse response = new TidalTokenResponse("token123", "Bearer", 3600);
        response.scope = "r_usr";

        // When: Serializing to JSON
        String json = objectMapper.writeValueAsString(response);

        // Then: Should produce correct JSON
        assertTrue(json.contains("\"access_token\":\"token123\""));
        assertTrue(json.contains("\"token_type\":\"Bearer\""));
        assertTrue(json.contains("\"expires_in\":3600"));
        assertTrue(json.contains("\"scope\":\"r_usr\""));
    }

    @Test
    void isValid_WhenTokenExists_ShouldReturnTrue() {
        // Given: Response with token
        TidalTokenResponse response = new TidalTokenResponse();
        response.accessToken = "valid-token";

        // When & Then: Should be valid
        assertTrue(response.isValid());
    }

    @Test
    void isValid_WhenTokenEmpty_ShouldReturnFalse() {
        // Given: Response with empty token
        TidalTokenResponse response = new TidalTokenResponse();
        response.accessToken = "";

        // When & Then: Should be invalid
        assertFalse(response.isValid());
    }

    @Test
    void isValid_WhenTokenNull_ShouldReturnFalse() {
        // Given: Response with null token
        TidalTokenResponse response = new TidalTokenResponse();
        response.accessToken = null;

        // When & Then: Should be invalid
        assertFalse(response.isValid());
    }

    @Test
    void getAuthorizationHeader_WhenValidToken_ShouldReturnBearerHeader() {
        // Given: Valid token response
        TidalTokenResponse response = new TidalTokenResponse("token123", "Bearer", 3600);

        // When: Getting authorization header
        String header = response.getAuthorizationHeader();

        // Then: Should return correct header
        assertEquals("Bearer token123", header);
    }

    @Test
    void getAuthorizationHeader_WhenCustomTokenType_ShouldUseCustomType() {
        // Given: Token response with custom type
        TidalTokenResponse response = new TidalTokenResponse("token123", "Custom", 3600);

        // When: Getting authorization header
        String header = response.getAuthorizationHeader();

        // Then: Should use custom type
        assertEquals("Custom token123", header);
    }

    @Test
    void getAuthorizationHeader_WhenNoTokenType_ShouldDefaultToBearer() {
        // Given: Token response without type
        TidalTokenResponse response = new TidalTokenResponse("token123", null, 3600);

        // When: Getting authorization header
        String header = response.getAuthorizationHeader();

        // Then: Should default to Bearer
        assertEquals("Bearer token123", header);
    }

    @Test
    void getAuthorizationHeader_WhenInvalidToken_ShouldReturnNull() {
        // Given: Invalid token response
        TidalTokenResponse response = new TidalTokenResponse();
        response.accessToken = null;

        // When: Getting authorization header
        String header = response.getAuthorizationHeader();

        // Then: Should return null
        assertNull(header);
    }
}