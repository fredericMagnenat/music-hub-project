# FLYWAY-2: Create dev-schema profile

Story: docs/stories/10.flyway-migration-workflow.md

## Description
Create development profiles in `application.properties` that allow Hibernate to generate schema instead of validating, while disabling Flyway auto-migration for schema generation mode.

## Acceptance Criteria
- Given the `dev-schema` profile is activated, when starting the application, then Hibernate creates schema instead of validating
- Given the `dev-schema` profile, when application starts, then Flyway migration is disabled
- Profile uses temporary database configuration to avoid affecting main dev database
- Optional H2 alternative profile `dev-schema-h2` is available for faster iteration

## Dependencies
- FLYWAY-1: Hibernate plugin must be configured first

## Estimate
- 1 pt

## Status
- Not Started

## Technical Details

### Files to modify:
- `apps/bootstrap/src/main/resources/application.properties`

### Configuration to add:
```properties
# Profile for schema generation (development only)
%dev-schema.quarkus.hibernate-orm.database.generation=create
%dev-schema.quarkus.hibernate-orm.log.sql=true
%dev-schema.quarkus.hibernate-orm.sql-load-script=no-file
%dev-schema.quarkus.flyway.migrate-at-start=false
%dev-schema.quarkus.datasource.jdbc.url=jdbc:postgresql://localhost:5433/musichubdata_temp

# Alternative: Use H2 for quick schema generation
%dev-schema-h2.quarkus.datasource.db-kind=h2
%dev-schema-h2.quarkus.datasource.jdbc.url=jdbc:h2:mem:schema-gen;DB_CLOSE_DELAY=-1
%dev-schema-h2.quarkus.hibernate-orm.database.generation=create
%dev-schema-h2.quarkus.flyway.migrate-at-start=false
```

## Validation Steps
1. Add profile configurations to application.properties
2. Test PostgreSQL profile: `mvn quarkus:dev -Dquarkus.profile=dev-schema`
3. Verify Hibernate creates schema (not validates)
4. Verify Flyway migration is skipped
5. Test H2 profile: `mvn quarkus:dev -Dquarkus.profile=dev-schema-h2`
6. Verify H2 in-memory database is used
7. Check application logs for schema creation statements

## Next Task
- FLYWAY-3: Implement migration generation script core logic

## Artifacts
- Modified: `apps/bootstrap/src/main/resources/application.properties`