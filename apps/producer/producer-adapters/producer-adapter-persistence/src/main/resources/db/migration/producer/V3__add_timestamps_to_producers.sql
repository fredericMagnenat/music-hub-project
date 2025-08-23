-- Add timestamp columns to producers table
-- These are technical concerns handled by the persistence layer

ALTER TABLE producers 
ADD COLUMN created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
ADD COLUMN updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP;

-- Create function to automatically update updated_at timestamp
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE 'plpgsql';

-- Create trigger to automatically set updated_at on UPDATE
CREATE TRIGGER update_producers_updated_at 
    BEFORE UPDATE ON producers 
    FOR EACH ROW 
    EXECUTE FUNCTION update_updated_at_column();

-- Update existing records to have proper initial timestamps
UPDATE producers 
SET created_at = CURRENT_TIMESTAMP, 
    updated_at = CURRENT_TIMESTAMP 
WHERE created_at IS NULL OR updated_at IS NULL;

-- Remove status column as it's no longer needed in the domain model
ALTER TABLE producers DROP COLUMN status;
