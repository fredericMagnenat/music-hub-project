package com.musichub.producer.domain.values;

import com.musichub.shared.domain.values.ProducerCode;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;
import java.util.UUID;

/**
 * ProducerId is a deterministic UUIDv5 derived from the immutable ProducerCode.
 */
public record ProducerId(UUID value) {

    private static final UUID NAMESPACE_PRODUCER = UUID.fromString("6ba7b811-9dad-11d1-80b4-00c04fd430c8");


    private static final byte[] NAMESPACE_PRODUCER_BYTES = ByteBuffer.allocate(16)
            .putLong(NAMESPACE_PRODUCER.getMostSignificantBits())
            .putLong(NAMESPACE_PRODUCER.getLeastSignificantBits())
            .array();



    // Version et variant bits pour UUID v5
    private static final byte VERSION_5_MASK = 0x0f;
    private static final byte VERSION_5_VALUE = 0x50;
    private static final byte VARIANT_MASK = 0x3f;
    private static final byte VARIANT_IETF = (byte) 0x80;


    public ProducerId {
        if (value == null) {
            throw new IllegalArgumentException("ProducerId value must not be null");
        }
    }

    public static ProducerId fromProducerCode(ProducerCode producerCode) {
        Objects.requireNonNull(producerCode, "ProducerCode must not be null");
        UUID uuid = uuid5ForProducer(producerCode.value());
        return new ProducerId(uuid);
    }


    /**
     * Generates a version 5 UUID for a producer, based on the provided string value
     * and a predefined namespace for producers.
     *
     * @param value the input string used to generate the UUID. Must not be null.
     * @return a UUID generated using the version 5 specification with the given input value.
     * @throws IllegalStateException if the SHA-1 algorithm is not available.
     */
    private static UUID uuid5ForProducer(String value) {
        try {
            MessageDigest sha1 = MessageDigest.getInstance("SHA-1");
            sha1.update(NAMESPACE_PRODUCER_BYTES);
            sha1.update(value.getBytes(StandardCharsets.UTF_8));
            byte[] hash = sha1.digest();

            hash[6] &= VERSION_5_MASK; // clear version
            hash[6] |= VERSION_5_VALUE; // set to version 5
            hash[8] &= VARIANT_MASK; // clear variant
            hash[8] |= VARIANT_IETF; // set to IETF variant

            long msb = toLong(hash, 0);
            long lsb = toLong(hash, 8);
            return new UUID(msb, lsb);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-1 algorithm not available", e);
        }
    }
    private static long toLong(byte[] bytes, int offset) {
        long value = 0;
        for (int i = 0; i < 8; i++) {
            value = (value << 8) | (bytes[offset + i] & 0xff);
        }
        return value;
    }


}
