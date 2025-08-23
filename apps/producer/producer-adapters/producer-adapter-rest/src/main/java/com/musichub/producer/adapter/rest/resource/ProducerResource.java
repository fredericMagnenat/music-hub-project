package com.musichub.producer.adapter.rest.resource;

import com.musichub.producer.domain.model.Producer;
import com.musichub.producer.domain.ports.in.RegisterTrackUseCase;
import com.musichub.shared.domain.values.ISRC;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.Set;


@Path("/api/v1/producers")
@ApplicationScoped
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Producers", description = "Endpoints for producer operations")
public class ProducerResource {

    @Inject
    RegisterTrackUseCase registerTrackUseCase;

    public static final class RegisterTrackRequest {
        @Schema(required = true, example = "FRLA12400001")
        public String isrc;
    }

    public static final class ProducerResponse {
        @Schema(required = true, example = "f36e54fa-ce8b-5498-9713-c231236ef2e8")
        public String id;
        
        @Schema(required = true, example = "FRLA1")
        public String producerCode;
        
        @Schema(example = "Universal Music France")
        public String name;
        
        @Schema(description = "List of track ISRCs")
        public Set<String> tracks;

        public ProducerResponse() {}

        public static ProducerResponse from(Producer producer) {
            ProducerResponse response = new ProducerResponse();
            response.id = producer.id().value().toString();
            response.producerCode = producer.producerCode().value();
            response.name = producer.name();
            response.tracks = producer.tracks().stream()
                .map(track -> track.isrc().value())
                .collect(java.util.LinkedHashSet::new, Set::add, Set::addAll);
            return response;
        }
    }

    @POST
    @Operation(
        summary = "Register a track by ISRC",
        description = "Submits an ISRC. The system finds or creates the corresponding Producer, validates the ISRC, and adds the track to the producer's catalog."
    )
    @APIResponses({
        @APIResponse(responseCode = "202", description = "Accepted. The track registration is in progress.",
            content = @Content(schema = @Schema(implementation = ProducerResponse.class))),
        @APIResponse(responseCode = "400", description = "Invalid ISRC format.",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @APIResponse(responseCode = "422", description = "Unresolvable ISRC (valid format but not found upstream).",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public Response register(
            @RequestBody(required = true, content = @Content(schema = @Schema(implementation = RegisterTrackRequest.class)))
            RegisterTrackRequest request) {
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
            // Check if this is an external service exception by examining the exception type name
            // This handles both ExternalServiceException and TrackNotFoundInExternalServiceException
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
        @Schema(required = true, example = "InvalidISRCFormat")
        public String error;
        @Schema(required = true, example = "ISRC 'XXX' is invalid")
        public String message;

        public ErrorResponse() {}
        public ErrorResponse(String error, String message) {
            this.error = error;
            this.message = message;
        }
    }
}
