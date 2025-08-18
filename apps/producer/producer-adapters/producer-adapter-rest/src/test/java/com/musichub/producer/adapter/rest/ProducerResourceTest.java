package com.musichub.producer.adapter.rest.resource;

import com.musichub.producer.domain.model.Producer;
import com.musichub.producer.domain.ports.in.RegisterTrackUseCase;
import com.musichub.shared.domain.values.ProducerCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import jakarta.ws.rs.core.Response;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProducerResource Unit Tests")
class ProducerResourceTest {

    // Mock exception class to simulate ExternalServiceException
    static class ExternalServiceException extends RuntimeException {
        public ExternalServiceException(String message) {
            super(message);
        }
    }

    @Mock
    RegisterTrackUseCase registerTrackUseCase;

    @InjectMocks
    ProducerResource producerResource;

    @Test
    @DisplayName("Should return 202 when track registration succeeds")
    void register_returns202_on_success() {
        // Given
        ProducerResource.RegisterTrackRequest request = new ProducerResource.RegisterTrackRequest();
        request.isrc = "FRLA12400001";
        
        Producer mockProducer = Producer.createNew(ProducerCode.of("FRLA1"), null);
        when(registerTrackUseCase.registerTrack("FRLA12400001")).thenReturn(mockProducer);

        // When
        Response response = producerResource.register(request);

        // Then
        assertEquals(202, response.getStatus());
        ProducerResource.ProducerResponse body = (ProducerResource.ProducerResponse) response.getEntity();
        assertEquals(mockProducer.id().value().toString(), body.id);
        assertEquals(mockProducer.producerCode().value(), body.producerCode);
    }

    @Test
    @DisplayName("Should return 400 when request is null")
    void register_returns400_when_request_is_null() {
        // When
        Response response = producerResource.register(null);

        // Then
        assertEquals(400, response.getStatus());
        ProducerResource.ErrorResponse error = (ProducerResource.ErrorResponse) response.getEntity();
        assertEquals("InvalidISRCFormat", error.error);
        assertEquals("Field 'isrc' is required", error.message);
    }

    @Test
    @DisplayName("Should return 400 when ISRC is null")
    void register_returns400_when_isrc_is_null() {
        // Given
        ProducerResource.RegisterTrackRequest request = new ProducerResource.RegisterTrackRequest();
        request.isrc = null;

        // When
        Response response = producerResource.register(request);

        // Then
        assertEquals(400, response.getStatus());
        ProducerResource.ErrorResponse error = (ProducerResource.ErrorResponse) response.getEntity();
        assertEquals("InvalidISRCFormat", error.error);
        assertEquals("Field 'isrc' is required", error.message);
    }

    @Test
    @DisplayName("Should return 400 when ISRC is blank")
    void register_returns400_when_isrc_is_blank() {
        // Given
        ProducerResource.RegisterTrackRequest request = new ProducerResource.RegisterTrackRequest();
        request.isrc = "   ";

        // When
        Response response = producerResource.register(request);

        // Then
        assertEquals(400, response.getStatus());
        ProducerResource.ErrorResponse error = (ProducerResource.ErrorResponse) response.getEntity();
        assertEquals("InvalidISRCFormat", error.error);
        assertEquals("Field 'isrc' is required", error.message);
    }

    @Test
    @DisplayName("Should return 400 when use case throws IllegalArgumentException")
    void register_returns400_on_illegal_argument_exception() {
        // Given
        ProducerResource.RegisterTrackRequest request = new ProducerResource.RegisterTrackRequest();
        request.isrc = "INVALID";
        
        when(registerTrackUseCase.registerTrack("INVALID"))
            .thenThrow(new IllegalArgumentException("ISRC format is invalid"));

        // When
        Response response = producerResource.register(request);

        // Then
        assertEquals(400, response.getStatus());
        ProducerResource.ErrorResponse error = (ProducerResource.ErrorResponse) response.getEntity();
        assertEquals("InvalidISRCFormat", error.error);
        assertEquals("ISRC format is invalid", error.message);
    }

    @Test
    @DisplayName("Should return 422 when use case throws ExternalServiceException")
    void register_returns422_on_external_service_exception() {
        // Given
        ProducerResource.RegisterTrackRequest request = new ProducerResource.RegisterTrackRequest();
        request.isrc = "FRLA12400001";
        
        when(registerTrackUseCase.registerTrack("FRLA12400001"))
            .thenThrow(new ExternalServiceException("Track not found on external service"));

        // When
        Response response = producerResource.register(request);

        // Then
        assertEquals(422, response.getStatus());
        ProducerResource.ErrorResponse error = (ProducerResource.ErrorResponse) response.getEntity();
        assertEquals("TRACK_NOT_FOUND_EXTERNAL", error.error);
        assertEquals("The ISRC was valid, but we could not find metadata for it on external services.", error.message);
    }

    @Test
    @DisplayName("Should return 422 when use case throws RuntimeException")
    void register_returns422_on_runtime_exception() {
        // Given
        ProducerResource.RegisterTrackRequest request = new ProducerResource.RegisterTrackRequest();
        request.isrc = "FRLA12400001";
        
        when(registerTrackUseCase.registerTrack("FRLA12400001"))
            .thenThrow(new RuntimeException("ISRC not found upstream"));

        // When
        Response response = producerResource.register(request);

        // Then
        assertEquals(422, response.getStatus());
        ProducerResource.ErrorResponse error = (ProducerResource.ErrorResponse) response.getEntity();
        assertEquals("UnresolvableISRC", error.error);
        assertEquals("ISRC not found upstream", error.message);
    }
}
