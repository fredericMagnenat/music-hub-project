package com.musichub.shared.events;

import com.musichub.shared.domain.values.ISRC;

import java.util.List;

public record TrackWasRegistered(
    ISRC isrc,
    String title,
    List<String> artistNames
) {}