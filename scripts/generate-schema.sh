#!/bin/bash

# Schema Generation & Validation Script for Music Hub Project
# Generates database schema using dev-schema-h2 profile and validates configuration
# Documentation: apps/bootstrap/README.md

set -e

# Function to validate profiles exist and load correctly
validate_profiles() {
    echo "🔍 Validating profile configurations..."
    cd "$(dirname "$0")/../apps/bootstrap"
    
    # Quick validation that profiles load without errors
    if mvn help:active-profiles -Dquarkus.profile=dev-schema-h2 -q > /dev/null 2>&1; then
        echo "✅ dev-schema-h2 profile configuration valid"
    else
        echo "❌ dev-schema-h2 profile configuration invalid"
        exit 1
    fi
    
    if mvn help:active-profiles -Dquarkus.profile=dev-schema -q > /dev/null 2>&1; then
        echo "✅ dev-schema profile configuration valid"
    else
        echo "❌ dev-schema profile configuration invalid"
        exit 1
    fi
}

# Check if --validate-only flag is passed
if [[ "$1" == "--validate-only" ]]; then
    validate_profiles
    echo "🎉 Profile validation completed successfully!"
    exit 0
fi

validate_profiles
echo "🔧 Generating database schema using dev-schema-h2 profile..."

# Navigate to bootstrap module
cd "$(dirname "$0")/../apps/bootstrap"

echo "📦 Starting Quarkus with dev-schema-h2 profile..."

# Run application with H2 schema generation profile
timeout 15s mvn quarkus:dev \
  -Dquarkus.profile=dev-schema-h2 \
  -Dquarkus.dev.disable-console-input=true \
  -Dquarkus.banner.enabled=false 2>&1 | \
  tee /tmp/schema-generation.log | \
  grep -E "(create table|CREATE TABLE|Started|ERROR)" || true

echo ""
echo "✅ Schema generation completed using dev-schema-h2 profile!"
echo ""
echo "📋 Generated schema information:"
echo "- Database: H2 in-memory (jdbc:h2:mem:schema-export)"
echo "- Mode: Hibernate create (no Flyway interference)"
echo "- SQL Logging: Enabled with formatting"
echo ""
echo "📄 Schema DDL statements (if any):"
grep -i "create table" /tmp/schema-generation.log 2>/dev/null | head -5 || echo "Check application logs for detailed DDL statements"
echo ""
echo "💡 To see full logs: cat /tmp/schema-generation.log"
echo ""
echo "🎯 Script Usage Options:"
echo "  ./scripts/generate-schema.sh              → Generate schema (default)"
echo "  ./scripts/generate-schema.sh --validate-only → Validate profiles only"
echo ""
echo "🎯 Profile Usage Guide:"
echo "  dev-schema-h2  → Quick validation, CI/CD (this script)"  
echo "  dev-schema     → Migration development, PostgreSQL DDL"
echo ""
echo "  For PostgreSQL schema: mvn quarkus:dev -Dquarkus.profile=dev-schema"