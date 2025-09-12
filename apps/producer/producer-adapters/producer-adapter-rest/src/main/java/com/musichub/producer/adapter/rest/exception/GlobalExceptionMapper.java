package com.musichub.producer.adapter.rest.exception;

import com.musichub.producer.adapter.rest.dto.response.ErrorResponse;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import org.jboss.resteasy.reactive.server.ServerExceptionMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GlobalExceptionMapper {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionMapper.class);

    @ServerExceptionMapper
    public Response mapIllegalArgumentException(IllegalArgumentException e) {
        log.warn("Invalid argument: {}", e.getMessage());
        return Response.status(Response.Status.BAD_REQUEST)
            .entity(new ErrorResponse("INVALID_ARGUMENT", e.getMessage()))
            .build();
    }

    @ServerExceptionMapper
    public Response mapRuntimeException(RuntimeException e) {
        log.error("Runtime exception: {}", e.getMessage(), e);

        String simpleName = e.getClass().getSimpleName();
        if (simpleName.contains("ExternalService") ||
                simpleName.contains("TrackNotFoundInExternalService")) {
            return Response.status(422)
                .entity(new ErrorResponse("EXTERNAL_SERVICE_ERROR",
                    "External service error occurred"))
                .build();
        }

        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
            .entity(new ErrorResponse("INTERNAL_ERROR", "An unexpected error occurred"))
            .build();
    }

    @ServerExceptionMapper
    public Response mapWebApplicationException(WebApplicationException e) {
        log.warn("Web application exception: {}", e.getMessage());
        return Response.status(e.getResponse().getStatus())
            .entity(new ErrorResponse("WEB_APPLICATION_ERROR", e.getMessage()))
            .build();
    }
}