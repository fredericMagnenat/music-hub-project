# Business Logic Implementation

## Data Consistency and Source of Truth

To resolve data conflicts from multiple sources, the application implements a "Source of Truth" hierarchy (`MANUAL > TIDAL > SPOTIFY...`) as defined in the Domain Charter. This logic is applied within the domain aggregates (`Track`, `Artist`) when new information is processed, ensuring that the entity's state always reflects the data from the most authoritative source available. The authoritative source is determined at the entity level, not the attribute level, meaning the system identifies the single highest-ranking source for a given entity and considers all of its data to be authoritative.

## Correlation ID Pattern Implementation

The application implements a comprehensive correlation ID pattern to ensure proper observability and tracing across the distributed system. This pattern is crucial for debugging, monitoring, and maintaining request traceability in production environments.

### Pattern Overview

Each service generates its own correlation ID following this format:
```
{service-name}-{timestamp}-{uuid-safe}
```

Example: `producer-1734567890123-a1b2c3d4e5f6789`

### Implementation Strategy

**1. Service-Specific ID Generation:**
```java
// In RegisterTrackService.java
@Override
public Producer registerTrack(String isrcValue, String correlationId) {
    // Generate service-specific correlation ID
    String serviceCorrelationId = CorrelationIdGenerator.buildServiceCorrelationId(correlationId, SERVICE_NAME);

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
```

**2. Shared Utility Class:**
```java
// In CorrelationIdGenerator.java (shared-kernel)
public static String buildServiceCorrelationId(String incomingCorrelationId, String serviceName) {
    if (incomingCorrelationId != null && !incomingCorrelationId.trim().isEmpty()) {
        return incomingCorrelationId + "-" + serviceName + "-service";
    }
    return generateServiceCorrelationId(serviceName);
}
```

### Key Benefits

- **🔍 Enhanced Observability**: Clear service identification in logs
- **🔗 Distributed Tracing**: Correlation IDs flow across service boundaries
- **🛡️ Backward Compatibility**: Existing clients work unchanged
- **📊 Monitoring Ready**: Structured logs for APM tools
- **🧪 Testable**: Dedicated test utilities for correlation ID validation

### Usage Examples

**Log Output:**
```json
{
  "timestamp": "2024-01-15T10:30:00.123Z",
  "level": "INFO",
  "logger": "com.musichub.producer.RegisterTrackService",
  "message": "Track registration completed",
  "correlation_id": "request-123-producer-service",
  "service": "producer",
  "business_context": {
    "producer_code": "FRLA1",
    "isrc": "FRLA12400001"
  }
}
```

**Chain of Correlation IDs:**
```
request-123 → request-123-producer-service → request-123-producer-service-artist-service
```

### Testing Strategy

**Unit Tests for Correlation ID Generation:**
```java
@Test
@DisplayName("Should generate unique correlation IDs")
void shouldGenerateUniqueCorrelationIds() {
    Set<String> generatedIds = new HashSet<>();
    for (int i = 0; i < 1000; i++) {
        String id = CorrelationIdGenerator.generateServiceCorrelationId("producer");
        generatedIds.add(id);
    }
    assertEquals(1000, generatedIds.size());
}
```

### Best Practices

1. **Always Set MDC Context**: Use MDC.put() for structured logging
2. **Cleanup After Use**: Always call MDC.clear() in finally blocks
3. **Service-Specific Suffix**: Append service name to maintain traceability
4. **Fallback Generation**: Generate ID if none provided
5. **Test Coverage**: Include correlation ID validation in tests

### Integration with Monitoring

The correlation ID pattern integrates seamlessly with:
- **OpenTelemetry**: Distributed tracing support
- **ELK Stack**: Structured log analysis
- **Prometheus**: Request tracing metrics
- **APM Tools**: End-to-end transaction monitoring

This implementation ensures that every request can be traced from the initial entry point through all service layers, providing complete observability for production debugging and performance monitoring.

----- 
