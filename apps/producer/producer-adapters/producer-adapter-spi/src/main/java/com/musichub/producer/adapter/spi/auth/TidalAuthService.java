package com.musichub.producer.adapter.spi.auth;

import java.time.Instant;
import java.util.Optional;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

/**
 * Service for managing Tidal OAuth2 authentication tokens.
 * Handles token acquisition, caching, and renewal.
 * <p>
 * This service implements the OAuth2 client credentials flow
 * and caches tokens until they expire.
 */
@ApplicationScoped
public class TidalAuthService {

    private static final Logger logger = LoggerFactory.getLogger(TidalAuthService.class);
    private static final String GRANT_TYPE = "client_credentials";

    @Inject
    @RestClient
    TidalAuthClient authClient;

    @ConfigProperty(name = "tidal.auth.client-id")
    Optional<String> clientId;

    @ConfigProperty(name = "tidal.auth.client-secret")
    Optional<String> clientSecret;

    @ConfigProperty(name = "tidal.auth.grant-type", defaultValue = GRANT_TYPE)
    String grantType;

    // Token cache
    private volatile TidalTokenResponse cachedToken;
    private volatile Instant tokenExpiry;

    /**
     * Get a valid access token for Tidal API calls.
     * Returns cached token if still valid, otherwise requests a new one.
     *
     * @return access token or null if authentication fails
     */
    public String getAccessToken() {
        if (isTokenValid()) {
            logger.debug("Using cached Tidal access token");
            return cachedToken.getAccessToken();
        }

        return refreshAccessToken();
    }

    /**
     * Get the full Authorization header value.
     *
     * @return "Bearer <token>" or null if authentication fails
     */
    public String getAuthorizationHeader() {
        String token = getAccessToken();
        return token != null ? "Bearer " + token : null;
    }

    /**
     * Force refresh of the access token.
     *
     * @return new access token or null if authentication fails
     */
    public String refreshAccessToken() {
        if (!hasValidCredentials()) {
            logger.warn("Tidal authentication credentials not configured properly");
            return null;
        }

        try {
            logger.debug("Requesting new Tidal access token");

            TidalTokenResponse response = authClient.getAccessToken(
                    grantType,
                    clientId.get(),
                    clientSecret.get());

            if (response != null && response.isValid()) {
                cachedToken = response;
                // Set expiry time with 5-minute buffer for safety
                long expiresIn = response.getExpiresIn() != null ? response.getExpiresIn() : 3600L;
                tokenExpiry = Instant.now().plusSeconds(expiresIn - 300);

                logger.info("Successfully obtained Tidal access token, expires in {} seconds", expiresIn);
                return response.getAccessToken();
            } else {
                logger.error("Invalid response from Tidal auth API");
                return null;
            }

        } catch (Exception e) {
            logger.error("Failed to obtain Tidal access token", e);
            return null;
        }
    }

    /**
     * Check if the cached token is still valid.
     */
    private boolean isTokenValid() {
        return cachedToken != null
                && cachedToken.isValid()
                && tokenExpiry != null
                && Instant.now().isBefore(tokenExpiry);
    }

    /**
     * Check if we have valid credentials configured.
     */
    private boolean hasValidCredentials() {
        return clientId.isPresent()
                && clientSecret.isPresent()
                && !clientId.get().trim().isEmpty()
                && !clientSecret.get().trim().isEmpty()
                && !"changeme".equals(clientId.get());
    }
}