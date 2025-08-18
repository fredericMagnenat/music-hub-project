package com.musichub.producer.adapter.spi;

import com.musichub.producer.adapter.spi.dto.TrackMetadataDto;
import com.musichub.producer.adapter.spi.exception.TrackNotFoundInExternalServiceException;
import com.musichub.producer.application.dto.ExternalTrackMetadata;
import com.musichub.producer.application.exception.ExternalServiceException;
import com.musichub.producer.application.ports.out.MusicPlatformPort;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Adapter implementation that bridges the application layer port (MusicPlatformPort) 
 * with the Tidal music platform service.
 * 
 * This adapter follows the hexagonal architecture pattern where:
 * - The application layer defines the port (interface)
 * - The adapter layer provides the implementation
 * - External dependencies are injected and isolated
 */
@ApplicationScoped
public class TidalMusicPlatformAdapter implements MusicPlatformPort {

    private static final Logger logger = LoggerFactory.getLogger(TidalMusicPlatformAdapter.class);

    private final TidalMusicPlatformService tidalService;

    @Inject
    public TidalMusicPlatformAdapter(TidalMusicPlatformService tidalService) {
        this.tidalService = tidalService;
    }

    @Override
    public ExternalTrackMetadata getTrackByIsrc(String isrc) {
        logger.debug("Adapter: fetching track metadata for ISRC: {}", isrc);
        
        try {
            // Call the Tidal service
            TrackMetadataDto tidalDto = tidalService.getTrackByIsrc(isrc);
            
            // Convert Tidal-specific DTO to application DTO
            ExternalTrackMetadata externalMetadata = mapToExternalTrackMetadata(tidalDto);
            
            logger.debug("Adapter: successfully mapped Tidal metadata to external metadata for ISRC: {}", isrc);
            return externalMetadata;
            
        } catch (TrackNotFoundInExternalServiceException e) {
            logger.error("Adapter: Tidal service failed to find track for ISRC: {} - {}", isrc, e.getMessage());
            
            // Convert SPI exception to application exception
            throw new ExternalServiceException(
                "Track not found in Tidal service: " + e.getMessage(),
                isrc,
                "tidal",
                e
            );
        } catch (Exception e) {
            logger.error("Adapter: unexpected error calling Tidal service for ISRC: {}", isrc, e);
            
            throw new ExternalServiceException(
                "Unexpected error calling Tidal service for ISRC: " + isrc,
                isrc,
                "tidal",
                e
            );
        }
    }

    /**
     * Maps Tidal-specific DTO to the generic application DTO.
     * This isolates the application layer from the specific external API structure.
     */
    private ExternalTrackMetadata mapToExternalTrackMetadata(TrackMetadataDto tidalDto) {
        return new ExternalTrackMetadata(
            tidalDto.isrc,
            tidalDto.title,
            tidalDto.artistNames,
            tidalDto.platform
        );
    }
}