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

    private static final UUID NAMESPACE_PRODUCER = UUID.fromString("6ba7b811-9dad-11d1-80b4-00c04fd430c8"); // RFC 4122 DNS ns

    public ProducerId {
        if (value == null) {
            throw new IllegalArgumentException("ProducerId value must not be null");
        }
    }

    public static ProducerId fromProducerCode(ProducerCode producerCode) {
        Objects.requireNonNull(producerCode, "ProducerCode must not be null");
        UUID uuid = uuid5(NAMESPACE_PRODUCER, producerCode.value());
        return new ProducerId(uuid);
    }

    private static UUID uuid5(UUID namespace, String name) {
        try {
            MessageDigest sha1 = MessageDigest.getInstance("SHA-1");
            byte[] namespaceBytes = toBytes(namespace);
            sha1.update(namespaceBytes);
            sha1.update(name.getBytes(StandardCharsets.UTF_8));
            byte[] hash = sha1.digest();

            // Take first 16 bytes for UUID
            hash[6] &= 0x0f; // clear version
            hash[6] |= 0x50; // set to version 5
            hash[8] &= 0x3f; // clear variant
            hash[8] |= 0x80; // set to IETF variant

            long msb = toLong(hash, 0);
            long lsb = toLong(hash, 8);
            return new UUID(msb, lsb);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-1 algorithm not available", e);
        }
    }

    private static byte[] toBytes(UUID uuid) {
        ByteBuffer buffer = ByteBuffer.allocate(16);
        buffer.putLong(uuid.getMostSignificantBits());
        buffer.putLong(uuid.getLeastSignificantBits());
        return buffer.array();
    }

    private static long toLong(byte[] bytes, int offset) {
        long value = 0;
        for (int i = 0; i < 8; i++) {
            value = (value << 8) | (bytes[offset + i] & 0xff);
        }
        return value;
    }
}
