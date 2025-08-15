-- Artist context initial schema
CREATE TABLE IF NOT EXISTS artists (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    status VARCHAR(32) NOT NULL
);

CREATE TABLE IF NOT EXISTS artist_track_references (
    artist_id UUID NOT NULL REFERENCES artists(id) ON DELETE CASCADE,
    isrc VARCHAR(12) NOT NULL,
    PRIMARY KEY (artist_id, isrc)
);
