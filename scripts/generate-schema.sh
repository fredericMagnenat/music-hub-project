#!/bin/bash

# Schema Export Script for Music Hub Project
# Generates database schema using Hibernate and Quarkus dev mode

set -e

echo "🔧 Generating database schema..."

# Navigate to bootstrap module
cd "$(dirname "$0")/../apps/bootstrap"

# Run Quarkus with schema export profile
echo "📦 Starting Quarkus with schema export configuration..."

# Use temporary config file for schema export
export QUARKUS_PROFILE=schema-export

# Run application with schema generation enabled
mvn quarkus:dev \
  -Dquarkus.hibernate-orm.database.generation=create \
  -Dquarkus.flyway.migrate-at-start=false \
  -Dquarkus.datasource.db-kind=h2 \
  -Dquarkus.datasource.jdbc.url=jdbc:h2:mem:schema-export \
  -Dquarkus.hibernate-orm.log.sql=true \
  -Dquarkus.hibernate-orm.scripts.action=create \
  -Dquarkus.hibernate-orm.scripts.create-target=target/generated-schema.sql \
  -Dquarkus.application.name=schema-export \
  -Dquarkus.banner.enabled=false \
  -Dquarkus.dev.disable-console-input=true &

# Get the PID of the background process
QUARKUS_PID=$!

# Wait for schema generation (give it time to initialize)
echo "⏳ Waiting for schema generation to complete..."
sleep 10

# Stop the Quarkus application
echo "🛑 Stopping Quarkus application..."
kill $QUARKUS_PID || true

# Wait for cleanup
sleep 2

# Check if schema was generated
if [ -f "target/generated-schema.sql" ]; then
    echo "✅ Schema generated successfully!"
    echo "📁 File location: apps/bootstrap/target/generated-schema.sql"
    echo "📄 First 10 lines:"
    head -10 target/generated-schema.sql
else
    echo "❌ Schema generation failed - file not found"
    echo "💡 Check console output above for errors"
    exit 1
fi

echo "🎉 Schema export completed!"