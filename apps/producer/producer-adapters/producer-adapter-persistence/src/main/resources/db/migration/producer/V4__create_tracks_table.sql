-- Create tracks table with JSON approach for sources
-- This approach stores sources as TEXT for H2 compatibility (JSONB is PostgreSQL-specific)
-- Sources are Value Objects with structure: {"sourceName": "SPOTIFY", "sourceId": "track-id"}

CREATE TABLE tracks (
    id UUID PRIMARY KEY,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    isrc VARCHAR(12) NOT NULL UNIQUE,
    title VARCHAR(500),
    sources TEXT,  -- JSON storage for Source Value Objects: [{"sourceName":"SPOTIFY","sourceId":"123"}]
    status VARCHAR(20)
);

-- Table for artists (still normalized as they're simple strings)
CREATE TABLE track_artists (
    track_id UUID NOT NULL REFERENCES tracks(id) ON DELETE CASCADE,
    artist_name VARCHAR(255) NOT NULL,
    PRIMARY KEY (track_id, artist_name)
);

-- Performance indexes
CREATE INDEX idx_tracks_isrc ON tracks(isrc);
CREATE INDEX idx_tracks_status ON tracks(status);
