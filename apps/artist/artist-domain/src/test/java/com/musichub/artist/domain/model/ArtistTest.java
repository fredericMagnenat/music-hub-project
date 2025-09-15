package com.musichub.artist.domain.model;

import com.musichub.artist.domain.values.ArtistName;
import com.musichub.artist.domain.values.Contribution;
import com.musichub.shared.domain.id.ArtistId;
import com.musichub.shared.domain.id.TrackId;
import com.musichub.shared.domain.values.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

@DisplayName("Artist Domain Model Tests")
class ArtistTest {

    @Nested
    @DisplayName("Artist Creation")
    class ArtistCreation {

        @Test
        @DisplayName("Should create provisional artist with name")
        void shouldCreateProvisionalArtistWithName() {
            // Given
            String name = "The Beatles";

            // When
            Artist artist = Artist.createProvisional(name);

            // Then
            assertThat(artist.getId()).isNotNull();
            assertThat(artist.getNameValue()).isEqualTo("The Beatles");
            assertThat(artist.getStatus()).isEqualTo(ArtistStatus.PROVISIONAL);
            assertThat(artist.getContributions()).isEmpty();
            assertThat(artist.getSources()).isEmpty();
        }

        @Test
        @DisplayName("Should create artist from existing data")
        void shouldCreateArtistFromExistingData() {
            // Given
            ArtistId id = new ArtistId(UUID.randomUUID());
            ArtistName name = ArtistName.of("Bob Dylan");
            ArtistStatus status = ArtistStatus.VERIFIED;
            Contribution contribution = Contribution.of(
                    new TrackId(UUID.randomUUID()),
                    "Like a Rolling Stone",
                    ISRC.of("USRC17607839")
            );
            Source source = Source.of("SPOTIFY", "4V8Sr092TqfHkfAA5fXXqG");

            // When
            Artist artist = Artist.from(id, name, status,
                    Arrays.asList(contribution), Arrays.asList(source));

            // Then
            assertThat(artist.getId()).isEqualTo(id);
            assertThat(artist.getName()).isEqualTo(name);
            assertThat(artist.getStatus()).isEqualTo(ArtistStatus.VERIFIED);
            assertThat(artist.getContributions()).containsExactly(contribution);
            assertThat(artist.getSources()).containsExactly(source);
        }

        @Test
        @DisplayName("Should create artist from basic parameters for backward compatibility")
        void shouldCreateArtistFromBasicParameters() {
            // Given
            ArtistId id = new ArtistId(UUID.randomUUID());
            String name = "Queen";
            ArtistStatus status = ArtistStatus.PROVISIONAL;

            // When
            Artist artist = Artist.from(id, name, status);

            // Then
            assertThat(artist.getId()).isEqualTo(id);
            assertThat(artist.getNameValue()).isEqualTo("Queen");
            assertThat(artist.getStatus()).isEqualTo(status);
            assertThat(artist.getContributions()).isEmpty();
            assertThat(artist.getSources()).isEmpty();
        }

        @Test
        @DisplayName("Should throw exception when creating with null name")
        void shouldThrowExceptionWhenCreatingWithNullName() {
            // Given
            String name = null;

            // When & Then
            assertThatThrownBy(() -> Artist.createProvisional(name))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessage("Artist name cannot be null");
        }
    }

    @Nested
    @DisplayName("Source Management")
    class SourceManagement {

        @Test
        @DisplayName("Should add source to artist")
        void shouldAddSourceToArtist() {
            // Given
            Artist artist = Artist.createProvisional("Metallica");
            Source source = Source.of("TIDAL", "1566681");

            // When
            Artist updatedArtist = artist.addSource(source);

            // Then
            assertThat(updatedArtist.getSources())
                    .hasSize(1)
                    .containsExactly(source);

            assertThat(artist.getSources()).isEmpty(); // Original unchanged
        }

