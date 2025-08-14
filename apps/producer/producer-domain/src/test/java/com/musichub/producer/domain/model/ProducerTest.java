package com.musichub.producer.domain.model;

import com.musichub.shared.domain.values.ISRC;
import com.musichub.shared.domain.values.ProducerCode;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ProducerTest {

    @Test
    void createNew_generatesDeterministicIdFromCode() {
        ProducerCode code = ProducerCode.of("FRLA1");
        Producer p1 = Producer.createNew(code, null);
        Producer p2 = Producer.createNew(code, "Any");
        assertEquals(p1.id(), p2.id());
        assertEquals(code, p1.producerCode());
    }

    @Test
    void addTrack_isIdempotent_andNormalizesInput() {
        Producer producer = Producer.createNew(ProducerCode.of("FRLA1"), null);
        assertTrue(producer.addTrack("fr-la1-24-00001")); // first add
        assertFalse(producer.addTrack("FRLA12400001")); // duplicate after normalization
        assertTrue(producer.hasTrack(ISRC.of("FRLA12400001")));
    }

    @Test
    void hasTrack_returnsFalseWhenAbsent() {
        Producer producer = Producer.createNew(ProducerCode.of("FRLA1"), null);
        assertFalse(producer.hasTrack(ISRC.of("FRLA12400001")));
    }
}
