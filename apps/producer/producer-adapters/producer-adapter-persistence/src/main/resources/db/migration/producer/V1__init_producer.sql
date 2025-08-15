-- Producer context initial schema
CREATE TABLE IF NOT EXISTS producers (
    id UUID PRIMARY KEY,
    producer_code VARCHAR(5) NOT NULL UNIQUE,
    name VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS producer_tracks (
    producer_id UUID NOT NULL REFERENCES producers(id) ON DELETE CASCADE,
    isrc VARCHAR(12) NOT NULL,
    PRIMARY KEY (producer_id, isrc)
);
