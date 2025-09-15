package com.musichub.artist.adapter.persistence.adapter;

import com.musichub.artist.adapter.persistence.config.PersistenceTestProfile;
import com.musichub.artist.domain.model.Artist;
import com.musichub.artist.domain.model.ArtistStatus;
import com.musichub.artist.domain.values.ArtistName;
import com.musichub.artist.domain.values.Contribution;
import com.musichub.shared.domain.id.ArtistId;
import com.musichub.shared.domain.id.TrackId;
import com.musichub.shared.domain.values.ISRC;
import com.musichub.shared.domain.values.Source;
import com.musichub.shared.domain.values.SourceType;
import io.quarkus.test.TestTransaction;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import jakarta.inject.Inject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

@QuarkusTest
@DisplayName("ArtistRepositoryAdapter Integration Tests")
@TestProfile(PersistenceTestProfile.class)
class ArtistRepositoryAdapterTest {

    @Inject
    ArtistRepositoryAdapter repository;

    // =================================
    // PERSISTENCE AND RETRIEVAL TESTS
    // =================================

    @Test
    @TestTransaction
    @DisplayName("Should persist and retrieve artist with complete data including contributions and sources")
    void shouldPersistAndRetrieveArtistWithCompleteData() {
        // Given
        var originalArtist = createCompleteTestArtist();

        // When - Save artist
        var savedArtist = repository.save(originalArtist);

        // Then - Verify save operation
        assertThat(savedArtist)
                .isNotNull()
                .satisfies(artist -> {
                    assertThat(artist.getId()).isEqualTo(originalArtist.getId());
                    assertThat(artist.getNameValue()).isEqualTo("The Beatles");
                    assertThat(artist.getStatus()).isEqualTo(ArtistStatus.PROVISIONAL);
                    assertThat(artist.getContributions()).hasSize(2);
                    assertThat(artist.getSources()).hasSize(2);
                });

        // When - Find by name
        var foundByName = repository.findByName("The Beatles");

        // Then - Verify retrieval by name
        assertThat(foundByName)
                .isPresent()
                .get()
                .satisfies(artist -> {
                    assertThat(artist.getId()).isEqualTo(originalArtist.getId());
                    assertThat(artist.getNameValue()).isEqualTo("The Beatles");
                    assertThat(artist.getStatus()).isEqualTo(ArtistStatus.PROVISIONAL);
                });

        // Verify contributions are properly preserved
        assertThat(foundByName.get().getContributions())
                .hasSize(2)
                .extracting(Contribution::title, c -> c.isrc().value())
                .containsExactlyInAnyOrder(
                        tuple("Hey Jude", "GB-EMI-71-00001"),
                        tuple("Let It Be", "GB-EMI-70-00002")
                );

        // Verify sources are properly preserved
        assertThat(foundByName.get().getSources())
                .hasSize(2)
                .extracting(Source::sourceType, Source::sourceId)
                .containsExactlyInAnyOrder(
                        tuple(SourceType.SPOTIFY, "spotify:artist:3WrFJ7ztbogyGnTHbHJFl2"),
                        tuple(SourceType.TIDAL, "tidal:artist:7804")
                );
    }

    @Test
    @TestTransaction
    @DisplayName("Should handle artists with minimal data (no contributions or sources)")
    void shouldHandleArtistsWithMinimalData() {
        // Given
        var minimalArtist = Artist.createProvisional("Solo Artist");

        // When
        var savedArtist = repository.save(minimalArtist);

        // Then
        assertThat(savedArtist)
                .satisfies(artist -> {
                    assertThat(artist.getNameValue()).isEqualTo("Solo Artist");
                    assertThat(artist.getStatus()).isEqualTo(ArtistStatus.PROVISIONAL);
                    assertThat(artist.getContributions()).isEmpty();
                    assertThat(artist.getSources()).isEmpty();
                });

        // Verify persistence
        var retrieved = repository.findByName("Solo Artist");
        assertThat(retrieved)
                .isPresent()
                .get()
                .satisfies(artist -> {
                    assertThat(artist.getContributions()).isEmpty();
                    assertThat(artist.getSources()).isEmpty();
                });
    }

