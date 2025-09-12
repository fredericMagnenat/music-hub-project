package com.musichub.producer.application.ports.out;

import java.util.List;

import com.musichub.producer.application.dto.TrackInfo;

/**
 * Repository port for Track-specific queries that span across producers.
 * 
 * Note: This is different from Producer aggregate operations.
 * Use this for read-only track queries and analytics.
 */
public interface TrackRepository {

    /**
     * Retrieves the most recent tracks across all producers.
     * 
     * @param limit maximum number of tracks to return
     * @return list of track information ordered by submission date (newest first)
     */
    List<TrackInfo> findRecentTracks(int limit);
}
