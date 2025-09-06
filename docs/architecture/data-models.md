# Data Models

These TypeScript interfaces serve as the official API contract between the backend and the frontend.

## "Producer"

```typescript
interface Producer {
  id: string; // UUID
  producerCode: string;
  name?: string | null;
  tracks: string[]; // List of track ISRC codes
}
```

## "Track" & Dependencies

```typescript
interface ArtistCredit {
  artistName: string;
  artistId?: string; // UUID
}

interface Source {
  sourceName: 'SPOTIFY' | 'TIDAL' | 'DEEZER' | 'APPLE_MUSIC' | 'MANUAL';
  externalId: string; // Renamed for clarity
}

interface Track {
  id: string; // UUID
  isrc: string;
  title: string;
  credits: ArtistCredit[];
  status: 'PROVISIONAL' | 'VERIFIED';
  sources: Source[];
  submissionDate: string; // ISO 8601 Date String
}
```

## "Artist"

```typescript
interface Contribution {
  trackId: string; // The track's UUID
  title: string;
  isrc: string;
}

interface Artist {
  id: string; // UUID
  name: string;
  status: 'PROVISIONAL' | 'VERIFIED';
  country?: string;
  sources: Source[]; // Platforms where the artist is identified
  contributions: Contribution[]; // The tracks to which the artist has contributed
  submissionDate: string; // ISO 8601 Date String
  producerIds: string[]; // List of associated producer UUIDs (assembled on the fly)
}
```

-----
