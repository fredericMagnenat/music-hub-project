# Story 7: Mettre à jour le pipeline CI/CD pour l'architecture révisée (Setup-7)

**En tant que** équipe d'ingénierie, **nous voulons** que le pipeline CI/CD reflète fidèlement la nouvelle architecture (monorepo Quarkus + Remix, modules `apps/*`), **afin que** les builds et tests s'exécutent correctement et rapidement à chaque changement.

---

### Contexte

L'architecture et la structure du dépôt ont été revues:
- Frontend: `apps/webui` (Remix + PNPM)
- Backend multi-modules Maven: parent `apps/pom.xml` (incluant `bootstrap`, `producer`, `artist`, `shared-kernel`)
- Branche par défaut actuelle du dépôt: `master`

Le workflow actuel `.github/workflows/ci.yaml` présente des divergences:
- Références obsolètes: `pnpm --filter frontend` et `--file apps/backend/pom.xml`
- Déclencheurs sur la branche `main` au lieu de `master`

---

### Objectif

Aligner et améliorer le pipeline CI afin de:
- Construire et tester le frontend (`apps/webui`) et le backend (parent `apps/pom.xml`)
- Utiliser Java 21 et PNPM 8, avec caches (Maven + PNPM) pour accélérer
- Déclencher sur `push` et `pull_request` vers `master`
- Optionnel: exécuter les jobs frontend et backend en parallèle pour un feedback plus rapide

---

### Tâches à faire

- [ ] Mettre à jour `.github/workflows/ci.yaml`:
  - [ ] Déclencheurs: `push` et `pull_request` vers `master`
  - [ ] Installer Node (ou PNPM) et activer le cache PNPM
  - [ ] Installer Java 21 (Temurin) et activer le cache Maven
  - [ ] Frontend:
    - [ ] `pnpm install --frozen-lockfile`
    - [ ] `pnpm --filter webui build`
    - [ ] `pnpm --filter webui test`
  - [ ] Backend:
    - [ ] `mvn -B -f apps/pom.xml verify` (inclut tests et Jacoco)
  - [ ] Upload d’artefacts utiles en cas d’échec (rapports tests)
- [ ] (Optionnel) Scinder en deux jobs `frontend` et `backend` exécutés en parallèle
- [ ] Vérifier que les caches sont effectifs (hits) sur un run subséquent
- [ ] Mettre à jour la documentation si nécessaire (références de chemins)

---

### Points d’attention

- Les chemins doivent refléter la structure: `apps/webui` et `apps/pom.xml`
- S’assurer que PNPM 8 est utilisé et compatible avec le lockfile
- Conserver Java 21 pour Quarkus
- La branche par défaut du dépôt étant `master`, ne pas utiliser `main` dans les déclencheurs

---

### Définition de "Terminé" (DoD)

- [ ] Le fichier `.github/workflows/ci.yaml` référence `apps/webui` et `apps/pom.xml`
- [ ] Les workflows se déclenchent sur `push` et `pull_request` vers `master`
- [ ] Les jobs front et back passent au vert sur un commit standard
- [ ] Les caches PNPM et Maven montrent des hits sur un second run
- [ ] En cas de test échoué (front ou back), le pipeline échoue et publie les rapports

---

### Annexe: squelette YAML proposé

```yaml
name: CI Pipeline

on:
  push:
    branches: [ master ]
  pull_request:
    branches: [ master ]

jobs:
  frontend:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - uses: actions/setup-node@v4
        with:
          node-version: 20
          cache: 'pnpm'
      - uses: pnpm/action-setup@v2
        with:
          version: 8
      - name: Install dependencies
        run: pnpm install --frozen-lockfile
      - name: Build webui
        run: pnpm --filter webui build
      - name: Test webui
        run: pnpm --filter webui test -- --reporter=junit --outputFile=./junit-webui.xml
      - name: Upload webui test report (on failure)
        if: failure()
        uses: actions/upload-artifact@v4
        with:
          name: webui-test-report
          path: junit-webui.xml

  backend:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - uses: actions/setup-java@v4
        with:
          distribution: 'temurin'
          java-version: '21'
          cache: 'maven'
      - name: Build & test (Maven multi-module)
        run: mvn -B -f apps/pom.xml verify
      - name: Upload surefire reports (on failure)
        if: failure()
        uses: actions/upload-artifact@v4
        with:
          name: maven-test-reports
          path: '**/target/surefire-reports/*.xml'
```

Remarque: adaptez les flags de test frontend selon l’outillage réel (Vitest/Jest) et configurez Jacoco/ArchUnit côté Maven si ce n’est pas déjà le cas.
