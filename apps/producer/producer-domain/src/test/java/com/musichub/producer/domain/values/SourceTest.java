package com.musichub.producer.domain.values;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;

import static org.junit.jupiter.api.Assertions.*;

class SourceTest {

    @Test
    @DisplayName("Should normalize and create Source when inputs are valid")
    void shouldCreateValidSource() {
        Source src = Source.of(" spotify ", " track123 ");
        assertEquals("SPOTIFY", src.sourceName());
        assertEquals("track123", src.sourceId());
    }

    @Test
    @DisplayName("Should reject unsupported sourceName")
    void shouldRejectUnsupportedSourceName() {
        assertThrows(IllegalArgumentException.class, () -> Source.of("UNKNOWN", "track123"));
    }

    @Test
    @DisplayName("Should reject blank sourceId")
    void shouldRejectBlankSourceId() {
        assertThrows(IllegalArgumentException.class, () -> Source.of("TIDAL", "   "));
    }

    @Nested
    @DisplayName("Source Priority")
    class SourcePriorityTests {

        @Test
        @DisplayName("Should return correct priority for each source")
        void returnCorrectPriorityForEachSource() {
            assertEquals(SourcePriority.MANUAL, Source.of("MANUAL", "id").priority());
            assertEquals(SourcePriority.TIDAL, Source.of("TIDAL", "id").priority());
            assertEquals(SourcePriority.SPOTIFY, Source.of("SPOTIFY", "id").priority());
            assertEquals(SourcePriority.DEEZER, Source.of("DEEZER", "id").priority());
            assertEquals(SourcePriority.APPLE_MUSIC, Source.of("APPLE_MUSIC", "id").priority());
        }

        @Test
        @DisplayName("Should compare priorities correctly")
        void comparePrioritiesCorrectly() {
            Source manualSource = Source.of("MANUAL", "id1");
            Source tidalSource = Source.of("TIDAL", "id2");
            Source spotifySource = Source.of("SPOTIFY", "id3");
            Source deezerSource = Source.of("DEEZER", "id4");
            Source appleMusicSource = Source.of("APPLE_MUSIC", "id5");

            // MANUAL has highest priority
            assertTrue(manualSource.hasHigherPriorityThan(tidalSource));
            assertTrue(manualSource.hasHigherPriorityThan(spotifySource));
            assertTrue(manualSource.hasHigherPriorityThan(deezerSource));
            assertTrue(manualSource.hasHigherPriorityThan(appleMusicSource));

            // TIDAL > SPOTIFY > DEEZER > APPLE_MUSIC
            assertTrue(tidalSource.hasHigherPriorityThan(spotifySource));
            assertTrue(spotifySource.hasHigherPriorityThan(deezerSource));
            assertTrue(deezerSource.hasHigherPriorityThan(appleMusicSource));

            // Reverse comparisons should be false
            assertFalse(tidalSource.hasHigherPriorityThan(manualSource));
            assertFalse(spotifySource.hasHigherPriorityThan(tidalSource));
            assertFalse(appleMusicSource.hasHigherPriorityThan(deezerSource));
        }

        @Test
        @DisplayName("Should handle same priority comparison")
        void handleSamePriorityComparison() {
            Source source1 = Source.of("SPOTIFY", "id1");
            Source source2 = Source.of("SPOTIFY", "id2");

            assertFalse(source1.hasHigherPriorityThan(source2));
            assertFalse(source2.hasHigherPriorityThan(source1));
        }

        @Test
        @DisplayName("Should reject null in priority comparison")
        void rejectNullInPriorityComparison() {
            Source source = Source.of("SPOTIFY", "id");
            assertThrows(NullPointerException.class, () -> source.hasHigherPriorityThan(null));
        }
    }
}
