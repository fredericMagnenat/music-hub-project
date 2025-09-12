-- Migration V1004: Fix ArtistCredit persistence mapping
-- Story: 1-04 - Fix ArtistCredit Persistence Mapping
-- Description: Replace track_artists table with track_artist_credits to support both artistName and artistId fields

-- Create new track_artist_credits table with both artistName and artistId columns
CREATE TABLE track_artist_credits (
    track_id UUID NOT NULL,
    artist_name VARCHAR(255) NOT NULL,
    artist_id UUID,
    CONSTRAINT fk_track_artist_credits_track FOREIGN KEY (track_id) REFERENCES tracks (id) ON DELETE CASCADE
);

-- Create index for performance on track_id lookups
CREATE INDEX idx_track_artist_credits_track_id ON track_artist_credits (track_id);

-- Create index for artist_id lookups (when not null)
CREATE INDEX idx_track_artist_credits_artist_id ON track_artist_credits (artist_id) WHERE artist_id IS NOT NULL;

-- Migrate existing data from track_artists to track_artist_credits
-- artist_id will be NULL for all existing records since this data wasn't preserved before
INSERT INTO track_artist_credits (track_id, artist_name, artist_id)
SELECT track_id, artist_name, NULL
FROM track_artists;

-- Drop the old track_artists table
DROP TABLE track_artists;

-- Add comment to document the change
COMMENT ON TABLE track_artist_credits IS 'Stores artist credits for tracks with both name and optional ID for proper Artist Context integration';
COMMENT ON COLUMN track_artist_credits.artist_name IS 'The artist name - required field';
COMMENT ON COLUMN track_artist_credits.artist_id IS 'The artist unique ID - optional field, may be null for unresolved artists';
