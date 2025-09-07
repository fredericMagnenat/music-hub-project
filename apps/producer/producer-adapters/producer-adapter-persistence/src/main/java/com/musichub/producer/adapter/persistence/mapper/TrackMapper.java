package com.musichub.producer.adapter.persistence.mapper;

import com.musichub.producer.adapter.persistence.entity.TrackEntity;
import com.musichub.producer.domain.model.Track;
import com.musichub.producer.domain.values.Source;
import com.musichub.producer.domain.values.TrackStatus;
import com.musichub.shared.domain.id.TrackId;
import com.musichub.shared.domain.values.ISRC;

import java.util.List;

/**
 * Mapper between Track domain model and TrackEntity persistence model.
 * With @JdbcTypeCode(SqlTypes.JSON) mapping, Sources are handled directly by Hibernate.
 */
public final class TrackMapper {

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
        // Generate deterministic TrackId based on ISRC
        entity.setTrackId(TrackId.fromISRC(domain.isrc().value()));
        entity.setIsrc(domain.isrc().value());
        entity.setTitle(domain.title());
        entity.setStatus(domain.status().name());
        entity.setArtistNames(List.copyOf(domain.artistNames()));
        entity.setSources(domain.sources()); // JsonB handles serialization automatically

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

        ISRC isrc = ISRC.of(entity.getIsrc());
        TrackStatus status = TrackStatus.valueOf(entity.getStatus());
        List<Source> sources = entity.getSources(); // JsonB handles deserialization automatically

        return Track.withArtistNames(isrc, entity.getTitle(), entity.getArtistNames(), sources, status);
    }

    // Note: JSON serialization/deserialization is handled automatically by Hibernate + JsonB
    // thanks to @JdbcTypeCode(SqlTypes.JSON) annotation on the sources field
}
