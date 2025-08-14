package com.musichub.producer.adapter.persistence;

import com.musichub.producer.domain.model.Producer;
import com.musichub.shared.domain.values.ISRC;
import com.musichub.shared.domain.values.ProducerCode;

import java.util.stream.Collectors;

public final class ProducerMapper {

    private ProducerMapper() {}

    public static ProducerEntity toDbo(Producer domain) {
        ProducerEntity entity = new ProducerEntity();
        entity.id = domain.id().value();
        entity.producerCode = domain.producerCode().value();
        entity.name = domain.name();
        entity.tracks = domain.tracks().stream().map(ISRC::value).collect(Collectors.toSet());
        return entity;
    }

    public static Producer toDomain(ProducerEntity dbo) {
        ProducerCode code = ProducerCode.of(dbo.producerCode);
        java.util.Set<ISRC> tracks = dbo.tracks == null ? java.util.Set.of() : dbo.tracks.stream().map(ISRC::of).collect(java.util.stream.Collectors.toCollection(java.util.LinkedHashSet::new));
        return Producer.from(
                com.musichub.producer.domain.values.ProducerId.fromProducerCode(code),
                code,
                dbo.name,
                tracks
        );
    }
}
