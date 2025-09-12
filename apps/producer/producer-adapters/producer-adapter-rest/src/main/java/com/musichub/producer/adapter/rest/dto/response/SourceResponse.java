package com.musichub.producer.adapter.rest.dto.response;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(description = "Response containing source information")
public class SourceResponse {
    @Schema(description = "Name of the source platform", examples = {"Spotify"})
    public String name;

    @Schema(description = "Identifier of the source", examples = {"spotify"})
    public String id;
}
