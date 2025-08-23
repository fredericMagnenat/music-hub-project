package com.musichub.producer.adapter.persistence.mapper;

import com.musichub.producer.adapter.persistence.entity.ProducerEntity;
import com.musichub.producer.domain.model.Producer;
import com.musichub.producer.domain.model.Track;
import com.musichub.producer.domain.values.ProducerId;
import com.musichub.producer.domain.values.Source;
import com.musichub.producer.domain.values.TrackStatus;
import com.musichub.shared.domain.values.ISRC;
import com.musichub.shared.domain.values.ProducerCode;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Mapper between Producer domain model and ProducerEntity persistence model.
 * Handles Value Object conversions and Set transformations.
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
        entity.tracks = domain.tracks().stream()
                .map(track -> track.isrc().value())
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
        
        // Note: We only have ISRC from persistence, so we create basic Track objects
        // The complete metadata would typically come from another source/query
        Set<Track> tracks = entity.tracks == null ? Set.of() :
                entity.tracks.stream().map(isrcValue -> {
                    ISRC isrc = ISRC.of(isrcValue);
                    // Create a minimal Track object - in a real system this might be enhanced
                    // with data from another table or reconstructed from events
                    Source defaultSource = Source.of("MANUAL", isrcValue);
                    return Track.of(isrc, "Unknown Title", List.of("Unknown Artist"), List.of(defaultSource), TrackStatus.PROVISIONAL);
                }).collect(Collectors.toCollection(java.util.LinkedHashSet::new));

        return Producer.from(producerId, producerCode, entity.name, tracks);
    }
}