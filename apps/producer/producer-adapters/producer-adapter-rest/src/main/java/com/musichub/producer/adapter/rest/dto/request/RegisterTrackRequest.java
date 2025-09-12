package com.musichub.producer.adapter.rest.dto.request;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import jakarta.validation.constraints.NotBlank;

public class RegisterTrackRequest {

    @Schema(required = true, examples = {"FRLA12400001"})
    @NotBlank(message = "ISRC is required")
    public String isrc;
}