        @Test
        @DisplayName("Should allow multiple sources of different types")
        void shouldAllowMultipleSourcesOfDifferentTypes() {
            // Given
            Artist artist = Artist.createProvisional("Black Sabbath");
            Source tidalSource = Source.of("TIDAL", "4050205");
            Source appleSource = Source.of("APPLE_MUSIC", "3996865");

            // When
            Artist result = artist
                    .addSource(tidalSource)
                    .addSource(appleSource);

            // Then
            assertThat(result.getSources())
                    .hasSize(2)
                    .extracting(Source::sourceType)
                    .containsExactlyInAnyOrder(SourceType.TIDAL, SourceType.APPLE_MUSIC);

            assertThat(result)
                    .satisfies(a -> {
                        assertThat(a.hasSource(SourceType.TIDAL)).isTrue();
                        assertThat(a.hasSource(SourceType.APPLE_MUSIC)).isTrue();
                    });
        }

        @Test
        @DisplayName("Should replace source when adding same type")
        void shouldReplaceSourceWhenAddingSameType() {
            // Given
            Artist artist = Artist.createProvisional("Same Type Artist");
            Source oldSpotify = Source.of("SPOTIFY", "oldId123");
            Source newSpotify = Source.of("SPOTIFY", "newId456");

            // When
            Artist result = artist
                    .addSource(oldSpotify)
                    .addSource(newSpotify);

            // Then
            assertThat(result.getSources())
                    .hasSize(1)
                    .first()
                    .satisfies(source -> {
                        assertThat(source).isEqualTo(newSpotify);
                        assertThat(source.sourceId()).isEqualTo("newId456");
                        assertThat(source.sourceType()).isEqualTo(SourceType.SPOTIFY);
                    });
        }

        @Test
        @DisplayName("Should be idempotent when adding same source twice")
        void shouldBeIdempotentWhenAddingSameSourceTwice() {
            // Given
            Artist artist = Artist.createProvisional("Test Artist");
            Source source = Source.of("SPOTIFY", "same-id");

            // When
            Artist result1 = artist.addSource(source);
            Artist result2 = result1.addSource(source);

            // Then
            assertThat(result2.getSources())
                    .hasSize(1)
                    .isEqualTo(result1.getSources());
        }

        @Test
        @DisplayName("Should get source by type")
        void shouldGetSourceByType() {
            // Given
            Artist artist = Artist.createProvisional("Deep Purple");
            Source source = Source.of("SPOTIFY", "1775I0eBeL5TiXUbzP6XZe");
            Artist artistWithSource = artist.addSource(source);

            // When
            var foundSource = artistWithSource.getSource(SourceType.SPOTIFY);

            // Then
            assertThat(foundSource)
                    .isPresent()
                    .hasValue(source);
        }

        @Test
        @DisplayName("Should return empty when source type not found")
        void shouldReturnEmptyWhenSourceTypeNotFound() {
            // Given
            Artist artist = Artist.createProvisional("Rainbow");

            // When
            var foundSource = artist.getSource(SourceType.TIDAL);

            // Then
            assertThat(foundSource).isEmpty();
        }

        @Test
        @DisplayName("Should throw exception when adding null source")
        void shouldThrowExceptionWhenAddingNullSource() {
            // Given
            Artist artist = Artist.createProvisional("Judas Priest");
            Source source = null;

            // When & Then
            assertThatThrownBy(() -> artist.addSource(source))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessage("Source cannot be null");
        }
    }

    @Nested
    @DisplayName("Status Management")
    class StatusManagement {

        @Test
        @DisplayName("Should mark provisional artist as verified")
        void shouldMarkProvisionalArtistAsVerified() {
            // Given
            Artist provisionalArtist = Artist.createProvisional("Queen");

            // When
            Artist verifiedArtist = provisionalArtist.markAsVerified();

            // Then
            assertThat(verifiedArtist.getStatus()).isEqualTo(ArtistStatus.VERIFIED);
            assertThat(verifiedArtist.getId()).isEqualTo(provisionalArtist.getId());
            assertThat(verifiedArtist.getName()).isEqualTo(provisionalArtist.getName());

            // Original artist should remain unchanged
            assertThat(provisionalArtist.getStatus()).isEqualTo(ArtistStatus.PROVISIONAL);
        }

