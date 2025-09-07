package com.musichub.shared.events;

/**
 * Represents source information in event payloads.
 * Contains the source name (platform) and external ID.
 */
public record SourceInfo(
    String sourceName,
    String sourceId
) {}