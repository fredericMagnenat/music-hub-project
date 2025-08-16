# FLYWAY-3: Implement migration generation script core logic

Story: docs/stories/10.flyway-migration-workflow.md

## Description
Create the core `scripts/generate-migration.sh` script with parameter validation, context validation, and basic file generation logic.

## Acceptance Criteria
- Given valid parameters (description, context), when running the script, then it validates input and shows appropriate messages
- Given invalid parameters, when running the script, then it shows usage help and exits with error code
- Script validates context is either 'producer' or 'artist'
- Script creates migration directory structure if it doesn't exist
- Basic colored output functions work correctly (status, success, warning, error)

## Dependencies
- FLYWAY-2: Profiles must exist for schema generation integration

## Estimate
- 2 pts

## Status
- Not Started

## Technical Details

### Files to create:
- `scripts/generate-migration.sh`

### Core script structure:
```bash
#!/bin/bash
set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Output functions
print_status() { echo -e "${BLUE}🔄 $1${NC}"; }
print_success() { echo -e "${GREEN}✅ $1${NC}"; }
print_warning() { echo -e "${YELLOW}⚠️  $1${NC}"; }
print_error() { echo -e "${RED}❌ $1${NC}"; }

# Parameter validation
MIGRATION_DESC="$1"
CONTEXT="$2"

# Usage validation and context validation logic
# Directory creation logic
```

### Context validation:
- Producer: versions V1-V99, path `apps/producer/producer-adapters/producer-adapter-persistence/src/main/resources/db/migration/producer/`
- Artist: versions V100-V199, path `apps/artist/artist-adapters/artist-adapter-persistence/src/main/resources/db/migration/artist/`

## Validation Steps
1. Create script file and make executable: `chmod +x scripts/generate-migration.sh`
2. Test without parameters: `./scripts/generate-migration.sh` (should show usage)
3. Test with invalid context: `./scripts/generate-migration.sh "test" "invalid"` (should error)
4. Test with valid parameters: `./scripts/generate-migration.sh "test_migration" "producer"` (should show progress)
5. Verify migration directories are created correctly
6. Test colored output functions work in terminal

## Next Task
- FLYWAY-4: Add version range validation and file generation

## Artifacts
- Created: `scripts/generate-migration.sh` (executable)
- Created: Migration directory structure as needed