package com.musichub.producer.adapter.rest.dto;

import java.time.LocalDateTime;
import java.util.List;

public class RecentTrackResponse {

    public String isrc;

    public String title;

    public List<String> artistNames;

    public SourceInfo source;

    public String status;

    public LocalDateTime submissionDate;

    public static class SourceInfo {
        public String name;

        public String externalId;
    }

}