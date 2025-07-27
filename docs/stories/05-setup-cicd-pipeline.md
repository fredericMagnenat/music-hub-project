# Story 5: Implémenter le pipeline CI/CD de base (Setup-5)

**En tant que** Développeur, **je veux** qu'un pipeline CI/CD de base soit configuré dans GitHub Actions qui compile, linterise, et teste le code à chaque push, **afin que** la qualité du code et la santé de l'intégration soient assurées en continu.

---

### Tâches à faire

- [ ] Dans le fichier `.github/workflows/ci.yaml`, définir un workflow qui se déclenche sur les `push` et `pull_request` vers la branche principale.
- [ ] Définir un job `build-and-test` qui s'exécute sur `ubuntu-latest`.
- [ ] Ajouter les étapes (steps) suivantes au job :
    - [ ] `actions/checkout@v3` pour récupérer le code.
    - [ ] `actions/setup-java@v3` pour configurer Java 21.
    - [ ] `pnpm/action-setup@v2` pour configurer PNPM.
    - [ ] Installer les dépendances root : `pnpm install`.
    - [ ] Construire le frontend : `pnpm --filter frontend build`.
    - [ ] Tester le frontend : `pnpm --filter frontend test`.
    - [ ] Construire et tester le backend : `mvn -B package --file apps/backend/pom.xml`.
- [ ] Pousser le fichier `ci.yaml` sur le dépôt.
- [ ] Vérifier dans l'onglet "Actions" de GitHub que le pipeline s'exécute et réussit.

---
### Définition de "Terminé" (DoD)

- [ ] Le fichier `ci.yaml` est présent et correctement configuré.
- [ ] Le pipeline se lance automatiquement sur un push.
- [ ] Toutes les étapes du pipeline (build et test pour le front et le back) se terminent avec succès. 