    @Test
    @TestTransaction
    @DisplayName("Should handle artists with special characters in names and track titles")
    void shouldHandleSpecialCharactersInNamesAndTitles() {
        // Given
        var specialArtistName = "Björk & Sigur Rós (Íslenska)";
        var specialTrackTitle = "Hoppípolla – [The Rains of Castamere] (2023 Remaster)";

        var artist = Artist.createProvisional(specialArtistName);
        var contribution = Contribution.of(
                new TrackId(UUID.randomUUID()),
                specialTrackTitle,
                ISRC.of("IS-Z03-20-00001")
        );
        var enrichedArtist = artist.addContribution(contribution);

        // When
        repository.save(enrichedArtist);

        // Then
        var foundArtist = repository.findByName(specialArtistName);
        assertThat(foundArtist)
                .isPresent()
                .get()
                .satisfies(a -> {
                    assertThat(a.getNameValue()).isEqualTo(specialArtistName);
                    assertThat(a.getContributions())
                            .first()
                            .extracting(Contribution::title)
                            .isEqualTo(specialTrackTitle);
                });
    }

    // ======================
    // FIND OPERATIONS TESTS
    // ======================

    @Test
    @TestTransaction
    @DisplayName("Should find artist by ID with complete data")
    void shouldFindArtistByIdWithCompleteData() {
        // Given
        var artist = createCompleteTestArtist();
        var savedArtist = repository.save(artist);

        // When
        var foundArtist = repository.findById(savedArtist.getId());

        // Then
        assertThat(foundArtist)
                .isPresent()
                .get()
                .satisfies(a -> {
                    assertThat(a.getId()).isEqualTo(savedArtist.getId());
                    assertThat(a.getNameValue()).isEqualTo("The Beatles");
                    assertThat(a.getContributions()).hasSize(2);
                    assertThat(a.getSources()).hasSize(2);
                });
    }

    @Test
    @TestTransaction
    @DisplayName("Should find artist by source across multiple platforms")
    void shouldFindArtistBySourceAcrossMultiplePlatforms() {
        // Given - Artist with multiple sources
        var artist = Artist.createProvisional("Multi-Platform Artist");
        var spotifySource = Source.of("SPOTIFY", "spotify:artist:123");
        var tidalSource = Source.of("TIDAL", "tidal:artist:456");
        var deezerSource = Source.of("DEEZER", "deezer:artist:789");

        var enrichedArtist = artist
                .addSource(spotifySource)
                .addSource(tidalSource)
                .addSource(deezerSource);

        repository.save(enrichedArtist);

        // When/Then - Should find by any source
        var foundBySpotify = repository.findBySource(SourceType.SPOTIFY, "spotify:artist:123");
        var foundByTidal = repository.findBySource(SourceType.TIDAL, "tidal:artist:456");
        var foundByDeezer = repository.findBySource(SourceType.DEEZER, "deezer:artist:789");

        // All should find the same artist
        assertThat(foundBySpotify).isPresent();
        assertThat(foundByTidal).isPresent();
        assertThat(foundByDeezer).isPresent();

        var artistIds = List.of(
                foundBySpotify.get().getId(),
                foundByTidal.get().getId(),
                foundByDeezer.get().getId()
        );

        assertThat(artistIds)
                .hasSize(3)
                .allSatisfy(id -> assertThat(id).isEqualTo(enrichedArtist.getId()));
    }

    @Test
    @TestTransaction
    @DisplayName("Should return empty results for non-existent entities")
    void shouldReturnEmptyResultsForNonExistentEntities() {
        // When/Then - Non-existent name
        assertThat(repository.findByName("Non-existent Artist"))
                .isEmpty();

        // When/Then - Non-existent ID
        assertThat(repository.findById(new ArtistId(UUID.randomUUID())))
                .isEmpty();

        // When/Then - Non-existent source
        assertThat(repository.findBySource(SourceType.SPOTIFY, "non-existent-source-id"))
                .isEmpty();
    }

    @Test
    @TestTransaction
    @DisplayName("Should handle case-sensitive name searches correctly")
    void shouldHandleCaseSensitiveNameSearches() {
        // Given
        repository.save(Artist.createProvisional("The Beatles"));

        // When/Then - Exact match should work
        assertThat(repository.findByName("The Beatles"))
                .isPresent();

        // When/Then - Different case should not match (assuming case-sensitive DB)
        assertThat(repository.findByName("the beatles"))
                .isEmpty();

        assertThat(repository.findByName("THE BEATLES"))
                .isEmpty();
    }

    // =======================
    // UPDATE OPERATIONS TESTS
    // =======================

