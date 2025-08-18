package com.musichub.producer.adapter.spi;

import com.musichub.producer.adapter.spi.dto.TrackMetadataDto;
import com.musichub.producer.adapter.spi.dto.tidal.TidalTracksResponse;
import com.musichub.producer.adapter.spi.mapper.TidalResponseMapper;
import com.musichub.producer.adapter.spi.exception.TrackNotFoundInExternalServiceException;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.WebApplicationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Service class that provides a clean API for retrieving track metadata from Tidal.
 * This service encapsulates the complexity of Tidal's JSON:API structure and provides
 * a simple interface that returns our domain DTOs.
 * 
 * Handles:
 * - API calls to Tidal with proper parameters
 * - Response mapping from Tidal's complex structure to our simple DTOs
 * - Error handling and conversion to our domain exceptions
 * - Logging and monitoring
 */
@ApplicationScoped
public class TidalMusicPlatformService {
    
    private static final Logger logger = LoggerFactory.getLogger(TidalMusicPlatformService.class);

    @Inject
    @RestClient
    MusicPlatformClient musicPlatformClient;

    @Inject
    TidalResponseMapper responseMapper;

    @ConfigProperty(name = "music-platform.default-country-code", defaultValue = "US")
    String defaultCountryCode;

    /**
     * Retrieves track metadata by ISRC from Tidal.
     * This is the main entry point for external callers and provides a clean,
     * simple interface that hides the complexity of Tidal's API.
     * 
     * @param isrc The International Standard Recording Code
     * @return TrackMetadataDto containing track information
     * @throws TrackNotFoundInExternalServiceException when track not found or service unavailable
     */
    public TrackMetadataDto getTrackByIsrc(String isrc) {
        logger.debug("Searching for track with ISRC: {}", isrc);
        
        try {
            // Call Tidal API with proper parameters
            TidalTracksResponse tidalResponse = musicPlatformClient.getTracksByIsrc(
                isrc,
                "artists", // Include artists to get artist names
                defaultCountryCode
            );

            logger.debug("Received response from Tidal for ISRC: {}, found {} tracks", 
                        isrc, tidalResponse.hasData() ? tidalResponse.data.size() : 0);

            // Map Tidal's complex response to our simple DTO
            TrackMetadataDto result = responseMapper.mapToTrackMetadata(tidalResponse, isrc);
            
            logger.info("Successfully retrieved track metadata for ISRC: {} - Title: '{}' by {}", 
                       isrc, result.title, result.artistNames);
            
            return result;

        } catch (WebApplicationException e) {
            logger.error("HTTP error when calling Tidal API for ISRC: {} - Status: {}", 
                        isrc, e.getResponse().getStatus());
            
            // Convert HTTP errors to our domain exceptions
            String errorMessage = String.format(
                "Failed to retrieve track from Tidal API for ISRC: %s (HTTP %d)", 
                isrc, e.getResponse().getStatus()
            );
            
            throw new TrackNotFoundInExternalServiceException(errorMessage, isrc, "tidal", e);
            
        } catch (TrackNotFoundInExternalServiceException e) {
            // Re-throw our domain exceptions
            logger.warn("Track not found in Tidal for ISRC: {} - {}", isrc, e.getMessage());
            throw e;
            
        } catch (Exception e) {
            logger.error("Unexpected error when calling Tidal API for ISRC: {}", isrc, e);
            
            // Convert unexpected exceptions to our domain exceptions
            String errorMessage = String.format(
                "Unexpected error retrieving track from Tidal for ISRC: %s - %s", 
                isrc, e.getMessage()
            );
            
            throw new TrackNotFoundInExternalServiceException(errorMessage, isrc, "tidal", e);
        }
    }
}