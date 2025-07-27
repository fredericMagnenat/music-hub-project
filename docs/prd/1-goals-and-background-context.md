# 1. Goals and Background Context

## Goals

* Créer un **produit** pour centraliser et vérifier les catalogues de tracks musicaux pour les producteurs, labels et artistes.
* Fournir une valeur utilisateur immédiate via une interface minimale dans le cadre d'un PoC (Proof of Concept).
* Valider une architecture technique Hexagonale et "Event-Driven" pour assurer la future maintenabilité et scalabilité de la solution.
* Éliminer la gestion manuelle et chronophage des catalogues actuellement effectuée dans des fichiers Excel.

## Background Context

Actuellement, les professionnels de la musique comme les producteurs, les labels et les artistes font face à des défis importants dans la gestion de leurs catalogues. Le processus est souvent manuel, reposant sur des tableurs comme Excel, ce qui est source d'erreurs, de perte de temps et d'un manque de fiabilité des données. Il n'existe pas de lien automatisé entre les métadonnées d'une track (comme son ISRC) et sa présence sur les différentes plateformes de streaming, obligeant à des vérifications manuelles fastidieuses.

Ce projet vise à résoudre ce problème en créant un **produit** centralisé. Le PoC se concentrera sur la fonctionnalité la plus critique identifiée : permettre à un utilisateur d'ajouter une track via son code ISRC, de récupérer automatiquement les métadonnées depuis les services de streaming, et de l'intégrer à son catalogue unifié, apportant ainsi un gain d'efficacité immédiat.

## Change Log

| Date       | Version | Description                                    | Author |
| :--------- | :------ | :--------------------------------------------- | :----- |
| 2025-07-25 | 1.0     | Version finale suite à l'élicitation complète. | PM     |

---
