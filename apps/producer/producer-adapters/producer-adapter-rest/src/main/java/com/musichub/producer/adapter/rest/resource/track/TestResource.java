package com.musichub.producer.adapter.rest.resource.producer;


import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/api/v1/test")
@ApplicationScoped
public class TestResource {

    private static final Logger log = LoggerFactory.getLogger(TestResource.class);

    @GET
    @Path("/ping")
    @Produces(MediaType.APPLICATION_JSON)
    public Response ping() {
        log.info("PING endpoint called");
        return Response.ok("pong").build();
    }
}
