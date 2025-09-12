package com.musichub.producer.adapter.rest.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import com.musichub.producer.adapter.rest.dto.response.RecentTrackResponse;
import com.musichub.producer.application.dto.TrackInfo;
import com.musichub.producer.domain.values.TrackStatus;
import com.musichub.shared.domain.values.ISRC;
import com.musichub.shared.domain.values.Source;

@DisplayName("TrackMapper Unit Tests")
class TrackMapperTest {

    private TrackMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = Mappers.getMapper(TrackMapper.class);
    }

    @Test
    @DisplayName("Should map TrackInfo to RecentTrackResponse correctly")
    void mapToRecentResponse_shouldMapCorrectly() {
        // Given
        ISRC isrc = ISRC.of("FRLA12400001");
        String title = "Test Track";
        List<String> artistNames = List.of("Artist One", "Artist Two");
        Source source1 = Source.of("SPOTIFY", "spotify_track_123");
        Source source2 = Source.of("TIDAL", "tidal_track_456");
        List<Source> sources = List.of(source1, source2);
        TrackStatus status = TrackStatus.VERIFIED;
        LocalDateTime submissionDate = LocalDateTime.of(2024, 1, 15, 10, 30);

        TrackInfo trackInfo = new TrackInfo(isrc, title, artistNames, sources, status, submissionDate);

        // When
        RecentTrackResponse response = mapper.mapToRecentResponse(trackInfo);

        // Then
        assertThat(response.isrc).isEqualTo(isrc.value());
        assertThat(response.title).isEqualTo(title);
        assertThat(response.artistNames).isEqualTo(artistNames);
        assertThat(response.status).isEqualTo(status.name());
        assertThat(response.submissionDate).isEqualTo(submissionDate);
        assertThat(response.source).isNotNull();
        assertThat(response.source.name).isEqualTo("SPOTIFY"); // First source
        assertThat(response.source.externalId).isEqualTo("spotify_track_123");
    }

    @Test
    @DisplayName("Should map TrackInfo with single source correctly")
    void mapToRecentResponse_shouldHandleSingleSource() {
        // Given
        ISRC isrc = ISRC.of("FRLA12400001");
        String title = "Test Track";
        List<String> artistNames = List.of("Artist One");
        Source source = Source.of("TIDAL", "tidal_track_456");
        List<Source> sources = List.of(source);
        TrackStatus status = TrackStatus.PROVISIONAL;
        LocalDateTime submissionDate = LocalDateTime.of(2024, 1, 15, 10, 30);

        TrackInfo trackInfo = new TrackInfo(isrc, title, artistNames, sources, status, submissionDate);

        // When
        RecentTrackResponse response = mapper.mapToRecentResponse(trackInfo);

        // Then
        assertThat(response.source.name).isEqualTo("TIDAL");
        assertThat(response.source.externalId).isEqualTo("tidal_track_456");
    }

    @Test
    @DisplayName("Should handle null sources list")
    void mapFirstSource_shouldHandleNullSources() {
        // Given
        List<Source> sources = null;

        // When
        RecentTrackResponse.SourceInfo result = mapper.mapFirstSource(sources);

        // Then
        assertThat(result).isNull();
    }

    @Test
    @DisplayName("Should handle empty sources list")
    void mapFirstSource_shouldHandleEmptySources() {
        // Given
        List<Source> sources = List.of();

        // When
        RecentTrackResponse.SourceInfo result = mapper.mapFirstSource(sources);

        // Then
        assertThat(result).isNull();
    }

    @Test
    @DisplayName("Should map first source from multiple sources")
    void mapFirstSource_shouldMapFirstSource() {
        // Given
        Source source1 = Source.of("SPOTIFY", "spotify_track_123");
        Source source2 = Source.of("TIDAL", "tidal_track_456");
        Source source3 = Source.of("DEEZER", "deezer_track_789");
        List<Source> sources = List.of(source1, source2, source3);

        // When
        RecentTrackResponse.SourceInfo result = mapper.mapFirstSource(sources);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.name).isEqualTo("SPOTIFY");
        assertThat(result.externalId).isEqualTo("spotify_track_123");
    }

    @Test
    @DisplayName("Should map TrackInfo with empty artist names list")
    void mapToRecentResponse_shouldHandleEmptyArtistNames() {
        // Given
        ISRC isrc = ISRC.of("FRLA12400001");
        String title = "Test Track";
        List<String> artistNames = List.of();
        Source source = Source.of("SPOTIFY", "spotify_track_123");
        List<Source> sources = List.of(source);
        TrackStatus status = TrackStatus.PROVISIONAL;
        LocalDateTime submissionDate = LocalDateTime.of(2024, 1, 15, 10, 30);

        TrackInfo trackInfo = new TrackInfo(isrc, title, artistNames, sources, status, submissionDate);

        // When
        RecentTrackResponse response = mapper.mapToRecentResponse(trackInfo);

        // Then
        assertThat(response.artistNames).isEmpty();
    }

    @Test
    @DisplayName("Should map TrackInfo with multiple artist names")
    void mapToRecentResponse_shouldHandleMultipleArtistNames() {
        // Given
        ISRC isrc = ISRC.of("FRLA12400001");
        String title = "Test Track";
        List<String> artistNames = List.of("Artist One", "Artist Two", "Artist Three");
        Source source = Source.of("SPOTIFY", "spotify_track_123");
        List<Source> sources = List.of(source);
        TrackStatus status = TrackStatus.VERIFIED;
        LocalDateTime submissionDate = LocalDateTime.of(2024, 1, 15, 10, 30);

        TrackInfo trackInfo = new TrackInfo(isrc, title, artistNames, sources, status, submissionDate);

        // When
        RecentTrackResponse response = mapper.mapToRecentResponse(trackInfo);

        // Then
        assertThat(response.artistNames).hasSize(3);
        assertThat(response.artistNames).containsExactly("Artist One", "Artist Two", "Artist Three");
    }

    @Test
    @DisplayName("Should map TrackInfo with different source types")
    void mapToRecentResponse_shouldHandleDifferentSourceTypes() {
        // Test with different source types to ensure proper mapping
        Source[] sources = {
                Source.of("TIDAL", "tidal_id"),
                Source.of("DEEZER", "deezer_id"),
                Source.of("APPLE_MUSIC", "apple_id"),
                Source.of("MANUAL", "manual_id")
        };

        for (Source source : sources) {
            // Given
            ISRC isrc = ISRC.of("FRLA12400001");
            String title = "Test Track";
            List<String> artistNames = List.of("Test Artist");
            List<Source> sourceList = List.of(source);
            TrackStatus status = TrackStatus.PROVISIONAL;
            LocalDateTime submissionDate = LocalDateTime.of(2024, 1, 15, 10, 30);

            TrackInfo trackInfo = new TrackInfo(isrc, title, artistNames, sourceList, status, submissionDate);

            // When
            RecentTrackResponse response = mapper.mapToRecentResponse(trackInfo);

            // Then
            assertThat(response.source.name).isEqualTo(source.getSourceName());
            assertThat(response.source.externalId).isEqualTo(source.sourceId());
        }
    }
}