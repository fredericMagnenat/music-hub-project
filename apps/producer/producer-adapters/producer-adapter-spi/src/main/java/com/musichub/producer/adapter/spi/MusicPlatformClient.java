package com.musichub.producer.adapter.spi;

import com.musichub.producer.adapter.spi.dto.TrackMetadataDto;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

/**
 * REST client interface for external music platform API integration.
 * This interface provides access to external music platforms (like Tidal) 
 * to retrieve track metadata by ISRC.
 * 
 * Configuration is managed through application.properties with:
 * - quarkus.rest-client.music-platform-client.url
 * - music-platform.api.key
 */
@RegisterRestClient(configKey = "music-platform-client")
@Path("/api/v1")
public interface MusicPlatformClient {
    
    /**
     * Retrieves track metadata from the external music platform using ISRC.
     * 
     * @param isrc The International Standard Recording Code
     * @return TrackMetadataDto containing track information
     * @throws com.musichub.producer.adapter.spi.exception.TrackNotFoundInExternalServiceException 
     *         when track is not found or service is unavailable
     */
    @GET
    @Path("/tracks/{isrc}")
    @Produces(MediaType.APPLICATION_JSON)
    TrackMetadataDto getTrackByIsrc(@PathParam("isrc") String isrc);
}