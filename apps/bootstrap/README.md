# Development Schema Profiles

This document describes the dev-schema profiles available for schema generation during migration development.

## When to Use Which Profile?

| Use Case | Recommended Profile | Why? |
|----------|-------------------|------|
| **Quick entity validation** | `dev-schema-h2` | Fast startup (2s), no external deps |
| **Migration development** | `dev-schema` | PostgreSQL-compatible DDL |
| **CI/CD schema checks** | `dev-schema-h2` | No infrastructure required |
| **Production DDL preview** | `dev-schema` | Real PostgreSQL dialect |

### Why Two Profiles? 🤔

**Question fréquente :** *"Pourquoi avoir deux profils qui font la même chose ?"*

**Réponse courte :** Performance et spécialisation.

**Analyse détaillée :**
- **Performance** : H2 démarre en ~2s vs PostgreSQL ~10s (5x plus rapide)
- **Indépendance** : H2 ne nécessite aucun service externe (idéal pour CI/CD)
- **Spécialisation** : Chaque profil a des cas d'usage distincts et valides
- **Compatibilité** : Nos entités simples (UUID, String) fonctionnent identiquement sur les deux
- **Coût maintenance** : Minimal, configuration stable

**Cas d'évolution future :** Si nous introduisons des fonctionnalités PostgreSQL spécifiques (JSON, arrays), nous devrons privilégier `dev-schema` pour la validation d'entités.

## Available Profiles

### 1. `dev-schema` (PostgreSQL)
- **Database**: PostgreSQL with temporary database (`musichubdata_temp`)  
- **Schema Generation**: Hibernate creates schema (`create` mode)
- **Flyway**: Disabled (`migrate-at-start=false`)
- **SQL Logging**: Enabled with formatting

**Usage:**
```bash
mvn quarkus:dev -Dquarkus.profile=dev-schema
```

### 2. `dev-schema-h2` (H2 In-Memory)
- **Database**: H2 in-memory (`jdbc:h2:mem:schema-export`) - unified with FLYWAY-1
- **Schema Generation**: Hibernate creates schema (`create` mode)  
- **Flyway**: Disabled (`migrate-at-start=false`)
- **SQL Logging**: Enabled with formatting
- **UI**: Quinoa disabled for faster startup
- **Error Handling**: Halts on generation errors for debugging

**Usage:**
```bash
mvn quarkus:dev -Dquarkus.profile=dev-schema-h2
```

## Purpose

These profiles are designed for:

1. **Migration Development**: Generate schema to understand what Hibernate would create
2. **Entity Validation**: Test JPA entity mappings without affecting main database
3. **Quick Iteration**: H2 profile provides fast startup for schema validation
4. **Schema Comparison**: Compare generated schema with existing migrations

## Configuration

The profiles are configured in `apps/bootstrap/src/main/resources/application.properties`:

```properties
# PostgreSQL schema generation profile
%dev-schema.quarkus.hibernate-orm.database.generation=create
%dev-schema.quarkus.flyway.migrate-at-start=false
%dev-schema.quarkus.datasource.jdbc.url=jdbc:postgresql://localhost:5433/musichubdata_temp

# H2 schema generation profile  
%dev-schema-h2.quarkus.datasource.db-kind=h2
%dev-schema-h2.quarkus.datasource.jdbc.url=jdbc:h2:mem:schema-gen;DB_CLOSE_DELAY=-1
%dev-schema-h2.quarkus.hibernate-orm.database.generation=create
%dev-schema-h2.quarkus.flyway.migrate-at-start=false
```

## Testing

Use the validation scripts:

```bash
# Generate schema (default usage)
./scripts/generate-schema.sh

# Validate profile configuration only
./scripts/generate-schema.sh --validate-only
```

## Integration with Migration Workflow

These profiles integrate with the migration generation workflow:

1. Modify your JPA entities
2. Use `dev-schema-h2` profile to quickly validate entity mappings
3. Use `dev-schema` profile to generate PostgreSQL-compatible schema
4. Create migration files using generated schema as reference
5. Return to normal `dev` profile for testing migrations

## Notes

- The H2 profile is faster for quick validation
- The PostgreSQL profile generates production-compatible schema
- Both profiles disable Flyway to prevent interference with schema generation
- SQL logging is enabled to capture generated DDL statements