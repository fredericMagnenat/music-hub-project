package com.musichub.producer.adapter.spi;

import org.eclipse.microprofile.rest.client.annotation.RegisterClientHeaders;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import com.musichub.producer.adapter.spi.auth.TidalClientHeadersFactory;
import com.musichub.producer.adapter.spi.dto.tidal.TidalTracksResponse;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;

/**
 * REST client interface for Tidal music platform API integration.
 * This interface provides access to Tidal's OpenAPI v2 to retrieve track
 * metadata by ISRC.
 * 
 * Uses Tidal's real API structure:
 * - GET /tracks with query parameters
 * - filter[isrc] parameter for ISRC search
 * - include=artists parameter to fetch artist data
 * - countryCode parameter (required by Tidal)
 * - JSON:API response format
 * 
 * Authentication is handled by TidalClientHeadersFactory which adds:
 * - Authorization header with Bearer token
 * - Accept: application/vnd.api+json
 * - X-Tidal-Client-ID header (if configured)
 * 
 * Configuration properties:
 * - quarkus.rest-client.music-platform-client.url (should point to Tidal
 * OpenAPI)
 * - tidal.auth.client-id
 * - tidal.auth.client-secret
 * - music-platform.api.key
 */
@RegisterRestClient(configKey = "music-platform-client")
@RegisterClientHeaders(TidalClientHeadersFactory.class)
@Path("/tracks")
public interface MusicPlatformClient {

    /**
     * Retrieves track metadata from Tidal using ISRC filter.
     * This matches Tidal's actual API structure: GET
     * /tracks?filter[isrc]=XXXX&include=artists&countryCode=US
     * 
     * @param isrc        The International Standard Recording Code to search for
     * @param include     Related resources to include (typically "artists")
     * @param countryCode ISO 3166-1 alpha-2 country code (required by Tidal)
     * @return TidalTracksResponse containing track data in JSON:API format
     * @throws jakarta.ws.rs.WebApplicationException for HTTP errors (404, 500,
     *                                               etc.)
     */
    @GET
    @Produces("application/vnd.api+json")
    TidalTracksResponse getTracksByIsrc(
            @QueryParam("filter[isrc]") String isrc,
            @QueryParam("include") String include,
            @QueryParam("countryCode") String countryCode);
}