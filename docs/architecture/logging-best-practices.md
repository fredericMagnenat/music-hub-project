# Logging Best Practices

**Updated**: This section reflects SonarQube-compliant exception handling patterns implemented in the codebase (Story DOC-1).

## Logging Strategy & Philosophy

Our logging approach follows these principles:

- **SonarQube Compliance**: "Either log OR rethrow" pattern prevents duplicate logging
- **Structured Logging**: JSON format for easier analysis
- **Contextual Information**: Correlation IDs for request tracing
- **Hexagonal Architecture**: Clear logging responsibilities per layer
- **Performance Awareness**: Asynchronous logging to minimize impact
- **Security First**: Automatic masking of sensitive data

## Quarkus Logging Framework

Centralized configuration using:

- **JBoss Logging** as facade
- **Logback** as implementation
- **JSON Encoder** for structuring
- **OpenTelemetry** for correlation

## Log Levels & Usage Guidelines

| Level | Usage | Examples |
|-------|--------|----------|
| ERROR | System failures, exceptions | Database connection lost, API timeouts |
| WARN | Recoverable issues | Deprecated API usage, configuration warnings |
| INFO | Business events | Track registered, Producer created |
| DEBUG | Development details | SQL queries, method parameters |
| TRACE | Detailed flow | Method entry/exit, loop iterations |

## Structured Logging with JSON

```json
{
  "timestamp": "2024-01-15T10:30:00.123Z",
  "level": "INFO",
  "logger": "com.musichub.producer.application.RegisterTrackService",
  "message": "Track successfully registered",
  "correlation_id": "req-123-456",
  "business_context": {
    "producer_code": "FRLA1",
    "isrc": "FRLA12400001",
    "operation": "track_registration"
  }
}
```

## Contextual Logging & Correlation

- **Request Correlation**: UUID generated per request
- **Business Context**: Business metadata in each log
- **User Context**: User identification (if applicable)
- **Performance Context**: Execution times and metrics

## Hexagonal Architecture Logging Patterns

**Exception Handling Compliance - SonarQube "Either log OR rethrow" Rule**

Our architecture follows strict exception handling patterns to prevent duplicate logging and ensure clean error flow:

| Layer | Responsibility | Pattern |
|-------|---------------|---------|
| **Domain** | Business rules | Log business events + throw domain exceptions |
| **Application** | Use case orchestration | Log + rethrow OR log + handle completely |
| **Adapter** | Infrastructure context | Rethrow with technical context (NO logging) |
| **REST** | HTTP boundary | Log final error + convert to HTTP response |

```java
// ✅ COMPLIANT: Adapter rethrows with context - NO duplicate logging
 @ApplicationScoped
public class ProducerRepositoryAdapter {
    try {
        // Database operations...
        return result;
    } catch (Exception e) {
        // ✅ Rethrow with context - do NOT log here
        throw new ProducerPersistenceException(
            String.format("Failed to retrieve producer (correlationId: %s)", 
                correlationId), e);
    }
}

// Application Layer - Log and rethrow for higher layers
log.error("Track registration failed - persistence error: {}", correlationId, e);
throw e; // Rethrow for REST layer to handle

// Domain Layer - Business Events
log.info("Producer created with code: {}", producerCode.value());
```

**Reference**: Complete patterns and examples in [Logging Best Practices](https://www.google.com/search?q=architecture/logging-best-practices.md)

## Security & Sensitive Data

- **Auto-masking**: Partial ISRCs, user IDs
- **Whitelist Approach**: Only authorized data is logged
- **Audit Trail**: Traceability of sensitive actions
- **Data Classification**: Log marking by sensitivity level

## Environment-Specific Configuration

```properties