package com.musichub.producer.adapter.persistence;

import com.musichub.producer.domain.model.Producer;
import com.musichub.producer.domain.values.ProducerId;
import com.musichub.shared.domain.values.ISRC;
import com.musichub.shared.domain.values.ProducerCode;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.TestTransaction;
import jakarta.inject.Inject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
@DisplayName("ProducerRepositoryImpl Integration Tests")
class ProducerRepositoryImplTest {

    @Inject
    ProducerRepositoryImpl repository;

    @Test
    @TestTransaction
    @DisplayName("Should save producer with tracks and retrieve by producer code with complete data")
    void save_and_findByProducerCode_roundtrip_with_tracks() {
        // Given
        ProducerCode producerCode = ProducerCode.of("FRLA1");
        Producer producer = Producer.createNew(producerCode, "Test Producer");
        producer.addTrack("FRLA12400001");
        producer.addTrack("FRLA12400002");

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

    @Test
    @TestTransaction
    @DisplayName("Should save producer with null name and retrieve correctly")
    void save_and_findByProducerCode_with_null_name() {
        // Given
        ProducerCode code = ProducerCode.of("FRLA2");
        Producer producer = Producer.createNew(code, null);
        producer.addTrack("FRLA12400001");
        producer.addTrack("FRLA12400002");

        // When
        Producer saved = repository.save(producer);

        // Then
        assertNotNull(saved);
        Optional<Producer> foundOptional = repository.findByProducerCode(code);
        assertTrue(foundOptional.isPresent());
        
        Producer found = foundOptional.get();
        assertEquals(2, found.tracks().size());
        assertTrue(found.hasTrack(ISRC.of("FRLA12400001")));
        assertTrue(found.hasTrack(ISRC.of("FRLA12400002")));
    }

    @Test
    @TestTransaction
    @DisplayName("Should return empty optional when producer not found by code")
    void findByProducerCode_shouldReturnEmptyWhenNotFound() {
        // Given
        ProducerCode nonExistentCode = ProducerCode.of("NONE1");

        // When
        Optional<Producer> result = repository.findByProducerCode(nonExistentCode);

        // Then
        assertFalse(result.isPresent(), "Should return empty optional for non-existent producer");
    }

    @Test
    @TestTransaction
    @DisplayName("Should update existing producer when saving with same ID")
    void save_shouldUpdateExistingProducerWithSameId() {
        // Given
        ProducerCode producerCode = ProducerCode.of("UPDT1");
        Producer originalProducer = Producer.createNew(producerCode, "Original Name");
        originalProducer.addTrack("UPDT12400001");
        
        // Save original
        Producer savedOriginal = repository.save(originalProducer);
        
        // Modify producer
        savedOriginal.rename("Updated Name");
        savedOriginal.addTrack("UPDT12400002");

        // When - Save updated producer
        repository.save(savedOriginal);

        // Then
        Optional<Producer> retrieved = repository.findByProducerCode(producerCode);
        assertTrue(retrieved.isPresent(), "Updated producer should be found");
        
        Producer result = retrieved.get();
        assertEquals("Updated Name", result.name(), "Name should be updated");
        assertEquals(2, result.tracks().size(), "Should have 2 tracks after update");
        assertTrue(result.hasTrack(ISRC.of("UPDT12400001")), "Should retain original track");
        assertTrue(result.hasTrack(ISRC.of("UPDT12400002")), "Should contain new track");
    }

    @Test
    @TestTransaction
    @DisplayName("Should maintain producer identity consistency across save operations")
    void save_shouldMaintainIdentityConsistency() {
        // Given
        ProducerCode code = ProducerCode.of("IDEN1");
        Producer producer1 = Producer.createNew(code, "Test");
        Producer producer2 = Producer.createNew(code, "Different Name");

        // When
        Producer saved1 = repository.save(producer1);
        Producer saved2 = repository.save(producer2);

        // Then - Same producer code should generate same ID (deterministic)
        assertEquals(saved1.id(), saved2.id(), "Producers with same code should have same deterministic ID");
    }

    @Test
    @TestTransaction
    @DisplayName("Should handle producers with no tracks correctly")
    void save_shouldHandleProducerWithoutTracks() {
        // Given
        ProducerCode code = ProducerCode.of("EMPT1");
        Producer producerWithoutTracks = Producer.createNew(code, "Empty Producer");

        // When
        repository.save(producerWithoutTracks);
        Optional<Producer> retrieved = repository.findByProducerCode(code);

        // Then
        assertTrue(retrieved.isPresent(), "Producer without tracks should be saved and retrieved");
        assertEquals(0, retrieved.get().tracks().size(), "Retrieved producer should have no tracks");
        assertEquals("Empty Producer", retrieved.get().name(), "Name should be preserved");
    }

    @Test
    @TestTransaction
    @DisplayName("Should handle null producer name correctly")
    void save_shouldHandleNullNameCorrectly() {
        // Given
        ProducerCode code = ProducerCode.of("NULL1");
        Producer producerWithNullName = Producer.createNew(code, null);

        // When
        repository.save(producerWithNullName);
        Optional<Producer> retrieved = repository.findByProducerCode(code);

        // Then
        assertTrue(retrieved.isPresent(), "Producer with null name should be saved and retrieved");
        assertNull(retrieved.get().name(), "Null name should be preserved");
    }

    @Test
    @TestTransaction
    @DisplayName("Should find producer by ID and map correctly")
    void findById_shouldMapCorrectly() {
        // Given
        ProducerCode code = ProducerCode.of("BYID1");
        Producer producer = Producer.createNew(code, "Find By ID Test");
        producer.addTrack("BYID12400001");
        
        // Save producer
        Producer saved = repository.save(producer);
        ProducerId savedId = saved.id();

        // When - Find by ID
        Optional<Producer> found = repository.findById(savedId);

        // Then
        assertTrue(found.isPresent(), "Producer should be found by ID");
        Producer foundProducer = found.get();
        assertEquals(savedId, foundProducer.id(), "ID should match");
        assertEquals(code, foundProducer.producerCode(), "Producer code should match");
        assertEquals("Find By ID Test", foundProducer.name(), "Name should match");
        assertEquals(1, foundProducer.tracks().size(), "Should have one track");
        assertTrue(foundProducer.hasTrack(ISRC.of("BYID12400001")), "Should contain the correct track");
    }

    @Test
    @TestTransaction
    @DisplayName("Should return empty when finding by non-existent ID")
    void findById_shouldReturnEmptyForNonExistentId() {
        // Given
        UUID nonExistentUuid = UUID.fromString("00000000-0000-0000-0000-000000000000");
        ProducerId nonExistentId = new ProducerId(nonExistentUuid);

        // When
        Optional<Producer> result = repository.findById(nonExistentId);

        // Then
        assertFalse(result.isPresent(), "Should return empty optional for non-existent ID");
    }
}
