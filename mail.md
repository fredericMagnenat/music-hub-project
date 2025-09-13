Sujet: Demande de création d'une story pour l'amélioration des logs dans RegisterTrackService

Cher Scrum Master,

Suite à l'analyse du code de RegisterTrackService.java par rapport aux bonnes pratiques de logging du projet (docs/architecture/logging-best-practices.md), j'ai identifié plusieurs améliorations nécessaires pour assurer la conformité et la qualité des logs.

**Analyse actuelle:**
- Utilisation correcte de SLF4J avec logger statique
- Niveaux debug/info appropriés pour le flux métier
- Gestion d'exceptions conforme ("log OR rethrow")
- Cependant: absence d'IDs de corrélation, pas de contexte métier structuré, pas de métriques de performance

**Écarts par rapport aux bonnes pratiques:**
1. IDs de corrélation manquants pour le traçage des requêtes
2. Absence de contexte métier ou métadonnées structurées
3. Pas de logs de timing de performance
4. Niveaux de détail des logs incohérents

**Plan de développement proposé:**
1. **Intégration des IDs de corrélation**
   - Ajouter paramètre correlationId à la méthode registerTrack (depuis la couche REST)
   - Propager correlationId dans toutes les méthodes privées
   - Inclure correlationId dans tous les statements de log

2. **Amélioration du logging structuré**
   - Ajouter contexte métier aux logs clés (producer_code, operation: "track_registration")
   - Convertir les logs info verbeux en format structuré
   - Assurer compatibilité avec l'encodeur JSON

3. **Monitoring des performances**
   - Ajouter logs de temps d'exécution pour les appels API externes
   - Logger le temps total d'exécution de la méthode au niveau INFO

4. **Optimisation des niveaux de log**
   - Réviser debug vs info - promouvoir les debug critiques métier vers info
   - Assurer niveau TRACE disponible pour flux détaillé si nécessaire

5. **Préparation des tests**
   - Vérifier le format de sortie des logs en environnement de test
   - Assurer pas de régression dans les fonctionnalités existantes
   - Tester la propagation des IDs de corrélation de bout en bout

Pourrais-tu créer une story pour ces améliorations ? Cela permettrait de suivre le travail de refactoring de manière structurée.

Cordialement,
James (Dev)
