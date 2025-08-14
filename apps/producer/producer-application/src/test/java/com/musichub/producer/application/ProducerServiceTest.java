package com.musichub.producer.application;

import com.musichub.producer.domain.model.Producer;
import com.musichub.producer.domain.ports.out.ProducerRepository;
import com.musichub.shared.domain.values.ProducerCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProducerService Application Layer Tests")
class ProducerServiceTest {

    private ProducerService service;

    @Mock
    private ProducerRepository repository;

    @BeforeEach
    void setUp() {
        repository = mock(ProducerRepository.class);
        service = new ProducerService(repository);
    }

    @Nested
    @DisplayName("Track Processing - registerTrack() method")
    class TrackProcessing {

        @Test
        @DisplayName("Should create new producer when absent, add track, and ensure idempotent behavior")
        void handle_createsProducerWhenAbsent_andAddsTrack_idempotent() {
            // Given
            String input = "fr-la1-24-00001";
            ProducerCode code = ProducerCode.of("FRLA1");
            Producer newProducer = Producer.createNew(code, null);

            when(repository.findByProducerCode(code)).thenReturn(Optional.empty());
            when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            // When
        Producer result = service.registerTrack(input);

            // Then
            verify(repository).findByProducerCode(code);
            verify(repository).save(any());
            assertTrue(result.hasTrack(com.musichub.shared.domain.values.ISRC.of("FRLA12400001")), 
                      "Producer should contain the normalized track");
            assertFalse(result.addTrack("FRLA12400001"), 
                       "Adding the same track again should return false (idempotent behavior)");
        }

        @Test
        @DisplayName("Should reuse existing producer and add track to existing collection")
        void handle_reusesExistingProducer() {
            // Given
            String input = "FRLA12400001";
            ProducerCode code = ProducerCode.of("FRLA1");
            Producer existing = Producer.createNew(code, null);

            when(repository.findByProducerCode(code)).thenReturn(Optional.of(existing));
            when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            // When
        Producer result = service.registerTrack(input);

            // Then
            verify(repository).findByProducerCode(code);
            verify(repository).save(any());
            assertTrue(result.hasTrack(com.musichub.shared.domain.values.ISRC.of("FRLA12400001")), 
                      "Existing producer should contain the new track");
        }
    }

    @Nested
    @DisplayName("Repository Interactions")
    class RepositoryInteractions {

        @Test
        @DisplayName("Should always save producer after track processing")
        void handle_shouldAlwaysSaveProducerAfterProcessing() {
            // Given
            String input = "FRLA12400001";
            ProducerCode code = ProducerCode.of("FRLA1");
            Producer existing = Producer.createNew(code, null);
            
            when(repository.findByProducerCode(code)).thenReturn(Optional.of(existing));
            when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            // When
            service.registerTrack(input);

            // Then
            verify(repository, times(1)).findByProducerCode(code);
            verify(repository, times(1)).save(any(Producer.class));
        }

        @Test
        @DisplayName("Should query repository by extracted producer code from ISRC")
        void handle_shouldQueryByExtractedProducerCode() {
            // Given
            String input = "GBUM71505078"; // Different producer code
            ProducerCode expectedCode = ProducerCode.of("GBUM7");
            
            when(repository.findByProducerCode(expectedCode)).thenReturn(Optional.empty());
            when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            // When
            service.registerTrack(input);

            // Then
            verify(repository).findByProducerCode(expectedCode);
        }
    }
}
