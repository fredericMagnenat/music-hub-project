package com.musichub.shared.domain.id;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;
import java.util.UUID;

public final class IdGenerator {

        private IdGenerator() {
        }

        // Version et variant bits pour UUID v5
        private static final byte VERSION_5_MASK = 0x0f;
        private static final byte VERSION_5_VALUE = 0x50;
        private static final byte VARIANT_MASK = 0x3f;
        private static final byte VARIANT_IETF = (byte) 0x80;

        public static UUID generateUUID() {
                return UUID.randomUUID();
        }

        public static UUID hash(String prefix) {
                Objects.requireNonNull(prefix, "Prefix must not be null");
                if (prefix.trim().isEmpty()) {
                        throw new IllegalArgumentException("Prefix must not be empty");
                }
                try {
                        MessageDigest digest = MessageDigest.getInstance("SHA-256");
                        byte[] hashBytes = digest.digest(prefix.getBytes());
                        ByteBuffer byteBuffer = ByteBuffer.wrap(hashBytes);
                        long mostSigBits = byteBuffer.getLong();
                        long leastSigBits = byteBuffer.getLong();
                        return new UUID(mostSigBits, leastSigBits);
                } catch (NoSuchAlgorithmException e) {
                        throw new RuntimeException("SHA-256 algorithm not found", e);
                }
        }

        /**
         * Generates a version 5 UUID based on the provided namespace UUID and input value.
         * This is a deterministic UUID generation method that will always produce the same
         * UUID for the same namespace and input value combination.
         *
         * @param namespace the namespace UUID used as a base for generation. Must not be null.
         * @param value the input string used to generate the UUID. Must not be null.
         * @return a UUID generated using the version 5 specification with the given namespace and input value.
         * @throws IllegalStateException if the SHA-1 algorithm is not available.
         */
        public static UUID generateUUIDv5(UUID namespace, String value) {
                Objects.requireNonNull(namespace, "Namespace must not be null");
                Objects.requireNonNull(value, "Value must not be null");

                try {
                        byte[] namespaceBytes = ByteBuffer.allocate(16)
                                .putLong(namespace.getMostSignificantBits())
                                .putLong(namespace.getLeastSignificantBits())
                                .array();

                        MessageDigest sha1 = MessageDigest.getInstance("SHA-1");
                        sha1.update(namespaceBytes);
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