    @Test
    @TestTransaction
    @DisplayName("Should update existing artist properties while preserving ID")
    void shouldUpdateExistingArtistPropertiesWhilePreservingId() {
        // Given
        var originalArtist = Artist.createProvisional("Original Name");
        var savedArtist = repository.save(originalArtist);

        // When - Update artist details
        var updatedArtist = Artist.from(
                savedArtist.getId(),
                ArtistName.of("Updated Name"),
                ArtistStatus.VERIFIED,
                List.of(),
                List.of()
        );
        var resultArtist = repository.save(updatedArtist);

        // Then - Verify update results
        assertThat(resultArtist)
                .satisfies(artist -> {
                    assertThat(artist.getId()).isEqualTo(savedArtist.getId());
                    assertThat(artist.getNameValue()).isEqualTo("Updated Name");
                    assertThat(artist.getStatus()).isEqualTo(ArtistStatus.VERIFIED);
                });

        // Verify persistence of changes
        var persistedArtist = repository.findById(savedArtist.getId());
        assertThat(persistedArtist)
                .isPresent()
                .get()
                .satisfies(artist -> {
                    assertThat(artist.getNameValue()).isEqualTo("Updated Name");
                    assertThat(artist.getStatus()).isEqualTo(ArtistStatus.VERIFIED);
                });
    }

    @Test
    @TestTransaction
    @DisplayName("Should update artist contributions and sources dynamically")
    void shouldUpdateArtistContributionsAndSourcesDynamically() {
        // Given - Artist with initial data
        var artist = createCompleteTestArtist();
        var savedArtist = repository.save(artist);

        // When - Add more contributions and sources
        var newContribution = Contribution.of(
                new TrackId(UUID.randomUUID()),
                "Come Together",
                ISRC.of("GB-EMI-69-00003")
        );
        var newSource = Source.of("APPLE_MUSIC", "apple:artist:136975");

        var updatedArtist = savedArtist
                .addContribution(newContribution)
                .addSource(newSource);

        var resultArtist = repository.save(updatedArtist);

        // Then - Verify additions
        assertThat(resultArtist.getContributions()).hasSize(3);
        assertThat(resultArtist.getSources()).hasSize(3);

        // Verify specific new additions
        assertThat(resultArtist.getContributions())
                .extracting(Contribution::title)
                .contains("Come Together");

        assertThat(resultArtist.getSources())
                .extracting(Source::sourceType)
                .contains(SourceType.APPLE_MUSIC);
    }

    @Test
    @TestTransaction
    @DisplayName("Should handle multiple consecutive updates correctly")
    void shouldHandleMultipleConsecutiveUpdatesCorrectly() {
        // Given
        var artist = Artist.createProvisional("Initial Name");
        var saved1 = repository.save(artist);

        // When - Multiple updates
        var updated1 = Artist.from(saved1.getId(), ArtistName.of("First Update"),
                ArtistStatus.PROVISIONAL, List.of(), List.of());
        var saved2 = repository.save(updated1);

        var updated2 = Artist.from(saved2.getId(), ArtistName.of("Second Update"),
                ArtistStatus.VERIFIED, List.of(), List.of());
        var saved3 = repository.save(updated2);

        var updated3 = Artist.from(saved3.getId(), ArtistName.of("Final Update"),
                ArtistStatus.VERIFIED, List.of(), List.of());
        var finalResult = repository.save(updated3);

        // Then - Verify final state
        assertThat(finalResult)
                .satisfies(a -> {
                    assertThat(a.getId()).isEqualTo(artist.getId()); // ID should remain constant
                    assertThat(a.getNameValue()).isEqualTo("Final Update");
                    assertThat(a.getStatus()).isEqualTo(ArtistStatus.VERIFIED);
                });

        // Verify in database
        var retrieved = repository.findById(artist.getId());
        assertThat(retrieved)
                .isPresent()
                .get()
                .satisfies(a -> {
                    assertThat(a.getNameValue()).isEqualTo("Final Update");
                    assertThat(a.getStatus()).isEqualTo(ArtistStatus.VERIFIED);
                });
    }

    // ================================
    // DATA INTEGRITY AND EDGE CASES
    // ================================

    @Test
    @TestTransaction
    @DisplayName("Should maintain data integrity through multiple operations")
    void shouldMaintainDataIntegrityThroughMultipleOperations() {
        // Given - Create multiple artists
        var artist1 = createTestArtistWithName("Artist One");
        var artist2 = createTestArtistWithName("Artist Two");
        var artist3 = createTestArtistWithName("Artist Three");

        // When - Save all artists
        var saved1 = repository.save(artist1);
        var saved2 = repository.save(artist2);
        var saved3 = repository.save(artist3);

        // Then - All should have unique IDs
        var allIds = List.of(saved1.getId(), saved2.getId(), saved3.getId());
        assertThat(allIds)
                .hasSize(3)
                .doesNotHaveDuplicates();

        // All should be retrievable by their names
        assertThat(repository.findByName("Artist One")).isPresent();
        assertThat(repository.findByName("Artist Two")).isPresent();
        assertThat(repository.findByName("Artist Three")).isPresent();

        // All should be retrievable by their IDs
        assertThat(repository.findById(saved1.getId())).isPresent();
        assertThat(repository.findById(saved2.getId())).isPresent();
        assertThat(repository.findById(saved3.getId())).isPresent();
    }

