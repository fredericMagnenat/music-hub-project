package com.musichub.producer.adapter.rest.dto.response;

import java.util.List;

public class TrackResponse {
    public String isrc;
    public String title;
    public List<ArtistCreditResponse> credits;
    public List<SourceResponse> sources;
    public String status;
}