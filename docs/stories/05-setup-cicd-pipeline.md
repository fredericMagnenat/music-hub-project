# Story 5: Implémenter le pipeline CI/CD de base (Setup-5)

**En tant que** Développeur, **je veux** qu'un pipeline CI/CD de base soit configuré dans GitHub Actions qui compile, linterise, et teste le code à chaque push, **afin que** la qualité du code et la santé de l'intégration soient assurées en continu.

---

### Tâches à faire

- [x] Dans le fichier `.github/workflows/ci.yaml`, définir un workflow qui se déclenche sur les `push` et `pull_request` vers la branche principale.
- [x] Définir un job `build-and-test` qui s'exécute sur `ubuntu-latest`.
- [x] Ajouter les étapes (steps) suivantes au job :
    - [x] `actions/checkout@v3` pour récupérer le code.
    - [x] `actions/setup-java@v3` pour configurer Java 21.
    - [x] (Optionnel) `pnpm/action-setup@v2` pour configurer PNPM si le frontend l'utilise.
    - [x] (Optionnel) Installer les dépendances root : `pnpm install` (si PNPM est utilisé).
    - [x] Construire le frontend : `pnpm --filter webui build` (si PNPM/Remix est utilisé).
    - [x] Tester le frontend : `pnpm --filter webui test` (si PNPM/Remix est utilisé).
    - [x] Construire et tester le backend : `mvn -B -f apps/pom.xml package`.
- [x] Pousser le fichier `ci.yaml` sur le dépôt.
- [x] Vérifier dans l'onglet "Actions" de GitHub que le pipeline s'exécute et réussit.

---
### Définition de "Terminé" (DoD)

- [x] Le fichier `ci.yaml` est présent et correctement configuré.
- [x] Le pipeline se lance automatiquement sur un push.
- [x] Toutes les étapes du pipeline (build et test pour le front et le back) se terminent avec succès. 