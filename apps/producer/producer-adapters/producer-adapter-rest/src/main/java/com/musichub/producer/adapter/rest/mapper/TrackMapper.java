package com.musichub.producer.adapter.rest.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import com.musichub.producer.adapter.rest.dto.RecentTrackResponse;
import com.musichub.producer.application.dto.TrackInfo;
import com.musichub.shared.domain.values.Source;

@Mapper(componentModel = "cdi")
public interface TrackMapper {

    @Mapping(target = "isrc", expression = "java(trackInfo.isrc().value())")
    @Mapping(target = "title", expression = "java(trackInfo.title())")
    @Mapping(target = "artistNames", expression = "java(new java.util.ArrayList<>(trackInfo.artistNames()))")
    @Mapping(target = "status", expression = "java(trackInfo.status().name())")
    @Mapping(target = "submissionDate", expression = "java(trackInfo.submissionDate())")
    @Mapping(target = "source", expression = "java(mapFirstSource(trackInfo.sources()))")
    RecentTrackResponse mapToRecentResponse(TrackInfo trackInfo);

    @Named("mapFirstSource")
    default RecentTrackResponse.SourceInfo mapFirstSource(List<Source> sources) {
        if (sources == null || sources.isEmpty()) {
            return null;
        }
        Source firstSource = sources.get(0);
        RecentTrackResponse.SourceInfo sourceInfo = new RecentTrackResponse.SourceInfo();
        sourceInfo.name = firstSource.getSourceName();
        sourceInfo.externalId = firstSource.sourceId();
        return sourceInfo;
    }
}