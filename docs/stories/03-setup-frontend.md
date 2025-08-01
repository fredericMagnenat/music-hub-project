# Story 3: Initialiser l'application Frontend (Setup-3)

**En tant que** Développeur, **je veux** que l'application frontend Remix soit initialisée et configurée dans le monorepo, **afin que** je puisse commencer à intégrer des composants UI.

---

### Tâches à faire

- [x] S'assurer que PNPM est installé globalement.
- [x] À la racine du monorepo, configurer `pnpm-workspace.yaml` pour qu'il reconnaisse l'application frontend :
  ```yaml
  packages:
    - 'apps/*'
    - 'packages/*'
  ```
- [x] Créer un `package.json` de base dans `apps/frontend` (s'il n'a pas été créé par l'outil d'initialisation).
- [x] Initialiser le projet Remix dans `apps/frontend` avec support TypeScript. Utiliser `pnpm create remix@latest`.
- [x] Installer les dépendances de test : `pnpm --filter frontend add -D vitest @testing-library/react`.
- [x] Initialiser `shadcn/ui` dans le projet frontend.
- [x] Vérifier que la commande `pnpm --filter frontend dev` lance le serveur de développement Remix sans erreur.
- [x] Vérifier que la commande `pnpm --filter frontend build` compile l'application sans erreur.

---
### Définition de "Terminé" (DoD)

- [x] Le projet Remix est fonctionnel dans `apps/frontend`.
- [x] Les dépendances sont installées et listées dans le `package.json` de l'application.
- [x] Les commandes de développement et de build fonctionnent.