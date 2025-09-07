package com.musichub.producer.domain.values;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("SourcePriority Domain Value Tests")
class SourcePriorityTest {

    @Nested
    @DisplayName("Priority Values")
    class PriorityValues {

        @Test
        @DisplayName("Should have correct priority values according to domain charter")
        void correctPriorityValues() {
            assertEquals(1, SourcePriority.MANUAL.getPriority());
            assertEquals(2, SourcePriority.TIDAL.getPriority());
            assertEquals(3, SourcePriority.SPOTIFY.getPriority());
            assertEquals(4, SourcePriority.DEEZER.getPriority());
            assertEquals(5, SourcePriority.APPLE_MUSIC.getPriority());
        }
    }

    @Nested
    @DisplayName("Source Name Mapping")
    class SourceNameMapping {

        @Test
        @DisplayName("Should map valid source names to correct priorities")
        void mapValidSourceNames() {
            assertEquals(SourcePriority.MANUAL, SourcePriority.fromSourceName("MANUAL"));
            assertEquals(SourcePriority.TIDAL, SourcePriority.fromSourceName("TIDAL"));
            assertEquals(SourcePriority.SPOTIFY, SourcePriority.fromSourceName("SPOTIFY"));
            assertEquals(SourcePriority.DEEZER, SourcePriority.fromSourceName("DEEZER"));
            assertEquals(SourcePriority.APPLE_MUSIC, SourcePriority.fromSourceName("APPLE_MUSIC"));
        }

        @Test
        @DisplayName("Should handle case-insensitive source names")
        void handleCaseInsensitiveSourceNames() {
            assertEquals(SourcePriority.MANUAL, SourcePriority.fromSourceName("manual"));
            assertEquals(SourcePriority.TIDAL, SourcePriority.fromSourceName("tidal"));
            assertEquals(SourcePriority.SPOTIFY, SourcePriority.fromSourceName("spotify"));
            assertEquals(SourcePriority.DEEZER, SourcePriority.fromSourceName("deezer"));
            assertEquals(SourcePriority.APPLE_MUSIC, SourcePriority.fromSourceName("apple_music"));
        }

        @Test
        @DisplayName("Should trim whitespace from source names")
        void trimWhitespaceFromSourceNames() {
            assertEquals(SourcePriority.MANUAL, SourcePriority.fromSourceName(" MANUAL "));
            assertEquals(SourcePriority.TIDAL, SourcePriority.fromSourceName("\tTIDAL\t"));
        }

        @Test
        @DisplayName("Should reject null source names")
        void rejectNullSourceNames() {
            assertThrows(NullPointerException.class, 
                () -> SourcePriority.fromSourceName(null));
        }

        @Test
        @DisplayName("Should reject invalid source names")
        void rejectInvalidSourceNames() {
            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, 
                () -> SourcePriority.fromSourceName("INVALID"));
            assertTrue(ex.getMessage().contains("Unsupported source name"));
        }
    }

    @Nested
    @DisplayName("Priority Comparison")
    class PriorityComparison {

        @Test
        @DisplayName("Should correctly identify higher priority sources")
        void identifyHigherPrioritySources() {
            assertTrue(SourcePriority.MANUAL.hasHigherPriorityThan(SourcePriority.TIDAL));
            assertTrue(SourcePriority.TIDAL.hasHigherPriorityThan(SourcePriority.SPOTIFY));
            assertTrue(SourcePriority.SPOTIFY.hasHigherPriorityThan(SourcePriority.DEEZER));
            assertTrue(SourcePriority.DEEZER.hasHigherPriorityThan(SourcePriority.APPLE_MUSIC));
        }

        @Test
        @DisplayName("Should correctly identify lower priority sources")
        void identifyLowerPrioritySources() {
            assertFalse(SourcePriority.TIDAL.hasHigherPriorityThan(SourcePriority.MANUAL));
            assertFalse(SourcePriority.SPOTIFY.hasHigherPriorityThan(SourcePriority.TIDAL));
            assertFalse(SourcePriority.DEEZER.hasHigherPriorityThan(SourcePriority.SPOTIFY));
            assertFalse(SourcePriority.APPLE_MUSIC.hasHigherPriorityThan(SourcePriority.DEEZER));
        }

        @Test
        @DisplayName("Should handle equal priority sources")
        void handleEqualPrioritySources() {
            assertFalse(SourcePriority.MANUAL.hasHigherPriorityThan(SourcePriority.MANUAL));
            assertFalse(SourcePriority.SPOTIFY.hasHigherPriorityThan(SourcePriority.SPOTIFY));
        }

        @Test
        @DisplayName("Should reject null in priority comparison")
        void rejectNullInPriorityComparison() {
            assertThrows(NullPointerException.class, 
                () -> SourcePriority.MANUAL.hasHigherPriorityThan(null));
        }
    }
}