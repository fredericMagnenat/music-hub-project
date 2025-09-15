package com.musichub.artist.adapter.spi;

import com.musichub.artist.application.ports.out.ArtistReconciliationPort;
import com.musichub.artist.domain.model.Artist;
import com.musichub.shared.domain.values.Source;
import com.musichub.shared.domain.values.SourceType;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * SPI adapter for Tidal API integration.
 * Implements artist reconciliation using Tidal's REST API.
 * Based on Swagger-MCP generated tools for GET /artists and GET /artists/{id}.
 */
@ApplicationScoped
public class TidalArtistClient implements ArtistReconciliationPort {

    private static final String TIDAL_API_BASE_URL = "https://openapi.tidal.com/v2";
    private static final String DEFAULT_COUNTRY_CODE = "US";

    private final Client httpClient;
    private final ObjectMapper objectMapper;

    public TidalArtistClient() {
        this.httpClient = ClientBuilder.newClient();
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public CompletableFuture<Optional<Artist>> findArtistByName(String artistName, SourceType sourceType) {
        if (!supports(sourceType)) {
            return CompletableFuture.completedFuture(Optional.empty());
        }

        return CompletableFuture.supplyAsync(() -> {
            try {
                // Search artists using Tidal API: GET /artists?filter[handle]=artistName
                Response response = httpClient
                    .target(TIDAL_API_BASE_URL)
                    .path("/artists")
                    .queryParam("countryCode", DEFAULT_COUNTRY_CODE)
                    .queryParam("filter[handle]", artistName.toLowerCase().replaceAll(" ", ""))
                    .request(MediaType.APPLICATION_JSON)
                    .get();

                if (response.getStatus() == 200) {
                    String jsonResponse = response.readEntity(String.class);
                    return parseArtistFromSearchResponse(jsonResponse);
                }

                return Optional.<Artist>empty();
            } catch (Exception e) {
                // Log error in production code
                return Optional.<Artist>empty();
            }
        });
    }

    @Override
    public CompletableFuture<Optional<Artist>> findArtistByExternalId(String externalId, SourceType sourceType) {
        if (!supports(sourceType)) {
            return CompletableFuture.completedFuture(Optional.empty());
        }

        return CompletableFuture.supplyAsync(() -> {
            try {
                // Get specific artist using Tidal API: GET /artists/{id}
                Response response = httpClient
                    .target(TIDAL_API_BASE_URL)
                    .path("/artists/" + externalId)
                    .queryParam("countryCode", DEFAULT_COUNTRY_CODE)
                    .request(MediaType.APPLICATION_JSON)
                    .get();

                if (response.getStatus() == 200) {
                    String jsonResponse = response.readEntity(String.class);
                    return parseArtistFromDetailResponse(jsonResponse);
                }

                return Optional.<Artist>empty();
            } catch (Exception e) {
                // Log error in production code
                return Optional.<Artist>empty();
            }
        });
    }

    @Override
    public boolean supports(SourceType sourceType) {
        return sourceType == SourceType.TIDAL;
    }

    /**
     * Parses artist data from Tidal search response.
     *
     * @param jsonResponse the JSON response from Tidal API
     * @return Optional containing parsed artist or empty if parsing fails
     */
    private Optional<Artist> parseArtistFromSearchResponse(String jsonResponse) {
        try {
            JsonNode root = objectMapper.readTree(jsonResponse);
            JsonNode data = root.path("data");

            if (data.isArray() && data.size() > 0) {
                JsonNode artistNode = data.get(0); // Get first result
                return parseArtistFromNode(artistNode);
            }

            return Optional.empty();
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    /**
     * Parses artist data from Tidal detail response.
     *
     * @param jsonResponse the JSON response from Tidal API
     * @return Optional containing parsed artist or empty if parsing fails
     */
    private Optional<Artist> parseArtistFromDetailResponse(String jsonResponse) {
        try {
            JsonNode root = objectMapper.readTree(jsonResponse);
            JsonNode artistNode = root.path("data");

            if (!artistNode.isMissingNode()) {
                return parseArtistFromNode(artistNode);
            }

            return Optional.empty();
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    /**
     * Parses artist data from a JSON node.
     *
     * @param artistNode the JSON node containing artist data
     * @return Optional containing parsed artist or empty if parsing fails
     */
    private Optional<Artist> parseArtistFromNode(JsonNode artistNode) {
        try {
            String tidalId = artistNode.path("id").asText();
            String name = artistNode.path("attributes").path("name").asText();

            if (tidalId.isEmpty() || name.isEmpty()) {
                return Optional.empty();
            }

            // Create artist with TIDAL source
            Artist artist = Artist.createProvisional(name);
            Source tidalSource = Source.of("TIDAL", tidalId);
            Artist enrichedArtist = artist.addSource(tidalSource).markAsVerified();

            return Optional.of(enrichedArtist);
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}