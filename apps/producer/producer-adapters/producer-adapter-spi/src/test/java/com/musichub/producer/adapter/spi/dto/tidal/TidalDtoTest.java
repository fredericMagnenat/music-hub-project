package com.musichub.producer.adapter.spi.dto.tidal;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Tidal JSON:API DTOs to ensure proper JSON mapping
 */
class TidalDtoTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void tidalTracksResponse_ShouldDeserializeFromJson() throws JsonProcessingException {
        // Given: JSON response structure similar to Tidal's API
        String json = """
            {
                "data": [
                    {
                        "id": "123456",
                        "type": "tracks",
                        "attributes": {
                            "title": "Bohemian Rhapsody",
                            "isrc": "GBUM71507409",
                            "duration": "PT5M55S",
                            "explicit": false
                        },
                        "relationships": {
                            "artists": {
                                "data": [
                                    {"id": "artist-1", "type": "artists"}
                                ]
                            }
                        }
                    }
                ],
                "included": [
                    {
                        "id": "artist-1",
                        "type": "artists",
                        "attributes": {
                            "name": "Queen"
                        }
                    }
                ],
                "meta": {
                    "total": 1
                }
            }
            """;

        // When: Deserializing JSON
        TidalTracksResponse response = objectMapper.readValue(json, TidalTracksResponse.class);

        // Then: Should map correctly
        assertNotNull(response);
        assertTrue(response.hasData());
        assertEquals(1, response.data.size());

        TidalTrackData track = response.getFirstTrack();
        assertNotNull(track);
        assertEquals("123456", track.id);
        assertEquals("tracks", track.type);
        assertEquals("Bohemian Rhapsody", track.attributes.title);
        assertEquals("GBUM71507409", track.attributes.isrc);
        assertEquals("PT5M55S", track.attributes.duration);
        assertFalse(track.attributes.explicit);

        // Check relationships
        assertNotNull(track.relationships);
        assertNotNull(track.relationships.artists);
        assertTrue(track.relationships.artists.hasData());
        assertEquals("artist-1", track.relationships.artists.data.get(0).id);
        assertEquals("artists", track.relationships.artists.data.get(0).type);

        // Check included resources
        assertNotNull(response.included);
        assertEquals(1, response.included.size());
        TidalIncludedResource artist = response.included.get(0);
        assertEquals("artist-1", artist.id);
        assertEquals("artists", artist.type);
        assertTrue(artist.isArtist());
        assertFalse(artist.isAlbum());
        assertEquals("Queen", artist.attributes.name);

        // Check meta
        assertNotNull(response.meta);
        assertEquals(1, response.meta.total);
    }

    @Test
    void tidalTracksResponse_ShouldHandleEmptyData() throws JsonProcessingException {
        // Given: Empty response
        String json = """
            {
                "data": [],
                "meta": {
                    "total": 0
                }
            }
            """;

        // When: Deserializing JSON
        TidalTracksResponse response = objectMapper.readValue(json, TidalTracksResponse.class);

        // Then: Should handle empty data
        assertNotNull(response);
        assertFalse(response.hasData());
        assertNull(response.getFirstTrack());
        assertEquals(0, response.meta.total);
    }

    @Test
    void tidalTrackAttributes_ShouldMapOptionalFields() throws JsonProcessingException {
        // Given: JSON with optional fields
        String json = """
            {
                "title": "Bohemian Rhapsody",
                "isrc": "GBUM71507409",
                "duration": "PT5M55S",
                "explicit": false,
                "popularity": 0.85,
                "bpm": 72.0,
                "key": "Bb",
                "keyScale": "MAJOR",
                "genreTags": ["Rock", "Progressive Rock"],
                "mediaTags": ["HIRES_LOSSLESS"],
                "copyright": {"text": "(c) 1975 Queen Productions Ltd"}
            }
            """;

        // When: Deserializing JSON
        TidalTrackAttributes attributes = objectMapper.readValue(json, TidalTrackAttributes.class);

        // Then: Should map all fields
        assertNotNull(attributes);
        assertEquals("Bohemian Rhapsody", attributes.title);
        assertEquals("GBUM71507409", attributes.isrc);
        assertEquals("PT5M55S", attributes.duration);
        assertFalse(attributes.explicit);
        assertEquals(0.85, attributes.popularity, 0.001);
        assertEquals(72.0, attributes.bpm, 0.001);
        assertEquals("Bb", attributes.key);
        assertEquals("MAJOR", attributes.keyScale);
        assertEquals(List.of("Rock", "Progressive Rock"), attributes.genreTags);
        assertEquals(List.of("HIRES_LOSSLESS"), attributes.mediaTags);
        assertNotNull(attributes.copyright);
        assertEquals("(c) 1975 Queen Productions Ltd", attributes.copyright.text);
    }

    @Test
    void tidalTrackAttributes_ShouldHandleCopyrightAsObject() throws JsonProcessingException {
        // Given: JSON with copyright as object (new Tidal API format)
        String json = """
            {
                "title": "Bohemian Rhapsody",
                "isrc": "GBUM71507409",
                "copyright": {"text": "(c) 1975 Queen Productions Ltd"}
            }
            """;

        // When: Deserializing JSON
        TidalTrackAttributes attributes = objectMapper.readValue(json, TidalTrackAttributes.class);

        // Then: Should map copyright object correctly
        assertNotNull(attributes);
        assertNotNull(attributes.copyright);
        assertEquals("(c) 1975 Queen Productions Ltd", attributes.copyright.text);
    }

    @Test
    void tidalArtistAttributes_ShouldMapCorrectly() throws JsonProcessingException {
        // Given: Artist attributes JSON
        String json = """
            {
                "name": "Queen",
                "popularity": 0.95,
                "active": true,
                "roles": ["MAIN_ARTIST", "COMPOSER"]
            }
            """;

        // When: Deserializing JSON
        TidalArtistAttributes attributes = objectMapper.readValue(json, TidalArtistAttributes.class);

        // Then: Should map correctly
        assertNotNull(attributes);
        assertEquals("Queen", attributes.name);
        assertEquals(0.95, attributes.popularity, 0.001);
        assertTrue(attributes.active);
        assertEquals(List.of("MAIN_ARTIST", "COMPOSER"), attributes.roles);
    }

    @Test
    void tidalResourceIdentifier_ShouldMapCorrectly() {
        // Given: Resource identifier data
        TidalResourceIdentifier resourceId = new TidalResourceIdentifier("artist-123", "artists");

        // When & Then: Should have correct values
        assertEquals("artist-123", resourceId.id);
        assertEquals("artists", resourceId.type);
    }

    @Test
    void tidalRelationshipData_ShouldCheckDataPresence() {
        // Given: Relationship data with and without data
        TidalRelationshipData withData = new TidalRelationshipData(
            List.of(new TidalResourceIdentifier("123", "artists"))
        );
        TidalRelationshipData withoutData = new TidalRelationshipData(List.of());
        TidalRelationshipData nullData = new TidalRelationshipData(null);

        // When & Then: Should correctly identify data presence
        assertTrue(withData.hasData());
        assertFalse(withoutData.hasData());
        assertFalse(nullData.hasData());
    }
}