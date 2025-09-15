package com.musichub.artist.adapter.persistence.mapper;

import com.musichub.artist.adapter.persistence.entity.ArtistEntity;
import com.musichub.artist.adapter.persistence.entity.ContributionEntity;
import com.musichub.artist.adapter.persistence.entity.SourceEntity;
import com.musichub.artist.domain.model.Artist;
import com.musichub.artist.domain.model.ArtistStatus;
import com.musichub.artist.domain.values.ArtistName;
import com.musichub.artist.domain.values.Contribution;
import com.musichub.shared.domain.id.ArtistId;
import com.musichub.shared.domain.id.TrackId;
import com.musichub.shared.domain.values.ISRC;
import com.musichub.shared.domain.values.Source;
import com.musichub.shared.domain.values.SourceType;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

@DisplayName("ArtistMapper Tests")
class ArtistMapperTest {

    private ArtistMapper mapper;

    @BeforeEach
    void setUp() {
        // Utiliser l'implémentation générée par MapStruct
        mapper = new ArtistMapperImpl();
    }

    @Nested
    @DisplayName("Domain to Entity Mapping")
    class DomainToEntityMapping {

        @Test
        @DisplayName("Should map complete Artist domain to ArtistEntity with all properties")
        void shouldMapCompleteArtistToEntity() {
            // Given
            var artistId = new ArtistId(UUID.randomUUID());
            var artistName = ArtistName.of("The Beatles");
            var trackId = new TrackId(UUID.randomUUID());
            var isrc = ISRC.of("US-S1Z-99-00001");
            var contribution = Contribution.of(trackId, "Hey Jude", isrc);
            var source = Source.of("SPOTIFY", "spotify:artist:3WrFJ7ztbogyGnTHbHJFl2");

            var artist = Artist.from(artistId, artistName, ArtistStatus.VERIFIED, 
                                   List.of(contribution), List.of(source));

            // When
            var entity = mapper.toDbo(artist);

            // Then
            assertThat(entity)
                .isNotNull()
                .satisfies(e -> {
                    assertThat(e.id).isEqualTo(artistId.value());
                    assertThat(e.name).isEqualTo("The Beatles");
                    assertThat(e.status).isEqualTo(ArtistStatus.VERIFIED);
                });

            assertThat(entity.contributions)
                .hasSize(1)
                .first()
                .satisfies(contrib -> {
                    assertThat(contrib.trackId).isEqualTo(trackId.value());
                    assertThat(contrib.title).isEqualTo("Hey Jude");
                    assertThat(contrib.isrc).isEqualTo("US-S1Z-99-00001");
                });

            assertThat(entity.sources)
                .hasSize(1)
                .first()
                .satisfies(src -> {
                    assertThat(src.sourceType).isEqualTo(SourceType.SPOTIFY);
                    assertThat(src.sourceId).isEqualTo("spotify:artist:3WrFJ7ztbogyGnTHbHJFl2");
                });
        }

        @Test
        @DisplayName("Should map Artist with empty collections")
        void shouldMapArtistWithEmptyCollections() {
            // Given
            var artist = Artist.from(
                new ArtistId(UUID.randomUUID()),
                ArtistName.of("Solo Artist"),
                ArtistStatus.PROVISIONAL,
                List.of(),
                List.of()
            );

            // When
            var entity = mapper.toDbo(artist);

            // Then
            assertThat(entity)
                .isNotNull()
                .satisfies(e -> {
                    assertThat(e.contributions).isEmpty();
                    assertThat(e.sources).isEmpty();
                    assertThat(e.name).isEqualTo("Solo Artist");
                    assertThat(e.status).isEqualTo(ArtistStatus.PROVISIONAL);
                });
        }

        @Test
        @DisplayName("Should handle null Artist gracefully")
        void shouldHandleNullArtist() {
            // When/Then
            assertThat(mapper.toDbo(null)).isNull();
        }
    }

    @Nested
    @DisplayName("Entity to Domain Mapping")
    class EntityToDomainMapping {

