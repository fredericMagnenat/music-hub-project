package com.musichub.bootstrap.health;

import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.HealthCheckResponseBuilder;
import org.eclipse.microprofile.health.Liveness;

import java.lang.management.ManagementFactory;

@Liveness
@ApplicationScoped
public class LivenessHealthCheck implements HealthCheck {

    @ConfigProperty(name = "health.memory.threshold.percentage", defaultValue = "90")
    int memoryThreshold;

    @Override
    public HealthCheckResponse call() {
        Log.debug("Performing liveness health check");

        // Informations sur la mémoire
        Runtime runtime = Runtime.getRuntime();
        long maxMemory = runtime.maxMemory();
        long totalMemory = runtime.totalMemory();
        long freeMemory = runtime.freeMemory();
        long usedMemory = totalMemory - freeMemory;
        double memoryUsagePercentage = (usedMemory * 100.0) / maxMemory;

        // Informations sur les threads
        int activeThreads = Thread.activeCount();
        long availableProcessors = runtime.availableProcessors();

        // Construction de la réponse avec métriques détaillées
        HealthCheckResponseBuilder builder = HealthCheckResponse.named("application-live")
                .withData("memory.max.mb", maxMemory / (1024 * 1024))
                .withData("memory.total.mb", totalMemory / (1024 * 1024))
                .withData("memory.free.mb", freeMemory / (1024 * 1024))
                .withData("memory.used.mb", usedMemory / (1024 * 1024))
                .withData("memory.used.percentage", String.format("%.2f%%", memoryUsagePercentage))
                .withData("threads.active", activeThreads)
                .withData("processors.available", availableProcessors)
                .withData("uptime.ms", System.currentTimeMillis() - ManagementFactory.getRuntimeMXBean().getStartTime())
                .up();

        // Vérifier si l'utilisation de la mémoire dépasse le seuil configuré
        if (memoryUsagePercentage > memoryThreshold) {
            Log.warn("Memory usage exceeds threshold: " + String.format("%.2f%%", memoryUsagePercentage) +
                    " > " + memoryThreshold + "%");
            builder.down();
        }

        return builder.build();
    }
}
