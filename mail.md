Sujet: Demande de création d'une story pour refactoriser RegisterTrackService vers ExecutionContext pattern

Cher Scrum Master,

Suite à la finalisation de la story 4.1 "Improve Logging in RegisterTrackService" (actuellement en "Ready for Review"), j'ai identifié une opportunité d'amélioration architecturale importante.

**Contexte :**
La story 4.1 a été implémentée avec succès et respecte tous les critères d'acceptation. Cependant, l'approche choisie (ajout de correlationId comme paramètre dans l'interface du domaine) crée une dette technique architecturale acceptable mais perfectible.

**Analyse architecturale :**
Après revue approfondie avec l'équipe, nous avons conclu que cette approche, bien que fonctionnelle :
- ✅ Respecte les délais et les critères métier
- ✅ Fournit l'observabilité requise
- ⚠️ Introduit des préoccupations techniques dans l'interface du domaine
- ⚠️ Ne suit pas parfaitement les principes de l'architecture hexagonale

**Proposition de refactor :**
Je propose de créer une nouvelle story pour implémenter le pattern **ExecutionContext** qui permettrait de :

1. **Séparer les préoccupations** : Retirer correlationId de l'interface domaine
2. **Maintenir l'observabilité** : Conserver tous les bénéfices de logging actuels
3. **Établir un pattern réutilisable** : Créer un standard pour les futures features
4. **Réduire la dette technique** : Améliorer la maintenabilité à long terme

**Plan de refactor détaillé (Phases) :**

**Phase 0 : Expérimentation (1-2h - RECOMMANDÉE)**
- POC ExecutionContext simple pour valider les risques ThreadLocal
- Tests de performance et memory leaks
- Validation de l'isolation des tests parallèles
- Décision go/no-go basée sur les métriques

**Phase 1 : Préparation (30-45 min)**
- Créer `ExecutionContext` et `ExecutionContextHolder` dans shared-kernel
- Définir les interfaces et contrats avec gestion d'erreurs robuste
- Implémenter cleanup automatique des contextes

**Phase 2 : Implémentation du pattern (45-60 min)**
- Modifier `RegisterTrackUseCase` pour retirer le paramètre correlationId
- Refactoriser `RegisterTrackService` pour utiliser `ExecutionContextHolder`
- Mettre à jour `ProducerResource` pour initialiser le contexte
- Ajouter monitoring et alertes pour les fuites potentielles

**Phase 3 : Tests et validation (45-60 min)**
- Créer des utilitaires de test pour `ExecutionContext`
- Tests spécifiques pour ThreadLocal isolation
- Tests de performance et memory profiling
- Tests d'intégration exhaustifs pour les correlation IDs

**Phase 4 : Documentation et finalisation (30 min)**
- Documenter le nouveau pattern dans les guidelines
- Mettre à jour la story 4.1 avec référence au refactor
- Vérifier que toutes les fonctionnalités existantes fonctionnent
- Plan de rollback documenté

**Bénéfices attendus :**
- ✅ Amélioration de la séparation des couches (architecture hexagonale)
- ✅ Pattern réutilisable pour d'autres services
- ✅ Réduction de la dette technique à long terme
- ✅ Meilleure maintenabilité future
- ✅ Respect des principes architecturaux

**Alternatives moins risquées :**
1. **Garder l'approche actuelle** : Documenter comme dette technique acceptable
2. **RequestContext au lieu de ThreadLocal** : Pattern plus simple avec RequestScope
3. **CorrelationId comme Value Object** : Wrapper type-safe sans ThreadLocal
4. **Phase d'expérimentation** : POC 1-2h pour valider les risques avant refactor complet

**Risques détaillés et mitigation :**

⚠️ **Risques importants identifiés :**

1. **Thread Safety et Memory Leaks (CRITIQUE)**
   - **Risque** : ThreadLocal dans ExecutionContext peut causer des fuites mémoire
   - **Impact** : Dégradation performance, OutOfMemoryError en production
   - **Mitigation** : try/finally systématique + tests de charge

2. **Tests parallèles et isolation**
   - **Risque** : Tests JUnit parallèles peuvent partager le contexte ThreadLocal
   - **Impact** : Tests flaky, faux positifs/négatifs
   - **Mitigation** : Utilitaires de test dédiés + isolation des contextes

3. **Performance et latence**
   - **Risque** : ThreadLocal lookups + context switching overhead
   - **Impact** : Dégradation 5-15% sur les appels fréquents
   - **Mitigation** : Benchmarks avant/après + monitoring continu

4. **Complexité de débogage**
   - **Risque** : Contexte invisible rend le debugging difficile
   - **Impact** : Temps de résolution incidents x2-x3
   - **Mitigation** : Logging enrichi + outils de monitoring

5. **Régression fonctionnelle**
   - **Risque** : Perte de correlation IDs dans certains chemins
   - **Impact** : Observabilité dégradée, troubleshooting impossible
   - **Mitigation** : Tests d'intégration exhaustifs + audit logs

6. **Adoption et formation équipe**
   - **Risque** : Pattern complexe à maîtriser pour l'équipe
   - **Impact** : Bugs introduits par mauvaise utilisation
   - **Mitigation** : Formation 30min + code reviews stricts

**Impact et effort :**
- **Effort estimé** : 4-6 heures (vs 2-3h initialement)
- **Risque global** : Moyen-Élevé (nécessite expertise ThreadLocal)
- **Régression** : Possible malgré les tests
- **Urgence** : À réévaluer selon tolérance aux risques

**Métriques de succès :**
- Temps de développement : < 6h (avec POC) / < 4h (sans POC)
- Tests : 100% passing + tests ThreadLocal spécifiques
- Code coverage : Maintenu > 80%
- Performance : Pas de dégradation > 5% (benchmarks requis)
- Memory : Pas de fuites détectées (profiling continu)
- Réutilisabilité : Pattern adopté dans au moins 2 autres services
- Observabilité : Correlation IDs préservés dans 100% des cas

**Questions pour discussion :**
- Vu les risques identifiés, faut-il commencer par une phase d'expérimentation (POC) ?
- L'équipe a-t-elle l'expertise nécessaire pour gérer les complexités ThreadLocal ?
- Préférons-nous une approche plus conservative (garder correlationId) pour cette itération ?
- Quel est le niveau de tolérance aux risques pour cette amélioration architecturale ?

Pourrais-tu créer cette nouvelle story ? Le POC permettrait de valider l'approche avant un investissement plus important.

Cordialement,
James (Dev)

---
**Références :**
- Story 4.1 : docs/stories/4.1.improve-logging-in-registertrackservice.md
- Guidelines architecture : docs/architecture/
- Code actuel : apps/producer/producer-application/src/main/java/com/musichub/producer/application/service/RegisterTrackService.java
