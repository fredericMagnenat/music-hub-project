package com.musichub.producer.adapter.spi.auth;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Unit tests for TidalAuthService
 */
@ExtendWith(MockitoExtension.class)
class TidalAuthServiceTest {

    @Mock
    @RestClient
    TidalAuthClient authClient;

    @InjectMocks
    TidalAuthService authService;

    @BeforeEach
    void setUp() throws Exception {
        // Use reflection to set the config properties since @ConfigProperty doesn't work in unit tests
        setConfigProperty("clientId", Optional.of("test-client-id"));
        setConfigProperty("clientSecret", Optional.of("test-client-secret"));
        setConfigProperty("grantType", "client_credentials");
    }

    @Test
    void getAccessToken_WhenValidCredentials_ShouldReturnToken() throws Exception {
        // Given: Valid token response from auth client
        TidalTokenResponse mockResponse = new TidalTokenResponse("test-access-token", 3600L);
        when(authClient.getAccessToken(anyString(), anyString(), anyString()))
                .thenReturn(mockResponse);

        // When: Getting access token
        String token = authService.getAccessToken();

        // Then: Should return the token
        assertNotNull(token);
        assertEquals("test-access-token", token);

        // Verify correct parameters were passed
        verify(authClient).getAccessToken("client_credentials", "test-client-id", "test-client-secret");
    }

    @Test
    void getAccessToken_WhenCachedTokenValid_ShouldReturnCachedToken() throws Exception {
        // Given: Valid token response that gets cached
        TidalTokenResponse mockResponse = new TidalTokenResponse("cached-token", 3600L);
        when(authClient.getAccessToken(anyString(), anyString(), anyString()))
                .thenReturn(mockResponse);

        // When: Getting access token twice
        String firstToken = authService.getAccessToken();
        String secondToken = authService.getAccessToken();

        // Then: Should return same token and only call auth client once
        assertEquals("cached-token", firstToken);
        assertEquals("cached-token", secondToken);
        verify(authClient, times(1)).getAccessToken(anyString(), anyString(), anyString());
    }

    @Test
    void getAccessToken_WhenInvalidCredentials_ShouldReturnNull() throws Exception {
        // Given: Invalid credentials
        setConfigProperty("clientId", Optional.of("changeme"));

        // When: Getting access token
        String token = authService.getAccessToken();

        // Then: Should return null
        assertNull(token);

        // Verify auth client was not called
        verifyNoInteractions(authClient);
    }

    @Test
    void getAccessToken_WhenAuthClientFails_ShouldReturnNull() throws Exception {
        // Given: Auth client throws exception
        when(authClient.getAccessToken(anyString(), anyString(), anyString()))
                .thenThrow(new RuntimeException("Auth service unavailable"));

        // When: Getting access token
        String token = authService.getAccessToken();

        // Then: Should return null
        assertNull(token);
    }

    @Test
    void getAuthorizationHeader_WhenValidToken_ShouldReturnBearerHeader() throws Exception {
        // Given: Valid token response
        TidalTokenResponse mockResponse = new TidalTokenResponse("test-token", 3600L);
        when(authClient.getAccessToken(anyString(), anyString(), anyString()))
                .thenReturn(mockResponse);

        // When: Getting authorization header
        String header = authService.getAuthorizationHeader();

        // Then: Should return properly formatted header
        assertNotNull(header);
        assertEquals("Bearer test-token", header);
    }

    @Test
    void getAuthorizationHeader_WhenNoToken_ShouldReturnNull() throws Exception {
        // Given: Invalid credentials
        setConfigProperty("clientId", Optional.empty());

        // When: Getting authorization header
        String header = authService.getAuthorizationHeader();

        // Then: Should return null
        assertNull(header);
    }

    @Test
    void refreshAccessToken_WhenCalled_ShouldGetNewToken() throws Exception {
        // Given: Valid token response
        TidalTokenResponse mockResponse = new TidalTokenResponse("refreshed-token", 3600L);
        when(authClient.getAccessToken(anyString(), anyString(), anyString()))
                .thenReturn(mockResponse);

        // When: Refreshing access token
        String token = authService.refreshAccessToken();

        // Then: Should return new token
        assertNotNull(token);
        assertEquals("refreshed-token", token);
    }

    /**
     * Helper method to set config properties via reflection
     */
    private void setConfigProperty(String fieldName, Object value) throws Exception {
        Field field = TidalAuthService.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(authService, value);
    }
}