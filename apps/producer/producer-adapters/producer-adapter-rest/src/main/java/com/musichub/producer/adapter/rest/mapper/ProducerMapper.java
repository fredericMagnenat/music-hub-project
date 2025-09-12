package com.musichub.producer.adapter.rest.mapper;

import com.musichub.producer.adapter.rest.dto.response.ProducerResponse;
import com.musichub.producer.adapter.rest.dto.response.TrackResponse;
import com.musichub.producer.adapter.rest.dto.response.ArtistCreditResponse;
import com.musichub.producer.adapter.rest.dto.response.SourceResponse;
import com.musichub.producer.domain.model.Producer;
import com.musichub.producer.domain.model.Track;
import com.musichub.producer.domain.values.ArtistCredit;
import com.musichub.shared.domain.values.Source;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.Set;

@Mapper(componentModel = "cdi")
public interface ProducerMapper {

    @Mapping(target = "id", expression = "java(producer.id().value().toString())")
    @Mapping(target = "producerCode", expression = "java(producer.producerCode().value())")
    @Mapping(target = "name", expression = "java(producer.name())")
    @Mapping(target = "tracks", expression = "java(mapTracks(producer.tracks()))")
    ProducerResponse toResponse(Producer producer);

    @Named("mapTracks")
    default Set<TrackResponse> mapTracks(Set<Track> tracks) {
        return tracks.stream()
            .map(this::mapTrack)
            .collect(java.util.stream.Collectors.toCollection(java.util.LinkedHashSet::new));
    }

    @Mapping(target = "isrc", expression = "java(track.isrc().value())")
    @Mapping(target = "title", expression = "java(track.title())")
    @Mapping(target = "credits", expression = "java(mapCredits(track.credits()))")
    @Mapping(target = "sources", expression = "java(mapSources(track.sources()))")
    @Mapping(target = "status", expression = "java(track.status().name())")
    TrackResponse mapTrack(Track track);

    @Named("mapCredits")
    default java.util.List<ArtistCreditResponse> mapCredits(java.util.List<ArtistCredit> credits) {
        return credits.stream()
            .map(this::mapArtistCredit)
            .collect(java.util.ArrayList::new, java.util.List::add, java.util.List::addAll);
    }

    @Named("mapSources")
    default java.util.List<SourceResponse> mapSources(java.util.List<Source> sources) {
        return sources.stream()
            .map(this::mapSource)
            .collect(java.util.ArrayList::new, java.util.List::add, java.util.List::addAll);
    }

    @Mapping(target = "artistName", expression = "java(credit.artistName())")
    @Mapping(target = "artistId", expression = "java(credit.artistId() != null ? credit.artistId().value().toString() : null)")
    ArtistCreditResponse mapArtistCredit(ArtistCredit credit);

    @Mapping(target = "name", expression = "java(source.getSourceName())")
    @Mapping(target = "id", expression = "java(source.sourceId())")
    SourceResponse mapSource(Source source);
}