        @Test
        @DisplayName("Should map complete ArtistEntity to Artist domain with all properties")
        void shouldMapCompleteEntityToArtist() {
            // Given
            var entity = createCompleteArtistEntity();

            // When
            var artist = mapper.toDomain(entity);

            // Then
            assertThat(artist)
                .isNotNull()
                .satisfies(a -> {
                    assertThat(a.getId().value()).isEqualTo(entity.id);
                    assertThat(a.getNameValue()).isEqualTo("Queen");
                    assertThat(a.getStatus()).isEqualTo(ArtistStatus.VERIFIED);
                });

            assertThat(artist.getContributions())
                .hasSize(2)
                .extracting(Contribution::title, c -> c.isrc().value())
                .containsExactlyInAnyOrder(
                    tuple("Bohemian Rhapsody", "GB-UM7-15-00362"),
                    tuple("We Will Rock You", "GB-UM7-77-00385")
                );

            assertThat(artist.getSources())
                .hasSize(2)
                .extracting(Source::sourceType, Source::sourceId)
                .containsExactlyInAnyOrder(
                    tuple(SourceType.SPOTIFY, "spotify:artist:1dfeR4HaWDbWqFHLkxsg1d"),
                    tuple(SourceType.TIDAL, "tidal:artist:7804")
                );
        }

        @Test
        @DisplayName("Should map ArtistEntity with empty collections")
        void shouldMapEntityWithEmptyCollections() {
            // Given
            var entity = createMinimalArtistEntity();

            // When
            var artist = mapper.toDomain(entity);

            // Then
            assertThat(artist)
                .isNotNull()
                .satisfies(a -> {
                    assertThat(a.getContributions()).isEmpty();
                    assertThat(a.getSources()).isEmpty();
                    assertThat(a.getNameValue()).isEqualTo("Minimal Artist");
                    assertThat(a.getStatus()).isEqualTo(ArtistStatus.PROVISIONAL);
                });
        }

        @Test
        @DisplayName("Should handle null ArtistEntity gracefully")
        void shouldHandleNullEntity() {
            // When/Then
            assertThat(mapper.toDomain(null)).isNull();
        }
    }

    @Nested
    @DisplayName("Contribution Mapping")
    class ContributionMapping {

        @Test
        @DisplayName("Should bidirectionally map Contribution objects correctly")
        void shouldBidirectionallyMapContributions() {
            // Given
            var trackId = new TrackId(UUID.randomUUID());
            var isrc = ISRC.of("FR-Z03-19-00001");
            var originalContribution = Contribution.of(trackId, "Daft Punk - Get Lucky", isrc);

            // When - map to entity and back to domain
            var contributionEntity = mapper.mapContributionToEntity(originalContribution);
            var mappedBackContribution = mapper.mapEntityToContribution(contributionEntity);

            // Then
            assertThat(contributionEntity)
                .isNotNull()
                .satisfies(entity -> {
                    assertThat(entity.trackId).isEqualTo(trackId.value());
                    assertThat(entity.title).isEqualTo("Daft Punk - Get Lucky");
                    assertThat(entity.isrc).isEqualTo("FR-Z03-19-00001");
                });

            assertThat(mappedBackContribution)
                .isNotNull()
                .isEqualTo(originalContribution);
        }

        @Test
        @DisplayName("Should handle null Contribution objects")
        void shouldHandleNullContributions() {
            // When/Then
            assertThat(mapper.mapContributionToEntity(null)).isNull();
            assertThat(mapper.mapEntityToContribution(null)).isNull();
        }
    }

    @Nested
    @DisplayName("Source Mapping")
    class SourceMapping {

        @Test
        @DisplayName("Should bidirectionally map Source objects for all platforms")
        void shouldBidirectionallyMapSources() {
            // Given - Test multiple source types
            var spotifySource = Source.of("SPOTIFY", "spotify:artist:4V8Sr092TqfHkfAA5fXXqG");
            var tidalSource = Source.of("TIDAL", "tidal:artist:123456");
            var deezerSource = Source.of("DEEZER", "deezer:artist:789012");

            var sources = List.of(spotifySource, tidalSource, deezerSource);

            // When/Then - Test each source type
            sources.forEach(originalSource -> {
                var sourceEntity = mapper.mapSourceToEntity(originalSource);
                var mappedBackSource = mapper.mapEntityToSource(sourceEntity);

                assertThat(sourceEntity)
                    .satisfies(entity -> {
                        assertThat(entity.sourceType).isEqualTo(originalSource.sourceType());
                        assertThat(entity.sourceId).isEqualTo(originalSource.sourceId());
                    });

                assertThat(mappedBackSource)
                    .isEqualTo(originalSource);
            });
        }

