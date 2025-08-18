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
        assertNull(dto.artistNames);
        assertNull(dto.platform);
    }

    @Test
    void parameterizedConstructor_ShouldSetAllFields() {
        // Given
        String isrc = "GBUM71507409";
        String title = "Bohemian Rhapsody";
        List<String> artistNames = List.of("Queen");
        String platform = "tidal";

        // When
        TrackMetadataDto dto = new TrackMetadataDto(isrc, title, artistNames, platform);

        // Then
        assertEquals(isrc, dto.isrc);
        assertEquals(title, dto.title);
        assertEquals(artistNames, dto.artistNames);
        assertEquals(platform, dto.platform);
    }

    @Test
    void jsonDeserialization_ShouldMapCorrectly() throws JsonProcessingException {
        // Given
        String json = """
            {
                "isrc": "GBUM71507409",
                "title": "Bohemian Rhapsody",
                "artists": ["Queen"],
                "platform": "tidal"
            }
            """;

        // When
        TrackMetadataDto dto = objectMapper.readValue(json, TrackMetadataDto.class);

        // Then
        assertEquals("GBUM71507409", dto.isrc);
        assertEquals("Bohemian Rhapsody", dto.title);
        assertEquals(List.of("Queen"), dto.artistNames);
        assertEquals("tidal", dto.platform);
    }

    @Test
    void jsonSerialization_ShouldProduceCorrectJson() throws JsonProcessingException {
        // Given
        TrackMetadataDto dto = new TrackMetadataDto(
            "GBUM71507409", 
            "Bohemian Rhapsody", 
            List.of("Queen"), 
            "tidal"
        );

        // When
        String json = objectMapper.writeValueAsString(dto);

        // Then
        assertTrue(json.contains("\"isrc\":\"GBUM71507409\""));
        assertTrue(json.contains("\"title\":\"Bohemian Rhapsody\""));
        assertTrue(json.contains("\"artists\":[\"Queen\"]"));
        assertTrue(json.contains("\"platform\":\"tidal\""));
    }

    @Test
    void jsonDeserialization_WithMultipleArtists_ShouldMapCorrectly() throws JsonProcessingException {
        // Given
        String json = """
            {
                "isrc": "GBUM71507410",
                "title": "Under Pressure",
                "artists": ["Queen", "David Bowie"],
                "platform": "tidal"
            }
            """;

        // When
        TrackMetadataDto dto = objectMapper.readValue(json, TrackMetadataDto.class);

        // Then
        assertEquals("GBUM71507410", dto.isrc);
        assertEquals("Under Pressure", dto.title);
        assertEquals(List.of("Queen", "David Bowie"), dto.artistNames);
        assertEquals(2, dto.artistNames.size());
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
        assertNull(dto.artistNames);
        assertNull(dto.platform);
    }

    @Test
    void toString_ShouldIncludeAllFields() {
        // Given
        TrackMetadataDto dto = new TrackMetadataDto(
            "GBUM71507409", 
            "Bohemian Rhapsody", 
            List.of("Queen"), 
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