# Naming Conventions

| Element | Frontend (Remix/React) | Backend (Java/Quarkus) | Example |
| :--- | :--- | :--- | :--- |
| UI Components | `PascalCase` | N/A | `TrackCard.tsx` |
| Route Files | `kebab-case.tsx` | N/A | `producers.$id.tsx` |
| Service Classes | N/A | `PascalCase` | `ProducerService.java` |
| REST Endpoints | N/A | `kebab-case` | `/api/v1/producers` |
| DB Tables | `snake_case` | `snake_case` | `producers`, `tracks` |
| DB Columns | `snake_case` | N/A | `producer_code`, `artist_names` | 