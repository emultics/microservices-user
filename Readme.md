# USER_MICRO Service

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-brightgreen)](https://spring.io/projects/spring-boot)
[![Kafka](https://img.shields.io/badge/Apache%20Kafka-3.x-orange)](https://kafka.apache.org/)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15-blue)](https://www.postgresql.org/)
[![Redis](https://img.shields.io/badge/Redis-7.x-red)](https://redis.io/)

## Overview

This document provides a comprehensive overview of the **USER_MICRO** service, a Spring Boot-based microservice for user management that implements event-driven architecture using the transactional outbox pattern, Change Data Capture (CDC), and comprehensive audit logging. The service exposes REST APIs for user CRUD operations while ensuring reliable event delivery to downstream consumers through Kafka.

This README covers the system's high-level architecture, core components, technology stack, and design patterns. For detailed setup instructions, refer to the [Getting Started guide](https://deepwiki.com/emultics/microservices-user/2-getting-started). For in-depth architectural patterns, see the [Architecture section](https://deepwiki.com/emultics/microservices-user/3-architecture). For API specifications, see [REST API Endpoints](https://deepwiki.com/emultics/microservices-user/4.1-rest-api-endpoints).

## System Architecture at a Glance

The USER_MICRO service implements a dual-path architecture that separates synchronous database operations from asynchronous event publishing:

**Architecture Diagram: Component Interaction Flow**

The system follows a clean separation of concerns:

- **UserService** handles direct database operations with Redis caching
- **UserOutboxService** implements the transactional outbox pattern for reliable event publishing
- **AuditAspect** uses AOP to capture all API invocations
- **Debezium** monitors the outbox database's write-ahead log (WAL) and publishes events to Kafka
- **Elasticsearch** aggregates audit events for compliance and analytics

## Core Components

### Application Layer

| Component | File Path | Responsibility |
| --- | --- | --- |
| **UserServiceSpringApplication** | `src/main/java/com/user/userServiceSpringApplication/UserServiceSpringApplication.java` | Main application entry point with `@EnableCaching`, `@EnableTransactionManagement`, and UTC timezone configuration |
| **UserController** | `com.user.userServiceSpringApplication.controller.UserController` | REST API endpoints for user operations, decorated with `@AuditRequest` for audit capture |
| **UserService** | `com.user.userServiceSpringApplication.service.UserService` | Synchronous CRUD operations with `@Cacheable` annotations for Redis integration |
| **UserOutboxService** | `com.user.userServiceSpringApplication.service.UserOutboxService` | Transactional event publishing using the outbox pattern with `@Transactional` boundary |
| **AuditAspect** | `com.user.userServiceSpringApplication.aspect.AuditAspect` | AOP-based cross-cutting concern capturing request/response metadata with `@Around` advice |

### Data Persistence

**Data Model Diagram: Database Schema and Entity Mapping**

The dual-database architecture separates operational data from event data:

- **userdb (port 5433)**: Stores user records accessed via `UserRepository`
- **outboxdb (port 5434)**: Stores transactional event records with `wal_level=logical` enabled for CDC

### Event Streaming Infrastructure

| Component | Port | Configuration |
| --- | --- | --- |
| **Debezium Connect** | 8083 | PostgresConnector with EventRouter SMT, monitors `user_kafka_outbox` and `audit_outbox` tables |
| **Apache Kafka** | 9092 | Message broker with topics `user.CREATE`, `user.UPDATE`, `user.DELETE`, `audit.*` |
| **Zookeeper** | 2181 | Cluster coordination for Kafka |
| **Elasticsearch** | 9200 | Time-series index `audit-events-YYYY.MM.dd` for compliance logs |
| **Kibana** | 5601 | Visualization and analytics dashboard |

## Technology Stack

**Technology Stack Diagram: Dependencies and Framework Integration**

### Key Dependencies

**Spring Boot Starters** (from [pom.xml](https://github.com/emultics/microservices-user/blob/main/pom.xml)):

- `spring-boot-starter-webmvc` - RESTful API framework
- `spring-boot-starter-data-jpa` - Object-relational mapping with Hibernate
- `spring-boot-starter-cache` - Cache abstraction with Redis backend
- `spring-boot-starter-kafka` - Kafka producer/consumer integration
- `spring-boot-starter-aop` - Aspect-oriented programming for cross-cutting concerns

**Database & Migration**:

- `postgresql` - JDBC driver for PostgreSQL
- `flyway-database-postgresql` (v11.19.0) - Schema versioning and migration
- Configured for dual datasources: `spring.datasource.user` and `spring.datasource.outbox`

**Utilities**:

- `jackson-datatype-jsr310` - Java 8 date/time serialization support
- `lombok` - Reduces boilerplate with annotations like `@Data`, `@Builder`
- `springdoc-openapi-starter-webmvc-ui` (v2.8.3) - Swagger/OpenAPI documentation

## Key Design Patterns

### Transactional Outbox Pattern

The service implements the transactional outbox pattern to guarantee reliable event publishing without distributed transactions:

**Transactional Outbox Flow: Atomic Write with Asynchronous Publishing**

**Key characteristics**:

1. **Atomicity**: Both `users` and `user_kafka_outbox` inserts occur in a single database transaction managed by `@Transactional`
2. **Immediate Response**: API returns success immediately after commit, not waiting for Kafka
3. **Guaranteed Delivery**: Debezium ensures eventual publication by reading the PostgreSQL write-ahead log
4. **Decoupling**: Application doesn't depend on Kafka availability

For detailed implementation, see [Event-Driven Architecture and Outbox Pattern](https://deepwiki.com/emultics/microservices-user/3.1-event-driven-architecture-and-outbox-pattern).

### Change Data Capture (CDC)

Debezium monitors the `outboxdb` database using PostgreSQL logical replication:

| Configuration Key | Value | Purpose |
| --- | --- | --- |
| `connector.class` | `io.debezium.connector.postgresql.PostgresConnector` | CDC connector type |
| `plugin.name` | `pgoutput` | Native PostgreSQL logical replication |
| `table.include.list` | `public.user_kafka_outbox` | Monitor specific outbox table |
| `transforms` | `outbox` | Apply EventRouter transformation |
| `transforms.outbox.type` | `io.debezium.transforms.outbox.EventRouter` | Route by event type |
| `transforms.outbox.route.by.field` | `event_type` | Field containing routing key |
| `transforms.outbox.route.topic.replacement` | `user.${routedByValue}` | Topic naming pattern |

**Connector Configuration**:

- Creates topics dynamically: `user.CREATE`, `user.UPDATE`, `user.DELETE`
- Expands JSON payload from `user_payload` column
- Uses `aggregate_id` as Kafka message key for partitioning

For CDC pipeline details, see [Change Data Capture (CDC) Pipeline](https://deepwiki.com/emultics/microservices-user/3.3-change-data-capture-(cdc)-pipeline).

### Aspect-Oriented Auditing

The `AuditAspect` uses Spring AOP to capture all API invocations decorated with `@AuditRequest`:

**Audit Aspect Flow: Cross-Cutting Concern Implementation**

**Captured Metadata**:

- `userId`, `roles`, `authType` - Authentication context
- `httpMethod`, `uri`, `clientIp` - Request metadata
- `requestPayload`, `responsePayload` - Data snapshots
- `executionTimeMs` - Performance metrics
- `timestamp` - Event time in UTC

Events flow: `audit_outbox` → Debezium → Kafka (`audit.USER_API_CALL`) → Elasticsearch → Kibana

For complete audit flow, see [Audit System](https://deepwiki.com/emultics/microservices-user/6-audit-system) and [Audit Event Flow to Elasticsearch](https://deepwiki.com/emultics/microservices-user/6.1-audit-event-flow-to-elasticsearch).

## API Surface

The service exposes REST endpoints on port 9000:

**API Endpoint Routing Diagram**

### Example Requests

**Create User**:

```http
POST http://localhost:9000/user/create
Content-Type: application/json

{
  "name": "suman manna",
  "email": "manna.suman134@gmail.com",
  "password": "1244d242",
  "active": true
}
```

**Fetch User**:

```http
GET http://localhost:9000/fetch/{id}
```

For complete API documentation, see [REST API Endpoints](https://deepwiki.com/emultics/microservices-user/4.1-rest-api-endpoints) and [Testing and API Examples](https://deepwiki.com/emultics/microservices-user/12-testing-and-api-examples).

## Infrastructure Components

### Database Configuration

The service connects to two PostgreSQL databases with non-standard ports to avoid conflicts:

| Database | JDBC URL | Username | Purpose |
| --- | --- | --- | --- |
| **userdb** | `jdbc:postgresql://localhost:5433/userdb` | `user` | Primary user data storage |
| **outboxdb** | `jdbc:postgresql://localhost:5434/outboxdb` | `outbox` | Transactional outbox and audit events with `wal_level=logical` |

**Configuration**:

- Dual datasource setup: `spring.datasource.user` and `spring.datasource.outbox`
- Hibernate dialect: `PostgreSQLDialect`
- DDL auto: `none` (schema managed by Flyway)
- Connection pooling: HikariCP (default in Spring Boot)

### Cache Layer

Redis caching on port 6379 improves read performance:

**Configuration**:

- Cache type: `redis`
- Cache name: `user-caching` (referenced in `@Cacheable` annotations)
- Enabled via `@EnableCaching` annotation

**Usage Pattern**: `UserService.getUserById()` leverages `@Cacheable` to cache user lookups, reducing database load.

### Event Streaming

**Kafka Configuration**:

- Bootstrap servers: `localhost:9092`
- The application doesn't directly produce to Kafka; events are published asynchronously via Debezium CDC.

## Contributing

See the [GitHub repository](https://github.com/emultics/microservices-user) for the full source code.
See the [Documentation](https://deepwiki.com/emultics/microservices-user) for more.
## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.