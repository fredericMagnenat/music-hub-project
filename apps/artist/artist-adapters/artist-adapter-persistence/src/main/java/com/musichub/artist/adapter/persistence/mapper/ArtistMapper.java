package com.musichub.artist.adapter.persistence.mapper;

import com.musichub.artist.adapter.persistence.entity.ArtistEntity;
import com.musichub.artist.adapter.persistence.entity.ContributionEntity;
import com.musichub.artist.adapter.persistence.entity.SourceEntity;
import com.musichub.artist.domain.model.Artist;
import com.musichub.artist.domain.model.ArtistStatus;
import com.musichub.artist.domain.values.ArtistName;
import com.musichub.artist.domain.values.Contribution;
import com.musichub.shared.domain.id.ArtistId;
import com.musichub.shared.domain.id.TrackId;
import com.musichub.shared.domain.values.ISRC;
import com.musichub.shared.domain.values.Source;
import com.musichub.shared.domain.values.SourceType;

import org.mapstruct.*;

import java.util.List;
import java.util.UUID;

/**
 * MapStruct mapper between Artist domain model and JPA entities.
 * Handles conversion of rich domain aggregates to/from persistence format.
 */
@Mapper(
    componentModel = "cdi",
    unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface ArtistMapper {

    /**
     * Converts Artist domain model to JPA entity.
     */
    @Mapping(target = "id", source = "id", qualifiedByName = "artistIdToUuid")
    @Mapping(target = "name", source = "nameValue")
    @Mapping(target = "status", source = "status")
    @Mapping(target = "contributions", source = "contributions")
    @Mapping(target = "sources", source = "sources")
    ArtistEntity toDbo(Artist domain);

    /**
     * Converts JPA entity to Artist domain model.
     * Uses custom method due to Artist.from() factory requirements.
     */
    default Artist toDomain(ArtistEntity entity) {
        if (entity == null) {
            return null;
        }

        ArtistId id = new ArtistId(entity.id);
        ArtistName artistName = ArtistName.of(entity.name);
        List<Contribution> contributions = entity.contributions.stream()
                .map(this::mapEntityToContribution)
                .toList();
        List<Source> sources = entity.sources.stream()
                .map(this::mapEntityToSource)
                .toList();

        return Artist.from(id, artistName, entity.status, contributions, sources);
    }

    /**
     * Maps Contribution domain object to ContributionEntity.
     */
    @Mapping(target = "trackId", source = "trackId", qualifiedByName = "trackIdToUuid")
    @Mapping(target = "title", source = "title")
    @Mapping(target = "isrc", source = "isrc", qualifiedByName = "isrcToString")
    ContributionEntity mapContributionToEntity(Contribution contribution);

    /**
     * Maps ContributionEntity to Contribution domain object.
     */
    default Contribution mapEntityToContribution(ContributionEntity entity) {
        if (entity == null) {
            return null;
        }
        return Contribution.of(
            new TrackId(entity.trackId),
            entity.title,
            ISRC.of(entity.isrc)
        );
    }

    /**
     * Maps Source domain object to SourceEntity.
     */
    @Mapping(target = "sourceType", source = "sourceType")
    @Mapping(target = "sourceId", source = "sourceId")
    SourceEntity mapSourceToEntity(Source source);

    /**
     * Maps SourceEntity to Source domain object.
     */
    default Source mapEntityToSource(SourceEntity entity) {
        if (entity == null) {
            return null;
        }
        return Source.of(entity.sourceType.name(), entity.sourceId);
    }

    // Custom mapping methods for ID conversions

    @Named("artistIdToUuid")
    default UUID artistIdToUuid(ArtistId artistId) {
        return artistId != null ? artistId.value() : null;
    }

    @Named("trackIdToUuid")
    default UUID trackIdToUuid(TrackId trackId) {
        return trackId != null ? trackId.value() : null;
    }

    @Named("isrcToString")
    default String isrcToString(ISRC isrc) {
        return isrc != null ? isrc.value() : null;
    }
}