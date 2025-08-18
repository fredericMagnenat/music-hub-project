package com.musichub.producer.adapter.spi.mapper;

import com.musichub.producer.adapter.spi.dto.TrackMetadataDto;
import com.musichub.producer.adapter.spi.dto.tidal.*;
import com.musichub.producer.adapter.spi.exception.TrackNotFoundInExternalServiceException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for TidalResponseMapper
 */
class TidalResponseMapperTest {

    private TidalResponseMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new TidalResponseMapper();
    }

    @Test
    void mapToTrackMetadata_WhenValidResponse_ShouldMapCorrectly() {
        // Given: A valid Tidal response with track and artist data
        String testIsrc = "GBUM71507409";
        TidalTracksResponse tidalResponse = createValidTidalResponse(testIsrc);

        // When: Mapping to TrackMetadataDto
        TrackMetadataDto result = mapper.mapToTrackMetadata(tidalResponse, testIsrc);

        // Then: Should map correctly
        assertNotNull(result);
        assertEquals(testIsrc, result.isrc);
        assertEquals("Bohemian Rhapsody", result.title);
        assertEquals(List.of("Queen"), result.artistNames);
        assertEquals("tidal", result.platform);
    }

    @Test
    void mapToTrackMetadata_WhenMultipleArtists_ShouldMapAllArtists() {
        // Given: A Tidal response with multiple artists
        String testIsrc = "GBUM71507410";
        TidalTracksResponse tidalResponse = createTidalResponseWithMultipleArtists(testIsrc);

        // When: Mapping to TrackMetadataDto
        TrackMetadataDto result = mapper.mapToTrackMetadata(tidalResponse, testIsrc);

        // Then: Should map all artists
        assertNotNull(result);
        assertEquals("Under Pressure", result.title);
        assertEquals(List.of("Queen", "David Bowie"), result.artistNames);
        assertEquals(2, result.artistNames.size());
    }

    @Test
    void mapToTrackMetadata_WhenNoArtistsIncluded_ShouldReturnEmptyArtistList() {
        // Given: A Tidal response without included artists
        String testIsrc = "GBUM71507409";
        TidalTracksResponse tidalResponse = createTidalResponseWithoutArtists(testIsrc);

        // When: Mapping to TrackMetadataDto
        TrackMetadataDto result = mapper.mapToTrackMetadata(tidalResponse, testIsrc);

        // Then: Should have empty artist list
        assertNotNull(result);
        assertEquals(testIsrc, result.isrc);
        assertEquals("Bohemian Rhapsody", result.title);
        assertTrue(result.artistNames.isEmpty());
        assertEquals("tidal", result.platform);
    }

    @Test
    void mapToTrackMetadata_WhenNullResponse_ShouldThrowException() {
        // Given: A null response
        String testIsrc = "GBUM71507409";

        // When & Then: Should throw TrackNotFoundInExternalServiceException
        TrackNotFoundInExternalServiceException exception = assertThrows(
            TrackNotFoundInExternalServiceException.class,
            () -> mapper.mapToTrackMetadata(null, testIsrc)
        );

        assertEquals(testIsrc, exception.getIsrc());
        assertEquals("tidal", exception.getPlatform());
        assertTrue(exception.getMessage().contains("No track found for ISRC"));
    }

    @Test
    void mapToTrackMetadata_WhenEmptyData_ShouldThrowException() {
        // Given: A response with no data
        String testIsrc = "GBUM71507409";
        TidalTracksResponse tidalResponse = new TidalTracksResponse();
        tidalResponse.data = List.of();

        // When & Then: Should throw TrackNotFoundInExternalServiceException
        TrackNotFoundInExternalServiceException exception = assertThrows(
            TrackNotFoundInExternalServiceException.class,
            () -> mapper.mapToTrackMetadata(tidalResponse, testIsrc)
        );

        assertEquals(testIsrc, exception.getIsrc());
        assertEquals("tidal", exception.getPlatform());
    }

    @Test
    void mapToTrackMetadata_WhenMissingAttributes_ShouldThrowException() {
        // Given: A response with track data but no attributes
        String testIsrc = "GBUM71507409";
        TidalTracksResponse tidalResponse = new TidalTracksResponse();
        TidalTrackData trackData = new TidalTrackData();
        trackData.id = "123456";
        trackData.type = "tracks";
        trackData.attributes = null; // Missing attributes
        tidalResponse.data = List.of(trackData);

        // When & Then: Should throw TrackNotFoundInExternalServiceException
        TrackNotFoundInExternalServiceException exception = assertThrows(
            TrackNotFoundInExternalServiceException.class,
            () -> mapper.mapToTrackMetadata(tidalResponse, testIsrc)
        );

        assertEquals(testIsrc, exception.getIsrc());
        assertEquals("tidal", exception.getPlatform());
        assertTrue(exception.getMessage().contains("Track data incomplete"));
    }

    private TidalTracksResponse createValidTidalResponse(String isrc) {
        TidalTracksResponse response = new TidalTracksResponse();

        // Create track data
        TidalTrackData trackData = new TidalTrackData();
        trackData.id = "123456";
        trackData.type = "tracks";
        trackData.attributes = new TidalTrackAttributes();
        trackData.attributes.isrc = isrc;
        trackData.attributes.title = "Bohemian Rhapsody";

        // Create artist relationship
        trackData.relationships = new TidalTrackRelationships();
        trackData.relationships.artists = new TidalRelationshipData();
        trackData.relationships.artists.data = List.of(
            new TidalResourceIdentifier("artist-1", "artists")
        );

        // Create included artist
        TidalIncludedResource artistResource = new TidalIncludedResource();
        artistResource.id = "artist-1";
        artistResource.type = "artists";
        artistResource.attributes = new TidalArtistAttributes();
        artistResource.attributes.name = "Queen";

        response.data = List.of(trackData);
        response.included = List.of(artistResource);

        return response;
    }

    private TidalTracksResponse createTidalResponseWithMultipleArtists(String isrc) {
        TidalTracksResponse response = new TidalTracksResponse();

        // Create track data
        TidalTrackData trackData = new TidalTrackData();
        trackData.id = "123456";
        trackData.type = "tracks";
        trackData.attributes = new TidalTrackAttributes();
        trackData.attributes.isrc = isrc;
        trackData.attributes.title = "Under Pressure";

        // Create artist relationships
        trackData.relationships = new TidalTrackRelationships();
        trackData.relationships.artists = new TidalRelationshipData();
        trackData.relationships.artists.data = List.of(
            new TidalResourceIdentifier("artist-1", "artists"),
            new TidalResourceIdentifier("artist-2", "artists")
        );

        // Create included artists
        TidalIncludedResource artist1 = new TidalIncludedResource();
        artist1.id = "artist-1";
        artist1.type = "artists";
        artist1.attributes = new TidalArtistAttributes();
        artist1.attributes.name = "Queen";

        TidalIncludedResource artist2 = new TidalIncludedResource();
        artist2.id = "artist-2";
        artist2.type = "artists";
        artist2.attributes = new TidalArtistAttributes();
        artist2.attributes.name = "David Bowie";

        response.data = List.of(trackData);
        response.included = List.of(artist1, artist2);

        return response;
    }

    private TidalTracksResponse createTidalResponseWithoutArtists(String isrc) {
        TidalTracksResponse response = new TidalTracksResponse();

        // Create track data without artists
        TidalTrackData trackData = new TidalTrackData();
        trackData.id = "123456";
        trackData.type = "tracks";
        trackData.attributes = new TidalTrackAttributes();
        trackData.attributes.isrc = isrc;
        trackData.attributes.title = "Bohemian Rhapsody";

        trackData.relationships = new TidalTrackRelationships();
        // No artists relationship

        response.data = List.of(trackData);
        response.included = List.of(); // No included resources

        return response;
    }
}