package com.musichub.producer.adapter.rest.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "Response containing recent track information")
public class RecentTrackResponse {

    @JsonProperty("isrc")
    @Schema(description = "International Standard Recording Code")
    public String isrc;

    @JsonProperty("title")
    @Schema(description = "Track title")
    public String title;

    @JsonProperty("artistNames")
    @Schema(description = "List of artist names")
    public List<String> artistNames;

    @JsonProperty("source")
    @Schema(description = "Source information where the track was found")
    public SourceInfo source;

    @JsonProperty("status")
    @Schema(description = "Track validation status")
    public String status;

    @JsonProperty("submissionDate")
    @Schema(description = "Date and time when the track was submitted")
    public LocalDateTime submissionDate;

    @Schema(description = "Source information")
    public static class SourceInfo {
        @JsonProperty("name")
        @Schema(description = "Source platform name")
        public String name;

        @JsonProperty("externalId")
        @Schema(description = "External identifier at the source")
        public String externalId;
    }

}