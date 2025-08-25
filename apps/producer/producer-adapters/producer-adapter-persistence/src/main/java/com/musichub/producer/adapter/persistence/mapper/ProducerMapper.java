package com.musichub.producer.adapter.persistence.mapper;

import com.musichub.producer.adapter.persistence.entity.ProducerEntity;
import com.musichub.producer.adapter.persistence.entity.TrackEntity;
import com.musichub.producer.domain.model.Producer;
import com.musichub.producer.domain.model.Track;
import com.musichub.producer.domain.values.ProducerId;
import com.musichub.shared.domain.values.ProducerCode;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * Mapper between Producer domain model and ProducerEntity persistence model.
 * Uses @OneToMany relationship with TrackEntity and delegates to TrackMapper.
 */
public final class ProducerMapper {

    private ProducerMapper() {
        // Utility class - prevent instantiation
    }

    /**
     * Converts a Producer domain object to ProducerEntity persistence object.
     *
     * @param domain the Producer domain object
     * @return ProducerEntity or null if domain is null
     */
    public static ProducerEntity toDbo(Producer domain) {
        if (domain == null) {
            return null;
        }

        ProducerEntity entity = new ProducerEntity();
        entity.id = domain.id().value();
        entity.producerCode = domain.producerCode().value();
        entity.name = domain.name();
        
        // Map tracks using TrackMapper and set bidirectional relationship
        entity.tracks = domain.tracks().stream()
                .map(track -> {
                    TrackEntity trackEntity = TrackMapper.toDbo(track);
                    trackEntity.setProducer(entity); // Set the bidirectional relationship
                    return trackEntity;
                })
                .collect(Collectors.toCollection(java.util.LinkedHashSet::new));

        return entity;
    }

    /**
     * Converts a ProducerEntity persistence object to Producer domain object.
     *
     * @param entity the ProducerEntity persistence object
     * @return Producer domain object or null if entity is null
     */
    public static Producer toDomain(ProducerEntity entity) {
        if (entity == null) {
            return null;
        }

        ProducerId producerId = new ProducerId(entity.id);
        ProducerCode producerCode = ProducerCode.of(entity.producerCode);
        
        // Map tracks using TrackMapper - now we have complete Track data
        Set<Track> tracks = entity.tracks == null ? Set.of() :
                entity.tracks.stream()
                        .map(TrackMapper::toDomain)
                        .collect(Collectors.toCollection(java.util.LinkedHashSet::new));

        return Producer.from(producerId, producerCode, entity.name, tracks);
    }
}