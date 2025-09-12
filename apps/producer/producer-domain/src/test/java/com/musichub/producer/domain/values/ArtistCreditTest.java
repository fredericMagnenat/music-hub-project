package com.musichub.producer.domain.values;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.musichub.shared.domain.id.ArtistId;

@DisplayName("ArtistCredit Domain Value Tests")
class ArtistCreditTest {

    @Nested
    @DisplayName("Construction and Factory Methods")
    class ConstructionAndFactoryMethods {

        @Test
        @DisplayName("Should create ArtistCredit with name only")
        void createWithNameOnly() {
            ArtistCredit credit = ArtistCredit.withName("Queen");

            assertEquals("Queen", credit.artistName());
            assertNull(credit.artistId());
            assertFalse(credit.isResolved());
        }

        @Test
        @DisplayName("Should create ArtistCredit with name and ID")
        void createWithNameAndId() {
            ArtistId artistId = ArtistId.newId();
            ArtistCredit credit = ArtistCredit.with("Queen", artistId);

            assertEquals("Queen", credit.artistName());
            assertEquals(artistId, credit.artistId());
            assertTrue(credit.isResolved());
        }

        @Test
        @DisplayName("Should create ArtistCredit using record constructor")
        void createUsingRecordConstructor() {
            ArtistId artistId = ArtistId.newId();
            ArtistCredit credit = new ArtistCredit("David Bowie", artistId);

            assertEquals("David Bowie", credit.artistName());
            assertEquals(artistId, credit.artistId());
            assertTrue(credit.isResolved());
        }

        @Test
        @DisplayName("Should allow null artistId")
        void allowNullArtistId() {
            ArtistCredit credit = new ArtistCredit("Freddie Mercury", null);

            assertEquals("Freddie Mercury", credit.artistName());
            assertNull(credit.artistId());
            assertFalse(credit.isResolved());
        }
    }

    @Nested
    @DisplayName("Validation")
    class Validation {

        @Test
        @DisplayName("Should reject null artistName")
        void rejectNullArtistName() {
            assertThrows(NullPointerException.class,
                    () -> new ArtistCredit(null, null));
        }

        @Test
        @DisplayName("Should reject blank artistName")
        void rejectBlankArtistName() {
            assertThrows(IllegalArgumentException.class,
                    () -> ArtistCredit.withName(""));
            assertThrows(IllegalArgumentException.class,
                    () -> ArtistCredit.withName("   "));
        }
    }

    @Nested
    @DisplayName("Artist Resolution")
    class ArtistResolution {

        @Test
        @DisplayName("Should update credit with artist ID")
        void updateCreditWithArtistId() {
            ArtistCredit original = ArtistCredit.withName("Queen");
            ArtistId artistId = ArtistId.newId();

            ArtistCredit resolved = original.withArtistId(artistId);

            assertEquals("Queen", resolved.artistName());
            assertEquals(artistId, resolved.artistId());
            assertTrue(resolved.isResolved());

            // Original should be unchanged
            assertNull(original.artistId());
            assertFalse(original.isResolved());
        }

        @Test
        @DisplayName("Should identify resolved vs unresolved credits")
        void identifyResolvedVsUnresolved() {
            ArtistCredit unresolved = ArtistCredit.withName("Unknown Artist");
            ArtistCredit resolved = ArtistCredit.with("Known Artist", ArtistId.newId());

            assertFalse(unresolved.isResolved());
            assertTrue(resolved.isResolved());
        }
    }

    @Nested
    @DisplayName("Equality and HashCode")
    class EqualityAndHashCode {

        @Test
        @DisplayName("Should be equal when name and ID are equal")
        void equalWhenNameAndIdEqual() {
            ArtistId artistId = ArtistId.newId();
            ArtistCredit credit1 = ArtistCredit.with("Queen", artistId);
            ArtistCredit credit2 = ArtistCredit.with("Queen", artistId);

            assertEquals(credit1, credit2);
            assertEquals(credit1.hashCode(), credit2.hashCode());
        }

        @Test
        @DisplayName("Should not be equal when names differ")
        void notEqualWhenNamesDiffer() {
            ArtistId artistId = ArtistId.newId();
            ArtistCredit credit1 = ArtistCredit.with("Queen", artistId);
            ArtistCredit credit2 = ArtistCredit.with("Beatles", artistId);

            assertNotEquals(credit1, credit2);
        }

        @Test
        @DisplayName("Should not be equal when IDs differ")
        void notEqualWhenIdsDiffer() {
            ArtistCredit credit1 = ArtistCredit.with("Queen", ArtistId.newId());
            ArtistCredit credit2 = ArtistCredit.with("Queen", ArtistId.newId());

            assertNotEquals(credit1, credit2);
        }

        @Test
        @DisplayName("Should be equal when both have null IDs and same name")
        void equalWhenBothHaveNullIds() {
            ArtistCredit credit1 = ArtistCredit.withName("Queen");
            ArtistCredit credit2 = ArtistCredit.withName("Queen");

            assertEquals(credit1, credit2);
            assertEquals(credit1.hashCode(), credit2.hashCode());
        }
    }
}