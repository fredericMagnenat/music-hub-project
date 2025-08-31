package com.musichub.producer.adapter.persistence.adapter;

import com.musichub.producer.adapter.persistence.config.PersistenceTestProfile;
import com.musichub.producer.application.dto.TrackInfo;
import com.musichub.producer.domain.model.Producer;
import com.musichub.producer.domain.values.Source;
import com.musichub.producer.domain.values.TrackStatus;
import com.musichub.shared.domain.values.ISRC;
import com.musichub.shared.domain.values.ProducerCode;
import io.quarkus.test.TestTransaction;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Comparator;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@QuarkusTest
@TestProfile(PersistenceTestProfile.class)
@DisplayName("TrackRepositoryAdapter Recent Tracks Integration Tests")
class TrackRepositoryAdapterTest {

    @Inject
    TrackRepositoryAdapter trackRepository;

    @Inject
    ProducerRepositoryAdapter producerRepository;

    @BeforeEach
    @TestTransaction
    void setUp() {
        // Clean setup is handled by @TestTransaction per test
    }

    @Test
    @TestTransaction
    @DisplayName("Should return tracks ordered by creation date descending")
    void findRecentTracks_shouldReturnTracksOrderedByCreationDate() {
        // Given - Create producers with tracks using domain objects
        ProducerCode producerCode1 = ProducerCode.of("FRLA1");
        Producer producer1 = Producer.createNew(producerCode1, "Producer 1");
        Source source = Source.of("SPOTIFY", "test-source");
        
        ProducerCode producerCode2 = ProducerCode.of("USRC1");
        Producer producer2 = Producer.createNew(producerCode2, "Producer 2");

        // Register tracks - they will get timestamp based on save order
        producer1.registerTrack(ISRC.of("FRLA12400001"), "First Track", List.of("Artist 1"), List.of(source));
        producer2.registerTrack(ISRC.of("USRC17600001"), "Second Track", List.of("Artist 2"), List.of(source));
        producer1.registerTrack(ISRC.of("FRLA12400002"), "Third Track", List.of("Artist 3"), List.of(source));

        // Save in order - first track should be oldest, third track should be newest
        producerRepository.save(producer1);
        producerRepository.save(producer2);
        
        // Add slight delay to ensure different timestamps
        try { Thread.sleep(10); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        
        // Save an additional track to producer1 to make it the most recent
        producer1.registerTrack(ISRC.of("FRLA12400003"), "Newest Track", List.of("Artist 4"), List.of(source));
        producerRepository.save(producer1);

        // When
        List<TrackInfo> result = trackRepository.findRecentTracks(10);

        // Then
        assertThat(result).hasSizeGreaterThanOrEqualTo(3);
        
        // Verify tracks are ordered by creation date descending
        for (int i = 0; i < result.size() - 1; i++) {
            assertThat(result.get(i).submissionDate())
                .isAfterOrEqualTo(result.get(i + 1).submissionDate());
        }
        
        // The most recently saved track should be first
        assertThat(result)
            .first()
            .extracting(TrackInfo::title)
            .isEqualTo("Newest Track");
    }

    @Test
    @TestTransaction
    @DisplayName("Should respect limit parameter")
    void findRecentTracks_shouldRespectLimitParameter() {
        // Given
        ProducerCode producerCode = ProducerCode.of("FRLA1");
        Producer producer = Producer.createNew(producerCode, "Test Producer");
        Source source = Source.of("SPOTIFY", "test-source");

        // Register 5 tracks
        for (int i = 1; i <= 5; i++) {
            producer.registerTrack(ISRC.of("FRLA1240000" + i), "Track " + i, 
                List.of("Artist " + i), List.of(source));
        }
        
        producerRepository.save(producer);

        // When
        List<TrackInfo> result = trackRepository.findRecentTracks(3);

        // Then - AssertJ fluent assertion
        assertThat(result).hasSize(3);
    }

    @Test
    @TestTransaction
    @DisplayName("Should return empty list when no tracks exist")
    void findRecentTracks_shouldReturnEmptyList_whenNoTracksExist() {
        // When
        List<TrackInfo> result = trackRepository.findRecentTracks(10);

        // Then - AssertJ empty check
        assertThat(result).isEmpty();
    }

    @Test
    @TestTransaction
    @DisplayName("Should map track entity to track info correctly")
    void findRecentTracks_shouldMapTrackEntityToTrackInfo_correctly() {
        // Given
        ProducerCode producerCode = ProducerCode.of("FRLA1");
        Producer producer = Producer.createNew(producerCode, "Test Producer");
        Source source = Source.of("TIDAL", "external-123");
        
        producer.registerTrack(ISRC.of("FRLA12400001"), "Test Track", 
            List.of("Artist 1", "Artist 2"), List.of(source));
        
        producerRepository.save(producer);

        // When
        List<TrackInfo> result = trackRepository.findRecentTracks(10);

        // Then - AssertJ complex object assertions
        assertThat(result)
            .hasSize(1)
            .first()
            .satisfies(track -> {
                assertThat(track.isrc().value()).isEqualTo("FRLA12400001");
                assertThat(track.title()).isEqualTo("Test Track");
                assertThat(track.artistNames()).containsExactly("Artist 1", "Artist 2");
                assertThat(track.status()).isEqualTo(TrackStatus.PROVISIONAL);
                assertThat(track.sources())
                    .hasSize(1)
                    .first()
                    .extracting(Source::sourceName)
                    .isEqualTo("TIDAL");
                assertThat(track.submissionDate()).isNotNull();
            });
    }

    @Test
    @TestTransaction
    @DisplayName("Should throw IllegalArgumentException when limit is invalid")
    void findRecentTracks_shouldThrowException_whenLimitIsInvalid() {
        // When & Then - AssertJ exception testing
        assertThatThrownBy(() -> trackRepository.findRecentTracks(0))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Limit must be positive, got: 0");
            
        assertThatThrownBy(() -> trackRepository.findRecentTracks(-5))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Limit must be positive, got: -5");
    }

    @Test
    @TestTransaction
    @DisplayName("Should cap limit to maximum when limit exceeds MAX_TRACKS_LIMIT")
    void findRecentTracks_shouldCapLimit_whenLimitExceedsMaximum() {
        // Given
        ProducerCode producerCode = ProducerCode.of("FRLA1");
        Producer producer = Producer.createNew(producerCode, "Test Producer");
        Source source = Source.of("SPOTIFY", "test-source");

        // Create 5 tracks (less than 1000)
        for (int i = 1; i <= 5; i++) {
            producer.registerTrack(ISRC.of("FRLA1240000" + i), "Track " + i, 
                List.of("Artist"), List.of(source));
        }
        
        producerRepository.save(producer);

        // When - Request more than MAX_TRACKS_LIMIT (1000)
        List<TrackInfo> result = trackRepository.findRecentTracks(1500);

        // Then - Should return all available tracks (5), not fail
        assertThat(result).hasSize(5);
    }

    @Test
    @TestTransaction
    @DisplayName("Should handle multiple tracks with different properties")
    void findRecentTracks_shouldHandleMultipleTracksWithDifferentProperties() {
        // Given
        ProducerCode producerCode1 = ProducerCode.of("FRLA1");
        Producer producer1 = Producer.createNew(producerCode1, "Producer 1");
        
        ProducerCode producerCode2 = ProducerCode.of("USRC1");
        Producer producer2 = Producer.createNew(producerCode2, "Producer 2");

        Source spotifySource = Source.of("SPOTIFY", "spotify-123");
        Source tidalSource = Source.of("TIDAL", "tidal-456");

        // Register tracks with different properties
        producer1.registerTrack(ISRC.of("FRLA12400001"), "Rock Song", 
            List.of("Band A"), List.of(spotifySource));
        producer2.registerTrack(ISRC.of("USRC17600001"), "Pop Song", 
            List.of("Artist B", "Artist C"), List.of(tidalSource));
        producer1.registerTrack(ISRC.of("FRLA12400002"), "Jazz Song", 
            List.of("Trio D"), List.of(spotifySource, tidalSource));

        producerRepository.save(producer1);
        producerRepository.save(producer2);

        // When
        List<TrackInfo> result = trackRepository.findRecentTracks(10);

        // Then - AssertJ advanced assertions
        assertThat(result)
            .hasSize(3)
            .extracting(TrackInfo::title)
            .containsExactlyInAnyOrder("Rock Song", "Pop Song", "Jazz Song");

        // Verify different artist counts
        assertThat(result)
            .extracting(TrackInfo::artistNames)
            .extracting(List::size)
            .containsExactlyInAnyOrder(1, 2, 1);

        // Verify all tracks have valid submission dates
        assertThat(result)
            .extracting(TrackInfo::submissionDate)
            .allSatisfy(date -> assertThat(date).isNotNull());
    }
}