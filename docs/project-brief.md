Project Brief : Outil de Centralisation de Catalogue Musical
Date : 26 juillet 2025

1. Titre du Projet
Outil de Centralisation et de Vérification des Catalogues de Tracks Musicaux.

2. Contexte et Justification
Actuellement, la gestion des catalogues musicaux par les producteurs, labels et artistes est une tâche principalement manuelle, chronophage et sujette aux erreurs, souvent réalisée via des fichiers Excel. Il existe un besoin clair d'automatiser et de centraliser ce processus pour améliorer l'efficacité et la fiabilité des données. Ce projet vise à développer un PoC (Proof of Concept) pour un outil répondant à cette problématique.

3. Problème à Résoudre
Les frustrations majeures identifiées avec les méthodes actuelles sont :

La gestion manuelle et chronophage des données.

L'absence de liens automatisés entre les différentes informations (tracks, artistes, etc.).

La nécessité de consulter manuellement chaque plateforme de streaming pour vérifier les métadonnées.

La difficulté à effectuer des recherches efficaces dans de grands volumes de données.

La gestion des droits qui s'effectue sur des systèmes séparés et non intégrés.

4. Objectifs du Projet (PoC)
Les objectifs principaux pour cette première phase (PoC) sont :

Valider une architecture technique : Mettre en place et tester une architecture moderne (Hexagonale, Event-Driven) pour assurer la scalabilité future de l'outil.

Fournir une valeur utilisateur immédiate : Développer une fonctionnalité clé qui résout le problème le plus urgent pour les utilisateurs, accessible via une interface minimale.

Priorité Fonctionnelle : La fonctionnalité prioritaire est "l'Ajout Automatisé" d'une track via son code ISRC.

5. Périmètre du PoC (Proof of Concept)
Fonctionnalités Incluses :
Le PoC se concentrera exclusivement sur le scénario utilisateur de l'Ajout Automatisé :

Action : L'utilisateur (producteur, label, etc.) saisit un code ISRC valide.

Prévisualisation : Le système recherche et affiche les métadonnées trouvées (Titre, Artiste(s)) et la liste des plateformes où le morceau est disponible.

Validation : L'utilisateur confirme que les informations récupérées sont correctes.

Intégration : Le système enregistre la track et ses métadonnées dans une base de données centralisée, en incluant les identifiants uniques de chaque plateforme.

Confirmation : L'utilisateur peut voir la track nouvellement ajoutée dans son catalogue centralisé.

Fonctionnalités Exclues (pour le moment) :
Les fonctionnalités suivantes, bien qu'identifiées comme désirables, sont hors du périmètre du PoC :

Mise en place de règles complexes pour la qualité des données.

Mise à jour des données basée sur une hiérarchie de sources fiables.

Notification automatique aux plateformes en cas d'erreurs dans leurs données.

Liaison des tracks à un catalogue d'artistes unifié.

6. Public Cible
Producteurs de musique

Labels de musique

Artistes

7. Risques et Dépendances
Dépendance Technique : La faisabilité du projet repose sur la capacité à interroger les API des services de streaming (Spotify, Apple Music, Tidal, etc.) pour récupérer les métadonnées des tracks. Une investigation technique est nécessaire pour valider cet accès.

8. Prochaines Étapes
Formaliser la User Story : Rédiger une "User Story" détaillée pour l'équipe de développement basée sur le scénario d'Ajout Automatisé.

Créer des Wireframes : Concevoir des maquettes simples de l'interface utilisateur pour le scénario du PoC.

Investigation Technique : Lister et analyser les API des services de streaming pour confirmer la faisabilité de la récupération de données.