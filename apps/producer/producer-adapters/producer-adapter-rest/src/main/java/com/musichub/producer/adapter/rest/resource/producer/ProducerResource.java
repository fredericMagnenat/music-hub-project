package com.musichub.producer.adapter.rest.resource.producer;

import com.musichub.producer.application.ports.in.RegisterTrackUseCase;
import com.musichub.producer.domain.model.Producer;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;


@Path("/producers")
@ApplicationScoped
public class ProducerResource {

    private static final Logger log = LoggerFactory.getLogger(ProducerResource.class);

    private final RegisterTrackUseCase registerTrackUseCase;

    public ProducerResource(RegisterTrackUseCase registerTrackUseCase) {
        this.registerTrackUseCase = registerTrackUseCase;
    }

    public static final class RegisterTrackRequest {
        @Schema(required = true, example = "FRLA12400001")
        public String isrc;
    }

    public static final class ProducerResponse {
        public String id;

        public String producerCode;

        public String name;

        public Set<TrackResponse> tracks;

        public ProducerResponse() {
        }

        public static ProducerResponse from(Producer producer) {
            ProducerResponse response = new ProducerResponse();
            response.id = producer.id().value().toString();
            response.producerCode = producer.producerCode().value();
            response.name = producer.name();
            response.tracks = producer.tracks().stream()
                    .map(TrackResponse::from)
                    .collect(java.util.LinkedHashSet::new, Set::add, Set::addAll);
            return response;
        }
    }

    public static final class TrackResponse {
        public String isrc;
        public String title;
        public java.util.List<ArtistCreditResponse> credits;
        public java.util.List<SourceResponse> sources;
        public String status;

        public TrackResponse() {
        }

        public static TrackResponse from(com.musichub.producer.domain.model.Track track) {
            TrackResponse response = new TrackResponse();
            response.isrc = track.isrc().value();
            response.title = track.title();
            response.credits = track.credits().stream()
                    .map(ArtistCreditResponse::from)
                    .collect(java.util.ArrayList::new, java.util.List::add, java.util.List::addAll);
            response.sources = track.sources().stream()
                    .map(SourceResponse::from)
                    .collect(java.util.ArrayList::new, java.util.List::add, java.util.List::addAll);
            response.status = track.status().name();
            return response;
        }
    }

    public static final class ArtistCreditResponse {
        public String artistName;
        public String artistId; // UUID as string, may be null

        public ArtistCreditResponse() {
        }

        public static ArtistCreditResponse from(com.musichub.producer.domain.values.ArtistCredit credit) {
            ArtistCreditResponse response = new ArtistCreditResponse();
            response.artistName = credit.artistName();
            response.artistId = credit.artistId() != null ? credit.artistId().value().toString() : null;
            return response;
        }
    }

    public static final class SourceResponse {
        public String name;
        public String id;

        public SourceResponse() {
        }

        public static SourceResponse from(com.musichub.producer.domain.values.Source source) {
            SourceResponse response = new SourceResponse();
            response.name = source.sourceName();
            response.id = source.sourceId();
            return response;
        }
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response register(RegisterTrackRequest request) {
        try {
            if (request == null || request.isrc == null || request.isrc.isBlank()) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(new ErrorResponse("InvalidISRCFormat", "Field 'isrc' is required"))
                        .build();
            }
            Producer producer = registerTrackUseCase.registerTrack(request.isrc);
            return Response.accepted(ProducerResponse.from(producer)).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse("InvalidISRCFormat", e.getMessage()))
                    .build();
        } catch (RuntimeException e) {
            String simpleName = e.getClass().getSimpleName();
            if (simpleName.contains("ExternalService") ||
                    simpleName.contains("TrackNotFoundInExternalService")) {
                return Response.status(422)
                        .entity(new ErrorResponse("TRACK_NOT_FOUND_EXTERNAL",
                                "The ISRC was valid, but we could not find metadata for it on external services."))
                        .build();
            } else {
                return Response.status(422)
                        .entity(new ErrorResponse("UnresolvableISRC", e.getMessage()))
                        .build();
            }
        }
    }

    public static final class ErrorResponse {
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
