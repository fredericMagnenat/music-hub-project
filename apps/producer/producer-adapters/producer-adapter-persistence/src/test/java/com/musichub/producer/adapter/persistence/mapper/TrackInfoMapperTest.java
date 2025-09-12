package com.musichub.producer.adapter.persistence.mapper;

import com.musichub.producer.adapter.persistence.entity.ArtistCreditEmbeddable;
import com.musichub.producer.adapter.persistence.entity.TrackEntity;
import com.musichub.producer.adapter.persistence.exception.ProducerPersistenceException;
import com.musichub.producer.application.dto.TrackInfo;
import com.musichub.shared.domain.values.Source;
import com.musichub.producer.domain.values.TrackStatus;
import com.musichub.shared.domain.values.ISRC;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("TrackInfoMapper Unit Tests")
class TrackInfoMapperTest {

    @Nested
    @DisplayName("toDto() Method Tests")
    class ToDtoTests {

        @Test
        @DisplayName("Should successfully map complete TrackEntity to TrackInfo")
        void shouldMapCompleteTrackEntityToTrackInfo() {
            // Given
            TrackEntity trackEntity = createValidTrackEntity();

            // When
            TrackInfo result = TrackInfoMapper.toDto(trackEntity);

            // Then - AssertJ complex object validation
            assertThat(result)
                .isNotNull()
                .satisfies(track -> {
                    assertThat(track.isrc()).isEqualTo(ISRC.of("FRXYZ1234567"));
                    assertThat(track.title()).isEqualTo("Test Track");
                    assertThat(track.artistNames()).containsExactly("Artist 1", "Artist 2");
                    assertThat(track.sources()).hasSize(2);
                    assertThat(track.status()).isEqualTo(TrackStatus.PROVISIONAL);
                    assertThat(track.submissionDate()).isEqualTo(trackEntity.getCreatedAt());
                });
        }

        @Test
        @DisplayName("Should handle null credits gracefully")
        void shouldHandleNullCreditsGracefully() {
            // Given
            TrackEntity trackEntity = createValidTrackEntity();
            trackEntity.setCredits(null);

            // When
            TrackInfo result = TrackInfoMapper.toDto(trackEntity);

            // Then - AssertJ fluent assertion
            assertThat(result.artistNames()).isEmpty();
        }

        @Test
        @DisplayName("Should handle null sources gracefully")
        void shouldHandleNullSourcesGracefully() {
            // Given
            TrackEntity trackEntity = createValidTrackEntity();
            trackEntity.setSources(null);

            // When
            TrackInfo result = TrackInfoMapper.toDto(trackEntity);

            // Then - AssertJ fluent assertion
            assertThat(result.sources()).isEmpty();
        }

