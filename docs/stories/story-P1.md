
### User Story: P1 - Validate and Create a Producer

> **As a** System, **when** a new ISRC is submitted, **I want** to validate that the "Producer Code" (the first 5 characters) exists, and create it if it's unknown, **in order to** ensure that each track has a referenced owner.

### Acceptance Criteria

1.  **Given** an ISRC for a **new** Producer is submitted
    **When** the `POST /producers` endpoint is called
    **Then** a new `Producer` record is created in the database with the correct `producerCode`
    **And** the system returns a `202 Accepted` status with the newly created Producer data.

2.  **Given** an ISRC for an **existing** Producer is submitted
    **When** the `POST /producers` endpoint is called
    **Then** no new `Producer` record is created
    **And** the system returns a `202 Accepted` status with the existing Producer data.

3.  **Given** an ISRC with an **invalid format** is submitted
    **When** the `POST /producers` endpoint is called
    **Then** the system returns a `400 Bad Request` error.

4.  **Given** an ISRC with a **valid format** but **unresolvable** (e.g., not found upstream) is submitted
    **When** the `POST /producers` endpoint is called
    **Then** the system returns a `422 Unprocessable Entity` error.

### Backend Technical Tasks (aligned with unified structure)

1.  **Module `packages/shared-types` (TypeScript):**
    *   Create `RegisterTrackRequest.ts` in `packages/shared-types/src/` defining the DTO shared entre frontend et backend.
      ```typescript
      export interface RegisterTrackRequest {
        isrc: string;
      }
      ```
    *   Exposer également les interfaces `Producer`, `Track`, `Source` conformément aux modèles de `docs/architecture/data-models.md` pour typer les réponses de l'API côté frontend.

2.  **Module `apps/shared-kernel` (Java):**
    *   Définir/centraliser les Value Objects partagés: `ISRC`, `ProducerCode` (tous deux déjà présents dans `com.musichub.shared.domain.values`).
    *   Définir `ProducerId` (UUID basé sur v5 à partir de `ProducerCode`) si ce n'est pas déjà fait.
    *   (Optionnel pour P1) Définir le contrat d'évènement `TrackWasRegistered`.

3.  **Module `producer-domain`:**
    *   Créer l'agrégat `Producer` et le port `ProducerRepository`.
    *   Utiliser `ProducerCode` et `ISRC` du `shared-kernel`.
    *   Implémenter l'usage de `ProducerId` (UUIDv5 dérivé du `ProducerCode`) pour l'idempotence.

4.  **Module `producer-application`:**
    *   Créer le service applicatif `ProducerService`.
    *   Ajouter la méthode `handle(RegisterTrackRequest request)` orchestrant: extraction du `producerCode`, recherche/création du `Producer`, ajout du track si nécessaire.

5.  **Module `producer-adapter-persistence`:**
    *   Implement `ProducerRepositoryImpl` using JPA/Panache.
    *   Define the `ProducerEntity` JPA entity and its mapping to the domain model.

6.  **Module `producer-adapter-rest`:**
    *   Créer le contrôleur REST `ProducerController` avec un endpoint `POST /producers`.
    *   Consommer le DTO `RegisterTrackRequest`.
    *   Mapper les exceptions domaine: `InvalidISRCFormat` → `400`, `UnresolvableISRC` (ou équivalent) → `422`.
    *   Respecter la base path `/api/v1` (via `quarkus.http.root-path=/api/v1` ou équivalent).

7.  **Module `apps/bootstrap`:**
    *   Assembler les modules (`shared-kernel`, `producer-*`) et exposer les ressources REST.
    *   Configurer la base path `/api/v1` si non gérée par les annotations de ressource.

### Frontend Technical Tasks (`apps/webui`)

1.  **Page/Route:**
    *   Créer une page (ex: `/`) contenant un formulaire simple avec un champ ISRC et un bouton "Add".
    *   The "Add" button should be disabled while the API request is in progress.

2.  **API Service:**
    *   Dans `apps/webui/app/services/`, créer `producer.service.ts`.
    *   Implémenter `registerTrack(isrc: string)` qui appelle `POST /api/v1/producers`, en important `RegisterTrackRequest` et les types de réponse depuis `@repo/shared-types`.
    *   Gérer les réponses de succès (`202` avec `Producer`) et d'erreur (`400`, `422`).

3.  **Components (`app/components/`):**
    *   Create an `ISRCInputField` component using `shadcn/ui` with client-side validation for the ISRC format.
    *   Create a `SubmitButton` component that can display a loading state (spinner).
    *   Use a `Toast` component (`shadcn/ui`) to display success or error messages to the user after the API call.

### Tests to Plan

*   **Backend:**
    *   Unit tests pour les Value Objects dans `shared-kernel` (`ISRC`, `ProducerCode`, et `ProducerId` si présent).
    *   Unit tests for the `Producer` aggregate.
    *   Integration tests (`@QuarkusTest`) pour `ProducerController` couvrant: succès (nouveau/existant), `400` (format invalide), `422` (ISRC valide mais non résolvable).
*   **Frontend:**
    *   Unit tests for the form and components with Vitest and RTL, including testing the disabled/loading state of the button.
    *   Tests for the API service to mock and verify both success and error handling. 