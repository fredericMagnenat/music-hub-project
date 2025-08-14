package com.musichub.producer.adapter.persistence;

import com.musichub.producer.domain.model.Producer;
import com.musichub.shared.domain.values.ProducerCode;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
class ProducerRepositoryImplTest {

    @Inject
    ProducerRepositoryImpl repository;

    @Test
    @Transactional
    void save_and_findByProducerCode_roundtrip_with_tracks() {
        ProducerCode code = ProducerCode.of("FRLA1");
        Producer producer = Producer.createNew(code, null);
        producer.addTrack("FRLA12400001");
        producer.addTrack("FRLA12400002");

        Producer saved = repository.save(producer);
        assertNotNull(saved);

        Producer found = repository.findByProducerCode(code).orElseThrow();
        assertEquals(2, found.tracks().size());
        assertTrue(found.hasTrack(com.musichub.shared.domain.values.ISRC.of("FRLA12400001")));
    }
}