        @Test
        @DisplayName("Should handle null Source objects")
        void shouldHandleNullSources() {
            // When/Then
            assertThat(mapper.mapSourceToEntity(null)).isNull();
            assertThat(mapper.mapEntityToSource(null)).isNull();
        }
    }

    @Nested
    @DisplayName("Roundtrip Mapping")
    class RoundtripMapping {

        @Test
        @DisplayName("Should preserve all data through complete roundtrip mapping")
        void shouldPreserveDataThroughRoundtripMapping() {
            // Given
            var originalArtist = createCompleteArtist();

            // When
            var entity = mapper.toDbo(originalArtist);
            var roundtripArtist = mapper.toDomain(entity);

            // Then - Verify complete data preservation
            assertThat(roundtripArtist)
                .usingRecursiveComparison()
                .isEqualTo(originalArtist);

            // Additional detailed verification
            assertThat(roundtripArtist.getContributions())
                .hasSize(originalArtist.getContributions().size())
                .containsExactlyInAnyOrderElementsOf(originalArtist.getContributions());

            assertThat(roundtripArtist.getSources())
                .hasSize(originalArtist.getSources().size())
                .containsExactlyInAnyOrderElementsOf(originalArtist.getSources());
        }

        @Test
        @DisplayName("Should maintain data integrity across multiple roundtrips")
        void shouldMaintainDataIntegrityAcrossMultipleRoundtrips() {
            // Given
            var originalArtist = createCompleteArtist();

            // When - Multiple roundtrips
            var firstRoundtrip = mapper.toDomain(mapper.toDbo(originalArtist));
            var secondRoundtrip = mapper.toDomain(mapper.toDbo(firstRoundtrip));
            var thirdRoundtrip = mapper.toDomain(mapper.toDbo(secondRoundtrip));

            // Then - All should be identical
            assertThat(firstRoundtrip)
                .usingRecursiveComparison()
                .isEqualTo(originalArtist);

            assertThat(secondRoundtrip)
                .usingRecursiveComparison()
                .isEqualTo(originalArtist);

            assertThat(thirdRoundtrip)
                .usingRecursiveComparison()
                .isEqualTo(originalArtist);
        }
    }

    @Nested
    @DisplayName("Edge Cases and Validation")
    class EdgeCasesAndValidation {

        @Test
        @DisplayName("Should handle artists with maximum allowed data")
        void shouldHandleArtistsWithMaximumData() {
            // Given - Artist with many contributions and sources
            var artist = createArtistWithManyRelations();

            // When
            var entity = mapper.toDbo(artist);
            var mappedBack = mapper.toDomain(entity);

            // Then
            assertThat(entity.contributions).hasSize(5);
            assertThat(entity.sources).hasSize(4);
            assertThat(mappedBack.getContributions()).hasSize(5);
            assertThat(mappedBack.getSources()).hasSize(4);

            // Verify all relations are preserved
            assertThat(mappedBack.getContributions())
                .extracting(Contribution::title)
                .containsExactlyInAnyOrder(
                    "Track 1", "Track 2", "Track 3", "Track 4", "Track 5"
                );

            assertThat(mappedBack.getSources())
                .extracting(Source::sourceType)
                .containsExactlyInAnyOrder(
                    SourceType.SPOTIFY, SourceType.TIDAL, SourceType.DEEZER, SourceType.APPLE_MUSIC
                );
        }

