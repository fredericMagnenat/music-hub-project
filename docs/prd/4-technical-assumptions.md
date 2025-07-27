# 4. Technical Assumptions

* **Repository Structure** : Monorepo.
* **Service Architecture** : Hexagonale, Domain-Driven Design (DDD), Event-Driven.
* **Testing Requirements** :
    * **Back-end (Quarkus)** : Tests unitaires (JUnit) et d'intégration. Couverture de code mesurée avec Jacoco (objectif 80%).
    * **Front-end (Remix)** : Tests unitaires et de composants avec Vitest et React Testing Library (objectif 80%).
    * **Tests End-to-End** : Hors du périmètre pour le PoC.
* **Additional Technical Assumptions** :
    * **Stack** : Java (Quarkus) pour le back-end, TypeScript (Remix) pour le front-end.
    * **Base de données** : PostgreSQL.
    * **Déploiement** : Conteneurs Docker.
    * **Style d'API** : REST.
    * **Bus d'Événements** : Interne à l'application Quarkus.
    * **Authentification** : Hors du périmètre pour le PoC.

* **Non-Functional Requirements** :
    * **NFR1** : Respect des principes de l'architecture Hexagonale.
    * **NFR2** : Temps de réponse < 5 secondes pour la recherche ISRC.
    * **NFR3** : Couverture de test de 80% (back-end et front-end).
    * **NFR4** : Implémentation du domaine selon les patrons DDD.
    * **NFR5** : Utilisation d'une approche Event-Driven.
    * **NFR6** : Logging structuré (JSON) pour les erreurs back-end.
    * **NFR7** : Exposition d'un point d'API `/health` pour le monitoring.

---
