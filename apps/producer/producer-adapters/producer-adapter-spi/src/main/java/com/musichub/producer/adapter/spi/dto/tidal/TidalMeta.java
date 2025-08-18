package com.musichub.producer.adapter.spi.dto.tidal;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Metadata in Tidal's JSON:API response.
 * Contains information about the response like total count, etc.
 */
public class TidalMeta {

    /**
     * Total number of resources available (for pagination)
     */
    @JsonProperty("total")
    public Integer total;

    public TidalMeta() {
    }

    public TidalMeta(Integer total) {
        this.total = total;
    }
}