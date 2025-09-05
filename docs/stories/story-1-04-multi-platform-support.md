### User Story: P3 - Support Multiple External Music Platforms

> **As a** System, **when** processing a track, **I want** to be able to query multiple external music platforms (e.g., Spotify, Tidal, Deezer), **in order to** increase the chances of finding comprehensive track metadata.

### Acceptance Criteria

1.  **Given** an ISRC to validate
    **When** the system queries for metadata
    **Then** it can sequentially query a configurable list of external platforms (e.g., Spotify then Tidal).

2.  **Given** metadata is found on the first platform
    **When** the system processes the track
    **Then** it does not query subsequent platforms for that ISRC.

3.  **Given** metadata is not found on the first platform
    **When** the system processes the track
    **Then** it attempts to find it on the next configured platform.

### Notes
- This story involves refactoring the single `MusicPlatformClient` into a strategy pattern.
- It will require creating specific client implementations for each platform.
- Configuration will be updated to manage multiple API endpoints and keys.
