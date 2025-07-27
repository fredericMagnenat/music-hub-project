# Story 4: Configurer l'environnement de développement unifié (Setup-4)

**En tant que** Développeur, **je veux** être capable de lancer une seule commande (`quarkus dev`) pour démarrer à la fois le backend et le frontend avec rechargement à chaud, **afin de** bénéficier d'une expérience de développement fluide et productive.

---

### Tâches à faire

- [ ] Dans le `pom.xml` du module `bootstrap` (`apps/backend/bootstrap/pom.xml`), ajouter la dépendance `quarkus-quinoa`.
- [ ] Dans le fichier de configuration de Quarkus (`apps/backend/bootstrap/src/main/resources/application.properties`), ajouter la configuration de Quinoa :
  ```properties
  # Quinoa UI
  quarkus.quinoa.ui-dir=../../frontend
  quarkus.quinoa.dev-server.port=3000
  ```
- [ ] Lancer la commande `quarkus dev` depuis le répertoire `apps/backend/bootstrap`.
- [ ] Vérifier que le backend Quarkus démarre.
- [ ] Vérifier que le serveur de développement Remix (frontend) est démarré automatiquement par Quarkus.
- [ ] Accéder à l'URL du frontend et vérifier que l'application Remix est servie.
- [ ] Modifier un fichier dans le frontend (`apps/frontend`) et vérifier que le rechargement à chaud (hot-reload) fonctionne dans le navigateur.

---
### Définition de "Terminé" (DoD)

- [ ] La commande `quarkus dev` lance les deux serveurs.
- [ ] L'application frontend est accessible via l'URL du serveur Quarkus.
- [ ] Le rechargement à chaud est fonctionnel pour le frontend et le backend. 