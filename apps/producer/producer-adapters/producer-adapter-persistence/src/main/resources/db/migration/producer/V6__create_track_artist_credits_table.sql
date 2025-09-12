-- producer context: create_track_artist_credits_table
-- Generated on 2025-09-12 12:10:57
-- Author: fred Magnenat <fred.magnenat@gmail.com>
-- Version: V6

-- Create the track_artist_credits table for @ElementCollection mapping
-- This table stores artist credits for tracks with artist_name and optional artist_id
-- Required by TrackEntity.credits @ElementCollection mapping

CREATE TABLE track_artist_credits (
    track_id UUID NOT NULL REFERENCES tracks(id) ON DELETE CASCADE,
    artist_name VARCHAR(255) NOT NULL,
    artist_id UUID NULL
);

-- Add primary key constraint (track_id, artist_name) to prevent duplicate credits
-- Note: artist_id is not part of PK as it can be null and updated later
ALTER TABLE track_artist_credits
ADD CONSTRAINT pk_track_artist_credits PRIMARY KEY (track_id, artist_name);

-- Performance indexes
CREATE INDEX idx_track_artist_credits_track_id ON track_artist_credits(track_id);
CREATE INDEX idx_track_artist_credits_artist_id ON track_artist_credits(artist_id);

-- Rollback strategy:
-- DROP TABLE track_artist_credits;

-- Migration checklist:
-- [x] SQL syntax validated
-- [ ] Migration tested on development database
-- [x] Rollback strategy documented
-- [x] Performance impact assessed for large tables (indexes added)
-- [ ] Migration reviewed by team
