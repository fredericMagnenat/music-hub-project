# FLYWAY-5: Integration testing and complete workflow validation

Story: docs/stories/10.flyway-migration-workflow.md

## Description
Complete end-to-end integration testing of the Flyway migration generation workflow, including schema export integration, migration application testing, and edge case validation.

## Acceptance Criteria
- Given a JPA entity change, when following complete workflow (modify entity → generate migration → apply migration), then schema is updated correctly
- Given generated migration, when applying with Flyway, then migration succeeds without errors
- Edge cases handled correctly: no existing migrations, version range limits, concurrent version conflicts
- Both PostgreSQL and H2 schema generation profiles work correctly
- Documentation examples match actual script behavior

## Dependencies
- FLYWAY-4: Complete script implementation required
- FLYWAY-2: Schema profiles must be available for testing
- FLYWAY-1: Hibernate plugin must work for schema reference

## Estimate
- 3 pts

## Status
- Not Started

## Technical Details

### Integration test scenarios:

1. **Complete workflow test:**
   - Modify existing Producer/Artist entity
   - Run schema export: `mvn hibernate-enhance:schema-export -f apps/bootstrap/pom.xml`
   - Generate migration: `./scripts/generate-migration.sh "add_test_field" "producer"`
   - Edit generated migration with actual SQL
   - Apply migration: `mvn quarkus:dev`
   - Verify database schema updated

2. **Edge case testing:**
   - Empty migration directory (should start at V1/V100)
   - Reaching version range limit (V99 producer, V199 artist)
   - Invalid context names
   - Missing schema export file
   - Concurrent script execution

3. **Schema generation profile testing:**
   - Test `dev-schema` with PostgreSQL
   - Test `dev-schema-h2` with H2
   - Verify schema differences are captured correctly

## Validation Steps
1. **Workflow Integration Test:**
   - Create test branch for entity modification
   - Add test field to ProducerEntity
   - Follow complete documented workflow
   - Verify migration file quality and completeness
   - Apply migration and verify database state

2. **Edge Case Tests:**
   - Test script with no existing migrations in both contexts
   - Test version range boundary conditions
   - Test invalid inputs and error messages
   - Test schema export integration when file missing

3. **Multi-context Test:**
   - Generate migrations in both producer and artist contexts
   - Verify version ranges don't conflict
   - Test migration application with multiple contexts

4. **Profile Testing:**
   - Test both dev-schema profiles work correctly
   - Verify schema generation produces useful output
   - Test integration with migration generation

5. **Documentation Validation:**
   - Verify all script examples in story work as documented
   - Test all command examples from migration workflow documentation
   - Validate script output matches documented examples

## Success Criteria
- [ ] Complete entity-to-migration workflow works end-to-end
- [ ] All edge cases handled gracefully with clear error messages
- [ ] Both schema generation profiles functional
- [ ] Generated migrations apply successfully with Flyway
- [ ] Script behavior matches documentation examples
- [ ] Version range validation prevents context conflicts

## Next Task
- Story completion and handoff to development

## Artifacts
- Validated: Complete `scripts/generate-migration.sh`
- Validated: Schema generation profiles in `application.properties`
- Validated: Hibernate plugin configuration in `pom.xml`
- Created: Test migration files demonstrating workflow
- Updated: Any documentation discrepancies found during testing