package com.musichub.producer.adapter.rest.dto.response;

public class ErrorResponse {
    public String error;
    public String message;

    public ErrorResponse() {
    }

    public ErrorResponse(String error, String message) {
        this.error = error;
        this.message = message;
    }
}