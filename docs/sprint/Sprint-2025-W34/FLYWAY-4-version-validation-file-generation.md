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
- Completed

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
- Created: `scripts/generate-migration.sh` (complete version)
- Validated: Version detection, range validation, file generation, schema integration

## Dev Agent Record

### Implementation Summary
Successfully implemented complete migration generation script with all required features:

1. **Version Detection**: Automatically detects highest existing migration version per context
2. **Range Validation**: Enforces Producer (V1-V99) and Artist (V100-V199) version ranges  
3. **Template Generation**: Creates migration files with context-specific examples
4. **Schema Reference**: Integrates recent schema generation output when available
5. **Git Metadata**: Includes author, email, timestamp in file headers
6. **Input Validation**: Validates context names and description format

### Key Features Implemented

#### Version Range Logic ✅
```bash
# Detects existing migrations and calculates next version
find "$migration_path" -name "V*.sql" | grep -o '^V[0-9]*' | sort -n | tail -1

# Validates against context-specific ranges
if [[ $next_version -lt $version_min || $next_version -gt $version_max ]]; then
    print_error "Version V$next_version is outside allowed range"
    exit 1
fi
```

#### Migration Template Generation ✅
- Context-specific SQL examples (Producer vs Artist)
- Migration checklist and rollback strategy sections
- Git metadata header with author and timestamp
- Schema reference integration from recent generation

#### Error Handling ✅
- Invalid context validation
- Description format validation (alphanumeric + spaces/hyphens/underscores)
- Version range overflow protection
- Clear error messages with usage examples

### Validation Results
✅ **Producer context**: V1 → V2 (within range)
✅ **Artist context**: V100 → V101 (within range)  
✅ **Range limits**: V99 → V100 fails for producer context
✅ **Invalid contexts**: Proper error messages
✅ **Invalid descriptions**: Special character validation
✅ **Schema integration**: Detects recent schema generation logs
✅ **Git metadata**: Author and email detection from git config

### Usage Examples
```bash
# Generate producer migration
./scripts/generate-migration.sh producer "Add status column"

# Generate artist migration  
./scripts/generate-migration.sh artist "Create genres table"

# Error cases
./scripts/generate-migration.sh invalid "Test"        # Invalid context
./scripts/generate-migration.sh producer "Test@#$"   # Invalid characters
```

### Integration with Schema Generation
The script integrates with `generate-schema.sh` by:
- Detecting recent schema generation logs (`/tmp/schema-generation.log`)
- Including relevant CREATE TABLE statements as comments
- Providing guidance to run schema generation for reference

### File Structure Created
```
apps/
├── producer/producer-adapters/producer-adapter-persistence/src/main/resources/db/migration/producer/
│   └── V{next}__description.sql
└── artist/artist-adapters/artist-adapter-persistence/src/main/resources/db/migration/artist/  
    └── V{next}__description.sql
```

### Next Steps
Ready for FLYWAY-5: Integration testing and workflow validation.