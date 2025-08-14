package com.musichub.producer.application;

import com.musichub.producer.domain.model.Producer;
import com.musichub.producer.domain.ports.out.ProducerRepository;
import com.musichub.shared.domain.values.ProducerCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ProducerServiceTest {

    private ProducerRepository repository;
    private ProducerService service;

    @BeforeEach
    void setUp() {
        repository = mock(ProducerRepository.class);
        service = new ProducerService(repository);
    }

    @Test
    void handle_createsProducerWhenAbsent_andAddsTrack_idempotent() {
        String input = "fr-la1-24-00001";
        ProducerCode code = ProducerCode.of("FRLA1");
        Producer newProducer = Producer.createNew(code, null);

        when(repository.findByProducerCode(code)).thenReturn(Optional.empty());
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Producer result = service.handle(input);

        verify(repository).findByProducerCode(code);
        verify(repository).save(any());
        assertTrue(result.hasTrack(com.musichub.shared.domain.values.ISRC.of("FRLA12400001")));
        assertFalse(result.addTrack("FRLA12400001")); // idempotent
    }

    @Test
    void handle_reusesExistingProducer() {
        String input = "FRLA12400001";
        ProducerCode code = ProducerCode.of("FRLA1");
        Producer existing = Producer.createNew(code, null);

        when(repository.findByProducerCode(code)).thenReturn(Optional.of(existing));
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Producer result = service.handle(input);
        assertTrue(result.hasTrack(com.musichub.shared.domain.values.ISRC.of("FRLA12400001")));
    }
}