    @Test
    @TestTransaction
    @DisplayName("Should handle large collections of contributions and sources")
    void shouldHandleLargeCollectionsOfContributionsAndSources() {
        // Given - Artist with many contributions and sources
        var artist = createArtistWithManyRelations();

        // When
        var savedArtist = repository.save(artist);

        // Then
        assertThat(savedArtist.getContributions()).hasSize(10);
        assertThat(savedArtist.getSources()).hasSize(4);

        // Verify retrieval
        var retrieved = repository.findById(savedArtist.getId());
        assertThat(retrieved)
                .isPresent()
                .get()
                .satisfies(a -> {
                    assertThat(a.getContributions()).hasSize(10);
                    assertThat(a.getSources()).hasSize(4);

                    // Verify all contributions are unique
                    assertThat(a.getContributions())
                            .extracting(Contribution::title)
                            .hasSize(10)
                            .doesNotHaveDuplicates();

                    // Verify all source types are present
                    assertThat(a.getSources())
                            .extracting(Source::sourceType)
                            .containsExactlyInAnyOrder(
                                    SourceType.SPOTIFY, SourceType.TIDAL,
                                    SourceType.DEEZER, SourceType.APPLE_MUSIC
                            );
                });
    }

    @Test
    @TestTransaction
    @DisplayName("Should handle duplicate sources correctly during updates")
    void shouldHandleDuplicateSourcesCorrectlyDuringUpdates() {
        // Given - Artist with a source
        var artist = Artist.createProvisional("Test Artist");
        var source = Source.of("SPOTIFY", "spotify:test:123");
        var enrichedArtist = artist.addSource(source);
        var savedArtist = repository.save(enrichedArtist);

        // When - Try to add the same source again
        var duplicateSource = Source.of("SPOTIFY", "spotify:test:123");
        var updatedArtist = savedArtist.addSource(duplicateSource);
        var resultArtist = repository.save(updatedArtist);

        // Then - Should not have duplicate sources
        assertThat(resultArtist.getSources())
                .hasSize(1) // Should still be 1, not 2
                .first()
                .satisfies(src -> {
                    assertThat(src.sourceType()).isEqualTo(SourceType.SPOTIFY);
                    assertThat(src.sourceId()).isEqualTo("spotify:test:123");
                });
    }

    // ===============
    // HELPER METHODS
    // ===============

    private Artist createCompleteTestArtist() {
        var artist = Artist.createProvisional("The Beatles");

        var contribution1 = Contribution.of(
                new TrackId(UUID.randomUUID()),
                "Hey Jude",
                ISRC.of("GB-EMI-71-00001")
        );

        var contribution2 = Contribution.of(
                new TrackId(UUID.randomUUID()),
                "Let It Be",
                ISRC.of("GB-EMI-70-00002")
        );

        var spotifySource = Source.of("SPOTIFY", "spotify:artist:3WrFJ7ztbogyGnTHbHJFl2");
        var tidalSource = Source.of("TIDAL", "tidal:artist:7804");

        return artist
                .addContribution(contribution1)
                .addContribution(contribution2)
                .addSource(spotifySource)
                .addSource(tidalSource);
    }

    private Artist createTestArtistWithName(String name) {
        var artist = Artist.createProvisional(name);
        var contribution = Contribution.of(
                new TrackId(UUID.randomUUID()),
                "Sample Track for " + name,
                ISRC.of("US-S1Z-99-" + String.format("%05d", Math.abs(name.hashCode()) % 100000))
        );
        var source = Source.of("SPOTIFY", "spotify:artist:" + Math.abs(name.hashCode()));

        return artist
                .addContribution(contribution)
                .addSource(source);
    }

    private Artist createArtistWithManyRelations() {
        var artist = Artist.createProvisional("Artist With Many Relations");

        // Add 10 contributions
        for (int i = 1; i <= 10; i++) {
            var contribution = Contribution.of(
                    new TrackId(UUID.randomUUID()),
                    "Track " + i,
                    ISRC.of("US-S1Z-99-" + String.format("%05d", i))
            );
            artist = artist.addContribution(contribution);
        }

        // Add sources for all major platforms
        var sources = List.of(
                Source.of("SPOTIFY", "spotify:artist:many1"),
                Source.of("TIDAL", "tidal:artist:many2"),
                Source.of("DEEZER", "deezer:artist:many3"),
                Source.of("APPLE_MUSIC", "apple:artist:many4")
        );

        for (var source : sources) {
            artist = artist.addSource(source);
        }

        return artist;
    }
}