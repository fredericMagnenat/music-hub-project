# Music Hub Monorepo

Monorepo pour le projet Music Hub - Une plateforme de streaming musical moderne.

## Structure du projet

```
.
├── apps/
│   ├── frontend/     # Application web frontend (React)
│   └── backend/      # API backend (Node.js)
├── packages/
│   └── shared-types/ # Types TypeScript partagés
└── infrastructure/
    └── terraform/    # Configuration infrastructure
```

## Prérequis

- Node.js >= 18.0.0
- pnpm >= 8.0.0

## Installation

```bash
pnpm install
```

## Scripts disponibles

- `pnpm dev` - Démarre tous les services en mode développement
- `pnpm build` - Build tous les packages
- `pnpm test` - Lance tous les tests
- `pnpm lint` - Lance le linting sur tous les packages
- `pnpm clean` - Nettoie tous les builds

## Développement

Chaque application/package dispose de ses propres scripts dans son `package.json`.

Pour travailler sur une application spécifique :
```bash
cd apps/frontend
pnpm dev
``` 