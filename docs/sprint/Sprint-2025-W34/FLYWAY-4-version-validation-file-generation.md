# FLYWAY-4: Add version range validation and migration file generation

Story: docs/stories/10.flyway-migration-workflow.md

## Description
Implement version number detection, range validation, and complete migration file generation with template and schema reference integration.

## Acceptance Criteria
- Given existing migrations in a context, when generating new migration, then next sequential version number is calculated correctly
- Given version would exceed context range, when generating migration, then script errors with clear message
- Generated migration file includes helpful template with context-specific examples
- Generated migration file includes schema reference from Hibernate plugin output (if available)
- File includes proper header with metadata (author, date, description)

## Dependencies
- FLYWAY-3: Core script logic must be implemented first

## Estimate
- 3 pts

## Status
- Not Started

## Technical Details

### Version range logic:
```bash
# Find next version number
CURRENT_VERSION=0
if [[ -d "$MIGRATION_PATH" ]]; then
    CURRENT_VERSION=$(find "$MIGRATION_PATH" -name "V*.sql" -exec basename {} \; 2>/dev/null | \
                     grep -o '^V[0-9]*' | \
                     sed 's/V//' | \
                     sort -n | \
                     tail -1 || echo "$((VERSION_MIN - 1))")
fi

NEXT_VERSION=$((CURRENT_VERSION + 1))

# Validate version is in range
if [[ $NEXT_VERSION -lt $VERSION_MIN || $NEXT_VERSION -gt $VERSION_MAX ]]; then
    print_error "Version $NEXT_VERSION is outside allowed range for $CONTEXT context ($VERSION_MIN-$VERSION_MAX)"
    exit 1
fi
```

### Migration file template structure:
```sql
-- $CONTEXT context: $MIGRATION_DESC  
-- Generated on $(date '+%Y-%m-%d %H:%M:%S')
-- Author: $(git config user.name || echo "Unknown")

-- TODO: Replace this template with your actual migration statements

-- Common migration patterns:
-- [Context-specific examples]

-- Generated schema for reference:
-- [Include output from hibernate plugin if available]
```

## Validation Steps
1. Create test migration files in both contexts with different version numbers
2. Test version detection: script should find highest existing version
3. Test version range enforcement: try to create V150 in producer context (should fail)
4. Test file generation: verify complete migration file is created with all sections
5. Test schema reference integration: run hibernate plugin first, then verify reference is included
6. Test git author detection in generated file header
7. Test with empty migration directories (should start at V1/V100)

## Next Task
- FLYWAY-5: Integration testing and workflow validation

## Artifacts
- Modified: `scripts/generate-migration.sh` (complete version)
- Generated: Test migration files for validation