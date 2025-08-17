package com.musichub.producer.domain.model;

import com.musichub.producer.domain.values.Source;
import com.musichub.shared.domain.values.ISRC;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TrackTest {

    @Test
    @DisplayName("Should create Track with validated fields and unmodifiable artists list")
    void shouldCreateTrack() {
        ISRC isrc = ISRC.of("FRLA12400001");
        Source src = Source.of("SPOTIFY", "v1");
        Track track = new Track(isrc, "Title", List.of("Artist 1", "Artist 2"), src);

        assertEquals(isrc, track.isrc());
        assertEquals("Title", track.title());
        assertEquals(2, track.artistNames().size());
        assertThrows(UnsupportedOperationException.class, () -> track.artistNames().add("X"));
        assertEquals(src, track.source());
    }

    @Test
    @DisplayName("Equality and hashCode should rely on ISRC only")
    void equalsAndHashcodeByIsrcOnly() {
        ISRC isrc = ISRC.of("FRLA12400001");
        Source src1 = Source.of("SPOTIFY", "v1");
        Source src2 = Source.of("TIDAL", "v2");

        Track t1 = new Track(isrc, "Title A", List.of("Artist A"), src1);
        Track t2 = new Track(ISRC.of("FR-LA1-24-00001"), "Different Title", List.of("Artist B"), src2);

        assertEquals(t1, t2);
        assertEquals(t1.hashCode(), t2.hashCode());
    }

    @Test
    @DisplayName("Should validate inputs")
    void shouldValidateInputs() {
        ISRC isrc = ISRC.of("FRLA12400001");
        Source src = Source.of("SPOTIFY", "v1");

        assertAll(
                () -> assertThrows(IllegalArgumentException.class, () -> new Track(isrc, "   ", List.of("Artist"), src)),
                () -> assertThrows(IllegalArgumentException.class, () -> new Track(isrc, "Title", List.of(), src)),
                () -> assertThrows(IllegalArgumentException.class, () -> new Track(isrc, "Title", List.of("  "), src)),
                () -> assertThrows(NullPointerException.class, () -> new Track(null, "Title", List.of("A"), src)),
                () -> assertThrows(NullPointerException.class, () -> new Track(isrc, "Title", List.of("A"), null))
        );
    }
}
