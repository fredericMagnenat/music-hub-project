package com.musichub.producer.adapter.spi.auth;

import org.eclipse.microprofile.rest.client.ext.ClientHeadersFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.MultivaluedHashMap;

/**
 * Client headers factory for Tidal API authentication.
 * Implements Quarkus REST Client best practices for adding authentication headers.
 * 
 * This factory:
 * 1. Uses TidalAuthService to obtain OAuth2 tokens
 * 2. Adds proper Authorization headers with Bearer tokens
 * 3. Sets required content-type headers for Tidal's JSON:API
 * 4. Handles authentication failures gracefully
 */
@ApplicationScoped
public class TidalClientHeadersFactory implements ClientHeadersFactory {

    private static final Logger logger = LoggerFactory.getLogger(TidalClientHeadersFactory.class);

    @Inject
    TidalAuthService authService;

    @Override
    public MultivaluedMap<String, String> update(MultivaluedMap<String, String> incomingHeaders,
                                                MultivaluedMap<String, String> clientOutgoingHeaders) {
        
        MultivaluedMap<String, String> headers = new MultivaluedHashMap<>();

        // Get OAuth2 token from authentication service
        String authHeader = authService.getAuthorizationHeader();
        if (authHeader != null) {
            headers.add("Authorization", authHeader);
            logger.debug("Added OAuth2 authorization header for Tidal API");
        } else {
            logger.warn("No valid authentication token available for Tidal API");
        }

        // Always add required headers for Tidal API (JSON:API specification)
        headers.add("Accept", "application/vnd.api+json");
        headers.add("Content-Type", "application/vnd.api+json");
        
        // Add User-Agent for API identification and debugging
        headers.add("User-Agent", "MusicHub/1.0.0");

        return headers;
    }
}