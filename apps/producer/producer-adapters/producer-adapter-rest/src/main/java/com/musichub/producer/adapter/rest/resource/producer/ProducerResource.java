package com.musichub.producer.adapter.rest.resource.producer;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.jboss.resteasy.reactive.RestResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.musichub.producer.adapter.rest.dto.request.RegisterTrackRequest;
import com.musichub.producer.adapter.rest.dto.response.ProducerResponse;
import com.musichub.producer.adapter.rest.mapper.ProducerMapper;
import com.musichub.producer.adapter.rest.util.ErrorHandler;
import com.musichub.producer.adapter.rest.util.RequestContextUtils;
import com.musichub.producer.application.ports.in.RegisterTrackUseCase;
import com.musichub.producer.domain.exception.TrackRegistrationException;
import com.musichub.producer.domain.model.Producer;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/producers")
@ApplicationScoped
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Producer Management", description = "APIs for managing producers and track registrations")
public class ProducerResource {

    private static final Logger log = LoggerFactory.getLogger(ProducerResource.class);

    private RegisterTrackUseCase registerTrackUseCase;

    private ProducerMapper producerMapper;

    @Inject
    public ProducerResource(RegisterTrackUseCase registerTrackUseCase, ProducerMapper producerMapper){
            this.producerMapper=producerMapper;
            this.registerTrackUseCase=registerTrackUseCase;
    }

    @POST
    @Operation(summary = "Register a new track", description = "Registers a new track for a producer using the provided ISRC")
    @APIResponses(value = {
        @APIResponse(responseCode = "202", description = "Track registered successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProducerResponse.class))),
        @APIResponse(responseCode = "400", description = "Invalid request data or ISRC already exists"),
        @APIResponse(responseCode = "500", description = "Internal server error")
    })
    public RestResponse<ProducerResponse> register(@Valid RegisterTrackRequest request) {
        String correlationId = RequestContextUtils.generateCorrelationId();

        try {
            validateRequest(request, correlationId);
            log.info("Registering track with ISRC: {} (correlationId: {})", request.isrc, correlationId);

            Producer producer = registerTrackUseCase.registerTrack(request.isrc);
            ProducerResponse response = producerMapper.toResponse(producer);

            log.info("Successfully registered track for producer: {} (correlationId: {})",
                    producer.id().value(), correlationId);

            return RestResponse.accepted(response);

        } catch (Exception e) {
            throw ErrorHandler.handleException(log, correlationId, "register track", e,
                                             TrackRegistrationException.class);
        } finally {
            RequestContextUtils.cleanup();
        }
    }

    private void validateRequest(RegisterTrackRequest request, String correlationId) {
        if (request == null) {
            throw new TrackRegistrationException("Request cannot be null (correlationId: " + correlationId + ")");
        }
        if (request.isrc == null || request.isrc.trim().isEmpty()) {
            throw new TrackRegistrationException("ISRC cannot be null or empty (correlationId: " + correlationId + ")");
        }
    }
}
