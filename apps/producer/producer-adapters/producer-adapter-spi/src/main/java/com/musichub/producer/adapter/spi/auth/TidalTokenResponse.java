package com.musichub.producer.adapter.spi.auth;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Response DTO for Tidal OAuth2 token requests.
 * Maps the JSON response from Tidal's /oauth2/token endpoint.
 */
public class TidalTokenResponse {

    /**
     * The access token to use for API calls
     */
    @JsonProperty("access_token")
    public String accessToken;

    /**
     * Token type (usually "Bearer")
     */
    @JsonProperty("token_type")
    public String tokenType;

    /**
     * Token expiration time in seconds
     */
    @JsonProperty("expires_in")
    public Integer expiresIn;

    /**
     * Granted scopes (if any)
     */
    @JsonProperty("scope")
    public String scope;

    public TidalTokenResponse() {
    }

    public TidalTokenResponse(String accessToken, String tokenType, Integer expiresIn) {
        this.accessToken = accessToken;
        this.tokenType = tokenType;
        this.expiresIn = expiresIn;
    }

    /**
     * Check if the token response is valid
     */
    public boolean isValid() {
        return accessToken != null && !accessToken.trim().isEmpty();
    }

    /**
     * Get the full authorization header value
     */
    public String getAuthorizationHeader() {
        if (!isValid()) {
            return null;
        }
        String type = tokenType != null ? tokenType : "Bearer";
        return type + " " + accessToken;
    }
}