package com.musichub.shared.domain.values;

import java.io.Serializable;
import java.util.Objects;
import java.util.regex.Pattern;

public record ISRC(String value) implements Serializable {

    private static final Pattern ISRC_PATTERN = Pattern.compile("^[A-Z]{2}[A-Z0-9]{3}[0-9]{7}$");

    public ISRC {
        Objects.requireNonNull(value, "ISRC value cannot be null");
        if (!ISRC_PATTERN.matcher(value).matches()) {
            throw new IllegalArgumentException("Invalid ISRC format: " + value);
        }
    }
}
