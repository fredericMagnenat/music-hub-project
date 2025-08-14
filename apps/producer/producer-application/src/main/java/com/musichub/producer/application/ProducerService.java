package com.musichub.producer.application;

import com.musichub.producer.domain.model.Producer;
import com.musichub.producer.domain.ports.in.RegisterTrackUseCase;
import com.musichub.producer.domain.ports.out.ProducerRepository;
import com.musichub.shared.domain.values.ISRC;
import com.musichub.shared.domain.values.ProducerCode;

import java.util.Objects;

public class ProducerService implements RegisterTrackUseCase {

    private final ProducerRepository producerRepository;

    public ProducerService(ProducerRepository producerRepository) {
        this.producerRepository = Objects.requireNonNull(producerRepository);
    }

    @Override
    public Producer registerTrack(String isrcValue) {
        ISRC isrc = ISRC.of(normalizeIsrc(isrcValue));
        ProducerCode code = ProducerCode.with(isrc);

        // find or create producer
        Producer producer = producerRepository.findByProducerCode(code)
                .orElseGet(() -> Producer.createNew(code, null));

        // idempotent add of track
        producer.addTrack(isrc);

        return producerRepository.save(producer);
    }

    private static String normalizeIsrc(String input) {
        return input.replace("-", "").trim().toUpperCase();
    }
}
