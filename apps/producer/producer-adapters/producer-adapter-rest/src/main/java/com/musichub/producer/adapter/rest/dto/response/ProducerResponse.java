package com.musichub.producer.adapter.rest.dto.response;

import java.util.Set;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(description = "Response containing producer information")
public class ProducerResponse {
    @Schema(description = "Unique identifier of the producer", examples = {"123e4567-e89b-12d3-a456-426614174000"})
    public String id;

    @Schema(description = "Code identifying the producer", examples = {"PROD001"})
    public String producerCode;

    @Schema(description = "Name of the producer", examples = {"Universal Music Group"})
    public String name;

    @Schema(description = "Set of tracks associated with the producer")
    public Set<TrackResponse> tracks;
}
