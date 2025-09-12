package com.musichub.producer.adapter.rest.resource.producer;

import org.jboss.resteasy.reactive.RestResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.musichub.producer.adapter.rest.dto.request.RegisterTrackRequest;
import com.musichub.producer.adapter.rest.dto.response.ProducerResponse;
import com.musichub.producer.adapter.rest.mapper.ProducerMapper;
import com.musichub.producer.application.ports.in.RegisterTrackUseCase;
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
    public RestResponse<ProducerResponse> register(@Valid RegisterTrackRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Request cannot be null");
        }

        log.info("Registering track with ISRC: {}", request.isrc);

        Producer producer = registerTrackUseCase.registerTrack(request.isrc);

        ProducerResponse response = producerMapper.toResponse(producer);

        log.info("Successfully registered track for producer: {}", producer.id().value());

        return RestResponse.accepted(response);
    }
}
