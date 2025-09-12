package com.musichub.producer.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.musichub.producer.application.dto.TrackInfo;
import com.musichub.producer.application.ports.out.TrackRepository;
import com.musichub.producer.application.service.GetRecentTracksService;
import com.musichub.shared.domain.values.Source;
import com.musichub.producer.domain.values.TrackStatus;
import com.musichub.shared.domain.values.ISRC;

@ExtendWith(MockitoExtension.class)
@DisplayName("GetRecentTracksService Application Layer Tests")
class GetRecentTracksServiceTest {

    private GetRecentTracksService service;

    @Mock
    private TrackRepository repository;

    @BeforeEach
    void setUp() {
        service = new GetRecentTracksService(repository);
    }

    @Test
    @DisplayName("Should retrieve recent tracks from repository with default limit")
    void getRecentTracks_shouldRetrieveFromRepository_withDefaultLimit() {
        // Given
        TrackInfo track1 = createTrackInfo("FRLA12400001", "Track 1");
        TrackInfo track2 = createTrackInfo("FRLA12400002", "Track 2");
        List<TrackInfo> expectedTracks = Arrays.asList(track1, track2);

        when(repository.findRecentTracks(10)).thenReturn(expectedTracks);

        // When
        List<TrackInfo> result = service.getRecentTracks();

        // Then
        assertThat(result).isEqualTo(expectedTracks);
        verify(repository).findRecentTracks(10);
    }

    @Test
    @DisplayName("Should retrieve recent tracks from repository with custom limit")
    void getRecentTracks_shouldRetrieveFromRepository_withCustomLimit() {
        // Given
        TrackInfo track1 = createTrackInfo("FRLA12400001", "Track 1");
        List<TrackInfo> expectedTracks = List.of(track1);

        when(repository.findRecentTracks(5)).thenReturn(expectedTracks);

        // When
        List<TrackInfo> result = service.getRecentTracks(5);

        // Then
        assertThat(result).isEqualTo(expectedTracks);
        verify(repository).findRecentTracks(5);
    }

    @Test
    @DisplayName("Should return empty list when no tracks exist")
    void getRecentTracks_shouldReturnEmptyList_whenNoTracksExist() {
        // Given
        when(repository.findRecentTracks(10)).thenReturn(Collections.emptyList());

        // When
        List<TrackInfo> result = service.getRecentTracks();

        // Then
        assertThat(result).isEmpty();
        verify(repository).findRecentTracks(10);
    }

    @Test
    @DisplayName("Should throw exception when limit is negative")
    void getRecentTracks_shouldThrowException_whenLimitIsNegative() {
        // When & Then
        assertThatThrownBy(() -> service.getRecentTracks(-1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Limit must be positive, got: -1");

        verifyNoInteractions(repository);
    }

    @Test
    @DisplayName("Should throw exception when limit is zero")
    void getRecentTracks_shouldThrowException_whenLimitIsZero() {
        // When & Then
        assertThatThrownBy(() -> service.getRecentTracks(0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Limit must be positive, got: 0");

        verifyNoInteractions(repository);
    }

    @Test
    @DisplayName("Should throw exception when limit exceeds maximum")
    void getRecentTracks_shouldThrowException_whenLimitExceedsMaximum() {
        // When & Then
        assertThatThrownBy(() -> service.getRecentTracks(101))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Limit cannot exceed 100, got: 101");

        verifyNoInteractions(repository);
    }

    private TrackInfo createTrackInfo(String isrcValue, String title) {
        return new TrackInfo(
                ISRC.of(isrcValue),
                title,
                List.of("Artist Name"),
                List.of(Source.of("TIDAL", isrcValue)),
                TrackStatus.PROVISIONAL,
                LocalDateTime.now());
    }
}