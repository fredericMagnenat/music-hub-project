package com.musichub.artist.domain;

import com.musichub.shared.domain.id.ArtistId;
import com.musichub.shared.domain.values.ISRC;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ArtistTest {

    @Nested
    @DisplayName("Artist creation")
    class ArtistCreation {

        @Test
        @DisplayName("Should create provisional artist with valid name")
        void validName_shouldCreateProvisionalArtistSuccessfully() {
            // Given
            String artistName = "John Doe";

            // When
            Artist artist = Artist.createProvisional(artistName);

            // Then
            assertNotNull(artist.getId());
            assertEquals(artistName, artist.getName());
            assertEquals(ArtistStatus.PROVISIONAL, artist.getStatus());
            assertTrue(artist.getTrackReferences().isEmpty());
        }

        @Test
        @DisplayName("Should throw exception when name is null for provisional creation")
        void nullName_shouldThrowException() {
            // When & Then
            NullPointerException exception = assertThrows(
                NullPointerException.class,
                () -> Artist.createProvisional(null)
            );
            assertEquals("Artist name cannot be null", exception.getMessage());
        }

        @Test
        @DisplayName("Should create artist from existing data")
        void validExistingData_shouldCreateArtistSuccessfully() {
            // Given
            ArtistId artistId = new ArtistId(UUID.randomUUID());
            String name = "Jane Smith";
            ArtistStatus status = ArtistStatus.VERIFIED;
            Set<ISRC> trackReferences = new HashSet<>();
            ISRC isrc1 = new ISRC("USRC17607839");
            ISRC isrc2 = new ISRC("GBUM71505078");
            trackReferences.add(isrc1);
            trackReferences.add(isrc2);

            // When
            Artist artist = Artist.from(artistId, name, status, trackReferences);

            // Then
            assertEquals(artistId, artist.getId());
            assertEquals(name, artist.getName());
            assertEquals(status, artist.getStatus());
            assertEquals(2, artist.getTrackReferences().size());
            assertTrue(artist.getTrackReferences().contains(isrc1));
            assertTrue(artist.getTrackReferences().contains(isrc2));
        }

        @Test
        @DisplayName("Should create defensive copy of track references")
        void trackReferencesModification_shouldNotAffectArtistData() {
            // Given
            ArtistId artistId = new ArtistId(UUID.randomUUID());
            String name = "Test Artist";
            ArtistStatus status = ArtistStatus.VERIFIED;
            Set<ISRC> originalTrackReferences = new HashSet<>();
            ISRC isrc = new ISRC("USRC17607839");
            originalTrackReferences.add(isrc);

            // When
            Artist artist = Artist.from(artistId, name, status, originalTrackReferences);
            
            // Modification de la collection originale
            originalTrackReferences.add(new ISRC("GBUM71505078"));

            // Then
            assertEquals(1, artist.getTrackReferences().size());
            assertTrue(artist.getTrackReferences().contains(isrc));
        }
    }

    @Nested
    @DisplayName("Track reference management")
    class TrackReferenceManagement {

        @Test
        @DisplayName("Should add valid track reference")
        void validTrackReference_shouldAddSuccessfully() {
            // Given
            Artist artist = Artist.createProvisional("Test Artist");
            ISRC isrc = new ISRC("USRC17607839");

            // When
            artist.addTrackReference(isrc);

            // Then
            assertEquals(1, artist.getTrackReferences().size());
            assertTrue(artist.getTrackReferences().contains(isrc));
        }

        @Test
        @DisplayName("Should throw exception when adding null track reference")
        void nullTrackReference_shouldThrowException() {
            // Given
            Artist artist = Artist.createProvisional("Test Artist");

            // When & Then
            NullPointerException exception = assertThrows(
                NullPointerException.class,
                () -> artist.addTrackReference(null)
            );
            assertEquals("ISRC cannot be null", exception.getMessage());
        }

        @Test
        @DisplayName("Should avoid duplicates when adding track references")
        void duplicateTrackReference_shouldNotCreateDuplicates() {
            // Given
            Artist artist = Artist.createProvisional("Test Artist");
            ISRC isrc = new ISRC("USRC17607839");

            // When
            artist.addTrackReference(isrc);
            artist.addTrackReference(isrc); // Ajout du même ISRC

            // Then
            assertEquals(1, artist.getTrackReferences().size());
        }

        @Test
        @DisplayName("Should return defensive copy of track references")
        void trackReferencesRetrieval_shouldReturnDefensiveCopy() {
            // Given
            Artist artist = Artist.createProvisional("Test Artist");
            ISRC isrc = new ISRC("USRC17607839");
            artist.addTrackReference(isrc);

            // When
            Set<ISRC> references = artist.getTrackReferences();
            references.clear(); // Tentative de modification

            // Then
            assertEquals(1, artist.getTrackReferences().size());
            assertTrue(artist.getTrackReferences().contains(isrc));
        }
    }

    @Nested
    @DisplayName("Getter methods")
    class GetterMethods {

        @Test
        @DisplayName("Should return correct artist ID")
        void getId_shouldReturnValidArtistId() {
            // Given
            Artist artist = Artist.createProvisional("Test Artist");

            // When
            ArtistId id = artist.getId();

            // Then
            assertNotNull(id);
        }

        @Test
        @DisplayName("Should return correct artist name")
        void getName_shouldReturnCorrectName() {
            // Given
            String expectedName = "Test Artist";
            Artist artist = Artist.createProvisional(expectedName);

            // When
            String actualName = artist.getName();

            // Then
            assertEquals(expectedName, actualName);
        }

        @Test
        @DisplayName("Should return correct artist status")
        void getStatus_shouldReturnCorrectStatus() {
            // Given
            Artist artist = Artist.createProvisional("Test Artist");

            // When
            ArtistStatus status = artist.getStatus();

            // Then
            assertEquals(ArtistStatus.PROVISIONAL, status);
        }
    }

    @Nested
    @DisplayName("Equals and hashCode contracts")
    class EqualsAndHashCodeContracts {

        @Test
        @DisplayName("Should be equal to itself")
        void sameInstance_shouldBeEqual() {
            // Given
            Artist artist = Artist.createProvisional("Test Artist");

            // When & Then
            assertEquals(artist, artist);
            assertEquals(artist.hashCode(), artist.hashCode());
        }

        @Test
        @DisplayName("Should be equal to another artist with same ID")
        void sameArtistId_shouldBeEqual() {
            // Given
            ArtistId sharedId = new ArtistId(UUID.randomUUID());
            Artist artist1 = Artist.from(sharedId, "Artist 1", ArtistStatus.VERIFIED, new HashSet<>());
            Artist artist2 = Artist.from(sharedId, "Artist 2", ArtistStatus.PROVISIONAL, new HashSet<>());

            // When & Then
            assertEquals(artist1, artist2);
            assertEquals(artist1.hashCode(), artist2.hashCode());
        }

        @Test
        @DisplayName("Should not be equal to another artist with different ID")
        void differentArtistId_shouldNotBeEqual() {
            // Given
            Artist artist1 = Artist.createProvisional("Artist 1");
            Artist artist2 = Artist.createProvisional("Artist 2");

            // When & Then
            assertNotEquals(artist1, artist2);
        }

        @Test
        @DisplayName("Should not be equal to null")
        void nullComparison_shouldNotBeEqual() {
            // Given
            Artist artist = Artist.createProvisional("Test Artist");

            // When & Then
            assertNotEquals(null, artist);
        }

        @Test
        @DisplayName("Should not be equal to object of different class")
        void differentClass_shouldNotBeEqual() {
            // Given
            Artist artist = Artist.createProvisional("Test Artist");
            Object otherObject = "Not an artist";

            // When & Then
            assertNotEquals(otherObject, artist);
        }
    }
}