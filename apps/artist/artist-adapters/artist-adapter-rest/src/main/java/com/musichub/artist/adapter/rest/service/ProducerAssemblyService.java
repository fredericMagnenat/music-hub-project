package com.musichub.artist.adapter.rest.service;

import com.musichub.artist.domain.model.Artist;
import com.musichub.shared.domain.values.ISRC;

import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service for assembling producer IDs for artist API responses.
 * Implements the cross-context query requirement from AC 4.
 *
 * For now, this extracts producer IDs from track ISRCs using the ProducerCode logic.
 * In a more complete implementation, this would query the Producer context.
 */
@ApplicationScoped
public class ProducerAssemblyService {

    /**
     * Assembles the list of producer IDs that an artist has collaborated with.
     * Based on the tracks (contributions) the artist has participated in.
     *
     * @param artist the artist to get producer IDs for
     * @return list of unique producer UUIDs
     */
    public List<UUID> getProducerIds(Artist artist) {
        return artist.getContributions().stream()
                .map(contribution -> contribution.isrc())
                .map(this::extractProducerCodeFromISRC)
                .distinct()
                .map(this::convertProducerCodeToUUID)
                .collect(Collectors.toList());
    }

    /**
     * Extracts producer code from an ISRC.
     * The first 5 characters of an ISRC represent the ProducerCode.
     *
     * @param isrc the ISRC to extract from
     * @return the producer code (first 5 characters)
     */
    private String extractProducerCodeFromISRC(ISRC isrc) {
        String isrcValue = isrc.value();
        if (isrcValue.length() >= 5) {
            return isrcValue.substring(0, 5);
        }
        return isrcValue; // Fallback for invalid ISRCs
    }

    /**
     * Converts a producer code to a deterministic UUID.
     * This is a simplified approach for the PoC.
     * In a real implementation, this would query the Producer context.
     *
     * @param producerCode the producer code
     * @return a deterministic UUID based on the producer code
     */
    private UUID convertProducerCodeToUUID(String producerCode) {
        // Generate deterministic UUID from producer code
        // Simple approach for the PoC - hash the producer code
        long hash = producerCode.hashCode();
        return new UUID(hash, hash);
    }
}