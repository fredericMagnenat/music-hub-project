package com.musichub.producer.adapter.rest.dto.response;

import java.util.List;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(description = "Response containing track information")
public class TrackResponse {
    @Schema(description = "International Standard Recording Code of the track", examples = {"US-S1Z-99-00001"})
    public String isrc;

    @Schema(description = "Title of the track", examples = {"Bohemian Rhapsody"})
    public String title;

    @Schema(description = "List of artist credits for the track")
    public List<ArtistCreditResponse> credits;

    @Schema(description = "List of sources for the track")
    public List<SourceResponse> sources;

    @Schema(description = "Current status of the track", examples = {"PENDING"})
    public String status;
}