        @Test
        @DisplayName("Should throw exception when marking already verified artist as verified")
        void shouldThrowExceptionWhenMarkingAlreadyVerifiedArtistAsVerified() {
            // Given
            Artist verifiedArtist = Artist.createProvisional("The Beatles").markAsVerified();

            // When & Then
            assertThatThrownBy(verifiedArtist::markAsVerified)
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessage("Only provisional artists can be marked as verified");
        }
    }

    @Test
    @DisplayName("Should allow multiple sources of different types (coexistence)")
    void shouldAllowMultipleSourcesOfDifferentTypes() {
        // Given
        Artist artist = Artist.createProvisional("Iron Maiden");
        Source spotifySource = Source.of("SPOTIFY", "6mdiAmATAx73kdxrNrnlao");
        Source tidalSource = Source.of("TIDAL", "4050205");

        // When
        Artist result = artist
                .addSource(spotifySource)
                .addSource(tidalSource);

        // Then - Both sources should coexist
        assertThat(result.getSources())
                .hasSize(2)
                .extracting(Source::sourceType)
                .containsExactlyInAnyOrder(SourceType.SPOTIFY, SourceType.TIDAL);

        assertThat(result)
                .satisfies(a -> {
                    assertThat(a.hasSource(SourceType.SPOTIFY)).isTrue();
                    assertThat(a.hasSource(SourceType.TIDAL)).isTrue();
                });
    }

    @Test
    @DisplayName("Should add contribution to artist")
    void shouldAddContributionToArtist() {
        // Given
        Artist artist = Artist.createProvisional("Pink Floyd");
        Contribution contribution = Contribution.of(
                new TrackId(UUID.randomUUID()),
                "Dark Side of the Moon",
                ISRC.of("GBUM71505078")
        );

        // When
        Artist updatedArtist = artist.addContribution(contribution);

        // Then
        assertThat(updatedArtist.getContributions())
                .hasSize(1)
                .containsExactly(contribution);

        assertThat(artist.getContributions()).isEmpty(); // Original unchanged
    }

    @Test
    @DisplayName("Should be idempotent when adding same contribution twice")
    void shouldBeIdempotentWhenAddingSameContributionTwice() {
        // Given
        Artist artist = Artist.createProvisional("Led Zeppelin");
        Contribution contribution = Contribution.of(
                new TrackId(UUID.randomUUID()),
                "Stairway to Heaven",
                ISRC.of("GBQR19700001")
        );

        // When
        Artist artistWith1 = artist.addContribution(contribution);
        Artist artistWith2 = artistWith1.addContribution(contribution);

        // Then
        assertThat(artistWith2.getContributions())
                .hasSize(1)
                .containsExactly(contribution);

        assertThat(artistWith1).isSameAs(artistWith2); // Should return same instance
    }

