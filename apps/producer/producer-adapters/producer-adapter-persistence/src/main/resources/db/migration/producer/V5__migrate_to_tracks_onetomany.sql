-- producer context: migrate_to_tracks_oneToMany
-- Generated on 2025-08-23 17:39:48
-- Author: fred Magnenat <fred.magnenat@gmail.com>
-- Version: V5

-- Migrate from @ElementCollection producer_tracks to @OneToMany tracks relationship
-- This migration establishes proper Track entities with full bidirectional relationship

-- Step 1: Add producer_id foreign key column to tracks table
ALTER TABLE tracks 
ADD COLUMN producer_id UUID REFERENCES producers(id) ON DELETE CASCADE;

-- Step 2: Migrate existing data from producer_tracks to tracks table (if any exists)
-- This handles any existing ISRC references by creating minimal Track records
INSERT INTO tracks (id, isrc, producer_id, created_at, updated_at, status)
SELECT 
    gen_random_uuid(),  -- Generate new UUID for track
    pt.isrc,
    pt.producer_id,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP,
    'PROVISIONAL'       -- Default status for migrated tracks
FROM producer_tracks pt
WHERE NOT EXISTS (
    SELECT 1 FROM tracks t WHERE t.isrc = pt.isrc
);

-- Step 3: Drop the old producer_tracks table (no longer needed with @OneToMany)
DROP TABLE IF EXISTS producer_tracks;

-- Step 4: Add index for performance on the new foreign key
CREATE INDEX idx_tracks_producer_id ON tracks(producer_id);

-- Step 5: Re-add status column to producers (needed by our domain model)
ALTER TABLE producers 
ADD COLUMN status VARCHAR(20);

-- Rollback strategy:
-- 1. CREATE TABLE producer_tracks (producer_id UUID, isrc VARCHAR(12), PRIMARY KEY (producer_id, isrc));
-- 2. INSERT INTO producer_tracks SELECT producer_id, isrc FROM tracks WHERE producer_id IS NOT NULL;
-- 3. ALTER TABLE tracks DROP COLUMN producer_id;
-- 4. DROP INDEX idx_tracks_producer_id;
-- 5. ALTER TABLE producers DROP COLUMN status;

-- Migration checklist:
-- [x] SQL syntax validated
-- [x] Migration tested on development database
-- [x] Rollback strategy documented
-- [x] Performance impact assessed for large tables (indexes added)
-- [x] Migration reviewed by team
