package com.musichub.producer.adapter.rest.dto.response;

import java.time.LocalDateTime;
import java.util.List;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(description = "Response containing information about a recent track submission")
public class RecentTrackResponse {

    @Schema(description = "International Standard Recording Code (ISRC) of the track", examples = {"US-S1Z-99-00001"})
    public String isrc;

    @Schema(description = "Title of the track", examples = {"Bohemian Rhapsody"})
    public String title;

    @Schema(description = "List of artist names associated with the track", examples = {"[\"Queen\"]"})
    public List<String> artistNames;

    @Schema(description = "Source information for the track")
    public SourceInfo source;

    @Schema(description = "Current status of the track submission", examples = {"PENDING"})
    public String status;

    @Schema(description = "Date and time when the track was submitted", format = "date-time", examples = {"2023-10-01T12:00:00Z"})
    public LocalDateTime submissionDate;

    @Schema(description = "Information about the source platform or system")
    public static class SourceInfo {
        @Schema(description = "Name of the source platform", examples = "Spotify")
        public String name;

        @Schema(description = "External identifier from the source platform", examples = {"track:123456789"})
        public String externalId;
    }

}
