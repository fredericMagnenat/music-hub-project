package com.musichub.producer.adapter.persistence.adapter;


import com.musichub.producer.adapter.persistence.config.PersistenceTestProfile;
import com.musichub.producer.domain.model.Producer;
import com.musichub.producer.domain.values.ArtistCredit;
import com.musichub.shared.domain.values.Source;
import com.musichub.shared.domain.values.ISRC;
import com.musichub.shared.domain.values.ProducerCode;
import io.quarkus.test.TestTransaction;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import jakarta.inject.Inject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
@DisplayName("ProducerRepositoryImpl Integration Tests")
@TestProfile(PersistenceTestProfile.class)
class ProducerRepositoryAdapterTest {

    @Inject
    ProducerRepositoryAdapter repository;

    @Test
    @TestTransaction
    @DisplayName("Should save producer with tracks and retrieve by producer code with complete data")
    void save_and_findByProducerCode_roundtrip_with_tracks() {
        // Given
        ProducerCode producerCode = ProducerCode.of("FRLA1");
        Producer producer = Producer.createNew(producerCode, "Test Producer");
        Source source = Source.of("SPOTIFY", "test-source");
        producer.registerTrack(ISRC.of("FRLA12400001"), "Track 1", List.of(ArtistCredit.withName("Artist 1")), List.of(source));
        producer.registerTrack(ISRC.of("FRLA12400002"), "Track 2", List.of(ArtistCredit.withName("Artist 2")), List.of(source));

        // When - Save
        Producer savedProducer = repository.save(producer);

        // Then - Verify save result
        assertNotNull(savedProducer, "Saved producer should not be null");
        assertEquals(producer.id(), savedProducer.id(), "Producer ID should be preserved");
        assertEquals(producer.producerCode(), savedProducer.producerCode(), "Producer code should be preserved");

        // When - Find by producer code
        Optional<Producer> foundProducer = repository.findByProducerCode(producerCode);

        // Then - Verify retrieval
        assertTrue(foundProducer.isPresent(), "Producer should be found by producer code");
        Producer retrievedProducer = foundProducer.get();

        assertEquals(producer.id(), retrievedProducer.id(), "Retrieved producer should have same ID");
        assertEquals(producer.producerCode(), retrievedProducer.producerCode(), "Retrieved producer should have same code");
        assertEquals(producer.name(), retrievedProducer.name(), "Retrieved producer should have same name");

        // Verify tracks are properly persisted and retrieved
        assertEquals(2, retrievedProducer.tracks().size(), "Producer should have 2 tracks");
        assertTrue(retrievedProducer.hasTrack(ISRC.of("FRLA12400001")), "Producer should contain first track");
        assertTrue(retrievedProducer.hasTrack(ISRC.of("FRLA12400002")), "Producer should contain second track");
    }
}
