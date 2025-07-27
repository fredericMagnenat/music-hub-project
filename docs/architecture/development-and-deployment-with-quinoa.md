# Development and Deployment with Quinoa

The integration between frontend and backend is managed by the [Quarkus Quinoa extension](https://quarkus.io/blog/quinoa-modern-ui-with-no-hassle/), which unifies their lifecycles.

*   **Local Development:** A single command, `quarkus dev`, from the `apps/backend` directory will start both the Quarkus backend and the Remix development server, with transparent proxying for a seamless live-reload experience.
*   **Build:** The `mvn package` command in `apps/backend` will first trigger the Remix build (`pnpm run build`) and then bundle the resulting static assets directly into the final Quarkus application JAR.
*   **Deployment:** The deployment process is maximally simplified. A single Docker container is built from the Quarkus application and deployed to AWS App Runner. This single container is responsible for serving both the API and the user interface assets.
