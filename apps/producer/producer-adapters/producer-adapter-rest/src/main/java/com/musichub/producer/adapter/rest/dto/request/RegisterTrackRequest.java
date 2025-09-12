package com.musichub.producer.adapter.rest.dto.request;

import jakarta.validation.constraints.NotBlank;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

public class RegisterTrackRequest {

    @Schema(required = true, example = "FRLA12400001")
    @NotBlank(message = "ISRC is required")
    public String isrc;
}