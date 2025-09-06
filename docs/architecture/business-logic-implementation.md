# Business Logic Implementation

## Data Consistency and Source of Truth

To resolve data conflicts from multiple sources, the application implements a "Source of Truth" hierarchy (`MANUAL > TIDAL > SPOTIFY...`) as defined in the Domain Charter. This logic is applied within the domain aggregates (`Track`, `Artist`) when new information is processed, ensuring that the entity's state always reflects the data from the most authoritative source available. The authoritative source is determined at the entity level, not the attribute level, meaning the system identifies the single highest-ranking source for a given entity and considers all of its data to be authoritative.

----- 
