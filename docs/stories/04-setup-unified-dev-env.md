# Story 4: Configurer l'environnement de développement unifié (Setup-4)

**En tant que** Développeur, **je veux** être capable de lancer une seule commande (`quarkus dev`) pour démarrer à la fois le backend et le frontend avec rechargement à chaud, **afin de** bénéficier d'une expérience de développement fluide et productive.

---

### Tâches à faire

- [x] Dans le `pom.xml` du module `bootstrap` (`apps/bootstrap/pom.xml`), ajouter la dépendance `quarkus-quinoa`.
- [x] Dans le fichier de configuration de Quarkus (`apps/bootstrap/src/main/resources/application.properties`), ajouter la configuration de Quinoa :
  ```properties
  # Quinoa UI
  quarkus.quinoa.ui-dir=../webui
  quarkus.quinoa.dev-server.port=5173
  ```
- [x] Lancer la commande `mvn quarkus:dev` depuis le répertoire `apps/bootstrap`.
- [x] Vérifier que le backend Quarkus démarre.
- [x] Vérifier que le serveur de développement Remix (frontend) est démarré automatiquement par Quarkus.
- [x] Accéder à l'URL du frontend et vérifier que l'application Remix est servie.
- [x] Modifier un fichier dans le frontend (`apps/webui`) et vérifier que le rechargement à chaud (hot-reload) fonctionne dans le navigateur.

---
### Définition de "Terminé" (DoD)

- [x] La commande `quarkus dev` lance les deux serveurs.
- [x] L'application frontend est accessible via l'URL du serveur Quarkus.
- [x] Le rechargement à chaud est fonctionnel pour le frontend et le backend. 