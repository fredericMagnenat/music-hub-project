package com.musichub.producer.domain.ports.out;

import com.musichub.producer.domain.model.Producer;
import com.musichub.producer.domain.values.ProducerId;
import com.musichub.shared.domain.values.ProducerCode;

import java.util.Optional;

public interface ProducerRepository {
    Optional<Producer> findById(ProducerId id);
    Optional<Producer> findByProducerCode(ProducerCode code);
    Producer save(Producer producer);
}
