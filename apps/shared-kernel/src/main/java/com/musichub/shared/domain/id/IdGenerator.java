package com.musichub.shared.domain.id;

import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;
import java.util.UUID;

public final class IdGenerator {

        private IdGenerator() {
        }

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
}