        @Test
        @DisplayName("Should handle special characters in artist names and track titles")
        void shouldHandleSpecialCharacters() {
            // Given
            var specialName = "Björk & Sigur Rós (Íslenska)";
            var specialTitle = "Hoppípolla – [The Rains of Castamere] (2023 Remaster)";
            
            var artist = Artist.from(
                new ArtistId(UUID.randomUUID()),
                ArtistName.of(specialName),
                ArtistStatus.VERIFIED,
                List.of(Contribution.of(
                    new TrackId(UUID.randomUUID()),
                    specialTitle,
                    ISRC.of("IS-Z03-20-00001")
                )),
                List.of()
            );

            // When
            var entity = mapper.toDbo(artist);
            var mappedBack = mapper.toDomain(entity);

            // Then
            assertThat(mappedBack.getNameValue()).isEqualTo(specialName);
            assertThat(mappedBack.getContributions())
                .first()
                .extracting(Contribution::title)
                .isEqualTo(specialTitle);
        }
    }

    // Helper methods
    private ArtistEntity createCompleteArtistEntity() {
        var entity = new ArtistEntity();
        entity.id = UUID.randomUUID();
        entity.name = "Queen";
        entity.status = ArtistStatus.VERIFIED;

        entity.contributions = List.of(
            createContributionEntity(UUID.randomUUID(), "Bohemian Rhapsody", "GB-UM7-15-00362"),
            createContributionEntity(UUID.randomUUID(), "We Will Rock You", "GB-UM7-77-00385")
        );

        entity.sources = List.of(
            createSourceEntity(SourceType.SPOTIFY, "spotify:artist:1dfeR4HaWDbWqFHLkxsg1d"),
            createSourceEntity(SourceType.TIDAL, "tidal:artist:7804")
        );

        return entity;
    }

    private ArtistEntity createMinimalArtistEntity() {
        var entity = new ArtistEntity();
        entity.id = UUID.randomUUID();
        entity.name = "Minimal Artist";
        entity.status = ArtistStatus.PROVISIONAL;
        entity.contributions = List.of();
        entity.sources = List.of();
        return entity;
    }

    private Artist createCompleteArtist() {
        var trackId1 = new TrackId(UUID.randomUUID());
        var trackId2 = new TrackId(UUID.randomUUID());
        
        return Artist.from(
            new ArtistId(UUID.randomUUID()),
            ArtistName.of("Complete Test Artist"),
            ArtistStatus.VERIFIED,
            List.of(
                Contribution.of(trackId1, "First Track", ISRC.of("US-S1Z-99-00001")),
                Contribution.of(trackId2, "Second Track", ISRC.of("US-S1Z-99-00002"))
            ),
            List.of(
                Source.of("SPOTIFY", "spotify:123"),
                Source.of("TIDAL", "tidal:456")
            )
        );
    }

    private Artist createArtistWithManyRelations() {
        var contributions = List.of(
            Contribution.of(new TrackId(UUID.randomUUID()), "Track 1", ISRC.of("US-S1Z-99-00001")),
            Contribution.of(new TrackId(UUID.randomUUID()), "Track 2", ISRC.of("US-S1Z-99-00002")),
            Contribution.of(new TrackId(UUID.randomUUID()), "Track 3", ISRC.of("US-S1Z-99-00003")),
            Contribution.of(new TrackId(UUID.randomUUID()), "Track 4", ISRC.of("US-S1Z-99-00004")),
            Contribution.of(new TrackId(UUID.randomUUID()), "Track 5", ISRC.of("US-S1Z-99-00005"))
        );

        var sources = List.of(
            Source.of("SPOTIFY", "spotify:artist:1"),
            Source.of("TIDAL", "tidal:artist:2"),
            Source.of("DEEZER", "deezer:artist:3"),
            Source.of("APPLE_MUSIC", "apple:artist:4")
        );

        return Artist.from(
            new ArtistId(UUID.randomUUID()),
            ArtistName.of("Artist With Many Relations"),
            ArtistStatus.VERIFIED,
            contributions,
            sources
        );
    }

    private ContributionEntity createContributionEntity(UUID trackId, String title, String isrc) {
        var entity = new ContributionEntity();
        entity.trackId = trackId;
        entity.title = title;
        entity.isrc = isrc;
        return entity;
    }

    private SourceEntity createSourceEntity(SourceType sourceType, String sourceId) {
        var entity = new SourceEntity();
        entity.sourceType = sourceType;
        entity.sourceId = sourceId;
        return entity;
    }
}