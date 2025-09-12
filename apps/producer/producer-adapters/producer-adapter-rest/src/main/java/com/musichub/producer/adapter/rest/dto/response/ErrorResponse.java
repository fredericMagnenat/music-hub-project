package com.musichub.producer.adapter.rest.dto.response;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(description = "Response containing error information")
public class ErrorResponse {
    @Schema(description = "Error code or type", examples = {"VALIDATION_ERROR"})
    public String error;

    @Schema(description = "Detailed error message", examples = {"The provided data is invalid"})
    public String message;

    public ErrorResponse() {
    }

    public ErrorResponse(String error, String message) {
        this.error = error;
        this.message = message;
    }
}
