# Story 2: Générer le squelette de l'application Backend (Setup-2)

**En tant que** Développeur, **je veux** que le projet multi-module Maven pour le backend Quarkus soit configuré, **afin que** les frontières architecturales (domaine, application, adaptateurs) soient établies et renforcées dès le départ.

---

### Tâches à faire

- [x] Dans le dossier `apps`, créer le `pom.xml` parent.
- [x] Ce `pom.xml` parent doit définir les modules suivants : `bootstrap`, `producer`, `artist`.
- [x] Pour chaque module de contexte (`producer`, `artist`):
    - [x] Créer son `pom.xml` parent (packaging `pom`).
    - [x] Créer les sous-modules : `[context]-domain`, `[context]-application`, `[context]-adapters`.
    - [x] Créer les `pom.xml` pour chaque sous-module.
- [x] Pour chaque module `adapters` (`producer-adapters`, `artist-adapters`):
    - [x] Créer son `pom.xml` parent (packaging `pom`).
    - [x] Créer les sous-modules d'adaptateurs spécifiques (ex: `producer-adapter-rest`, `artist-adapter-persistence`).
    - [x] Créer les `pom.xml` pour chaque adaptateur.
- [x] S'assurer que la structure des répertoires correspond exactement à celle définie dans `docs/architecture/unified-project-structure.md`.
- [x] Le projet doit pouvoir être compilé via `mvn package` depuis `apps` sans erreurs (même s'il ne contient pas encore de code source).

---
### Définition de "Terminé" (DoD)

- [x] La structure de répertoires et de fichiers `pom.xml` est créée dans `apps`.
- [x] La commande `mvn package` s'exécute avec succès depuis `apps`. 