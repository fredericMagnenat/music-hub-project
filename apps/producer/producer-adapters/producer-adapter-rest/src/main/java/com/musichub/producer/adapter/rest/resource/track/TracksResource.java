package com.musichub.producer.adapter.rest.resource.track;

import java.time.Instant;
import java.util.List;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.musichub.producer.adapter.rest.dto.response.RecentTrackResponse;
import com.musichub.producer.adapter.rest.mapper.TrackMapper;
import com.musichub.producer.adapter.rest.util.ErrorHandler;
import com.musichub.producer.adapter.rest.util.RequestContextUtils;
import com.musichub.producer.application.dto.TrackInfo;
import com.musichub.producer.application.ports.in.GetRecentTracksUseCase;
import com.musichub.producer.domain.exception.TrackRetrievalException;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/tracks")
@ApplicationScoped
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "Track Management", description = "APIs for managing tracks and retrieving track information")
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
    @Operation(summary = "Get recent tracks", description = "Retrieves a list of recently submitted tracks")
    @APIResponses(value = {
        @APIResponse(responseCode = "200", description = "List of recent tracks retrieved successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(type = SchemaType.ARRAY, implementation = RecentTrackResponse.class))),
        @APIResponse(responseCode = "500", description = "Internal server error")
    })
    public Response getRecentTracks() {
        String correlationId = RequestContextUtils.generateCorrelationId();
        Instant startTime = Instant.now();

        try {
            logRequestStart(correlationId);
            List<TrackInfo> tracks = getRecentTracksUseCase.getRecentTracks();
            List<RecentTrackResponse> response = processTracks(tracks, trackMapper);

            logSuccess(correlationId, response.size(), startTime);
            return Response.ok(response).build();

        } catch (Exception e) {
            throw ErrorHandler.handleException(log, correlationId, "retrieve recent tracks", e,
                                             TrackRetrievalException.class);
        } finally {
            RequestContextUtils.cleanup();
        }
    }

    private void logRequestStart(String correlationId) {
        log.info("GET /tracks/recent - Starting recent tracks retrieval (correlationId: {})", correlationId);
    }

    private List<RecentTrackResponse> processTracks(List<TrackInfo> tracks, TrackMapper mapper) {
        log.debug("Retrieved {} track entities from application layer", tracks.size());
        return tracks.stream()
                .map(mapper::mapToRecentResponse)
                .toList();
    }

    private void logSuccess(String correlationId, int count, Instant startTime) {
        long processingTime = Instant.now().toEpochMilli() - startTime.toEpochMilli();
        log.info("GET /tracks/recent - Successfully processed {} tracks in {}ms (correlationId: {})",
            count, processingTime, correlationId);

        if (count == 0) {
            log.info("No recent tracks found (correlationId: {})", correlationId);
        }
    }
}
