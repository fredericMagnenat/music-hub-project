package com.musichub.producer.adapter.spi.dto.tidal;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Links for pagination in Tidal's JSON:API response.
 * Contains URLs for navigating through paginated results.
 */
public class TidalLinks {

    /**
     * Link to the first page
     */
    @JsonProperty("first")
    public String first;

    /**
     * Link to the last page
     */
    @JsonProperty("last")
    public String last;

    /**
     * Link to the previous page
     */
    @JsonProperty("prev")
    public String prev;

    /**
     * Link to the next page
     */
    @JsonProperty("next")
    public String next;

    /**
     * Link to the current page (self)
     */
    @JsonProperty("self")
    public String self;

    public TidalLinks() {
    }
}