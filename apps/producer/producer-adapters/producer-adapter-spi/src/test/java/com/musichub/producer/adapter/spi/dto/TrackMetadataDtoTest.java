package com.musichub.producer.adapter.spi.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for TrackMetadataDto JSON mapping and basic functionality
 */
class TrackMetadataDtoTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void defaultConstructor_ShouldCreateEmptyInstance() {
        // When
        TrackMetadataDto dto = new TrackMetadataDto();

        // Then
        assertNotNull(dto);
        assertNull(dto.isrc);
        assertNull(dto.title);
        assertNull(dto.artists);
        assertNull(dto.platform);
    }

    @Test
    void parameterizedConstructor_ShouldSetAllFields() {
        // Given
        String isrc = "GBUM71507409";
        String title = "Bohemian Rhapsody";
        ArtistDto queen = new ArtistDto();
        queen.name = "Queen";
        queen.id = java.util.UUID.randomUUID();
        List<ArtistDto> artists = List.of(queen);
        String platform = "tidal";

        // When
        TrackMetadataDto dto = new TrackMetadataDto(isrc, title, artists, platform);

        // Then
        assertEquals(isrc, dto.isrc);
        assertEquals(title, dto.title);
        assertEquals(artists, dto.artists);
        assertEquals(platform, dto.platform);
    }

    @Test
    void jsonDeserialization_ShouldMapCorrectly() throws JsonProcessingException {
        // Given
        String json = """
            {
                "isrc": "GBUM71507409",
                "title": "Bohemian Rhapsody",
                "artists": [{"name": "Queen", "id": "12345678-1234-1234-1234-123456789abc"}],
                "platform": "tidal"
            }
            """;

        // When
        TrackMetadataDto dto = objectMapper.readValue(json, TrackMetadataDto.class);

        // Then
        assertEquals("GBUM71507409", dto.isrc);
        assertEquals("Bohemian Rhapsody", dto.title);
        assertEquals(1, dto.artists.size());
        assertEquals("Queen", dto.artists.get(0).name);
        assertEquals("tidal", dto.platform);
    }

    @Test
    void jsonSerialization_ShouldProduceCorrectJson() throws JsonProcessingException {
        // Given
        ArtistDto queen = new ArtistDto();
        queen.name = "Queen";
        queen.id = java.util.UUID.fromString("12345678-1234-1234-1234-123456789abc");
        TrackMetadataDto dto = new TrackMetadataDto(
            "GBUM71507409",
            "Bohemian Rhapsody",
            List.of(queen),
            "tidal"
        );

        // When
        String json = objectMapper.writeValueAsString(dto);

        // Then
        assertTrue(json.contains("\"isrc\":\"GBUM71507409\""));
        assertTrue(json.contains("\"title\":\"Bohemian Rhapsody\""));
        assertTrue(json.contains("\"name\":\"Queen\""));
        assertTrue(json.contains("\"platform\":\"tidal\""));
    }

    @Test
    void jsonDeserialization_WithMultipleArtists_ShouldMapCorrectly() throws JsonProcessingException {
        // Given
        String json = """
            {
                "isrc": "GBUM71507410",
                "title": "Under Pressure",
                "artists": [{"name": "Queen", "id": "12345678-1234-1234-1234-123456789abc"}, {"name": "David Bowie", "id": "87654321-4321-4321-4321-cba987654321"}],
                "platform": "tidal"
            }
            """;

        // When
        TrackMetadataDto dto = objectMapper.readValue(json, TrackMetadataDto.class);

        // Then
        assertEquals("GBUM71507410", dto.isrc);
        assertEquals("Under Pressure", dto.title);
        assertEquals(2, dto.artists.size());
        assertEquals("Queen", dto.artists.get(0).name);
        assertEquals("David Bowie", dto.artists.get(1).name);
        assertEquals("tidal", dto.platform);
    }

    @Test
    void jsonDeserialization_WithMissingFields_ShouldHandleGracefully() throws JsonProcessingException {
        // Given
        String json = """
            {
                "isrc": "GBUM71507409",
                "title": "Bohemian Rhapsody"
            }
            """;

        // When
        TrackMetadataDto dto = objectMapper.readValue(json, TrackMetadataDto.class);

        // Then
        assertEquals("GBUM71507409", dto.isrc);
        assertEquals("Bohemian Rhapsody", dto.title);
        assertNull(dto.artists);
        assertNull(dto.platform);
    }

    @Test
    void toString_ShouldIncludeAllFields() {
        // Given
        ArtistDto queen = new ArtistDto();
        queen.name = "Queen";
        queen.id = java.util.UUID.fromString("12345678-1234-1234-1234-123456789abc");
        TrackMetadataDto dto = new TrackMetadataDto(
            "GBUM71507409",
            "Bohemian Rhapsody",
            List.of(queen),
            "tidal"
        );

        // When
        String toString = dto.toString();

        // Then
        assertTrue(toString.contains("GBUM71507409"));
        assertTrue(toString.contains("Bohemian Rhapsody"));
        assertTrue(toString.contains("Queen"));
        assertTrue(toString.contains("tidal"));
        assertTrue(toString.contains("TrackMetadataDto"));
    }
}