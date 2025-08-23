package com.musichub.producer.domain.model;

import com.musichub.producer.domain.values.Source;
import com.musichub.producer.domain.values.TrackStatus;
import com.musichub.shared.domain.values.ISRC;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TrackTest {

    @Test
    @DisplayName("Should create Track with validated fields and unmodifiable lists")
    void shouldCreateTrack() {
        ISRC isrc = ISRC.of("FRLA12400001");
        List<Source> sources = List.of(Source.of("SPOTIFY", "track123"), Source.of("TIDAL", "track456"));
        Track track = new Track(isrc, "Title", List.of("Artist 1", "Artist 2"), sources, TrackStatus.PROVISIONAL);

        assertEquals(isrc, track.isrc());
        assertEquals("Title", track.title());
        assertEquals(2, track.artistNames().size());
        assertThrows(UnsupportedOperationException.class, () -> track.artistNames().add("X"));
        assertEquals(2, track.sources().size());
        assertThrows(UnsupportedOperationException.class, () -> track.sources().add(Source.of("DEEZER", "track789")));
        assertEquals(TrackStatus.PROVISIONAL, track.status());
    }

    @Test
    @DisplayName("Equality and hashCode should rely on ISRC only")
    void equalsAndHashcodeByIsrcOnly() {
        ISRC isrc = ISRC.of("FRLA12400001");
        List<Source> sources1 = List.of(Source.of("SPOTIFY", "track123"));
        List<Source> sources2 = List.of(Source.of("TIDAL", "track456"));

        Track t1 = new Track(isrc, "Title A", List.of("Artist A"), sources1, TrackStatus.PROVISIONAL);
        Track t2 = new Track(ISRC.of("FR-LA1-24-00001"), "Different Title", List.of("Artist B"), sources2, TrackStatus.VERIFIED);

        assertEquals(t1, t2);
        assertEquals(t1.hashCode(), t2.hashCode());
    }

    @Test
    @DisplayName("Should validate inputs")
    void shouldValidateInputs() {
        ISRC isrc = ISRC.of("FRLA12400001");
        List<Source> sources = List.of(Source.of("SPOTIFY", "track123"));
        TrackStatus status = TrackStatus.PROVISIONAL;

        assertAll(
                () -> assertThrows(IllegalArgumentException.class, () -> new Track(isrc, "   ", List.of("Artist"), sources, status)),
                () -> assertThrows(IllegalArgumentException.class, () -> new Track(isrc, "Title", List.of(), sources, status)),
                () -> assertThrows(IllegalArgumentException.class, () -> new Track(isrc, "Title", List.of("  "), sources, status)),
                () -> assertThrows(IllegalArgumentException.class, () -> new Track(isrc, "Title", List.of("Artist"), List.of(), status)),
                () -> assertThrows(IllegalArgumentException.class, () -> new Track(isrc, "Title", List.of("Artist"), null, status)),
                () -> assertThrows(NullPointerException.class, () -> new Track(null, "Title", List.of("A"), sources, status)),
                () -> assertThrows(NullPointerException.class, () -> new Track(isrc, "Title", List.of("A"), sources, null))
        );
    }
}
