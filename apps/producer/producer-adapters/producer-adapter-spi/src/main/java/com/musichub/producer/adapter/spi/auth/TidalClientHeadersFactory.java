package com.musichub.producer.adapter.spi.auth;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.ext.ClientHeadersFactory;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.MultivaluedHashMap;
import java.util.Optional;

/**
 * Client headers factory for Tidal API authentication.
 * Implements Quarkus REST Client best practices for adding authentication headers.
 * This factory is automatically invoked for each HTTP request to add required headers.
 */
@ApplicationScoped
public class TidalClientHeadersFactory implements ClientHeadersFactory {

    @ConfigProperty(name = "tidal.auth.client-id")
    Optional<String> clientId;

    @ConfigProperty(name = "tidal.auth.client-secret")
    Optional<String> clientSecret;

    @ConfigProperty(name = "music-platform.api.key")
    Optional<String> apiKey;

    @Override
    public MultivaluedMap<String, String> update(MultivaluedMap<String, String> incomingHeaders,
                                                MultivaluedMap<String, String> clientOutgoingHeaders) {
        
        MultivaluedMap<String, String> headers = new MultivaluedHashMap<>();

        // Add API key if available (for simple authentication)
        apiKey.ifPresent(key -> {
            if (!key.isEmpty() && !key.equals("dev-api-key")) {
                headers.add("Authorization", "Bearer " + key);
            }
        });

        // Add client ID for OAuth2 flows (if needed)
        clientId.ifPresent(id -> {
            if (!id.isEmpty() && !id.equals("changeme")) {
                headers.add("X-Tidal-Client-ID", id);
            }
        });

        // Always add required headers for Tidal API
        headers.add("Accept", "application/vnd.api+json");
        headers.add("Content-Type", "application/vnd.api+json");
        
        // Add User-Agent for API identification
        headers.add("User-Agent", "MusicHub/1.0.0");

        return headers;
    }
}