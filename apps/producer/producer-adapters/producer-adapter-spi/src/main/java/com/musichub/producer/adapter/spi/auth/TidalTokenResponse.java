package com.musichub.producer.adapter.spi.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.json.bind.annotation.JsonbProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Response DTO for Tidal OAuth2 token requests.
 * Maps the JSON response from Tidal's /oauth2/token endpoint.
 */
@Getter
@Setter
@NoArgsConstructor
public class TidalTokenResponse {

    @JsonbProperty("access_token")
    private String accessToken;

    @JsonbProperty("expires_in")
    private Long expiresIn;  // Changé de long à Long pour permettre null

    public TidalTokenResponse(String accessToken, Long expiresIn) {
        this.accessToken = accessToken;
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
        return "Bearer " + accessToken;
    }
}