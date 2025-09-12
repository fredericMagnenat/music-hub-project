package com.musichub.producer.adapter.rest.resource.track;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import com.musichub.producer.adapter.rest.dto.RecentTrackResponse;
import com.musichub.producer.adapter.rest.mapper.TrackMapper;
import com.musichub.producer.application.dto.TrackInfo;
import com.musichub.producer.application.ports.in.GetRecentTracksUseCase;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/tracks")
@ApplicationScoped
@Produces(MediaType.APPLICATION_JSON)
public class TracksResource {

    private static final Logger log = LoggerFactory.getLogger(TracksResource.class);

    private GetRecentTracksUseCase getRecentTracksUseCase;

    private TrackMapper trackMapper;

    public TracksResource(GetRecentTracksUseCase getRecentTracksUseCase, TrackMapper trackMapper){
        this.getRecentTracksUseCase=getRecentTracksUseCase;
        this.trackMapper=trackMapper;
    }

    @GET
    @Path("/recent")
    public Response getRecentTracks() {
        // Generate correlation ID for request tracing
        String correlationId = UUID.randomUUID().toString();
        MDC.put("correlationId", correlationId);

        Instant startTime = Instant.now();
        int tracksCount = 0;

        try {
            log.info("GET /tracks/recent - Starting recent tracks retrieval (correlationId: {})", correlationId);

            List<TrackInfo> recentTracks = getRecentTracksUseCase.getRecentTracks();
            tracksCount = recentTracks.size();

            log.debug("Retrieved {} track entities from application layer (correlationId: {})", tracksCount, correlationId);

            List<RecentTrackResponse> response = recentTracks.stream()
                    .map(trackMapper::mapToRecentResponse)
                    .toList();

            long processingTime = Instant.now().toEpochMilli() - startTime.toEpochMilli();

            // Defensive check for response list
            int responseSize = (response != null) ? response.size() : 0;

            log.info("GET /tracks/recent - Successfully processed {} tracks in {}ms (correlationId: {})",
                responseSize, processingTime, correlationId);

            // Log business context for monitoring
            if (tracksCount > 0 && response != null && !response.isEmpty() && response.get(0) != null) {
                RecentTrackResponse firstTrack = response.get(0);
                log.debug("Recent tracks summary - Total: {}, First ISRC: {} (correlationId: {})",
                    tracksCount,
                    firstTrack.isrc,
                    correlationId);
            } else {
                log.info("No recent tracks found (correlationId: {})", correlationId);
            }

            return Response.ok(response).build();

        } catch (Exception e) {
            long processingTime = Instant.now().toEpochMilli() - startTime.toEpochMilli();

            log.error("GET /tracks/recent - Failed to retrieve recent tracks after {}ms - {} (correlationId: {})",
                processingTime,
                e.getMessage(),
                correlationId,
                e);

            // Rethrow with contextual information for GlobalExceptionMapper to handle HTTP response
            throw new IllegalStateException("Failed to retrieve recent tracks after " + processingTime + "ms (correlationId: " + correlationId + ")", e);

        } finally {
            // Clean up MDC context
            MDC.remove("correlationId");
        }
    }
}
