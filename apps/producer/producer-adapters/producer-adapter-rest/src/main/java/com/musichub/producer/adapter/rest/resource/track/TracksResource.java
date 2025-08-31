package com.musichub.producer.adapter.rest.resource.track;

import com.musichub.producer.adapter.rest.dto.RecentTrackResponse;
import com.musichub.producer.adapter.rest.resource.producer.ProducerResource;
import com.musichub.producer.application.dto.TrackInfo;
import com.musichub.producer.application.ports.in.GetRecentTracksUseCase;
import com.musichub.producer.domain.values.Source;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Path("/api/v1/tracks")
@ApplicationScoped
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "Tracks", description = "Endpoints for track queries")
public class TracksResource {

    private static final Logger log = LoggerFactory.getLogger(TracksResource.class);

    private final GetRecentTracksUseCase getRecentTracksUseCase;

    public TracksResource(GetRecentTracksUseCase getRecentTracksUseCase) {
        this.getRecentTracksUseCase = getRecentTracksUseCase;
    }

    @GET
    @Path("/recent")
    @Operation(
        summary = "Get recent tracks",
        description = "Retrieves the 10 most recently submitted tracks from all producers, ordered by submission date (newest first)."
    )
    @APIResponses({
        @APIResponse(responseCode = "200",
                    description = "Success. Returns list of recent tracks (may be empty).",
                    content = @Content(mediaType = "application/json",
                                     schema = @Schema(implementation = RecentTrackResponse[].class))),
        @APIResponse(responseCode = "500", 
                    description = "Internal server error.",
                    content = @Content(mediaType = "application/json",
                                     schema = @Schema(implementation = ProducerResource.ErrorResponse.class)))
    })
    public Response getRecentTracks() {
        try {
            log.info("GET /api/v1/tracks/recent - retrieving recent tracks");
            
            List<TrackInfo> recentTracks = getRecentTracksUseCase.getRecentTracks();
            
            List<RecentTrackResponse> response = recentTracks.stream()
                .map(this::mapToResponse)
                .toList();
            
            log.info("GET /api/v1/tracks/recent - returning {} tracks", response.size());
            
            return Response.ok(response).build();
            
        } catch (Exception e) {
            log.error("GET /api/v1/tracks/recent - unexpected error occurred", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("InternalError", "An unexpected error occurred"))
                    .build();
        }
    }

    @GET
    @Path("/ping")
    public Response ping() {
        log.info("PING endpoint called");
        return Response.ok("pong").build();
    }

    @GET
    @Path("/debug")
    @Operation(summary = "Debug endpoint", description = "Debug endpoint to test the service")
    public Response debugEndpoint() {
        log.info("DEBUG: Entering debug endpoint");
        return Response.ok("Debug endpoint reached").build();
    }

    private RecentTrackResponse mapToResponse(TrackInfo trackInfo) {
        RecentTrackResponse response = new RecentTrackResponse();
        response.isrc = trackInfo.isrc().value();
        response.title = trackInfo.title();
        response.artistNames = List.copyOf(trackInfo.artistNames());
        response.status = trackInfo.status().name();
        response.submissionDate = trackInfo.submissionDate();
        
        if (!trackInfo.sources().isEmpty()) {
            Source firstSource = trackInfo.sources().get(0);
            RecentTrackResponse.SourceInfo sourceInfo = new RecentTrackResponse.SourceInfo();
            sourceInfo.name = firstSource.sourceName();
            sourceInfo.externalId = firstSource.sourceId();
            response.source = sourceInfo;
        }

        return response;
    }

    @Schema(description = "Error response")
    public static class ErrorResponse {
        @Schema(description = "Error code")
        public String error;
        
        @Schema(description = "Error message")
        public String message;

        public ErrorResponse() {}

        public ErrorResponse(String error, String message) {
            this.error = error;
            this.message = message;
        }
    }
}