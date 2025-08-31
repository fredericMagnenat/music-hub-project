package com.musichub.producer.adapter.rest.resource.track;

import com.musichub.producer.adapter.rest.dto.RecentTrackResponse;
import com.musichub.producer.application.dto.TrackInfo;
import com.musichub.producer.application.ports.in.GetRecentTracksUseCase;
import com.musichub.producer.domain.values.Source;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Path("/tracks")
@ApplicationScoped
@Produces(MediaType.APPLICATION_JSON)
public class TracksResource {

    private static final Logger log = LoggerFactory.getLogger(TracksResource.class);

    private final GetRecentTracksUseCase getRecentTracksUseCase;

    public TracksResource(GetRecentTracksUseCase getRecentTracksUseCase) {
        this.getRecentTracksUseCase = getRecentTracksUseCase;
    }

    @GET
    @Path("/recent")
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

    public static class ErrorResponse {
        public String error;

        public String message;

        public ErrorResponse() {
        }

        public ErrorResponse(String error, String message) {
            this.error = error;
            this.message = message;
        }
    }
}