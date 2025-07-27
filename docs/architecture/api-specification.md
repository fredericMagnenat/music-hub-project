# API Specification

Based on the REST style, this OpenAPI 3.0 specification defines the core endpoints.

```yaml
openapi: 3.0.0
info:
  title: "Music Data Hub API"
  version: "1.0.0"
  description: "API for managing the music catalog."
servers:
  - url: "/api/v1"

paths:
  /producers:
    post:
      summary: "Register a new track"
      description: "Submits an ISRC. The system finds or creates the corresponding Producer, validates the ISRC, and adds the track to the producer's catalog."
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
          description: "Accepted. The track registration is in progress."
          content:
            application/json:
              schema:
                $ref: "#/components/schemas/Producer"
        '400':
          description: "Invalid ISRC format."
        '422':
          description: "Unprocessable ISRC (e.g., valid but not found)."

  /producers/{producerId}:
    get:
      summary: "Get a Producer by ID"
      parameters:
        - in: path
          name: producerId
          required: true
          schema:
            type: string
            format: uuid
      responses:
        '200':
          description: "A Producer object."
          content:
            application/json:
              schema:
                $ref: "#/components/schemas/Producer"
        '404':
          description: "Producer not found."

  /artists:
    get:
      summary: "List all Artists"
      responses:
        '200':
          description: "A list of artists."
          content:
            application/json:
              schema:
                type: array
                items:
                  $ref: "#/components/schemas/Artist"

components:
  schemas:
    Producer:
      type: object
      properties:
        id:
          type: string
          format: uuid
        producerCode:
          type: string
        name:
          type: string
        tracks:
          type: array
          items:
            $ref: "#/components/schemas/Track"

    Track:
      type: object
      properties:
        isrc:
          type: string
        title:
          type: string
        artistNames:
          type: array
          items:
            type: string
        sources:
          type: array
          items:
            $ref: "#/components/schemas/Source"
        status:
          type: string
          enum: [Provisional, Verified]

    Source:
      type: object
      properties:
        sourceName:
          type: string
          enum: [Spotify, Tidal, Deezer, Apple Music, Manual]
        sourceId:
          type: string

    Artist:
      type: object
      properties:
        id:
          type: string
          format: uuid
        name:
          type: string
        status:
          type: string
          enum: [Provisional, Verified]
        sources:
          type: array
          items:
            $ref: "#/components/schemas/Source"
        trackIds:
          type: array
          items:
            type: string

```
