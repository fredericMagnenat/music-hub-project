package com.musichub.producer.application.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import com.musichub.shared.domain.values.Source;
import com.musichub.producer.domain.values.TrackStatus;
import com.musichub.shared.domain.values.ISRC;

/**
 * Domain DTO representing track information with producer context.
 * Used for queries that need both track and producer information.
 */
public record TrackInfo(
        ISRC isrc,
        String title,
        List<String> artistNames,
        List<Source> sources,
        TrackStatus status,
        LocalDateTime submissionDate) {
    public TrackInfo {
        Objects.requireNonNull(isrc, "isrc must not be null");
        Objects.requireNonNull(title, "title must not be null");
        Objects.requireNonNull(artistNames, "artistNames must not be null");
        Objects.requireNonNull(sources, "sources must not be null");
        Objects.requireNonNull(status, "status must not be null");
        Objects.requireNonNull(submissionDate, "submissionDate must not be null");
    }
}