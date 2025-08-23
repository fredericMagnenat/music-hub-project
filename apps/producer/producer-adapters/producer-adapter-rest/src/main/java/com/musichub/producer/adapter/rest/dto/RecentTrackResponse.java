package com.musichub.producer.adapter.rest.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "Response containing recent track information")
public class RecentTrackResponse {

    @JsonProperty("id")
    @Schema(description = "Unique identifier for the track", example = "550e8400-e29b-41d4-a716-446655440000")
    public String id;

    @JsonProperty("isrc")
    @Schema(description = "International Standard Recording Code", example = "FRLA12400001")
    public String isrc;

    @JsonProperty("title")
    @Schema(description = "Track title", example = "Amazing Song")
    public String title;

    @JsonProperty("artistNames")
    @Schema(description = "List of artist names", example = "[\"Artist One\", \"Artist Two\"]")
    public List<String> artistNames;

    @JsonProperty("source")
    @Schema(description = "Source information where the track was found")
    public SourceInfo source;

    @JsonProperty("status")
    @Schema(description = "Track validation status", example = "VALIDATED")
    public String status;

    @JsonProperty("submissionDate")
    @Schema(description = "Date and time when the track was submitted", example = "2025-08-22T10:30:00Z")
    public LocalDateTime submissionDate;

    @JsonProperty("producer")
    @Schema(description = "Producer information")
    public ProducerInfo producer;

    @Schema(description = "Source information")
    public static class SourceInfo {
        @JsonProperty("name")
        @Schema(description = "Source platform name", example = "TIDAL")
        public String name;

        @JsonProperty("externalId")
        @Schema(description = "External identifier at the source", example = "12345678")
        public String externalId;
    }

    @Schema(description = "Producer information")
    public static class ProducerInfo {
        @JsonProperty("id")
        @Schema(description = "Producer unique identifier")
        public String id;

        @JsonProperty("producerCode")
        @Schema(description = "Producer code", example = "FRLA1")
        public String producerCode;

        @JsonProperty("name")
        @Schema(description = "Producer name", example = "Label Records")
        public String name;
    }
}