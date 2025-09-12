package com.musichub.producer.adapter.rest.dto.response;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(description = "Response containing artist credit information for a track")
public class ArtistCreditResponse {
    @Schema(description = "The name of the artist", examples = {"The Beatles"})
    public String artistName;

    @Schema(description = "Unique identifier of the artist as UUID string", examples = {"123e4567-e89b-12d3-a456-426614174000"}, nullable = true)
    public String artistId; // UUID as string, may be null
}
