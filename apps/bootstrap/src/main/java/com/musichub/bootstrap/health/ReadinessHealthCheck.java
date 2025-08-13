package com.musichub.bootstrap.health;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.HealthCheckResponseBuilder;
import org.eclipse.microprofile.health.Readiness;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Readiness health check for the Music Data Hub application.
 * This health check verifies that the application is ready to serve requests
 * by checking the database connection.
 */
@Readiness
@ApplicationScoped
public class ReadinessHealthCheck implements HealthCheck {

    @ConfigProperty(name = "health.database.timeout.ms", defaultValue = "1000")
    int databaseTimeout;

    @Inject
    MeterRegistry registry;


    //private static final Logger LOG = Logger.getLogger(ReadinessHealthCheck.class);
    
    @Inject
    DataSource dataSource;
    
    @Override
    public HealthCheckResponse call() {
        Log.debug("Performing readiness health check");

        Timer.Sample sample = Timer.start(registry);

        try (Connection connection = dataSource.getConnection()) {
            // Vérification de la connexion à la base de données
            boolean isValid = connection.isValid(databaseTimeout);
            long responseTime = sample.stop(registry.timer("database.connection.check"));

            // Construction de la réponse avec des métriques détaillées
            HealthCheckResponseBuilder responseBuilder = HealthCheckResponse.named("database-ready")
                    .withData("responseTime", responseTime + "ms")
                    .withData("databaseProduct", connection.getMetaData().getDatabaseProductName())
                    .withData("databaseVersion", connection.getMetaData().getDatabaseProductVersion())
                    .status(isValid);

            // Ajouter des informations sur l'état de la transaction
            responseBuilder.withData("autoCommit", connection.getAutoCommit());

            if (isValid) {
                Log.debug("Database connection check successful in " + responseTime + "ms");
            } else {
                Log.warn("Database connection is not valid");
            }

            return responseBuilder.build();

        } catch (SQLException e) {
            Log.error("Error checking database connection: " + e.getMessage(), e);
            return HealthCheckResponse.down("Database connection error: " + e.getMessage());
        }

    }
}