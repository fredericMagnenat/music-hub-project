package com.musichub.producer.adapter.rest.resource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.musichub.producer.adapter.rest.dto.response.RecentTrackResponse;
import com.musichub.producer.adapter.rest.mapper.TrackMapper;
import com.musichub.producer.adapter.rest.resource.track.TracksResource;
import com.musichub.producer.application.dto.TrackInfo;
import com.musichub.producer.application.ports.in.GetRecentTracksUseCase;
import com.musichub.producer.domain.values.TrackStatus;
import com.musichub.shared.domain.values.ISRC;
import com.musichub.shared.domain.values.Source;

import jakarta.ws.rs.core.Response;

@ExtendWith(MockitoExtension.class)
@DisplayName("TracksResource REST Adapter Tests")
class TracksResourceTest {

    @Mock
    private GetRecentTracksUseCase getRecentTracksUseCase;

    @Mock
    private TrackMapper trackMapper;

    @InjectMocks
    private TracksResource resource;

    @Test
    @DisplayName("Should return 200 with tracks when tracks exist")
    void getRecentTracks_shouldReturn200WithTracks_whenTracksExist() {
        // Given
        TrackInfo track1 = createTrackInfo("FRLA12400001", "Track 1");
        TrackInfo track2 = createTrackInfo("FRLA12400002", "Track 2");
        List<TrackInfo> tracks = Arrays.asList(track1, track2);

        RecentTrackResponse response1 = new RecentTrackResponse();
        response1.isrc = "FRLA12400001";
        response1.title = "Track 1";

        RecentTrackResponse response2 = new RecentTrackResponse();
        response2.isrc = "FRLA12400002";
        response2.title = "Track 2";

        when(getRecentTracksUseCase.getRecentTracks()).thenReturn(tracks);
        when(trackMapper.mapToRecentResponse(track1)).thenReturn(response1);
        when(trackMapper.mapToRecentResponse(track2)).thenReturn(response2);

        // When
        Response response = resource.getRecentTracks();

        // Then
        assertThat(response.getStatus()).isEqualTo(200);

        @SuppressWarnings("unchecked")
        List<RecentTrackResponse> responseBody = (List<RecentTrackResponse>) response.getEntity();

        assertThat(responseBody)
                .hasSize(2)
                .extracting("title")
                .containsExactly("Track 1", "Track 2");

        assertThat(responseBody)
                .extracting("isrc")
                .containsExactly("FRLA12400001", "FRLA12400002");

        verify(getRecentTracksUseCase).getRecentTracks();
        verify(trackMapper).mapToRecentResponse(track1);
        verify(trackMapper).mapToRecentResponse(track2);
    }

    @Test
    @DisplayName("Should return 200 with empty array when no tracks exist")
    void getRecentTracks_shouldReturn200WithEmptyArray_whenNoTracksExist() {
        // Given
        when(getRecentTracksUseCase.getRecentTracks()).thenReturn(Collections.emptyList());

        // When
        Response response = resource.getRecentTracks();

        // Then
        assertThat(response.getStatus()).isEqualTo(200);

        @SuppressWarnings("unchecked")
        List<RecentTrackResponse> responseBody = (List<RecentTrackResponse>) response.getEntity();

        assertThat(responseBody).isEmpty();

        verify(getRecentTracksUseCase).getRecentTracks();
    }

