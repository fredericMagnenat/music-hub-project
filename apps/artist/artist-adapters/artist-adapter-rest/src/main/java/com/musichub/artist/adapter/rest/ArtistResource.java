package com.musichub.artist.adapter.rest;

import com.musichub.artist.adapter.rest.dto.ArtistResponse;
import com.musichub.artist.adapter.rest.mapper.ArtistResponseMapper;
import com.musichub.artist.adapter.rest.service.ProducerAssemblyService;
import com.musichub.artist.application.ports.out.ArtistRepository;
import com.musichub.artist.domain.model.Artist;
import com.musichub.shared.domain.id.ArtistId;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * REST controller for Artist endpoints.
 * Exposes the rich Artist domain model as defined in AC 4.
 * Implements GET /artists/{id} and GET /artists?name=xyz endpoints.
 */
@Path("/api/v1/artists")
@ApplicationScoped
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ArtistResource {

    private final ArtistRepository artistRepository;
    private final ProducerAssemblyService producerAssemblyService;

    @Inject
    public ArtistResource(ArtistRepository artistRepository,
                         ProducerAssemblyService producerAssemblyService) {
        this.artistRepository = artistRepository;
        this.producerAssemblyService = producerAssemblyService;
    }

    /**
     * Retrieves an artist by their unique ID.
     * Returns the rich domain model with contributions, sources, and producer IDs.
     *
     * @param artistId the artist UUID
     * @return ArtistResponse with complete domain data
     */
    @GET
    @Path("/{id}")
    public Response getArtistById(@PathParam("id") UUID artistId) {
        Optional<Artist> artistOpt = artistRepository.findById(new ArtistId(artistId));

        if (artistOpt.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"error\":\"Artist not found\"}")
                    .build();
        }

        Artist artist = artistOpt.get();
        List<UUID> producerIds = producerAssemblyService.getProducerIds(artist);
        ArtistResponse response = ArtistResponseMapper.toResponse(artist, producerIds);

        return Response.ok(response).build();
    }

    /**
     * Searches for artists by name.
     * Returns the first matching artist with complete domain data.
     *
     * @param name the artist name to search for
     * @return ArtistResponse with complete domain data or 404 if not found
     */
    @GET
    public Response searchArtistByName(@QueryParam("name") String name) {
        if (name == null || name.trim().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\":\"Name parameter is required\"}")
                    .build();
        }

        Optional<Artist> artistOpt = artistRepository.findByName(name.trim());

        if (artistOpt.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"error\":\"Artist not found\"}")
                    .build();
        }

        Artist artist = artistOpt.get();
        List<UUID> producerIds = producerAssemblyService.getProducerIds(artist);
        ArtistResponse response = ArtistResponseMapper.toResponse(artist, producerIds);

        return Response.ok(response).build();
    }
}