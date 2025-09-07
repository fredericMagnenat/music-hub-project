package com.musichub.producer.domain.model;

import com.musichub.producer.domain.values.ArtistCredit;
import com.musichub.producer.domain.values.Source;
import com.musichub.producer.domain.values.TrackStatus;
import com.musichub.shared.domain.values.ISRC;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Track Source of Truth Hierarchy Tests")
class TrackSourcePriorityTest {

    private static final ISRC TEST_ISRC = ISRC.of("FRLA12400001");

    @Nested
    @DisplayName("Highest Priority Source")
    class HighestPrioritySource {

        @Test
        @DisplayName("Should identify highest priority source with single source")
        void identifyHighestPrioritySourceWithSingleSource() {
            Track track = Track.of(
                TEST_ISRC,
                "Test Track",
                List.of(ArtistCredit.withName("Artist")),
                List.of(Source.of("SPOTIFY", "spotify-123")),
                TrackStatus.PROVISIONAL
            );

            Source highestPriority = track.getHighestPrioritySource();
            assertEquals("SPOTIFY", highestPriority.sourceName());
        }

        @Test
        @DisplayName("Should identify highest priority source with multiple sources")
        void identifyHighestPrioritySourceWithMultipleSources() {
            Track track = Track.of(
                TEST_ISRC,
                "Test Track",
                List.of(ArtistCredit.withName("Artist")),
                List.of(
                    Source.of("SPOTIFY", "spotify-123"),
                    Source.of("TIDAL", "tidal-456"),
                    Source.of("DEEZER", "deezer-789")
                ),
                TrackStatus.PROVISIONAL
            );

            Source highestPriority = track.getHighestPrioritySource();
            assertEquals("TIDAL", highestPriority.sourceName());
        }

        @Test
        @DisplayName("Should identify MANUAL as highest priority when present")
        void identifyManualAsHighestPriority() {
            Track track = Track.of(
                TEST_ISRC,
                "Test Track",
                List.of(ArtistCredit.withName("Artist")),
                List.of(
                    Source.of("SPOTIFY", "spotify-123"),
                    Source.of("MANUAL", "manual-456"),
                    Source.of("TIDAL", "tidal-789")
                ),
                TrackStatus.PROVISIONAL
            );

            Source highestPriority = track.getHighestPrioritySource();
            assertEquals("MANUAL", highestPriority.sourceName());
        }
    }

    @Nested
    @DisplayName("Update with Source Priority")
    class UpdateWithSourcePriority {

        @Test
        @DisplayName("Should update metadata when new source has higher priority")
        void updateMetadataWhenNewSourceHasHigherPriority() {
            Track originalTrack = Track.of(
                TEST_ISRC,
                "Original Title",
                List.of(ArtistCredit.withName("Original Artist")),
                List.of(Source.of("SPOTIFY", "spotify-123")),
                TrackStatus.PROVISIONAL
            );

            Track updatedTrack = originalTrack.updateWithSourcePriority(
                "Updated Title",
                List.of(ArtistCredit.withName("Updated Artist")),
                Source.of("TIDAL", "tidal-456"),
                TrackStatus.VERIFIED
            );

            assertEquals("Updated Title", updatedTrack.title());
            assertEquals(List.of("Updated Artist"), updatedTrack.artistNames());
            assertEquals(TrackStatus.VERIFIED, updatedTrack.status());
            assertEquals(2, updatedTrack.sources().size());
            assertTrue(updatedTrack.sources().contains(Source.of("SPOTIFY", "spotify-123")));
            assertTrue(updatedTrack.sources().contains(Source.of("TIDAL", "tidal-456")));
        }

        @Test
        @DisplayName("Should not update metadata when new source has lower priority")
        void notUpdateMetadataWhenNewSourceHasLowerPriority() {
            Track originalTrack = Track.of(
                TEST_ISRC,
                "Original Title",
                List.of(ArtistCredit.withName("Original Artist")),
                List.of(Source.of("TIDAL", "tidal-123")),
                TrackStatus.VERIFIED
            );

            Track updatedTrack = originalTrack.updateWithSourcePriority(
                "Updated Title",
                List.of(ArtistCredit.withName("Updated Artist")),
                Source.of("SPOTIFY", "spotify-456"),
                TrackStatus.PROVISIONAL
            );

            // Metadata should remain unchanged
            assertEquals("Original Title", updatedTrack.title());
            assertEquals(List.of("Original Artist"), updatedTrack.artistNames());
            assertEquals(TrackStatus.VERIFIED, updatedTrack.status());
            
            // But source should be added
            assertEquals(2, updatedTrack.sources().size());
            assertTrue(updatedTrack.sources().contains(Source.of("TIDAL", "tidal-123")));
            assertTrue(updatedTrack.sources().contains(Source.of("SPOTIFY", "spotify-456")));
        }

