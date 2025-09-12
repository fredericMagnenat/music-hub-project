package com.musichub.producer.adapter.spi.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

public class ArtistDto {

    @JsonProperty("name")
    public String name;

    @JsonProperty("id")
    public UUID id;
}
