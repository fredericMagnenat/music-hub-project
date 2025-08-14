package com.musichub.producer.adapter.persistence;

import com.musichub.producer.domain.model.Producer;
import com.musichub.producer.domain.values.ProducerId;
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
        return Producer.createNew(
                ProducerCode.of(dbo.producerCode),
                dbo.name
        );
        // Note: Tracks mapped below to preserve existing set
    }

    public static Producer toDomainWithTracks(ProducerEntity dbo) {
        Producer producer = toDomain(dbo);
        dbo.tracks.forEach(isrc -> producer.addTrack(ISRC.of(isrc)));
        return producer;
    }
}