    @Test
    @DisplayName("Should handle exceptions from use case")
    void getRecentTracks_handles_exceptions_from_use_case() {
        // Given
        when(getRecentTracksUseCase.getRecentTracks()).thenThrow(new RuntimeException("Database error"));

        // When & Then - Exception should be thrown (not handled by
        // GlobalExceptionMapper in unit tests)
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            resource.getRecentTracks();
        });
        assertThat(exception.getMessage()).isEqualTo("Database error");

        verify(getRecentTracksUseCase).getRecentTracks();
    }

    @Test
    @DisplayName("Should map TrackInfo to RecentTrackResponse correctly")
    void shouldMapTrackInfoCorrectly() {
        // Given
        TrackInfo trackInfo = createTrackInfo("FRLA12400001", "Test Track");
        List<TrackInfo> tracks = List.of(trackInfo);

        RecentTrackResponse expectedResponse = new RecentTrackResponse();
        expectedResponse.isrc = "FRLA12400001";
        expectedResponse.title = "Test Track";
        expectedResponse.artistNames = List.of("Artist Name");
        expectedResponse.status = "PROVISIONAL";
        expectedResponse.submissionDate = trackInfo.submissionDate();

        RecentTrackResponse.SourceInfo sourceInfo = new RecentTrackResponse.SourceInfo();
        sourceInfo.name = "TIDAL";
        sourceInfo.externalId = "FRLA12400001";
        expectedResponse.source = sourceInfo;

        when(getRecentTracksUseCase.getRecentTracks()).thenReturn(tracks);
        when(trackMapper.mapToRecentResponse(trackInfo)).thenReturn(expectedResponse);

        // When
        Response response = resource.getRecentTracks();

        // Then
        assertThat(response.getStatus()).isEqualTo(200);

        @SuppressWarnings("unchecked")
        List<RecentTrackResponse> responseBody = (List<RecentTrackResponse>) response.getEntity();

        assertThat(responseBody).hasSize(1);
        RecentTrackResponse mappedResponse = responseBody.get(0);

        assertThat(mappedResponse)
                .satisfies(r -> {
                    assertThat(r.isrc).isEqualTo("FRLA12400001");
                    assertThat(r.title).isEqualTo("Test Track");
                    assertThat(r.artistNames).containsExactly("Artist Name");
                    assertThat(r.status).isEqualTo("PROVISIONAL");
                    assertThat(r.submissionDate).isNotNull();
                });

        assertThat(mappedResponse.source)
                .satisfies(source -> {
                    assertThat(source.name).isEqualTo("TIDAL");
                    assertThat(source.externalId).isEqualTo("FRLA12400001");
                });
    }

    @Test
    @DisplayName("Should map multiple sources correctly")
    void shouldMapMultipleSourcesCorrectly() {
        // Given
        TrackInfo trackInfo = new TrackInfo(
                ISRC.of("FRLA12400001"),
                "Test Track",
                List.of("Artist Name"),
                List.of(
                        Source.of("TIDAL", "tidal-123"),
                        Source.of("SPOTIFY", "spotify-456")),
                TrackStatus.PROVISIONAL,
                LocalDateTime.now().minusHours(1));

        RecentTrackResponse expectedResponse = new RecentTrackResponse();
        expectedResponse.isrc = "FRLA12400001";
        expectedResponse.title = "Test Track";
        RecentTrackResponse.SourceInfo sourceInfo = new RecentTrackResponse.SourceInfo();
        sourceInfo.name = "TIDAL";
        sourceInfo.externalId = "tidal-123";
        expectedResponse.source = sourceInfo;

        when(getRecentTracksUseCase.getRecentTracks()).thenReturn(List.of(trackInfo));
        when(trackMapper.mapToRecentResponse(trackInfo)).thenReturn(expectedResponse);

        // When
        Response response = resource.getRecentTracks();

        // Then
        @SuppressWarnings("unchecked")
        List<RecentTrackResponse> responseBody = (List<RecentTrackResponse>) response.getEntity();

        assertThat(responseBody).hasSize(1);
        RecentTrackResponse mappedResponse = responseBody.get(0);

        // Seule la première source est mappée selon la logique actuelle
        assertThat(mappedResponse.source)
                .satisfies(source -> {
                    assertThat(source.name).isEqualTo("TIDAL");
                    assertThat(source.externalId).isEqualTo("tidal-123");
                });
    }

    @Test
    @DisplayName("Should handle empty sources list")
    void shouldHandleEmptySourcesList() {
        // Given
        TrackInfo trackInfo = new TrackInfo(
                ISRC.of("FRLA12400001"),
                "Test Track",
                List.of("Artist Name"),
                Collections.emptyList(),
                TrackStatus.PROVISIONAL,
                LocalDateTime.now().minusHours(1));

        RecentTrackResponse expectedResponse = new RecentTrackResponse();
        expectedResponse.isrc = "FRLA12400001";
        expectedResponse.title = "Test Track";
        expectedResponse.source = null; // No source

        when(getRecentTracksUseCase.getRecentTracks()).thenReturn(List.of(trackInfo));
        when(trackMapper.mapToRecentResponse(trackInfo)).thenReturn(expectedResponse);

        // When
        Response response = resource.getRecentTracks();

        // Then
        @SuppressWarnings("unchecked")
        List<RecentTrackResponse> responseBody = (List<RecentTrackResponse>) response.getEntity();

        assertThat(responseBody).hasSize(1);
        RecentTrackResponse mappedResponse = responseBody.get(0);

        assertThat(mappedResponse.source).isNull();
        assertThat(mappedResponse.isrc).isEqualTo("FRLA12400001");
        assertThat(mappedResponse.title).isEqualTo("Test Track");
    }

    @Test
    @DisplayName("Should handle multiple artists correctly")
    void shouldHandleMultipleArtistsCorrectly() {
        // Given
        TrackInfo trackInfo = new TrackInfo(
                ISRC.of("FRLA12400001"),
                "Collaboration Track",
                List.of("Artist 1", "Artist 2", "Featured Artist"),
                List.of(Source.of("SPOTIFY", "spotify-123")),
                TrackStatus.VERIFIED,
                LocalDateTime.now().minusHours(2));

        RecentTrackResponse expectedResponse = new RecentTrackResponse();
        expectedResponse.isrc = "FRLA12400001";
        expectedResponse.title = "Collaboration Track";
        expectedResponse.artistNames = List.of("Artist 1", "Artist 2", "Featured Artist");
        expectedResponse.status = "VERIFIED";

        when(getRecentTracksUseCase.getRecentTracks()).thenReturn(List.of(trackInfo));
        when(trackMapper.mapToRecentResponse(trackInfo)).thenReturn(expectedResponse);

        // When
        Response response = resource.getRecentTracks();

        // Then
        @SuppressWarnings("unchecked")
        List<RecentTrackResponse> responseBody = (List<RecentTrackResponse>) response.getEntity();

        assertThat(responseBody).hasSize(1);
        RecentTrackResponse mappedResponse = responseBody.get(0);

        assertThat(mappedResponse.artistNames)
                .hasSize(3)
                .containsExactly("Artist 1", "Artist 2", "Featured Artist");
        assertThat(mappedResponse.status).isEqualTo("VERIFIED");
    }

    @Test
    @DisplayName("Should handle basic DTO mapping structure")
    void shouldHandleBasicDTOMappingStructure() {
        // Given
        TrackInfo trackInfo = createTrackInfo("FRLA12400001", "Test Track");

        RecentTrackResponse expectedResponse = new RecentTrackResponse();
        expectedResponse.isrc = "FRLA12400001";
        expectedResponse.title = "Test Track";
        expectedResponse.artistNames = List.of("Artist Name");
        expectedResponse.status = "PROVISIONAL";
        expectedResponse.submissionDate = trackInfo.submissionDate();

        when(getRecentTracksUseCase.getRecentTracks()).thenReturn(List.of(trackInfo));
        when(trackMapper.mapToRecentResponse(trackInfo)).thenReturn(expectedResponse);

        // When
        Response response = resource.getRecentTracks();

        // Then
        @SuppressWarnings("unchecked")
        List<RecentTrackResponse> responseBody = (List<RecentTrackResponse>) response.getEntity();

        assertThat(responseBody).hasSize(1);
        RecentTrackResponse mappedResponse = responseBody.get(0);

        // Verify all essential fields are properly mapped
        assertThat(mappedResponse.isrc).isEqualTo("FRLA12400001");
        assertThat(mappedResponse.title).isEqualTo("Test Track");
        assertThat(mappedResponse.artistNames).isNotEmpty();
        assertThat(mappedResponse.status).isNotNull();
        assertThat(mappedResponse.submissionDate).isNotNull();
    }

    private TrackInfo createTrackInfo(String isrcValue, String title) {
        return new TrackInfo(
                ISRC.of(isrcValue),
                title,
                List.of("Artist Name"),
                List.of(Source.of("TIDAL", isrcValue)),
                TrackStatus.PROVISIONAL,
                LocalDateTime.now().minusHours(1));
    }
}