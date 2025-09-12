-- Add timestamp columns to producers table
-- These are technical concerns handled by the persistence layer

-- Add columns one at a time for H2 compatibility
ALTER TABLE producers ADD COLUMN created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP;
ALTER TABLE producers ADD COLUMN updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP;

-- Update existing records to have proper initial timestamps
UPDATE producers
SET created_at = CURRENT_TIMESTAMP,
    updated_at = CURRENT_TIMESTAMP
WHERE created_at IS NULL OR updated_at IS NULL;

-- Remove status column as it's no longer needed in the domain model
ALTER TABLE producers DROP COLUMN status;
