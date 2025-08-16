# FLYWAY-1: Configure Hibernate Schema Export Plugin

Story: docs/stories/10.flyway-migration-workflow.md

## Description
Configure the `hibernate-enhance-maven-plugin` in `apps/bootstrap/pom.xml` to enable schema export functionality for migration generation workflow.

## Acceptance Criteria
- Given the plugin is configured, when running `mvn hibernate-enhance:schema-export`, then a schema SQL file is generated in `target/generated-schema.sql`
- Given the plugin configuration, when executing with PostgreSQL driver, then the generated schema uses PostgreSQL-specific syntax
- Plugin execution phase is set to `none` (manual execution only)
- Output file is formatted and includes namespace creation

## Dependencies
- None (foundation task)

## Estimate
- 1 pt

## Status
- Completed

## Technical Details

### Files to modify:
- `apps/bootstrap/pom.xml`

### Configuration to add:
```xml
<plugin>
    <groupId>org.hibernate.orm.tooling</groupId>
    <artifactId>hibernate-enhance-maven-plugin</artifactId>
    <version>${hibernate.version}</version>
    <executions>
        <execution>
            <id>schema-export</id>
            <goals>
                <goal>schema-export</goal>
            </goals>
            <phase>none</phase>
            <configuration>
                <outputFile>target/generated-schema.sql</outputFile>
                <delimiter>;</delimiter>
                <format>true</format>
                <createNamespaces>true</createNamespaces>
            </configuration>
        </execution>
    </executions>
    <dependencies>
        <dependency>
            <groupId>org.postgresql</groupId>
            <artifactId>postgresql</artifactId>
            <version>${postgresql.version}</version>
        </dependency>
    </dependencies>
</plugin>
```

## Validation Steps
1. Add plugin configuration to POM
2. Run `mvn hibernate-enhance:schema-export -f apps/bootstrap/pom.xml`
3. Verify `apps/bootstrap/target/generated-schema.sql` is created
4. Verify generated SQL contains PostgreSQL-specific syntax
5. Verify file is properly formatted with semicolon delimiters

## Next Task
- FLYWAY-2: Create dev-schema profile

## Artifacts
- Modified: `apps/bootstrap/pom.xml`
- Generated: `apps/bootstrap/target/generated-schema.sql` (validation artifact)
- Created: `apps/bootstrap/src/main/resources/schema-export.properties` (configuration)
- Created: `scripts/generate-schema.sh` (helper script)

## Dev Agent Record

### Implementation Notes
The original `hibernate-enhance-maven-plugin` specified in the story is no longer available in Hibernate 6+ / Quarkus 3.x. The implementation has been adapted to use modern Quarkus/Hibernate tooling:

1. **Modern Approach**: Used `exec-maven-plugin` with Quarkus runtime for schema generation
2. **Configuration**: Created `schema-export.properties` profile for dedicated schema generation
3. **Helper Script**: Created `scripts/generate-schema.sh` for easy schema generation workflow
4. **Validation**: Schema file generated with PostgreSQL syntax and proper formatting

### Files Modified
- `apps/bootstrap/pom.xml` - Added exec-maven-plugin for schema export
- `apps/bootstrap/src/main/resources/schema-export.properties` - Schema export configuration
- `scripts/generate-schema.sh` - Schema generation helper script
- `apps/bootstrap/target/generated-schema.sql` - Generated schema example

### Alternative Execution Methods
1. Using helper script: `./scripts/generate-schema.sh`
2. Using Maven directly: `mvn exec:java@schema-export -f apps/bootstrap/pom.xml`
3. Using Quarkus dev mode with schema generation profile

### Status
**COMPLETED** - Schema export functionality implemented with modern Quarkus/Hibernate 6+ tooling.