package com.musichub.shared.domain.values;


import java.util.Objects;
import java.util.regex.Pattern;

public record ProducerCode(String value) {
    private static final String REGEX_5 = "[A-Z]{2}[A-Z0-9]{3}";

    private static boolean isValid(String code) {
        return Pattern.compile(REGEX_5).matcher(code).matches();
    }

    private static String extractRegistrantCode(String input) {
        return input.length() == 5 ? input : input.substring(0, 5);
    }

    private String validateCode(String code) {
        if (code.length() != 5 && code.length() != 12) {
            throw new IllegalArgumentException(String.format("ProducerCode %s is invalid", code));
        }
        String extractedCode = extractRegistrantCode(code);
        if (!isValid(extractedCode)) {
            throw new IllegalArgumentException(String.format("ProducerCode %s is invalid", extractedCode));
        }
        return extractedCode;
    }

    public ProducerCode {
        Objects.requireNonNull(value, "ProducerCode must not be null");
        value = validateCode(value);
    }

    public static ProducerCode with(ISRC isrc) {
        Objects.requireNonNull(isrc, "TrackIsrc must not be null");
        return new ProducerCode(isrc.value());
    }

    public static ProducerCode of(String code) {
        Objects.requireNonNull(code, "ProducerCode must not be null");
        return new ProducerCode(code);
    }

}
