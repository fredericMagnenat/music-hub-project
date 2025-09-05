# Epic 1: Core Producer Management System - Brownfield Enhancement

## Epic Goal

Établir un système robuste de gestion des producteurs avec validation ISRC automatique et création de catalogue, permettant aux utilisateurs de soumettre et valider des ISRCs de manière fiable.

## Epic Description

### Existing System Context

- **Current relevant functionality:** Application fullstack Music Hub avec architecture hexagonale Quarkus + Remix
- **Technology stack:** Backend Quarkus (Java 21), Frontend Remix (TypeScript), PostgreSQL avec Flyway
- **Integration points:** API REST `/api/v1/producers`, bus d'événements Vert.x, contextes Producer/Artist

### Enhancement Details

- **What's being added/changed:** Système complet de gestion des producteurs avec validation ISRC, intégration API externe, et événements inter-contextes
- **How it integrates:** Via architecture hexagonale existante avec nouveaux modules producer-domain, producer-application, producer-adapters
- **Success criteria:** Validation ISRC fonctionnelle, création automatique producteurs, événements `TrackWasRegistered` publiés

## Stories

1. **Story 1-01:** Validate and Create a Producer - Validation ISRC et création automatique de producteurs
   - Implémentation des Value Objects (ISRC, ProducerCode) dans shared-kernel
   - Création de l'agrégat Producer avec logique métier
   - API REST endpoint `/api/v1/producers` avec validation et gestion d'erreurs
   - Interface utilisateur avec formulaire ISRC et feedback

2. **Story 1-02:** Integrate a Track and Publish an Event - Intégration des tracks avec publication d'événements
   - Client HTTP pour intégration API externe (MusicPlatformClient)
   - Entité Track et logique d'ajout dans l'agrégat Producer
   - Publication d'événements TrackWasRegistered sur le bus Vert.x
   - Gestion des erreurs d'API externe et feedback utilisateur

3. **Story 1-03:** Recent Tracks API Endpoint - Endpoint pour dashboard tracks récentes
   - Implémentation de `GET /api/v1/tracks/recent`
   - Service de récupération des 10 tracks les plus récentes
   - Tri par date de soumission décroissante
   - Intégration avec RecentTracksList frontend existant

## Compatibility Requirements

- ✅ Existing APIs remain unchanged
- ✅ Database schema changes are backward compatible via Flyway
- ✅ UI changes follow existing patterns (shadcn/ui + Tailwind)
- ✅ Performance impact is minimal through async architecture

## Risk Mitigation

- **Primary Risk:** Intégration avec APIs externes (timeout, rate limiting, disponibilité)
- **Mitigation:** 
  - Circuit breakers et retry policies implémentés dans producer-adapter-spi
  - Gestion d'erreurs robuste avec codes HTTP appropriés (400, 422)
  - Timeout configurables et monitoring des API externes
- **Rollback Plan:** Désactivation du module producer-adapter-spi via configuration Quarkus

## Definition of Done

- ✅ All stories completed with acceptance criteria met (1-01, 1-02, 1-03)
- ✅ Existing functionality verified through testing (regression tests)
- ✅ Integration points working correctly (API REST, événements)
- ✅ Documentation updated appropriately (architecture, API spec)
- ✅ No regression in existing features (test coverage maintained)

## Business Value

- **Primary Value:** Validation automatique des ISRCs avec feedback immédiat
- **Secondary Value:** Base solide pour la gestion de catalogue musical
- **Technical Value:** Architecture événementielle extensible pour futurs contextes

## Dependencies

- **Prerequisites:** Monorepo structure (Epic 0), Architecture hexagonale établie
- **External Dependencies:** APIs musicales externes pour validation ISRC
- **Internal Dependencies:** Shared-kernel avec Value Objects partagés

## Success Metrics

- **Functional:** 100% des ISRCs valides traités avec succès
- **Technical:** < 2s temps de réponse pour validation ISRC
- **Quality:** 80%+ code coverage maintenu
- **User Experience:** Feedback clair pour succès et erreurs

---

**Status:** 🔄 **IN PROGRESS** - Stories 1-01 and 1-02 completed, 1-03 ready for development
**Created:** 2025-08-22 by Sarah (Product Owner)
**Epic Type:** Brownfield Enhancement