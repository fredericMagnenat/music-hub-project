-- Artist context migration: refactor to rich domain model
-- Version: V103
-- Description: Refactor Artist table to support rich domain model with contributions and sources
--              Remove old track_references table and add new contributions and sources tables

-- Remove old track references table if exists
DROP TABLE IF EXISTS artist_track_references;

-- Update artists table structure
ALTER TABLE artists
    ALTER COLUMN name TYPE VARCHAR(255),
    ALTER COLUMN status TYPE VARCHAR(20),
    DROP COLUMN IF EXISTS country;

-- Create artist_contributions table for @ElementCollection
CREATE TABLE IF NOT EXISTS artist_contributions (
    artist_id UUID NOT NULL,
    track_id UUID NOT NULL,
    track_title VARCHAR(255) NOT NULL,
    track_isrc VARCHAR(15) NOT NULL,
    CONSTRAINT fk_artist_contributions_artist
        FOREIGN KEY (artist_id) REFERENCES artists(id)
        ON DELETE CASCADE
);

-- Create artist_sources table for @ElementCollection
CREATE TABLE IF NOT EXISTS artist_sources (
    artist_id UUID NOT NULL,
    source_type VARCHAR(20) NOT NULL,
    source_id VARCHAR(100) NOT NULL,
    CONSTRAINT fk_artist_sources_artist
        FOREIGN KEY (artist_id) REFERENCES artists(id)
        ON DELETE CASCADE
);

-- Create indexes for performance
CREATE INDEX IF NOT EXISTS idx_artist_contributions_artist_id ON artist_contributions(artist_id);
CREATE INDEX IF NOT EXISTS idx_artist_contributions_track_id ON artist_contributions(track_id);
CREATE INDEX IF NOT EXISTS idx_artist_contributions_isrc ON artist_contributions(track_isrc);

CREATE INDEX IF NOT EXISTS idx_artist_sources_artist_id ON artist_sources(artist_id);
CREATE INDEX IF NOT EXISTS idx_artist_sources_type_id ON artist_sources(source_type, source_id);

-- Add constraint for source type enum values
ALTER TABLE artist_sources
    ADD CONSTRAINT chk_artist_sources_type
    CHECK (source_type IN ('MANUAL', 'TIDAL', 'SPOTIFY', 'DEEZER', 'APPLE_MUSIC'));

-- Add constraint for artist status enum values
ALTER TABLE artists
    ADD CONSTRAINT chk_artists_status
    CHECK (status IN ('PROVISIONAL', 'VERIFIED'));