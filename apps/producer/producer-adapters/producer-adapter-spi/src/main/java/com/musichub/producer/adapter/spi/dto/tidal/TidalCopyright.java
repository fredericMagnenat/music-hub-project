package com.musichub.producer.adapter.spi.dto.tidal;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Copyright information from Tidal API response.
 * Tidal API returns copyright as an object with text field.
 */
public class TidalCopyright {

    /**
     * The copyright text
     */
    @JsonProperty("text")
    public String text;

    public TidalCopyright() {
    }

    public TidalCopyright(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text != null ? text : "";
    }
}