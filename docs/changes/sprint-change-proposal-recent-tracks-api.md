# Sprint Change Proposal - Recent Tracks API Endpoint

**Document ID:** SCP-001  
**Date:** 2025-08-22  
**Status:** En Attente d'Approbation  
**Priorité:** HAUTE  
**Impact MVP:** Minimal  

---

## 1. Résumé Exécutif

### Problème Identifié
Le frontend `recentTracks` dans la Story 9 (Dashboard - Recent Tracks) retourne des données statiques via `localStorage` au lieu de données dynamiques du backend. Cette situation crée une déconnexion entre les sessions utilisateur et compromet l'expérience utilisateur en production.

### Solution Proposée
Création d'une Story P3 technique dans l'Epic 1 pour implémenter l'endpoint API backend manquant `GET /api/v1/tracks/recent` et mise à jour du frontend pour utiliser cette API au lieu du localStorage.

### Impact Estimé
- **Effort:** 1-2 jours de développement
- **Risque:** Faible (extension naturelle de l'architecture existante)
- **Bénéfice:** Données synchronisées entre utilisateurs/sessions

---

## 2. Contexte et Déclencheur

### Découverte de l'Issue
Après livraison et approbation QA de la Story 9, l'équipe a réalisé que l'endpoint API backend pour récupérer les tracks récentes n'existe pas, bien que la fonctionnalité frontend soit complètement opérationnelle.

### Impact Immédiat
- Application en production avec données non synchronisées entre utilisateurs/sessions
- Expérience utilisateur dégradée (données locales uniquement)
- Nécessite une correction rapide pour maintenir la cohérence des données

### État Actuel
- ✅ Story 9 frontend complète et approuvée QA
- ❌ Endpoint backend `GET /api/v1/tracks/recent` manquant
- ⚠️ Données temporaires localStorage en production

---

## 3. Analyse d'Impact sur les Épiques

### Epic 3 - UI/UX Foundation & Dashboard
- **Status:** ✅ COMPLÉTÉE mais avec gap technique identifié
- **Changements requis:** AUCUN - L'épique reste complétée
- **Notes:** Story 9 fonctionnelle avec données temporaires (comportement attendu en développement)

### Epic 1 - Core Producer Management  
- **Status:** ✅ COMPLÉTÉE mais EXTENSION NÉCESSAIRE
- **Changements requis:** AJOUT d'une nouvelle Story P3
- **Impact:** Extension mineure de l'épique existante

### Autres Épiques
- **Epic 2 (Artist Context):** AUCUN IMPACT
- **Futures épiques:** AUCUN IMPACT

---

## 4. Ajustements d'Artefacts Nécessaires

### 4.1 API Specification (`docs/architecture/api-specification.md`)

**AJOUT REQUIS:** Nouveau endpoint pour tracks récentes

```yaml
/tracks/recent:
  get:
    summary: "Get recent tracks across all producers"
    description: "Returns the most recent 10 tracks submitted/validated, ordered by submission date."
    responses:
      '200':
        description: "List of recent tracks."
        content:
          application/json:
            schema:
              type: array
              items:
                $ref: "#/components/schemas/Track"
```

### 4.2 Epic 1 (`docs/epics/epic-1-core-producer-management.md`)

**AJOUT REQUIS:** Nouvelle story technique

```markdown
3. **Story P3:** Recent Tracks API Endpoint - Endpoint pour dashboard tracks récentes
   - Implémentation de `GET /api/v1/tracks/recent`
   - Service de récupération des 10 tracks les plus récentes
   - Tri par date de soumission décroissante
   - Intégration avec RecentTracksList frontend existant
```

### 4.3 Architecture Document (`docs/architecture.md`)

**MISE À JOUR REQUISE:** Documentation du nouvel endpoint dans la section API

---

## 5. Solution Détaillée

### 5.1 Approche Recommandée
**Ajustement Direct via Story Technique Supplémentaire**

### 5.2 Implémentation Backend

#### Nouvelle méthode de service
- Création dans `ProducerService` ou `TrackService`
- Récupération des 10 tracks les plus récentes tous producteurs confondus
- Tri par date de soumission décroissante

#### Endpoint REST
- Ajout dans le contrôleur existant
- Route: `GET /api/v1/tracks/recent`
- Réponse: Array de Track objects

### 5.3 Intégration Frontend

#### Modifications RecentTracksList
- Remplacement du localStorage par appel API
- Maintien de l'interface utilisateur existante (aucun changement visuel)
- Ajout de la gestion d'erreurs et des états de chargement

### 5.4 Tests Requis

#### Backend
- Tests unitaires pour le nouvel endpoint
- Validation du tri et de la limite de 10 tracks

#### Frontend  
- Tests d'intégration avec API réelle
- Tests de la migration localStorage → API

#### Intégration
- Validation de la cohérence entre soumission ISRC et affichage recent tracks

---

## 6. Impact sur le MVP

### Scope MVP
✅ **AUCUN CHANGEMENT** - La fonctionnalité est déjà livrée, il s'agit uniquement d'une correction technique

### Goals MVP  
✅ **MAINTENUS** - Le dashboard avec recent tracks reste l'objectif principal

### Timeline MVP
⚡ **IMPACT MINIMAL** - Story technique rapide (1-2 jours maximum)

---

## 7. Plan d'Action Détaillé

### Actions Immédiates (Priorité 1)

1. **Créer Story P3** avec acceptance criteria spécifiques
2. **Assigner à l'équipe dev** pour implémentation prioritaire  
3. **Mettre à jour API specification** avec le nouvel endpoint
4. **Modifier RecentTracksList** pour utiliser l'API au lieu du localStorage

### Critères de Réussite

#### Fonctionnel
- Données synchronisées entre utilisateurs/sessions
- Affichage des 10 tracks les plus récentes

#### Technique  
- Temps de réponse < 1s pour l'endpoint recent tracks
- Tests backend et frontend passants

#### UX
- Aucune régression visuelle
- Amélioration de l'expérience utilisateur (données cohérentes)

### Tests de Validation

#### Backend
- Endpoint retourne max 10 tracks récentes ordonnées
- Gestion correcte des cas vides/erreurs

#### Frontend
- RecentTracksList affiche données API au lieu localStorage
- États de chargement et gestion d'erreurs

#### Intégration
- Données cohérentes entre soumission ISRC et affichage recent tracks

---

## 8. Gestion des Risques

### Risques Identifiés

| Risque | Probabilité | Impact | Mitigation |
|--------|-------------|---------|------------|
| Performance dégradée | Faible | Moyen | Indexation base de données, cache |
| Régression frontend | Faible | Élevé | Tests complets avant déploiement |
| Retard de livraison | Faible | Faible | Story simple, architecture existante |

### Plan de Rollback
En cas de problème critique, possibilité de revenir temporairement au localStorage jusqu'à résolution.

---

## 9. Plan de Handoff

### Agent Suivant
**DEV Agent** pour implémentation Story P3

### Contexte pour DEV
- Story 9 (frontend) ✅ **COMPLÈTE** et approuvée QA
- **BESOIN:** Seulement l'endpoint backend `GET /api/v1/tracks/recent`
- **INTÉGRATION:** Remplacer localStorage par API call dans `RecentTracksList.tsx`
- **PRIORITÉ:** HAUTE - Application en production avec données non synchronisées

### Fichiers à Modifier

#### Backend
- `apps/producer/producer-application/src/main/java/.../TrackService.java` (nouveau endpoint)
- Contrôleur REST correspondant

#### Frontend
- `apps/webui/app/components/RecentTracksList.tsx` (remplacer localStorage par API)

#### Documentation
- `docs/architecture/api-specification.md` (documentation endpoint)

---

## 10. Approbation

### Validation de la Proposal

- ✅ **Issue clairement définie:** recentTracks statique → dynamique
- ✅ **Impacts quantifiés:** 1 story supplémentaire, 3 fichiers à modifier
- ✅ **Chemin avec rationale:** Extension API naturelle, effort minimal
- ✅ **Actions spécifiques:** Story P3, endpoint backend, modification frontend
- ✅ **Handoff clair:** DEV Agent avec contexte complet

### Demande d'Approbation

**Approuvez-vous cette Sprint Change Proposal qui propose de:**

1. Créer Story P3 dans Epic 1 pour l'endpoint `GET /api/v1/tracks/recent`
2. Modifier l'API Specification pour documenter le nouvel endpoint
3. Mettre à jour RecentTracksList pour utiliser l'API au lieu du localStorage
4. Priorité HAUTE pour corriger les données non synchronisées en production

---

**Signatures d'Approbation:**

| Rôle | Nom | Date | Signature |
|------|-----|------|-----------|
| Product Owner | | | |
| Tech Lead | | | |
| Scrum Master | | | |

---

*Document généré le 2025-08-22*  
*Version: 1.0*  
*Statut: En Attente d'Approbation*