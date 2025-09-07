package com.musichub.producer.domain.model;

import com.musichub.producer.domain.values.ArtistCredit;
import com.musichub.producer.domain.values.Source;
import com.musichub.producer.domain.values.TrackStatus;
import com.musichub.shared.domain.values.ISRC;
import com.musichub.shared.domain.values.ProducerCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ProducerAddTrackWithEntityTest {

    @Test
    @DisplayName("Should add valid Track entity and be idempotent on duplicate")
    void addValidTrackEntity_isIdempotent() {
        var producer = Producer.createNew(ProducerCode.of("FRLA1"), null);
        var track = new Track(ISRC.of("FRLA12400001"), "Title", List.of(ArtistCredit.withName("Artist")), List.of(Source.of("SPOTIFY", "track123")), TrackStatus.PROVISIONAL);

        assertTrue(producer.addTrack(track));
        assertFalse(producer.addTrack(track));
        assertTrue(producer.hasTrack(ISRC.of("FRLA12400001")));
    }

    @Test
    @DisplayName("Should reject Track with mismatched producer code")
    void rejectTrackWithMismatchedProducerCode() {
        var producer = Producer.createNew(ProducerCode.of("GBUM7"), null);
        var track = new Track(ISRC.of("FRLA12400001"), "Title", List.of(ArtistCredit.withName("Artist")), List.of(Source.of("SPOTIFY", "track123")), TrackStatus.PROVISIONAL);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> producer.addTrack(track));
        assertTrue(ex.getMessage().contains("producer code"));
    }

    @Test
    @DisplayName("Should reject null Track")
    void rejectNullTrack() {
        var producer = Producer.createNew(ProducerCode.of("FRLA1"), null);
        assertThrows(NullPointerException.class, () -> producer.addTrack((Track) null));
    }
}
