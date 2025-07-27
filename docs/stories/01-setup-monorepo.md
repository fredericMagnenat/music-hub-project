# Story 1: Mettre en place la structure du Monorepo (Setup-1)

**En tant que** Développeur, **je veux** que la structure complète du répertoire monorepo soit créée et poussée sur Git, **afin que** je dispose d'un emplacement clair et organisé pour tout le code et la documentation du projet.

---

### Tâches à faire

- [ ] Créer le répertoire racine du projet s'il n'existe pas.
- [ ] Initialiser un dépôt Git (`git init`).
- [ ] Créer la structure de dossiers suivante :
    - `.github/workflows/`
    - `apps/frontend/`
    - `apps/backend/`
    - `packages/shared-types/`
    - `infrastructure/terraform/`
- [ ] Créer les fichiers vides suivants à la racine :
    - `.gitignore`
    - `package.json`
    - `pnpm-workspace.yaml`
    - `README.md`
    - `.github/workflows/ci.yaml`
- [ ] Ajouter tous les nouveaux fichiers à l'index Git (`git add .`).
- [ ] Créer le premier commit (`git commit -m "feat(setup): initialize monorepo structure"`).

---
### Définition de "Terminé" (DoD)

- [ ] Toutes les tâches ci-dessus sont cochées.
- [ ] Le commit initial est poussé sur la branche principale du dépôt distant. 