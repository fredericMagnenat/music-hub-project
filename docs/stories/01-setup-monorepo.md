# Story 1: Mettre en place la structure du Monorepo (Setup-1)

**En tant que** Développeur, **je veux** que la structure complète du répertoire monorepo soit créée et poussée sur Git, **afin que** je dispose d'un emplacement clair et organisé pour tout le code et la documentation du projet.

---

## Status
Done


### Tâches à faire

- [x] Créer le répertoire racine du projet s'il n'existe pas.
- [x] Initialiser un dépôt Git (`git init`).
- [x] Créer la structure de dossiers suivante :
    - `.github/workflows/`
    - `apps/webui/`
    - `apps/` (modules backend: `bootstrap/`, `producer/`, `artist/`, `shared-kernel/`)
    - `packages/shared-types/`
    - `infrastructure/terraform/`
- [x] Créer les fichiers vides suivants à la racine :
    - `.gitignore`
    - `package.json` (si utilisation de pnpm/npm pour le frontend)
    - `pnpm-workspace.yaml` (optionnel)
    - `README.md`
    - `.github/workflows/ci.yaml`
- [x] Ajouter tous les nouveaux fichiers à l'index Git (`git add .`).
- [x] Créer le premier commit (`git commit -m "feat(setup): initialize monorepo structure"`).

---
### Définition de "Terminé" (DoD)

- [x] Toutes les tâches ci-dessus sont cochées.
- [x] Le commit initial est poussé sur la branche principale du dépôt distant. 