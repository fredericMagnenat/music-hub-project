package com.musichub.bootstrap.db;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.TestTransaction;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;
import static org.junit.jupiter.api.Assertions.assertEquals;

@QuarkusTest
@DisplayName("Flyway migration workflow integration tests")
class FlywayMigrationWorkflowIntegrationTest {

    @Inject
    EntityManager em;

    @Test
    @TestTransaction
    @DisplayName("Should have applied producer V2 and artist V102 with expected columns present")
    void shouldApplyMigrationsAndHaveColumns() {
        // Verify Flyway recorded versions in history
        Long countV2 = ((Number) em.createNativeQuery("SELECT COUNT(*) FROM \"flyway_schema_history\" WHERE \"version\" = '2'")
                .getSingleResult()).longValue();
        Long countV101 = ((Number) em.createNativeQuery("SELECT COUNT(*) FROM \"flyway_schema_history\" WHERE \"version\" = '102'")
                .getSingleResult()).longValue();
        assertEquals(1L, countV2);
        assertEquals(1L, countV101);

        // Verify producers.status column exists by attempting a DDL query that selects it
        // If column doesn't exist, database will throw
        em.createNativeQuery("SELECT status FROM producers WHERE 1=0").getResultList();
        em.createNativeQuery("SELECT country FROM artists WHERE 1=0").getResultList();

        // Insert minimal rows to ensure no constraint regressions
        em.createNativeQuery("INSERT INTO producers (id, producer_code) VALUES (?1, 'TEST1')")
                .setParameter(1, UUID.randomUUID())
                .executeUpdate();
        em.createNativeQuery("INSERT INTO artists (id, name, status) VALUES (?1, 'ART', 'ACTIVE')")
                .setParameter(1, UUID.randomUUID())
                .executeUpdate();
    }
}
