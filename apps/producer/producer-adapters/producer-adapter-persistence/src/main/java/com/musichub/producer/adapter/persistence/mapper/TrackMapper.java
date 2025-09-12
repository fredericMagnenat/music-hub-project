package com.musichub.producer.adapter.persistence.mapper;

import java.util.List;

import com.musichub.producer.adapter.persistence.entity.ArtistCreditEmbeddable;
import com.musichub.producer.adapter.persistence.entity.TrackEntity;
import com.musichub.producer.domain.model.Track;
import com.musichub.producer.domain.values.ArtistCredit;
import com.musichub.shared.domain.values.Source;
import com.musichub.producer.domain.values.TrackStatus;
import com.musichub.shared.domain.id.ArtistId;
import com.musichub.shared.domain.id.TrackId;
import com.musichub.shared.domain.values.ISRC;

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
        entity.setCredits(mapCreditsToEmbeddable(domain.credits()));
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

        List<ArtistCredit> credits = entity.getCredits() != null ?
                mapEmbeddableToCredits(entity.getCredits()) : List.of();
        return Track.of(isrc, entity.getTitle(), credits, sources, status);
    }

    /**
     * Maps domain ArtistCredit objects to persistence ArtistCreditEmbeddable objects.
     */
    private static List<ArtistCreditEmbeddable> mapCreditsToEmbeddable(List<ArtistCredit> credits) {
        return credits.stream()
                .map(credit -> ArtistCreditEmbeddable.with(
                        credit.artistName(),
                        credit.artistId() != null ? credit.artistId().value() : null))
                .toList();
    }

    /**
     * Maps persistence ArtistCreditEmbeddable objects to domain ArtistCredit objects.
     */
    private static List<ArtistCredit> mapEmbeddableToCredits(List<ArtistCreditEmbeddable> embeddables) {
        if (embeddables == null) {
            return List.of();
        }
        return embeddables.stream()
                .map(embeddable -> ArtistCredit.with(
                        embeddable.getArtistName(),
                        embeddable.getArtistId() != null ? new ArtistId(embeddable.getArtistId()) : null))
                .toList();
    }

    // Note: JSON serialization/deserialization is handled automatically by Hibernate + JsonB
    // thanks to @JdbcTypeCode(SqlTypes.JSON) annotation on the sources field
}
