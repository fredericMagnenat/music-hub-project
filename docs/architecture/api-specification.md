# API Specification

The following OpenAPI 3.0 specification serves as the formal technical contract for the REST API.

```yaml
openapi: 3.0.0
info:
  title: Music Hub API
  version: 1.0.0
  description: API for centralized management of music catalogs.
servers:
  - url: /api
    description: Main server

paths:
  /producers:
    post:
      summary: Registers a new track via its ISRC
      operationId: registerTrack
      requestBody:
        required: true
        content:
          application/json:
            schema:
              type: object
              properties:
                isrc:
                  type: string
                  example: "FRLA12400001"
              required:
                - isrc
      responses:
        '202':
          description: Accepted. The track is being processed.
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/Producer'
        '400':
          description: Invalid ISRC format.
        '422':
          description: ISRC not found on external services.

  /tracks/recent:
    get:
      summary: Retrieves the latest registered tracks
      operationId: getRecentTracks
      responses:
        '200':
          description: A list of recent tracks.
          content:
            application/json:
              schema:
                type: array
                items:
                  $ref: '#/components/schemas/Track'

  /artists:
    get:
      summary: Lists all artists
      operationId: listArtists
      responses:
        '200':
          description: A list of all artists.
          content:
            application/json:
              schema:
                type: array
                items:
                  $ref: '#/components/schemas/Artist'

components:
  schemas:
    # All schemas (Producer, Track, Artist, etc.) are defined here.
    # For clarity, the full version is omitted but corresponds
    # to the TypeScript interfaces in the Data Models section.
```

-----
