package com.musichub.producer.adapter.spi.auth;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

/**
 * REST client for Tidal OAuth2 authentication API.
 * Used to obtain access tokens for API calls.
 * 
 * Configuration property: quarkus.rest-client.tidal-auth.url
 */
@RegisterRestClient(configKey = "tidal-auth")
@Path("/oauth2")
public interface TidalAuthClient {

    /**
     *
     * @param grantType
     * @param clientId
     * @param clientSecret
     * @return
     */
    @POST
    @Path("/token")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    TidalTokenResponse getAccessToken(@FormParam("grant_type") String grantType,
                                      @FormParam("client_id") String clientId,
                                      @FormParam("client_secret") String clientSecret);
}