    @Test
    @DisplayName("Should throw exception when adding null contribution")
    void shouldThrowExceptionWhenAddingNullContribution() {
        // Given
        Artist artist = Artist.createProvisional("AC/DC");

        // When & Then
        assertThatThrownBy(() -> artist.addContribution(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("Contribution cannot be null");
    }

    @Nested
    @DisplayName("Edge Cases")
    class EdgeCases {

        @Test
        @DisplayName("Should handle artist names with special characters")
        void shouldHandleArtistNamesWithSpecialCharacters() {
            // Given
            String nameWithSpecialChars = "Motörhead & Friends (2024)";

            // When
            Artist artist = Artist.createProvisional(nameWithSpecialChars);

            // Then
            assertThat(artist.getNameValue()).isEqualTo(nameWithSpecialChars);
        }

        @Test
        @DisplayName("Should handle very long artist names")
        void shouldHandleVeryLongArtistNames() {
            // Given - Use exactly 255 characters (the maximum allowed)
            String longName = "A".repeat(255);

            // When
            Artist artist = Artist.createProvisional(longName);

            // Then
            assertThat(artist.getNameValue()).hasSize(255);
        }

        @Test
        @DisplayName("Should throw exception when artist name exceeds 255 characters")
        void shouldThrowExceptionWhenArtistNameExceeds255Characters() {
            // Given - Use 256 characters (above the limit)
            String tooLongName = "A".repeat(256);

            // When & Then
            assertThatThrownBy(() -> Artist.createProvisional(tooLongName))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Artist name cannot exceed 255 characters");
        }

        @Test
        @DisplayName("Should handle multiple rapid source additions")
        void shouldHandleMultipleRapidSourceAdditions() {
            // Given
            Artist artist = Artist.createProvisional("Performance Test");

            // When
            Artist result = artist;
            for (int i = 0; i < 10; i++) {
                Source source = Source.of("SPOTIFY", "id" + i);
                result = result.addSource(source);
            }

            // Then
            assertThat(result.getSources()).hasSize(1); // Should replace, not accumulate
            assertThat(result.getSource(SourceType.SPOTIFY))
                    .isPresent()
                    .get()
                    .satisfies(s -> assertThat(s.sourceId()).isEqualTo("id9"));
        }
    }

    @Nested
    @DisplayName("Immutability and Equality")
    class ImmutabilityAndEqualityTests {

        @Test
        @DisplayName("Should be equal when IDs are equal")
        void shouldBeEqualWhenIdsAreEqual() {
            // Given
            ArtistId id = new ArtistId(UUID.randomUUID());
            Artist artist1 = Artist.from(id, "The Beatles", ArtistStatus.VERIFIED);
            Artist artist2 = Artist.from(id, "The Beatles", ArtistStatus.PROVISIONAL);

            // When & Then
            assertThat(artist1)
                    .isEqualTo(artist2)
                    .hasSameHashCodeAs(artist2);
        }

        @Test
        @DisplayName("Should not be equal when IDs are different")
        void shouldNotBeEqualWhenIdsAreDifferent() {
            // Given
            Artist artist1 = Artist.createProvisional("The Rolling Stones");
            Artist artist2 = Artist.createProvisional("The Rolling Stones");

            // When & Then
            assertThat(artist1).isNotEqualTo(artist2);
        }

        @Test
        @DisplayName("Should maintain immutability when adding contributions")
        void shouldMaintainImmutabilityWhenAddingContributions() {
            // Given
            Artist originalArtist = Artist.createProvisional("U2");
            Contribution contribution = Contribution.of(
                    new TrackId(UUID.randomUUID()),
                    "With or Without You",
                    ISRC.of("USIR18700123")
            );

            // When
            Artist updatedArtist = originalArtist.addContribution(contribution);

            // Then
            assertThat(originalArtist.getContributions()).isEmpty();
            assertThat(updatedArtist.getContributions()).hasSize(1);
            assertThat(originalArtist).isNotSameAs(updatedArtist);
        }

        @Test
        @DisplayName("Should have proper toString representation")
        void shouldHaveProperToStringRepresentation() {
            // Given
            Artist artist = Artist.createProvisional("Radiohead");
            Contribution contribution = Contribution.of(
                    new TrackId(UUID.randomUUID()),
                    "Creep",
                    ISRC.of("GBUM71505078")
            );
            Source source = Source.of("SPOTIFY", "4Z8W4fKeB5YxbusRsdQVPb");

            Artist enrichedArtist = artist
                    .addContribution(contribution)
                    .addSource(source);

            // When
            String toString = enrichedArtist.toString();

            // Then
            assertThat(toString)
                    .contains("Artist{")
                    .contains("id=" + enrichedArtist.getId())
                    .contains("name=" + enrichedArtist.getName())
                    .contains("status=" + enrichedArtist.getStatus())
                    .contains("contributions=1")
                    .contains("sources=1");
        }
    }

    @Nested
    @DisplayName("Name Priority Management")
    class NamePriorityManagementTests {

        @Test
        @DisplayName("Should update name when new source has higher priority")
        void shouldUpdateNameWhenNewSourceHasHigherPriority() {
            // Given
            Artist artist = Artist.createProvisional("Beatles")
                    .addSource(Source.of("SPOTIFY", "spotify123"));
            ArtistName tidalName = ArtistName.of("The Beatles");

            // When (TIDAL has higher priority than SPOTIFY)
            Artist updatedArtist = artist.updateNameFromSource(tidalName, SourceType.TIDAL);

            // Then
            assertThat(updatedArtist.getName()).isEqualTo(tidalName);
            assertThat(updatedArtist.getNameValue()).isEqualTo("The Beatles");

            // Original artist should remain unchanged
            assertThat(artist.getNameValue()).isEqualTo("Beatles");
        }

        @Test
        @DisplayName("Should not update name when new source has lower priority")
        void shouldNotUpdateNameWhenNewSourceHasLowerPriority() {
            // Given
            Artist artist = Artist.createProvisional("The Beatles")
                    .addSource(Source.of("TIDAL", "tidal123"));
            ArtistName spotifyName = ArtistName.of("Beatles");

            // When (SPOTIFY has lower priority than TIDAL)
            Artist result = artist.updateNameFromSource(spotifyName, SourceType.SPOTIFY);

            // Then
            assertThat(result).isSameAs(artist); // No change, same instance
            assertThat(result.getNameValue()).isEqualTo("The Beatles");
        }

        @Test
        @DisplayName("Should update name when no sources exist")
        void shouldUpdateNameWhenNoSourcesExist() {
            // Given
            Artist artist = Artist.createProvisional("Old Name");
            ArtistName newName = ArtistName.of("New Name");

            // When
            Artist updatedArtist = artist.updateNameFromSource(newName, SourceType.MANUAL);

            // Then
            assertThat(updatedArtist.getName()).isEqualTo(newName);
            assertThat(updatedArtist.getNameValue()).isEqualTo("New Name");
        }

        @Test
        @DisplayName("Should throw exception when updating name with null parameters")
        void shouldThrowExceptionWhenUpdatingNameWithNullParameters() {
            // Given
            Artist artist = Artist.createProvisional("Test Artist");
            ArtistName testName = ArtistName.of("Test");

            // When & Then
            assertThatThrownBy(() -> artist.updateNameFromSource(null, SourceType.TIDAL))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessage("Artist name cannot be null");

            assertThatThrownBy(() -> artist.updateNameFromSource(testName, null))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessage("Source type cannot be null");
        }

        @Test
        @DisplayName("Should respect source hierarchy in name updates")
        void shouldRespectSourceHierarchyInNameUpdates() {
            // Given
            Artist artist = Artist.createProvisional("Initial Name")
                    .addSource(Source.of("APPLE_MUSIC", "apple123"))
                    .addSource(Source.of("DEEZER", "deezer456"));

            ArtistName manualName = ArtistName.of("Manual Override");
            ArtistName spotifyName = ArtistName.of("Spotify Name");

            // When - MANUAL has highest priority
            Artist manualUpdate = artist.updateNameFromSource(manualName, SourceType.MANUAL);

            // Then - Should update because MANUAL > anything
            assertThat(manualUpdate.getNameValue()).isEqualTo("Manual Override");

            // When - SPOTIFY has higher priority than DEEZER/APPLE_MUSIC
            Artist spotifyUpdate = artist.updateNameFromSource(spotifyName, SourceType.SPOTIFY);

            // Then - Should update because SPOTIFY > DEEZER > APPLE_MUSIC
            assertThat(spotifyUpdate.getNameValue()).isEqualTo("Spotify Name");
        }
    }

    @Nested
    @DisplayName("Integration Scenarios")
    class IntegrationScenariosTests {

        @Test
        @DisplayName("Should debug name update behavior step by step")
        void shouldDebugNameUpdateBehaviorStepByStep() {
            // Given
            Artist artist = Artist.createProvisional("Initial Name");

            // When - Step 1: Add SPOTIFY source
            Artist withSpotify = artist.addSource(Source.of("SPOTIFY", "spotify123"));

            // Then - Verify SPOTIFY integration
            assertThat(withSpotify.getSources()).hasSize(1);
            assertThat(withSpotify.hasSource(SourceType.SPOTIFY)).isTrue();

            // When - Step 2: Update name with SPOTIFY source
            Artist withSpotifyName = withSpotify.updateNameFromSource(ArtistName.of("Spotify Name"), SourceType.SPOTIFY);

            // Then - Verify name update
            assertThat(withSpotifyName.getNameValue()).isEqualTo("Spotify Name");

            // When - Step 3: Add TIDAL source
            Artist withTidal = withSpotifyName.addSource(Source.of("TIDAL", "tidal456"));

            // When - Step 4: Update name with TIDAL source (higher priority)
            Artist withTidalName = withTidal.updateNameFromSource(ArtistName.of("Tidal Name"), SourceType.TIDAL);

            // Then - Verify final state
            assertThat(withTidalName.getNameValue()).isEqualTo("Tidal Name");
            assertThat(withTidalName.getSources()).hasSize(2);
        }

        @Test
        @DisplayName("Should handle complete enrichment workflow")
        void shouldHandleCompleteEnrichmentWorkflow() {
            // Given - Start with provisional artist (no sources yet)
            Artist artist = Artist.createProvisional("Initial Name");

            // When - Simulate enrichment process step by step
            Artist withSpotify = artist.addSource(Source.of("SPOTIFY", "spotify123"));

            // First name update - should work since no previous sources
            Artist withSpotifyName = withSpotify.updateNameFromSource(ArtistName.of("Spotify Name"), SourceType.SPOTIFY);

            Artist withTidal = withSpotifyName.addSource(Source.of("TIDAL", "tidal456"));

            // Second name update - should work since TIDAL > SPOTIFY
            Artist withTidalName = withTidal.updateNameFromSource(ArtistName.of("Tidal Name"), SourceType.TIDAL);

            Artist enriched = withTidalName.markAsVerified();

            // Then
            assertThat(enriched.getStatus()).isEqualTo(ArtistStatus.VERIFIED);
            assertThat(enriched.getNameValue()).isEqualTo("Tidal Name"); // TIDAL has higher priority
            assertThat(enriched.getSources()).hasSize(2);
            assertThat(enriched.hasSource(SourceType.SPOTIFY)).isTrue();
            assertThat(enriched.hasSource(SourceType.TIDAL)).isTrue();
        }

        @Test
        @DisplayName("Should handle realistic enrichment workflow")
        void shouldHandleRealisticEnrichmentWorkflow() {
            // Given - Start with provisional artist
            Artist artist = Artist.createProvisional("Initial Name");

            // When - Simulate realistic enrichment (name gets updated when source is higher priority)
            Artist step1 = artist.updateNameFromSource(ArtistName.of("Spotify Name"), SourceType.SPOTIFY);
            Artist step2 = step1.addSource(Source.of("SPOTIFY", "spotify123"));
            Artist step3 = step2.updateNameFromSource(ArtistName.of("Tidal Name"), SourceType.TIDAL);
            Artist step4 = step3.addSource(Source.of("TIDAL", "tidal456"));
            Artist enriched = step4.markAsVerified();

            // Then
            assertThat(enriched.getStatus()).isEqualTo(ArtistStatus.VERIFIED);
            assertThat(enriched.getNameValue()).isEqualTo("Tidal Name"); // TIDAL has higher priority
            assertThat(enriched.getSources()).hasSize(2);
            assertThat(enriched.hasSource(SourceType.SPOTIFY)).isTrue();
            assertThat(enriched.hasSource(SourceType.TIDAL)).isTrue();
        }

        @Test
        @DisplayName("Should maintain immutability throughout workflow")
        void shouldMaintainImmutabilityThroughoutWorkflow() {
            // Given
            Artist original = Artist.createProvisional("Original");

            // When
            Artist step1 = original.addSource(Source.of("SPOTIFY", "123"));
            Artist step2 = step1.updateNameFromSource(ArtistName.of("Updated"), SourceType.SPOTIFY);
            Artist step3 = step2.markAsVerified();

            // Then - Each step creates new instance
            assertThat(original).isNotSameAs(step1);
            assertThat(step1).isNotSameAs(step2);
            assertThat(step2).isNotSameAs(step3);

            // Original remains unchanged
            assertThat(original.getNameValue()).isEqualTo("Original");
            assertThat(original.getStatus()).isEqualTo(ArtistStatus.PROVISIONAL);
            assertThat(original.getSources()).isEmpty();
        }
    }
}
