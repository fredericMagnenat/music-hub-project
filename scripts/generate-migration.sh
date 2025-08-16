#!/bin/bash

# Migration Generation Script for Music Hub Project
# Generates Flyway migration files with version validation and schema reference
# Documentation: apps/bootstrap/README.md

set -e

# Script configuration
SCRIPT_DIR="$(dirname "$0")"
PROJECT_ROOT="$(cd "$SCRIPT_DIR/.." && pwd)"

# Context configuration with version ranges
# Producer: V1-V99, Artist: V100-V199
declare -A VERSION_RANGES
VERSION_RANGES[producer]="1:99"
VERSION_RANGES[artist]="100:199"

# Migration directories mapping
declare -A MIGRATION_DIRS
MIGRATION_DIRS[producer]="apps/producer/producer-adapters/producer-adapter-persistence/src/main/resources/db/migration/producer"
MIGRATION_DIRS[artist]="apps/artist/artist-adapters/artist-adapter-persistence/src/main/resources/db/migration/artist"

# Utility functions
print_usage() {
    echo "🎯 Migration Generation Script"
    echo ""
    echo "Usage: $0 <context> <description>"
    echo ""
    echo "Contexts:"
    echo "  producer  → V1-V99   (Producer domain migrations)"
    echo "  artist    → V100-V199 (Artist domain migrations)"
    echo ""
    echo "Examples:"
    echo "  $0 producer \"Add producer status column\""
    echo "  $0 artist \"Create artist genres table\""
    echo ""
    echo "🔧 Features:"
    echo "  ✅ Automatic version number detection"
    echo "  ✅ Version range validation"
    echo "  ✅ Template generation with context examples"
    echo "  ✅ Schema reference integration"
    echo "  ✅ Git metadata in file header"
}

print_error() {
    echo "❌ ERROR: $1" >&2
}

print_success() {
    echo "✅ $1"
}

print_info() {
    echo "🔍 $1"
}

# Validation functions
validate_context() {
    local context="$1"
    if [[ -z "${VERSION_RANGES[$context]:-}" ]]; then
        print_error "Invalid context '$context'. Valid contexts: ${!VERSION_RANGES[*]}"
        print_usage
        exit 1
    fi
}

validate_description() {
    local description="$1"
    if [[ -z "$description" ]]; then
        print_error "Description is required"
        print_usage
        exit 1
    fi
    
    # Validate description format (no special characters that could cause issues)
    if [[ ! "$description" =~ ^[a-zA-Z0-9[:space:]_-]+$ ]]; then
        print_error "Description contains invalid characters. Use only letters, numbers, spaces, hyphens, and underscores."
        exit 1
    fi
}

# Version detection and validation
find_next_version() {
    local context="$1"
    local migration_path="$PROJECT_ROOT/${MIGRATION_DIRS[$context]}"
    local range="${VERSION_RANGES[$context]}"
    local version_min=$(echo "$range" | cut -d: -f1)
    local version_max=$(echo "$range" | cut -d: -f2)
    
    print_info "Scanning for existing migrations in $context context..." >&2
    
    local current_version=0
    if [[ -d "$migration_path" ]]; then
        # Find highest version number from existing migration files
        current_version=$(find "$migration_path" -name "V*.sql" -exec basename {} \; 2>/dev/null | \
                         grep -o '^V[0-9]*' | \
                         sed 's/V//' | \
                         sort -n | \
                         tail -1 || echo $((version_min - 1)))
    else
        print_info "Migration directory does not exist yet: $migration_path" >&2
        current_version=$((version_min - 1))
    fi
    
    local next_version=$((current_version + 1))
    
    print_info "Current highest version: V$current_version" >&2
    print_info "Next version will be: V$next_version" >&2
    
    # Validate version is in allowed range
    if [[ $next_version -lt $version_min || $next_version -gt $version_max ]]; then
        print_error "Version V$next_version is outside allowed range for $context context (V$version_min-V$version_max)"
        print_error "Maximum versions reached for this context!"
        exit 1
    fi
    
    # Return version number via stdout (info messages go to stderr)
    echo "$next_version"
}

