
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

5.  **Given** a Producer already exists and the submitted ISRC is **already registered** under that Producer
    **When** the `POST /producers` endpoint is called with the same ISRC
    **Then** the system returns `202 Accepted`
    **And** no duplicate track is created (idempotent behavior).

6.  **Given** a valid ISRC is submitted
    **When** the `POST /producers` endpoint is called
    **Then** the system processes registration **asynchronously** and returns `202 Accepted` with the current Producer representation (state at request time).

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
    *   (Optionnel pour P1) Définir le contrat d'évènement `TrackWasRegistered`.

3.  **Module `producer-domain`:**
    *   Créer l'agrégat `Producer` et le port `ProducerRepository`.
    *   Utiliser `ProducerCode` et `ISRC` du `shared-kernel`.
    *   Définir `ProducerId` dans ce module (UUIDv5 dérivé du `ProducerCode`) et l'utiliser pour garantir l'idempotence.

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
    *   Instrumenter des compteurs/metrics minimales (ex: `isrc_invalid_400`, `isrc_unresolvable_422`).

### Frontend Technical Tasks (`apps/webui`)

1.  **Page/Route:**
    *   Créer une page (ex: `/`) contenant un formulaire simple avec un champ ISRC et un bouton "Add".
    *   The "Add" button should be disabled while the API request is in progress.

2.  **API Service:**
    *   Dans `apps/webui/app/services/`, créer `producer.service.ts`.
    *   Implémenter `registerTrack(isrc: string)` qui appelle `POST /api/v1/producers`, en important `RegisterTrackRequest` et les types de réponse depuis `@repo/shared-types`.
    *   Gérer les réponses de succès (`202` avec `Producer` tel que connu) et d'erreur (`400`, `422`).
    *   Distinguer les messages utilisateur pour `400` (format invalide) vs `422` (non résolvable amont).

3.  **Components (`app/components/`):**
    *   Create an `ISRCInputField` component using `shadcn/ui` with client-side validation for the ISRC format.
    *   Create a `SubmitButton` component that can display a loading state (spinner).
    *   Use a `Toast` component (`shadcn/ui`) to display success or error messages to the user after the API call.
    *   Appliquer la même normalisation que backend (trim, uppercase, suppression des tirets) avant validation côté client.

### Règles métier et techniques

- **Normalisation ISRC**: trim, suppression des tirets, mise en majuscules avant validation.
- **Extraction `ProducerCode`**: les 5 premiers caractères de l'ISRC normalisé.
- **Idempotence**: ré-appeler avec le même ISRC ne crée aucun doublon de track.
- **Format d'erreur standard** (JSON): `{ "error": string, "message": string, "details"?: object }`.
- **Sémantique `202`**: traitement asynchrone; la réponse renvoie l'état courant du `Producer` au moment de la requête.

### Exemples d'API

- **Request**
```json
{ "isrc": "FR-LA1-24-00001" }
```

- **202 Accepted (succès, état courant)**
```json
{ "id": "0f5b3cae-...", "producerCode": "FRLA1", "name": null, "tracks": [] }
```

- **400 Bad Request (format invalide)**
```json
{ "error": "InvalidISRCFormat", "message": "ISRC 'xxx' is invalid" }
```

- **422 Unprocessable Entity (valide mais non résolvable)**
```json
{ "error": "UnresolvableISRC", "message": "ISRC not found upstream" }
```

### Tests to Plan

*   **Backend:**
    *   Unit tests pour les Value Objects dans `shared-kernel` (`ISRC`, `ProducerCode`).
    *   Unit tests pour `ProducerId` dans `producer-domain` (génération v5, égalité, sérialisation si applicable).
    *   Unit tests for the `Producer` aggregate.
    *   Integration tests (`@QuarkusTest`) pour `ProducerController` couvrant: succès (nouveau/existant), idempotence (pas de doublon), normalisation d'entrée (tirets/minuscules), mapping d'exceptions vers `400/422` avec payload d'erreur attendu.
*   **Frontend:**
    *   Unit tests for the form and components with Vitest and RTL, including testing the disabled/loading state of the button.
    *   Tests for the API service to mock and verify both success and error handling, avec messages distincts pour `400` et `422`. 