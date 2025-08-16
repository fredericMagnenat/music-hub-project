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
- Not Started

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