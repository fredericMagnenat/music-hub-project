package com.musichub.producer.application.ports.in;

import com.musichub.producer.application.dto.TrackInfo;

import java.util.List;

/**
 * Use case for retrieving recent tracks across all producers.
 */
public interface GetRecentTracksUseCase {
    
    /**
     * Retrieves the most recently submitted tracks from all producers.
     * 
     * @param limit maximum number of tracks to return (defaults to 10)
     * @return list of tracks ordered by submission date (newest first)
     */
    List<TrackInfo> getRecentTracks(int limit);
    
    /**
     * Retrieves the 10 most recently submitted tracks from all producers.
     * 
     * @return list of tracks ordered by submission date (newest first)
     */
    default List<TrackInfo> getRecentTracks() {
        return getRecentTracks(10);
    }
}