package com.musichub.producer.adapter.rest.resource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import org.jboss.resteasy.reactive.RestResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.musichub.producer.adapter.rest.dto.request.RegisterTrackRequest;
import com.musichub.producer.adapter.rest.dto.response.ProducerResponse;
import com.musichub.producer.adapter.rest.mapper.ProducerMapper;
import com.musichub.producer.adapter.rest.resource.producer.ProducerResource;
import com.musichub.producer.application.ports.in.RegisterTrackUseCase;
import com.musichub.producer.domain.model.Producer;
import com.musichub.shared.domain.values.ProducerCode;

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

    @Mock
    ProducerMapper producerMapper;

    @InjectMocks
    ProducerResource producerResource;

    @Test
    @DisplayName("Should return 202 when track registration succeeds")
    void register_returns202_on_success() {
        // Given
        RegisterTrackRequest request = new RegisterTrackRequest();
        request.isrc = "FRLA12400001";

        Producer mockProducer = Producer.createNew(ProducerCode.of("FRLA1"), null);
        ProducerResponse mockResponse = new ProducerResponse();
        mockResponse.id = mockProducer.id().value().toString();
        mockResponse.producerCode = mockProducer.producerCode().value();

        when(registerTrackUseCase.registerTrack("FRLA12400001", "test-correlation-id")).thenReturn(mockProducer);
        when(producerMapper.toResponse(mockProducer)).thenReturn(mockResponse);

        // When
        RestResponse<ProducerResponse> response = producerResource.register(request);

        // Then
        assertEquals(202, response.getStatus());
        ProducerResponse body = response.getEntity();
        assertEquals(mockProducer.id().value().toString(), body.id);
        assertEquals(mockProducer.producerCode().value(), body.producerCode);
    }

    @Test
    @DisplayName("Should handle null request gracefully")
    void register_handles_null_request() {
        // Given - null request
        // When & Then - Should throw IllegalArgumentException
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            producerResource.register(null);
        });
        assertEquals("Request cannot be null", exception.getMessage());
    }
}
