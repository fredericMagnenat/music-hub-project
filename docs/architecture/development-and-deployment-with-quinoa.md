# Development and Deployment with Quinoa

The integration between frontend and backend is managed by the [Quarkus Quinoa extension](https://quarkus.io/blog/quinoa-modern-ui-with-no-hassle/), which unifies their lifecycles.

*   **Local Development:** From the `apps/bootstrap` directory, run `mvn quarkus:dev`. This starts the Quarkus backend and the Remix dev server (configured via Quinoa) with transparent proxying for live-reload.
*   **Build:** Run `mvn package` from the `apps` directory (or `mvn package` inside `apps/bootstrap`). The build will trigger the Remix build in `apps/webui` and bundle the produced assets into the Quarkus application JAR.
*   **Deployment:** Build a single Docker container from the Quarkus application and deploy it (e.g., to AWS App Runner). The container serves both the API and the UI assets.