        @Test
        @DisplayName("Should handle empty credits and sources")
        void shouldHandleEmptyCreditsAndSources() {
            // Given
            TrackEntity trackEntity = createValidTrackEntity();
            trackEntity.setCredits(List.of());
            trackEntity.setSources(List.of());

            // When
            TrackInfo result = TrackInfoMapper.toDto(trackEntity);

            // Then - AssertJ multiple assertions
            assertThat(result.artistNames()).isEmpty();
            assertThat(result.sources()).isEmpty();
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException when TrackEntity is null")
        void shouldThrowIllegalArgumentExceptionWhenTrackEntityIsNull() {
            // When & Then - AssertJ exception testing
            assertThatThrownBy(() -> TrackInfoMapper.toDto(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("TrackEntity cannot be null");
        }

        @Test
        @DisplayName("Should throw ProducerPersistenceException when ISRC is invalid")
        void shouldThrowProducerPersistenceExceptionWhenIsrcIsInvalid() {
            // Given
            TrackEntity trackEntity = createValidTrackEntity();
            trackEntity.setIsrc("INVALID_ISRC");

            // When & Then - AssertJ exception with cause validation
            assertThatThrownBy(() -> TrackInfoMapper.toDto(trackEntity))
                .isInstanceOf(ProducerPersistenceException.class)
                .hasMessage("Error mapping track entity with ISRC: INVALID_ISRC")
                .hasCauseInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("Should throw ProducerPersistenceException when TrackStatus is invalid")
        void shouldThrowProducerPersistenceExceptionWhenTrackStatusIsInvalid() {
            // Given
            TrackEntity trackEntity = createValidTrackEntity();
            trackEntity.setStatus("INVALID_STATUS");

            // When & Then - AssertJ exception testing
            assertThatThrownBy(() -> TrackInfoMapper.toDto(trackEntity))
                .isInstanceOf(ProducerPersistenceException.class)
                .hasMessage("Error mapping track entity with ISRC: FRXYZ1234567")
                .hasCauseInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("Should preserve all source details correctly")
        void shouldPreserveAllSourceDetailsCorrectly() {
            // Given
            TrackEntity trackEntity = createValidTrackEntity();

            // When
            TrackInfo result = TrackInfoMapper.toDto(trackEntity);

            // Then - AssertJ source validation with extracting
            assertThat(result.sources())
                .hasSize(2)
                .extracting(Source::getSourceName)
                .containsExactlyInAnyOrder("SPOTIFY", "TIDAL");

            assertThat(result.sources())
                .extracting(Source::sourceId)
                .containsExactlyInAnyOrder("spotify-track-123", "tidal-track-456");
        }

        @Test
        @DisplayName("Should handle different track statuses")
        void shouldHandleDifferentTrackStatuses() {
            // Given - Test each valid status
            TrackEntity provisionalTrack = createTrackEntity("FRXYZ1234567", "Provisional Track", TrackStatus.PROVISIONAL);
            TrackEntity verifiedTrack = createTrackEntity("FRXYZ1234568", "Verified Track", TrackStatus.VERIFIED);

            // When
            TrackInfo provisionalResult = TrackInfoMapper.toDto(provisionalTrack);
            TrackInfo verifiedResult = TrackInfoMapper.toDto(verifiedTrack);

            // Then - AssertJ status validation
            assertThat(provisionalResult.status()).isEqualTo(TrackStatus.PROVISIONAL);
            assertThat(verifiedResult.status()).isEqualTo(TrackStatus.VERIFIED);
        }
    }

    @Nested
    @DisplayName("toDtoList() Method Tests")
    class ToDtoListTests {

        @Test
        @DisplayName("Should successfully map list of TrackEntities to TrackInfos")
        void shouldMapListOfTrackEntitiesToTrackInfos() {
            // Given
            TrackEntity track1 = createValidTrackEntity();
            TrackEntity track2 = createTrackEntity("FRXYZ7654321", "Track 2", TrackStatus.VERIFIED);
            List<TrackEntity> trackEntities = List.of(track1, track2);

            // When
            List<TrackInfo> result = TrackInfoMapper.toDtoList(trackEntities);

            // Then - AssertJ collection validation with extracting
            assertThat(result)
                .hasSize(2)
                .extracting(TrackInfo::isrc)
                .extracting(ISRC::value)
                .containsExactly("FRXYZ1234567", "FRXYZ7654321");

            assertThat(result)
                .extracting(TrackInfo::title)
                .containsExactly("Test Track", "Track 2");

            assertThat(result)
                .extracting(TrackInfo::status)
                .containsExactly(TrackStatus.PROVISIONAL, TrackStatus.VERIFIED);
        }

        @Test
        @DisplayName("Should return empty list when input list is null")
        void shouldReturnEmptyListWhenInputIsNull() {
            // When
            List<TrackInfo> result = TrackInfoMapper.toDtoList(null);

            // Then - AssertJ empty check
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("Should return empty list when input list is empty")
        void shouldReturnEmptyListWhenInputIsEmpty() {
            // Given
            List<TrackEntity> emptyList = List.of();

            // When
            List<TrackInfo> result = TrackInfoMapper.toDtoList(emptyList);

            // Then - AssertJ empty check
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("Should propagate exception when mapping fails for one entity")
        void shouldPropagateExceptionWhenMappingFailsForOneEntity() {
            // Given
            TrackEntity validTrack = createValidTrackEntity();
            TrackEntity invalidTrack = createTrackEntity("INVALID_ISRC", "Invalid Track", TrackStatus.PROVISIONAL);
            List<TrackEntity> trackEntities = List.of(validTrack, invalidTrack);

            // When & Then - AssertJ exception with message containing
            assertThatThrownBy(() -> TrackInfoMapper.toDtoList(trackEntities))
                .isInstanceOf(ProducerPersistenceException.class)
                .hasMessageContaining("Error mapping track entity with ISRC: INVALID_ISRC");
        }

        @Test
        @DisplayName("Should handle mixed track types in list")
        void shouldHandleMixedTrackTypesInList() {
            // Given - Different track configurations
            TrackEntity minimalTrack = createTrackEntity("FRXYZ1111111", "Minimal", TrackStatus.PROVISIONAL);
            minimalTrack.setCredits(List.of(ArtistCreditEmbeddable.withName("Single Artist")));
            minimalTrack.setSources(List.of(Source.of("MANUAL", "manual-001")));

            TrackEntity complexTrack = createTrackEntity("FRXYZ2222222", "Complex", TrackStatus.VERIFIED);
            complexTrack.setCredits(List.of(
                ArtistCreditEmbeddable.withName("Artist A"),
                ArtistCreditEmbeddable.withName("Artist B"), 
                ArtistCreditEmbeddable.withName("Artist C")
            ));
            complexTrack.setSources(List.of(
                Source.of("SPOTIFY", "spot-123"),
                Source.of("APPLE_MUSIC", "apple-456"),
                Source.of("TIDAL", "tidal-789")
            ));

            List<TrackEntity> trackEntities = List.of(minimalTrack, complexTrack);

            // When
            List<TrackInfo> result = TrackInfoMapper.toDtoList(trackEntities);

            // Then - AssertJ complex validation
            assertThat(result)
                .hasSize(2)
                .satisfies(tracks -> {
                    // First track (minimal)
                    assertThat(tracks.get(0))
                        .satisfies(track -> {
                            assertThat(track.artistNames()).hasSize(1);
                            assertThat(track.sources()).hasSize(1);
                        });
                    
                    // Second track (complex)
                    assertThat(tracks.get(1))
                        .satisfies(track -> {
                            assertThat(track.artistNames()).hasSize(3);
                            assertThat(track.sources()).hasSize(3);
                        });
                });
        }

        @Test
        @DisplayName("Should preserve order in list mapping")
        void shouldPreserveOrderInListMapping() {
            // Given - Multiple tracks in specific order
            TrackEntity first = createTrackEntity("FRXYZ0000001", "First Track", TrackStatus.PROVISIONAL);
            TrackEntity second = createTrackEntity("FRXYZ0000002", "Second Track", TrackStatus.VERIFIED);
            TrackEntity third = createTrackEntity("FRXYZ0000003", "Third Track", TrackStatus.PROVISIONAL);
            
            List<TrackEntity> orderedEntities = List.of(first, second, third);

            // When
            List<TrackInfo> result = TrackInfoMapper.toDtoList(orderedEntities);

            // Then - AssertJ order validation
            assertThat(result)
                .hasSize(3)
                .extracting(TrackInfo::title)
                .containsExactly("First Track", "Second Track", "Third Track");
                
            assertThat(result)
                .extracting(TrackInfo::isrc)
                .extracting(ISRC::value)
                .containsExactly("FRXYZ0000001", "FRXYZ0000002", "FRXYZ0000003");
        }
    }

    @Nested
    @DisplayName("Edge Cases and Data Integrity")
    class EdgeCasesTests {

        @Test
        @DisplayName("Should handle tracks with special characters in data")
        void shouldHandleTracksWithSpecialCharacters() {
            // Given
            TrackEntity trackEntity = createValidTrackEntity();
            trackEntity.setTitle("Café Müller & Sons™");
            trackEntity.setCredits(List.of(
                ArtistCreditEmbeddable.withName("Björk"),
                ArtistCreditEmbeddable.withName("François & 中文艺术家")
            ));

            // When
            TrackInfo result = TrackInfoMapper.toDto(trackEntity);

            // Then - AssertJ special character handling
            assertThat(result.title()).isEqualTo("Café Müller & Sons™");
            assertThat(result.artistNames())
                .containsExactly("Björk", "François & 中文艺术家");
        }

        @Test
        @DisplayName("Should handle edge case timestamps")
        void shouldHandleEdgeCaseTimestamps() {
            // Given
            TrackEntity trackEntity = createValidTrackEntity();
            LocalDateTime edgeDate = LocalDateTime.of(1999, 12, 31, 23, 59, 59);
            trackEntity.setCreatedAt(edgeDate);

            // When
            TrackInfo result = TrackInfoMapper.toDto(trackEntity);

            // Then - AssertJ date validation
            assertThat(result.submissionDate()).isEqualTo(edgeDate);
        }

        @Test
        @DisplayName("Should validate all fields are properly mapped")
        void shouldValidateAllFieldsAreProperlyMapped() {
            // Given
            TrackEntity trackEntity = createValidTrackEntity();
            LocalDateTime specificTime = LocalDateTime.of(2024, 6, 15, 14, 30, 45);
            trackEntity.setCreatedAt(specificTime);

            // When
            TrackInfo result = TrackInfoMapper.toDto(trackEntity);

            // Then - AssertJ comprehensive validation
            assertThat(result)
                .isNotNull()
                .extracting(
                    TrackInfo::isrc,
                    TrackInfo::title,
                    TrackInfo::status,
                    TrackInfo::submissionDate
                )
                .containsExactly(
                    ISRC.of("FRXYZ1234567"),
                    "Test Track",
                    TrackStatus.PROVISIONAL,
                    specificTime
                );

            assertThat(result.artistNames()).isNotEmpty();
            assertThat(result.sources()).isNotEmpty();
        }
    }

    // Helper methods for creating test data
    private TrackEntity createValidTrackEntity() {
        return createTrackEntity("FRXYZ1234567", "Test Track", TrackStatus.PROVISIONAL);
    }

    private TrackEntity createTrackEntity(String isrc, String title, TrackStatus status) {
        TrackEntity track = new TrackEntity();
        track.setIsrc(isrc);
        track.setTitle(title);
        track.setStatus(status.name());
        track.setCredits(List.of(
            ArtistCreditEmbeddable.withName("Artist 1"),
            ArtistCreditEmbeddable.withName("Artist 2")
        ));
        track.setSources(List.of(
                Source.of("SPOTIFY", "spotify-track-123"),
                Source.of("TIDAL", "tidal-track-456")
        ));
        track.setCreatedAt(LocalDateTime.of(2024, 1, 1, 12, 0, 0));
        return track;
    }
}
