package com.musichub.producer.adapter.persistence.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.musichub.producer.adapter.persistence.entity.TrackEntity;
import com.musichub.producer.domain.model.Track;
import com.musichub.producer.domain.values.Source;
import com.musichub.producer.domain.values.TrackStatus;
import com.musichub.shared.domain.values.ISRC;

import java.util.List;

/**
 * Mapper between Track domain model and TrackEntity persistence model.
 * Handles JSON serialization/deserialization of Sources and Value Object conversions.
 */
public final class TrackMapper {

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final TypeReference<List<Source>> SOURCE_LIST_TYPE = new TypeReference<>() {};

    private TrackMapper() {
        // Utility class - prevent instantiation
    }

    /**
     * Converts a Track domain object to TrackEntity persistence object.
     *
     * @param domain the Track domain object
     * @return TrackEntity or null if domain is null
     */
    public static TrackEntity toDbo(Track domain) {
        if (domain == null) {
            return null;
        }

        TrackEntity entity = new TrackEntity();
        entity.isrc = domain.isrc().value();
        entity.title = domain.title();
        entity.status = domain.status().name();
        entity.artistNames = List.copyOf(domain.artistNames());
        entity.sourcesJson = serializeSourcesToJson(domain.sources());

        return entity;
    }

    /**
     * Converts a TrackEntity persistence object to Track domain object.
     *
     * @param entity the TrackEntity persistence object
     * @return Track domain object or null if entity is null
     */
    public static Track toDomain(TrackEntity entity) {
        if (entity == null) {
            return null;
        }

        ISRC isrc = ISRC.of(entity.isrc);
        TrackStatus status = TrackStatus.valueOf(entity.status);
        List<Source> sources = deserializeSourcesFromJson(entity.sourcesJson);

        return Track.of(isrc, entity.title, entity.artistNames, sources, status);
    }

    private static String serializeSourcesToJson(List<Source> sources) {
        try {
            return objectMapper.writeValueAsString(sources);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize sources to JSON: " + sources, e);
        }
    }

    private static List<Source> deserializeSourcesFromJson(String sourcesJson) {
        if (sourcesJson == null) {
            // Return null to let Track.of() validate and throw proper domain exception
            return null;
        }
        
        try {
            return objectMapper.readValue(sourcesJson, SOURCE_LIST_TYPE);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to deserialize sources from JSON: " + sourcesJson, e);
        }
    }
}