# Schema reference generation
get_schema_reference() {
    local schema_log="/tmp/schema-generation.log"
    
    print_info "Attempting to get schema reference from recent generation..."
    
    # Check if we have recent schema generation output
    if [[ -f "$schema_log" && $(find "$schema_log" -mmin -30 2>/dev/null) ]]; then
        print_info "Found recent schema generation log"
        # Extract CREATE TABLE statements
        local schema_content
        schema_content=$(grep -i "create table\|CREATE TABLE" "$schema_log" 2>/dev/null | head -10 || echo "")
        
        if [[ -n "$schema_content" ]]; then
            echo "-- Recent schema generation reference:"
            echo "-- (Generated from dev-schema-h2 profile)"
            echo "$schema_content" | sed 's/^/-- /'
        else
            echo "-- No CREATE TABLE statements found in recent schema generation"
            echo "-- Run './scripts/generate-schema.sh' to generate fresh schema reference"
        fi
    else
        echo "-- No recent schema generation found"
        echo "-- Run './scripts/generate-schema.sh' to generate schema reference"
        echo "-- This will help you understand current database structure"
    fi
}

# Migration file generation
generate_migration_file() {
    local context="$1"
    local version="$2"
    local description="$3"
    local migration_path="$PROJECT_ROOT/${MIGRATION_DIRS[$context]}"
    
    # Create migration directory if it doesn't exist
    mkdir -p "$migration_path"
    
    # Generate filename
    local sanitized_desc=$(echo "$description" | tr ' ' '_' | tr '[:upper:]' '[:lower:]')
    local filename="V${version}__${sanitized_desc}.sql"
    local filepath="$migration_path/$filename"
    
    # Check if file already exists
    if [[ -f "$filepath" ]]; then
        print_error "Migration file already exists: $filepath"
        exit 1
    fi
    
    # Get git author info
    local author=$(git config user.name 2>/dev/null || echo "Unknown")
    local email=$(git config user.email 2>/dev/null || echo "unknown@example.com")
    local timestamp=$(date '+%Y-%m-%d %H:%M:%S')
    
    # Generate context-specific examples
    local examples
    case "$context" in
        producer)
            examples="-- ADD COLUMN example:
-- ALTER TABLE producers ADD COLUMN created_at TIMESTAMP DEFAULT NOW();
-- 
-- ADD INDEX example:
-- CREATE INDEX idx_producers_code ON producers(producer_code);
-- 
-- INSERT DATA example:
-- INSERT INTO producers (id, producer_code, name) VALUES 
--   (gen_random_uuid(), 'PROD1', 'Example Producer');"
            ;;
        artist)
            examples="-- ADD COLUMN example:
-- ALTER TABLE artists ADD COLUMN created_at TIMESTAMP DEFAULT NOW();
-- 
-- ADD INDEX example:
-- CREATE INDEX idx_artists_name ON artists(name);
-- 
-- INSERT DATA example:
-- INSERT INTO artists (id, name, status) VALUES 
--   (gen_random_uuid(), 'Example Artist', 'ACTIVE');"
            ;;
        *)
            examples="-- Add your migration statements here"
            ;;
    esac
    
    # Generate the complete migration file
    cat > "$filepath" << EOF
-- $context context: $description
-- Generated on $timestamp
-- Author: $author <$email>
-- Version: V$version

-- TODO: Replace this template with your actual migration statements
-- IMPORTANT: Test your migration on a copy of production data first!

$examples

-- Rollback strategy (document how to reverse this migration):
-- TODO: Document rollback steps or create corresponding DOWN migration

$(get_schema_reference)

-- Migration checklist:
-- [ ] SQL syntax validated
-- [ ] Migration tested on development database
-- [ ] Rollback strategy documented
-- [ ] Performance impact assessed for large tables
-- [ ] Migration reviewed by team
EOF

    print_success "Migration file generated: $filepath"
    echo ""
    echo "📄 File location: ${filepath#$PROJECT_ROOT/}"
    echo "📝 Version: V$version"
    echo "👤 Author: $author"
    echo "📅 Timestamp: $timestamp"
    
    return 0
}

# Main execution
main() {
    echo "🚀 Music Hub Migration Generator"
    echo ""
    
    # Validate arguments
    if [[ $# -ne 2 ]]; then
        print_error "Invalid number of arguments"
        print_usage
        exit 1
    fi
    
    local context="$1"
    local description="$2"
    
    # Validate inputs
    validate_context "$context"
    validate_description "$description"
    
    # Find next version number
    local next_version
    next_version=$(find_next_version "$context")
    
    # Generate migration file
    generate_migration_file "$context" "$next_version" "$description"
    
    echo ""
    echo "🎉 Migration generation completed!"
    echo ""
    echo "📋 Next steps:"
    echo "1. Edit the generated migration file"
    echo "2. Test the migration on development database"
    echo "3. Review with team"
    echo "4. Apply migration: mvn flyway:migrate"
    echo ""
    echo "💡 Schema reference: Run './scripts/generate-schema.sh' for current DDL"
}

# Execute main function with all arguments
main "$@"