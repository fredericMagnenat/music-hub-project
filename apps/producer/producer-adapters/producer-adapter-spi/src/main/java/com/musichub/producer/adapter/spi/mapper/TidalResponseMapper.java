package com.musichub.producer.adapter.spi.mapper;

import java.util.List;
import java.util.stream.Collectors;

import com.musichub.producer.adapter.spi.dto.ArtistDto;
import com.musichub.producer.adapter.spi.dto.TrackMetadataDto;
import com.musichub.producer.adapter.spi.dto.tidal.TidalIncludedResource;
import com.musichub.producer.adapter.spi.dto.tidal.TidalTrackData;
import com.musichub.producer.adapter.spi.dto.tidal.TidalTracksResponse;
import com.musichub.producer.adapter.spi.exception.TrackNotFoundInExternalServiceException;

import jakarta.enterprise.context.ApplicationScoped;

/**
 * Mapper to convert Tidal's complex JSON:API response structure
 * to our simplified TrackMetadataDto.
 * 
 * This handles the transformation from Tidal's JSON:API format
 * with relationships and included resources to our domain model.
 */
@ApplicationScoped
public class TidalResponseMapper {

    /**
     * Maps a Tidal tracks response to our TrackMetadataDto.
     * 
     * @param tidalResponse the response from Tidal's /tracks endpoint
     * @param requestedIsrc the ISRC that was searched for (for error context)
     * @return mapped TrackMetadataDto
     * @throws TrackNotFoundInExternalServiceException if no track found
     */
    public TrackMetadataDto mapToTrackMetadata(TidalTracksResponse tidalResponse, String requestedIsrc) {
        if (tidalResponse == null || !tidalResponse.hasData()) {
            throw new TrackNotFoundInExternalServiceException(
                    "No track found for ISRC: " + requestedIsrc,
                    requestedIsrc,
                    "tidal");
        }

        TidalTrackData trackData = tidalResponse.getFirstTrack();
        if (trackData.attributes == null) {
            throw new TrackNotFoundInExternalServiceException(
                    "Track data incomplete for ISRC: " + requestedIsrc,
                    requestedIsrc,
                    "tidal");
        }

        // Extract artists from included resources
        List<ArtistDto> artists = extractArtists(trackData, tidalResponse.included);

        return new TrackMetadataDto(
                trackData.attributes.isrc,
                trackData.attributes.title,
                artists,
                "tidal");
    }

    /**
     * Extracts artists from the track's relationships and included resources.
     *
     * @param trackData         the track data containing relationships
     * @param includedResources the included resources from the response
     * @return list of artists
     */
    private List<ArtistDto> extractArtists(TidalTrackData trackData, List<TidalIncludedResource> includedResources) {
        if (trackData.relationships == null ||
                trackData.relationships.artists == null ||
                !trackData.relationships.artists.hasData()) {
            return List.of(); // No artists found
        }

        // Get artist IDs from relationships
        List<String> artistIds = trackData.relationships.artists.data.stream()
                .filter(ref -> "artists".equals(ref.type))
                .map(ref -> ref.id)
                .collect(Collectors.toList());

        // Find corresponding artist data in included resources
        if (includedResources == null || includedResources.isEmpty()) {
            return List.of(); // No included data available
        }

        return includedResources.stream()
                .filter(resource -> resource.isArtist() && artistIds.contains(resource.id))
                .filter(resource -> resource.attributes != null && resource.attributes.name != null)
                .map(resource -> {
                    ArtistDto artist = new ArtistDto();
                    artist.name = resource.attributes.name;
                    artist.id = null; // Tidal uses string IDs, we don't have UUID mapping
                    return artist;
                })
                .collect(Collectors.toList());
    }
}