package com.musichub.artist.adapter.rest.mapper;

import com.musichub.artist.adapter.rest.dto.ArtistResponse;
import com.musichub.artist.adapter.rest.dto.ContributionResponse;
import com.musichub.artist.adapter.rest.dto.SourceResponse;
import com.musichub.artist.domain.model.Artist;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Mapper for converting Artist domain models to REST response DTOs.
 * Handles the assembly of producerIds as required by AC 4.
 */
public class ArtistResponseMapper {

    /**
     * Maps an Artist domain model to an ArtistResponse DTO.
     *
     * @param artist the Artist domain model
     * @return the corresponding ArtistResponse DTO
     */
    public static ArtistResponse toResponse(Artist artist) {
        return toResponse(artist, Collections.emptyList());
    }

    /**
     * Maps an Artist domain model to an ArtistResponse DTO with producer IDs.
     *
     * @param artist the Artist domain model
     * @param producerIds the list of producer IDs this artist has collaborated with
     * @return the corresponding ArtistResponse DTO
     */
    public static ArtistResponse toResponse(Artist artist, List<UUID> producerIds) {
        List<ContributionResponse> contributions = artist.getContributions().stream()
                .map(contribution -> new ContributionResponse(
                    contribution.trackId().value(),
                    contribution.title(),
                    contribution.isrc().value()
                ))
                .collect(Collectors.toList());

        List<SourceResponse> sources = artist.getSources().stream()
                .map(source -> new SourceResponse(
                    source.sourceType().name(),
                    source.sourceId()
                ))
                .collect(Collectors.toList());

        return new ArtistResponse(
            artist.getId().value(),
            artist.getNameValue(),
            artist.getStatus().name(),
            contributions,
            sources,
            producerIds
        );
    }
}