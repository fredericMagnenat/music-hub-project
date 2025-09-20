Sujet: Demande de création de Story Technique : Refactoring des modules partagés

Destinataire: Bob (Scrum Master)

---

Bonjour Bob,

Pour faire suite à nos discussions sur l'amélioration de l'architecture, nous avons validé la nécessité d'un refactoring important concernant notre code technique partagé.

Pourrions-nous créer une story technique pour tracer cette modification ?

**Titre suggéré pour la story :** `Refactor: Créer le module shared-technical et déplacer les utilitaires partagés`

**Objectif / Valeur :**
L'objectif est d'améliorer la clarté de l'architecture et la maintenabilité à long terme en séparant clairement les concepts de domaine partagés (`shared-kernel`) des utilitaires techniques transverses. Cela prévient la "pollution" de notre cœur de métier et renforce la discipline de notre design.

**Critères d'acceptation / Tâches à réaliser :**

1.  **Créer le module parent `apps/shared-technical`** (packaging `pom`).
2.  **Créer le sous-module `apps/shared-technical/shared-util`**.
3.  **Déplacer** la classe `CorrelationIdGenerator` de `shared-kernel` vers `shared-util`.
4.  **Créer le sous-module `apps/shared-technical/shared-adapter-spi`**.
5.  **Déplacer** le client `TidalAuthClient` de `producer-adapter-spi` vers `shared-adapter-spi`.
6.  **Mettre à jour les `pom.xml`** des modules `producer` et `artist` (et autres si nécessaire) pour qu'ils dépendent des nouveaux modules de `shared-technical` au lieu des anciens emplacements.
7.  **Valider** que le projet compile et que tous les tests d'intégration passent après le refactoring.

Merci de l'ajouter au backlog.

Cordialement,

Winston (Architecte)



claude mcp add context7 -- npx -s project -y @upstash/context7-mcp --api-key ctx7sk-336dafdb-e821-4f09-a31b-3486a5ed25f9


✅ QA Gate: CRÉÉ & APPROUVÉ
⏳ Technical Review: En attente (Architecte)
⏳ Business Sign-off: En attente (Bob - Scrum Master)

Le QA Gate est maintenant disponible et la story peut officiellement passer en review technique !

Souhaitez-vous que je committe les changements pour finaliser l'implémentation ?

---

**Sujet :** Demande de création d'une story pour correction des tests ArtistEnrichmentService et ArtistService

**Destinataire :** Bob (Scrum Master)

---

Bonjour Bob,

J'espère que ce mail te trouve bien. Je rencontre actuellement des problèmes avec les tests unitaires dans le module `artist-application` qui nécessitent une intervention structurée.

## Contexte
Suite à la résolution d'un bug CDI critique (injection de `List<ArtistReconciliationPort>`), j'ai lancé les tests pour valider que les corrections n'introduisaient pas de régressions. Malheureusement, 17 tests sur 18 échouent actuellement.

## Problèmes identifiés

### Tests ArtistEnrichmentService (11 échecs)
- **Fusion des données d'artiste** : Les artistes restent en statut `PROVISIONAL` au lieu de passer à `VERIFIED` après enrichissement
- **Logique de recherche** : La méthode `findArtistByName` n'est pas appelée sur les ports appropriés selon la hiérarchie des sources
- **Gestion d'erreurs** : Les exceptions ne sont pas correctement propagées dans les tests d'erreur
- **Hiérarchie des sources** : La priorité MANUAL > TIDAL > SPOTIFY > DEEZER > APPLE_MUSIC n'est pas respectée

### Tests ArtistService (6 échecs)
- **Gestion des espaces** : Les espaces dans les noms d'artistes ne sont pas préservés comme attendu
- **Contributions de collaboration** : La logique de détermination des contributions multiples d'artistes est incorrecte
- **Stubbings Mockito** : Plusieurs stubbings inutiles causent des erreurs `UnnecessaryStubbingException`

## Impact
- Les tests ne passent pas, ce qui bloque la validation des fonctionnalités
- Risque de régressions non détectées lors des prochains développements
- L'enrichissement des artistes depuis les APIs externes (TIDAL, SPOTIFY, etc.) ne fonctionne pas correctement

## Demande
Pourrais-tu créer une story dans le backlog avec les informations suivantes :

**Titre :** Corriger les tests unitaires ArtistEnrichmentService et ArtistService

**Description :**
```
En tant que développeur,
Je veux que tous les tests unitaires des services Artist passent,
Afin de garantir la qualité et la fiabilité du code d'enrichissement des artistes.

Critères d'acceptation :
- Tous les tests ArtistEnrichmentServiceTest passent (18 tests)
- Tous les tests ArtistServiceTest passent (7 tests)
- La logique d'enrichissement des artistes fonctionne correctement
- La hiérarchie des sources externes est respectée
- Les contributions d'artistes sont correctement gérées
```

**Tâches estimées :**
- Analyse et correction de la logique de fusion des données d'artiste (2h)
- Correction de la logique de recherche dans les sources externes (2h)
- Correction de la gestion des espaces et contributions (1h)
- Nettoyage des stubbings Mockito et tests d'erreur (1h)
- Tests de régression complets (1h)

**Priorité :** Haute (bloque la validation des fonctionnalités d'enrichissement)

**Dependencies :** Aucune

Merci d'avance pour ta prise en charge rapide de cette demande. N'hésite pas si tu as besoin de plus de détails techniques.

Cordialement,

James (Développeur Full Stack)
