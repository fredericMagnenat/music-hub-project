package com.musichub.producer.domain.values;

import com.musichub.shared.domain.id.IdGenerator;
import com.musichub.shared.domain.values.ProducerCode;

import java.util.Objects;
import java.util.UUID;

/**
 * ProducerId is a deterministic UUIDv5 derived from the immutable ProducerCode.
 */
public record ProducerId(UUID value) {

    private static final UUID NAMESPACE_PRODUCER = UUID.fromString("550e8400-e29b-41d4-a716-446655440001");

    public ProducerId {
        if (value == null) {
            throw new IllegalArgumentException("ProducerId value must not be null");
        }
    }

    public static ProducerId fromProducerCode(ProducerCode producerCode) {
        Objects.requireNonNull(producerCode, "ProducerCode must not be null");
        UUID uuid = IdGenerator.generateUUIDv5(NAMESPACE_PRODUCER, producerCode.value());
        return new ProducerId(uuid);
    }
}