        @Test
        @DisplayName("Should update metadata when new source has equal priority")
        void updateMetadataWhenNewSourceHasEqualPriority() {
            Track originalTrack = Track.of(
                TEST_ISRC,
                "Original Title",
                List.of(ArtistCredit.withName("Original Artist")),
                List.of(Source.of("SPOTIFY", "spotify-123")),
                TrackStatus.PROVISIONAL
            );

            Track updatedTrack = originalTrack.updateWithSourcePriority(
                "Updated Title",
                List.of(ArtistCredit.withName("Updated Artist")),
                Source.of("SPOTIFY", "spotify-456"),
                TrackStatus.VERIFIED
            );

            assertEquals("Updated Title", updatedTrack.title());
            assertEquals(List.of("Updated Artist"), updatedTrack.artistNames());
            assertEquals(TrackStatus.VERIFIED, updatedTrack.status());
        }

        @Test
        @DisplayName("Should not add duplicate source")
        void notAddDuplicateSource() {
            Track originalTrack = Track.of(
                TEST_ISRC,
                "Original Title",
                List.of(ArtistCredit.withName("Original Artist")),
                List.of(Source.of("SPOTIFY", "spotify-123")),
                TrackStatus.PROVISIONAL
            );

            Track updatedTrack = originalTrack.updateWithSourcePriority(
                "Updated Title",
                List.of(ArtistCredit.withName("Updated Artist")),
                Source.of("SPOTIFY", "spotify-123"), // Same source
                TrackStatus.VERIFIED
            );

            assertEquals(1, updatedTrack.sources().size());
            assertEquals("Updated Title", updatedTrack.title());
        }

        @Test
        @DisplayName("Should handle partial updates (null values)")
        void handlePartialUpdates() {
            Track originalTrack = Track.of(
                TEST_ISRC,
                "Original Title",
                List.of(ArtistCredit.withName("Original Artist")),
                List.of(Source.of("SPOTIFY", "spotify-123")),
                TrackStatus.PROVISIONAL
            );

            Track updatedTrack = originalTrack.updateWithSourcePriority(
                null, // Keep original title
                null, // Keep original artists
                Source.of("TIDAL", "tidal-456"),
                null  // Keep original status
            );

            assertEquals("Original Title", updatedTrack.title());
            assertEquals(List.of("Original Artist"), updatedTrack.artistNames());
            assertEquals(TrackStatus.PROVISIONAL, updatedTrack.status());
            assertTrue(updatedTrack.sources().contains(Source.of("TIDAL", "tidal-456")));
        }

        @Test
        @DisplayName("Should reject null source")
        void rejectNullSource() {
            Track track = Track.of(
                TEST_ISRC,
                "Test Title",
                List.of(ArtistCredit.withName("Artist")),
                List.of(Source.of("SPOTIFY", "spotify-123")),
                TrackStatus.PROVISIONAL
            );

            assertThrows(NullPointerException.class, 
                () -> track.updateWithSourcePriority("New Title", List.of(ArtistCredit.withName("New Artist")), null, TrackStatus.VERIFIED));
        }

        @Test
        @DisplayName("Should handle MANUAL source overriding everything")
        void handleManualSourceOverridingEverything() {
            Track originalTrack = Track.of(
                TEST_ISRC,
                "Original Title",
                List.of(ArtistCredit.withName("Original Artist")),
                List.of(
                    Source.of("TIDAL", "tidal-123"),
                    Source.of("SPOTIFY", "spotify-456")
                ),
                TrackStatus.PROVISIONAL
            );

            Track updatedTrack = originalTrack.updateWithSourcePriority(
                "Manual Title",
                List.of(ArtistCredit.withName("Manual Artist")),
                Source.of("MANUAL", "manual-789"),
                TrackStatus.VERIFIED
            );

            assertEquals("Manual Title", updatedTrack.title());
            assertEquals(List.of("Manual Artist"), updatedTrack.artistNames());
            assertEquals(TrackStatus.VERIFIED, updatedTrack.status());
            assertEquals(3, updatedTrack.sources().size());
            assertEquals("MANUAL", updatedTrack.getHighestPrioritySource().sourceName());
        }
    }
}