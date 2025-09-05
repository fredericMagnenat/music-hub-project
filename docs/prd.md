# **Product Requirements Document (PRD) : Catalogue Musical Centralisé**

* **Version :** 1.0
* **Date :** 25 juillet 2025
* **Auteur :** PM (John)

---

## 1. Goals and Background Context

### Goals

* Créer un **produit** pour centraliser et vérifier les catalogues de tracks musicaux pour les producteurs, labels et artistes.
* Fournir une valeur utilisateur immédiate via une interface minimale dans le cadre d'un PoC (Proof of Concept).
* Valider une architecture technique Hexagonale et "Event-Driven" pour assurer la future maintenabilité et scalabilité de la solution.
* Éliminer la gestion manuelle et chronophage des catalogues actuellement effectuée dans des fichiers Excel.

### Background Context

Actuellement, les professionnels de la musique comme les producteurs, les labels et les artistes font face à des défis importants dans la gestion de leurs catalogues. Le processus est souvent manuel, reposant sur des tableurs comme Excel, ce qui est source d'erreurs, de perte de temps et d'un manque de fiabilité des données. Il n'existe pas de lien automatisé entre les métadonnées d'une track (comme son ISRC) et sa présence sur les différentes plateformes de streaming, obligeant à des vérifications manuelles fastidieuses.

Ce projet vise à résoudre ce problème en créant un **produit** centralisé. Le PoC se concentrera sur la fonctionnalité la plus critique identifiée : permettre à un utilisateur d'ajouter une track via son code ISRC, de récupérer automatiquement les métadonnées depuis les services de streaming, et de l'intégrer à son catalogue unifié, apportant ainsi un gain d'efficacité immédiat.

### Change Log

| Date       | Version | Description                                    | Author |
| :--------- | :------ | :--------------------------------------------- | :----- |
| 2025-07-25 | 1.0     | Version finale suite à l'élicitation complète. | PM     |

---

## 2. Architecture Domaine et User Stories

### Bounded Context 1 : Producer

* **Objectif :** Gérer le cycle de vie des tracks en garantissant qu'elles appartiennent à un "Producer" (identifié par son code unique) valide.
* **Stories Clés :**
    * **Story 1-01 : Valider et Créer un Producer**
        > **En tant que** Système, **quand** un nouvel ISRC est soumis, **je veux** valider que le "Producer Code" (les 5 premiers caractères) existe, et le créer s'il est inconnu, **afin de** garantir que chaque track a un propriétaire référencé.
    * **Story 1-02 : Intégrer une Track et Publier un Événement**
        > **En tant que** Producer (utilisateur), **quand** je valide l'ajout d'une track, **je veux** que la track soit ajoutée à l'agrégat du bon "Producer" et qu'un événement `TrackWasRegistered` soit publié, **afin de** sécuriser l'enregistrement de la track et d'informer le reste du système.

### Bounded Context 2 : Artist

* **Objectif :** Gérer les profils des artistes et maintenir une liste de leurs œuvres en réaction aux événements du système.
* **Stories Clés :**
    * **Story 2-01 : Mettre à Jour un Artiste suite à un Événement**
        > **En tant que** Contexte Artiste, **quand** je reçois un événement `TrackWasRegistered`, **je veux** trouver l'artiste correspondant dans mon catalogue ou le créer s'il est inconnu, puis y lier la track, **afin de** maintenir les profils à jour de manière autonome.
        >
        > **Critères d'Acceptation :**
        > 1.  **Given** un événement `TrackWasRegistered` est reçu pour un artiste qui **existe déjà**, **When** l'événement est traité, **Then** une référence à la nouvelle track est ajoutée à la liste de l'artiste existant.
        > 2.  **Given** un événement `TrackWasRegistered` est reçu pour un artiste qui **n'existe pas**, **When** l'événement est traité, **Then** un nouveau profil d'artiste "provisoire" est créé automatiquement avec les données de l'événement, **And** une référence à la nouvelle track est ajoutée à ce nouvel artiste.
    * **Story A2 : Gérer les Données d'un Artiste**
        > **En tant que** Producer (utilisateur), **je veux** pouvoir consulter et potentiellement mettre à jour les informations d'un artiste (ex: nom de scène, etc.), **afin de** garantir la qualité des données de mon catalogue d'artistes.

---

## 3. User Interface Design Goals

* **Overall UX Vision** : L'expérience utilisateur doit être simple, épurée et extrêmement efficace. Le parcours principal de l'utilisateur doit être fluide et sans friction.
* **Key Interaction Paradigms** : Le produit se comportera comme une "Single-Page Application" (SPA) moderne avec un retour visuel immédiat et une validation instantanée des champs.
* **Core Screens and Views** : Tableau de bord principal (liste des tracks), Formulaire d'ajout par ISRC, Écran de prévisualisation, Formulaire de création manuelle.
* **Accessibility** : Viser un niveau de conformité WCAG AA.
* **Branding** : Aucun pour le PoC. Le design sera neutre et fonctionnel.
* **Target Device and Platforms** : Web Responsive (Desktop et tablette).

---

## 4. Technical Assumptions

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

## 5. Checklist Results Report

* **Complétude Générale :** 95%
* **Adéquation du périmètre (MVP/PoC) :** Idéal (Just Right)
* **Prêt pour la phase d'architecture :** Oui
* **Déficiences Critiques :** Aucune.
* **Décision Finale :** ✅ **PRÊT POUR L'ARCHITECTE**

---

## 6. Next Steps

Ce document constitue la source de vérité unique pour le PoC.

1.  **Handoff à l'Architecte :** L'Architecte peut utiliser ce document pour concevoir la solution technique détaillée.
2.  **Handoff au Designer UX/UI :** Le Designer peut utiliser les "User Interface Design Goals" et les User Stories pour créer des wireframes et des maquettes.