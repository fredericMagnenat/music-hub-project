# Coding Standards

This section defines the minimal but critical rules that development agents (AI or human) must follow. The goal is to enforce consistency and adhere to the architectural decisions made.

## Commit Message Format

All git commits **must** follow the [Conventional Commits](https://www.conventionalcommits.org/en/v1.0.0/) specification. This creates a clear and machine-readable commit history, which facilitates automated versioning and changelog generation.

* **Example `feat`:** `feat(producer): allow track registration via ISRC`
* **Example `fix`:** `fix(api): correct artist name serialization`
* **Example `docs`:** `docs(architecture): add db migration strategy`

## Critical Full-stack Rules

1.  **Domain Immutability**: Domain objects (`domain` layer) **must be** immutable. Any modification to an aggregate must result in a new instance of that aggregate.
2.  **Enforce Value Objects & Shared Kernel**:
    * In the backend domain, **prefer creating Value Objects** (`ISRC`, `ProducerCode`, `ArtistName`, etc.) over using primitive types like `String` for any data that has intrinsic rules, format, or constraints.
    * If a Value Object or event contract is used by more than one bounded context, it **must** be placed in the `apps/shared-kernel` module.
    * This enforces domain invariants and creates a clear, reusable "Shared Kernel".
3.  **No Logic in Adapters**: REST controllers and other adapters **must be** as "thin" as possible. They only convert requests/events into application service calls and map the results. All business logic **must reside** in the `application` and `domain` layers.
4.  **Application Use Case Entry Points**: The frontend (via REST controllers) and other contexts (via events) **must never** interact directly with repositories or the domain. They **must always** go through the application layer's use case interfaces (e.g., `RegisterTrackUseCase.java`). The implementing classes are often called "Application Services".
5**Configuration over Environment Variables**: **Never** access environment variables directly (`process.env` or `System.getenv`). Use the configuration mechanisms provided by the frameworks (Quarkus or Remix) for clean dependency injection of configuration.

## Correlation ID Management

**Each service must generate its own correlation ID** to ensure proper observability and tracing across the distributed system:

### Implementation Pattern

```java
@Override
public Result operation(String incomingCorrelationId) {
    // Generate service-specific correlation ID
    String serviceCorrelationId = (incomingCorrelationId != null && !incomingCorrelationId.isEmpty())
        ? incomingCorrelationId + "-" + SERVICE_NAME + "-service"
        : generateServiceCorrelationId();

    // Set MDC context for structured logging
    MDC.put("correlation_id", serviceCorrelationId);
    MDC.put("service", SERVICE_NAME);

    try {
        // Business logic using serviceCorrelationId
        return performOperation(serviceCorrelationId);
    } finally {
        MDC.clear();
    }
}

private String generateServiceCorrelationId() {
    return SERVICE_NAME + "-" + System.currentTimeMillis() + "-" +
           UUID.randomUUID().toString().substring(0, 8);
}
```

### Key Principles

1. **Service-Specific IDs**: Each service appends its name to create unique correlation IDs
2. **Fallback Generation**: If no incoming correlation ID, generate one automatically
3. **MDC Context**: Always set MDC context for structured logging
4. **Cleanup**: Always clear MDC context in finally block
5. **Interface Compatibility**: Keep interfaces unchanged to maintain backward compatibility

### Benefits

- ✅ **Clear Service Boundaries**: Easy to identify which service processed the request
- ✅ **Distributed Tracing**: Correlation IDs flow properly across service boundaries
- ✅ **Observability**: Structured logs with service-specific context
- ✅ **Backward Compatibility**: Existing clients continue to work unchanged

### Example Output

```
correlationId: request-123-producer-service-artist-service
```

This pattern ensures each service contributes to the correlation chain while maintaining clean separation of concerns.

-----
