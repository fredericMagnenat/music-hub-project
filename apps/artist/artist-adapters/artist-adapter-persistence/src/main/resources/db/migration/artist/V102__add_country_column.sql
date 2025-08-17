-- Artist context migration: add country column
-- Version: V102
-- Description: Add optional country column to artists table for workflow validation tests

ALTER TABLE artists
    ADD COLUMN IF NOT EXISTS country VARCHAR(2);
