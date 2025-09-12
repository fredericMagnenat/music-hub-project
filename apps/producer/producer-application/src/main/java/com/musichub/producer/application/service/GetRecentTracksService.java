package com.musichub.producer.application.service;

import java.util.List;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.musichub.producer.application.dto.TrackInfo;
import com.musichub.producer.application.ports.in.GetRecentTracksUseCase;
import com.musichub.producer.application.ports.out.TrackRepository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class GetRecentTracksService implements GetRecentTracksUseCase {

    private static final Logger logger = LoggerFactory.getLogger(GetRecentTracksService.class);

    private final TrackRepository trackRepository;

    @Inject
    public GetRecentTracksService(TrackRepository trackRepository) {
        this.trackRepository = Objects.requireNonNull(trackRepository);
    }

    @Override
    public List<TrackInfo> getRecentTracks(int limit) {
        if (limit <= 0) {
            throw new IllegalArgumentException("Limit must be positive, got: " + limit);
        }
        if (limit > 100) {
            throw new IllegalArgumentException("Limit cannot exceed 100, got: " + limit);
        }

        logger.debug("Retrieving {} most recent tracks across all producers", limit);

        List<TrackInfo> recentTracks = trackRepository.findRecentTracks(limit);

        logger.info("Retrieved {} recent tracks from repository", recentTracks.size());

        return recentTracks;
    }
}