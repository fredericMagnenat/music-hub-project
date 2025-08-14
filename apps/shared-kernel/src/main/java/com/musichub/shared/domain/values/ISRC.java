package com.musichub.shared.domain.values;

import java.util.Objects;
import java.util.regex.Pattern;

public record ISRC(String value) {

    private static final Pattern ISRC_PATTERN = Pattern.compile("^[A-Z]{2}[A-Z0-9]{3}\\d{2}\\d{5}$");
    private static final String MESSAGE_ERROR_FORMAT = "ISRC value '%s' is invalid. The ISRC value must be composed of 12 alphanumeric characters, with two letters for the recording country, two letters or a digit for the label or producer, two letters for the artist or composer, and three letters or a digit for the recording value.";


    public ISRC {
        Objects.requireNonNull(value, "ISRC value cannot be null");
        if (!ISRC_PATTERN.matcher(value.replace("-", "")).matches()) {
            throw new IllegalArgumentException(String.format(MESSAGE_ERROR_FORMAT, value));
        }
    }

    public static ISRC of(String value) {
        return new ISRC(value);
    }

}
