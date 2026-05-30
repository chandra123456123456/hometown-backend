# HomeTown service conventions

Every feature service follows this exact shape so modules stay consistent and the
gateway/aggregator can wire them without surprises. Keep comments in code **very minimal**.

## Module = standalone Spring Boot app
Each feature module is a runnable Spring Boot application (microservices mode) AND a thin
jar consumable by the `hometown-api` aggregator later.

- `groupId` = `com.hometown` (inherited from parent), `artifactId` = `<name>-service`.
- Parent = `hometown-backend` 0.0.1-SNAPSHOT.
- Base Java package = `com.hometown.<svc>` (e.g. `com.hometown.user`).
- Include `spring-boot-maven-plugin` with `<classifier>exec</classifier>` so the plain jar
  remains usable as a dependency:
  ```xml
  <plugin>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-maven-plugin</artifactId>
    <configuration><classifier>exec</classifier></configuration>
  </plugin>
  ```

## Standard dependencies
`common`, `spring-boot-starter-web`, `spring-boot-starter-data-jpa`,
`spring-boot-starter-validation`, `spring-boot-starter-actuator`,
`micrometer-registry-prometheus`, `org.postgresql:postgresql` (runtime),
`org.flywaydb:flyway-core` AND `org.flywaydb:flyway-database-postgresql` (required by Flyway 10+
for the Postgres dialect), `spring-cloud-starter-netflix-eureka-client`.
Add `spring-boot-starter-security` only where auth is enforced (user-service).

## Package layout (under `com.hometown.<svc>`)
```
<Svc>ServiceApplication.java     @SpringBootApplication, main()
domain/        JPA entities (@Entity, @Table(schema="<svc>"))
repo/          Spring Data repositories
dto/           request/response records
service/       business logic (@Service)
web/           @RestController (paths under /api/...)
config/        security / beans if needed
api/           (optional) interface contract for split-later Feign binding
```

## Database: schema-per-service + Flyway
- Every entity: `@Table(schema = "<svc>", name = "...")`.
- Flyway migrations in `src/main/resources/db/migration/` named `V1__init.sql`, `V2__...`.
- First migration creates the schema: `CREATE SCHEMA IF NOT EXISTS <svc>;` then tables.
- `application.yml`: `spring.jpa.hibernate.ddl-auto=validate` (Flyway owns DDL),
  `spring.flyway.default-schema=<svc>`, `spring.flyway.schemas=<svc>`,
  `spring.flyway.create-schemas=true`.

## application.yml template
```yaml
server:
  port: <PORT>
spring:
  application:
    name: <svc>-service
  datasource:
    url: ${DATABASE_URL:jdbc:postgresql://localhost:5432/hometown}
    username: ${DB_USER:hometown}
    password: ${DB_PASSWORD:hometown}
  jpa:
    hibernate:
      ddl-auto: validate
    properties:
      hibernate.default_schema: <svc>
  flyway:
    default-schema: <svc>
    schemas: <svc>
    create-schemas: true
eureka:
  client:
    service-url:
      defaultZone: ${EUREKA_URI:http://localhost:8761/eureka}
  instance:
    prefer-ip-address: true
management:
  endpoints:
    web:
      exposure:
        include: health,info,prometheus,metrics
  metrics:
    tags:
      application: <svc>-service
```

## Port map
| Service | Port | Schema |
|---|---|---|
| discovery-server | 8761 | — |
| api-gateway | 8080 | — |
| user-service | 8081 | users |
| product-service | 8082 | products |
| cart-service | 8083 | carts |
| order-service | 8084 | orders |
| payment-service | 8085 | payments |
| shipping-service | 8086 | shipping |
| analytics-service | 8087 | analytics |

## Shared building blocks (from `common`)
- `com.hometown.common.security.JwtService` — issue/validate JWT (autoconfigured bean).
- `com.hometown.common.security.Roles` — `CUSTOMER`, `ADMIN`.
- `com.hometown.common.web.ApiException` — `ApiException.notFound(...)`, `.badRequest(...)`, etc.
- `ApiError` + `GlobalExceptionHandler` are autoconfigured — do not redefine.

## Cross-service calls
Use Feign clients (`@FeignClient(name = "PRODUCT-SERVICE")`) returning a shared `*-api` DTO.
Keep the interface in `api/` so it can later back either a Feign client or a local bean.

## REST conventions
- JSON, plural resource paths under `/api/...`.
- Validate request DTOs with `@Valid` + jakarta.validation annotations.
- Return `ResponseEntity` with correct status; throw `ApiException` for errors.
- Public (guest) endpoints: product browsing, serviceability/estimate. Everything mutating
  the user's own data requires a valid JWT; admin endpoints require `ROLE_ADMIN`.

## Multi-seller readiness
`Product`, `OrderItem`, and analytics events carry a nullable `sellerId` (default house seller).
