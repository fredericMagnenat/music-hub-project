# Resilience and Observability

*   **Observability:**
    *   **Metrics:** Application metrics (JVM, HTTP requests, etc.) will be exposed using **Micrometer** and the `quarkus-micrometer-registry-otlp` extension. This will allow sending them to any OpenTelemetry-compatible backend.
    *   **Logging:** Logging will follow Quarkus standards, producing structured (JSON) logs in production for easy analysis.

*   **Resilience:**
    *   **External Calls:** For the PoC, no retry policies or circuit breakers will be implemented for the call to the external music API. This will be a consideration for the post-PoC version. Basic error handling (timeouts, 5xx errors) will be implemented.
    *   **Caching:** No caching strategy is defined for the PoC.
