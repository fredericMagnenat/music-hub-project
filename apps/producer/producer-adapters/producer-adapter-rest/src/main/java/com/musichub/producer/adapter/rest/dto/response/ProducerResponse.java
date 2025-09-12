package com.musichub.producer.adapter.rest.dto.response;

import java.util.Set;

public class ProducerResponse {
    public String id;
    public String producerCode;
    public String name;
    public Set<TrackResponse> tracks;
}