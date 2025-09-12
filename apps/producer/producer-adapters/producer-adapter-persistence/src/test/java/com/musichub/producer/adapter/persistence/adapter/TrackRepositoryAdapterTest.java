package com.musichub.producer.adapter.persistence.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.musichub.producer.adapter.persistence.config.PersistenceTestProfile;
import com.musichub.producer.application.dto.TrackInfo;
import com.musichub.producer.domain.model.Producer;
import com.musichub.producer.domain.values.ArtistCredit;
import com.musichub.shared.domain.values.Source;
import com.musichub.producer.domain.values.TrackStatus;
import com.musichub.shared.domain.values.ISRC;
import com.musichub.shared.domain.values.ProducerCode;

import io.quarkus.test.TestTransaction;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import jakarta.inject.Inject;

@QuarkusTest
@TestProfile(PersistenceTestProfile.class)
@DisplayName("TrackRepositoryAdapter Integration Tests")
class TrackRepositoryAdapterTest {

    @Inject
    TrackRepositoryAdapter trackRepository;

    @Inject
    ProducerRepositoryAdapter producerRepository;

    private Producer createProducerWithTracks(String producerCodeValue, String producerName, int trackCount) {
        ProducerCode producerCode = ProducerCode.of(producerCodeValue);
        Producer producer = Producer.createNew(producerCode, producerName);
        Source source = Source.of("SPOTIFY", "test-source");
        
        for (int i = 1; i <= trackCount; i++) {
            String isrcValue = producerCodeValue + "240000" + i;
            // ✅ CORRECTION: Utiliser registerTrack() avec List<ArtistCredit>
            producer.registerTrack(ISRC.of(isrcValue), "Track " + i, 
                List.of(ArtistCredit.withName("Artist " + i)), List.of(source));
        }
        
        return producerRepository.save(producer);
    }

    @Test
    @TestTransaction
    @DisplayName("Should return tracks ordered by creation date descending")
    void findRecentTracks_shouldReturnTracksOrderedByCreationDate() {
        // Given - Create producers with tracks
        createProducerWithTracks("FRLA1", "Producer 1", 2);
        
        // Add delay to ensure different timestamps
        try { Thread.sleep(50); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        
        createProducerWithTracks("USRC1", "Producer 2", 1);

        // When
        List<TrackInfo> result = trackRepository.findRecentTracks(10);

        // Then
        assertThat(result).hasSizeGreaterThanOrEqualTo(3);
        
        // Verify tracks are ordered by creation date descending
        for (int i = 0; i < result.size() - 1; i++) {
            assertThat(result.get(i).submissionDate())
                .as("Track at position %d should be newer than or equal to track at position %d", i, i + 1)
                .isAfterOrEqualTo(result.get(i + 1).submissionDate());
        }
    }

    @Test
    @TestTransaction
    @DisplayName("Should respect limit parameter")
    void findRecentTracks_shouldRespectLimitParameter() {
        // Given
        createProducerWithTracks("FRLA1", "Test Producer", 5);

        // When
        List<TrackInfo> result = trackRepository.findRecentTracks(3);

        // Then
        assertThat(result).hasSize(3);
    }

    @Test
    @TestTransaction
    @DisplayName("Should return empty list when no tracks exist")
    void findRecentTracks_shouldReturnEmptyList_whenNoTracksExist() {
        // When
        List<TrackInfo> result = trackRepository.findRecentTracks(10);

        // Then
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
            List.of(ArtistCredit.withName("Artist 1"), ArtistCredit.withName("Artist 2")), 
            List.of(source));
        
        producerRepository.save(producer);

        // When
        List<TrackInfo> result = trackRepository.findRecentTracks(10);

        // Then
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
                    .satisfies(src -> assertThat(src.getSourceName()).isEqualTo("TIDAL"));
                assertThat(track.submissionDate()).isNotNull();
            });
    }

    @Test
    @TestTransaction
    @DisplayName("Should throw IllegalArgumentException when limit is invalid")
    void findRecentTracks_shouldThrowException_whenLimitIsInvalid() {
        // When & Then
        assertThatThrownBy(() -> trackRepository.findRecentTracks(0))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Limit must be positive");
            
        assertThatThrownBy(() -> trackRepository.findRecentTracks(-5))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Limit must be positive");
    }

    @Test
    @TestTransaction
    @DisplayName("Should handle large limit parameter correctly")
    void findRecentTracks_shouldHandleLargeLimit() {
        // Given
        createProducerWithTracks("FRLA1", "Test Producer", 5);

        // When - Request more than available
        List<TrackInfo> result = trackRepository.findRecentTracks(1500);

        // Then - Should return all available tracks
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
            List.of(ArtistCredit.withName("Band A")), List.of(spotifySource));
        producer2.registerTrack(ISRC.of("USRC17600001"), "Pop Song", 
            List.of(ArtistCredit.withName("Artist B"), ArtistCredit.withName("Artist C")), 
            List.of(tidalSource));
        producer1.registerTrack(ISRC.of("FRLA12400002"), "Jazz Song", 
            List.of(ArtistCredit.withName("Trio D")), List.of(spotifySource, tidalSource));

        producerRepository.save(producer1);
        producerRepository.save(producer2);

        // When
        List<TrackInfo> result = trackRepository.findRecentTracks(10);

        // Then
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
