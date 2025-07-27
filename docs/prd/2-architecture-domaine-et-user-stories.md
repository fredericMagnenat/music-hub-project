# 2. Architecture Domaine et User Stories

## Bounded Context 1 : Producer

* **Objectif :** Gérer le cycle de vie des tracks en garantissant qu'elles appartiennent à un "Producer" (identifié par son code unique) valide.
* **Stories Clés :**
    * **Story P1 : Valider et Créer un Producer**
        > **En tant que** Système, **quand** un nouvel ISRC est soumis, **je veux** valider que le "Producer Code" (les 5 premiers caractères) existe, et le créer s'il est inconnu, **afin de** garantir que chaque track a un propriétaire référencé.
    * **Story P2 : Intégrer une Track et Publier un Événement**
        > **En tant que** Producer (utilisateur), **quand** je valide l'ajout d'une track, **je veux** que la track soit ajoutée à l'agrégat du bon "Producer" et qu'un événement `TrackWasRegistered` soit publié, **afin de** sécuriser l'enregistrement de la track et d'informer le reste du système.

## Bounded Context 2 : Artist

* **Objectif :** Gérer les profils des artistes et maintenir une liste de leurs œuvres en réaction aux événements du système.
* **Stories Clés :**
    * **Story A1 : Mettre à Jour un Artiste suite à un Événement**
        > **En tant que** Contexte Artiste, **quand** je reçois un événement `TrackWasRegistered`, **je veux** trouver l'artiste correspondant dans mon catalogue ou le créer s'il est inconnu, puis y lier la track, **afin de** maintenir les profils à jour de manière autonome.
        >
        > **Critères d'Acceptation :**
        > 1.  **Given** un événement `TrackWasRegistered` est reçu pour un artiste qui **existe déjà**, **When** l'événement est traité, **Then** une référence à la nouvelle track est ajoutée à la liste de l'artiste existant.
        > 2.  **Given** un événement `TrackWasRegistered` est reçu pour un artiste qui **n'existe pas**, **When** l'événement est traité, **Then** un nouveau profil d'artiste "provisoire" est créé automatiquement avec les données de l'événement, **And** une référence à la nouvelle track est ajoutée à ce nouvel artiste.
    * **Story A2 : Gérer les Données d'un Artiste**
        > **En tant que** Producer (utilisateur), **je veux** pouvoir consulter et potentiellement mettre à jour les informations d'un artiste (ex: nom de scène, etc.), **afin de** garantir la qualité des données de mon catalogue d'artistes.

---
