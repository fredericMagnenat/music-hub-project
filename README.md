# Music Hub Project

Monorepo du projet Music Hub. Backend en Quarkus (Java), frontend en Remix (TypeScript), avec un flux unifié via Quinoa.

## Structure

```text
.
├── apps/
│   ├── webui/           # Application Remix (UI)
│   ├── bootstrap/       # Module Quarkus exécutable (REST, wiring)
│   ├── producer/        # Contexte métier "producer" (multi-modules)
│   ├── artist/          # Contexte métier "artist" (multi-modules)
│   └── shared-kernel/   # Valeurs et événements partagés (Java)
├── docs/                # Architecture, PRD, stories, specs
├── docker-compose.yml   # Services locaux (ex: PostgreSQL)
└── README.md
```

## Prérequis

- Java 21, Maven 3.9+
- Node.js 18+ (pour `apps/webui`)
- Docker (optionnel, pour Postgres local)

## Démarrage rapide

1) Base de données (optionnel)
```bash
docker compose up -d postgres
```

2) Dev unifié (backend + frontend via Quinoa)
```bash
cd apps/
mvn quarkus:dev
```
- API: http://localhost:8080
- UI: servie par Quarkus (Quinoa démarre le dev server Remix et proxy automatiquement)

3) Alternative: lancer le frontend seul (avec proxy vers backend)
```bash
cd apps/webui
npm install
npm run dev  # Le proxy Vite redirige /api vers http://localhost:8080
```
**Note:** Assurez-vous que le backend tourne sur le port 8080 avant de démarrer le frontend.

## Build

- Backend (agrégé) :
```bash
mvn -f apps/pom.xml package
```

- Frontend :
```bash
cd apps/webui && npm run build
```
(Quinoa déclenche le build UI lors du build Quarkus.)

## Tests

- Backend :
```bash
mvn -f apps/pom.xml test
mvn -f apps/pom.xml -Ptest-coverage verify
```

- Frontend :
```bash
cd apps/webui && npm test
```

## Documentation

- Architecture: `docs/architecture/index.md`
- Diagrammes, modèles de données, choix techniques: voir `docs/architecture/`
