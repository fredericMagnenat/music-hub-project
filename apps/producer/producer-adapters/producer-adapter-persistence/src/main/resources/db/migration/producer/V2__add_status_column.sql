-- Producer context migration: add status column
-- Version: V2
-- Description: Add optional status column to producers table for workflow validation tests

ALTER TABLE producers
    ADD COLUMN IF NOT EXISTS status VARCHAR(20);
