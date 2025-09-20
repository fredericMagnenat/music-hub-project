package com.musichub.artist.application.config;

import com.musichub.artist.application.ports.out.ArtistReconciliationPort;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Any;
import jakarta.enterprise.inject.Instance;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Named;

import java.util.List;
import java.util.stream.Collectors;

/**
 * CDI configuration for artist reconciliation ports.
 * Provides a producer method to inject all available ArtistReconciliationPort implementations.
 */
@ApplicationScoped
public class ArtistReconciliationConfig {

    /**
     * Produces a list of all available ArtistReconciliationPort implementations.
     * This allows the ArtistEnrichmentService to inject all reconciliation ports
     * without knowing the specific implementations.
     *
     * @param reconciliationPorts all available reconciliation port instances
     * @return list of reconciliation ports
     */
    @Produces
    @ApplicationScoped
    public List<ArtistReconciliationPort> reconciliationPorts(@Any Instance<ArtistReconciliationPort> reconciliationPorts) {
        return reconciliationPorts.stream()
                .collect(Collectors.toList());
    }
}