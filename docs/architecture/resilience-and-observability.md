# Resilience and Observability

## Observability

  * **Metrics:** Application metrics (JVM, HTTP requests, etc.) will be exposed via Micrometer and the `quarkus-micrometer-registry-otlp` extension. This will allow them to be sent to any OpenTelemetry-compatible backend.
  * **Logging:** Logging will follow Quarkus standards, producing structured logs (JSON) in production for easier analysis.

## Resilience

  * **External Calls:** For the PoC, no retry or circuit breaker policies will be implemented for calls to the external music API. Basic error handling (timeouts, 5xx errors) will be implemented.
  * **Caching:** No caching strategy is defined for the PoC.

----- 
