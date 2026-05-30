# HomeTown

E-commerce platform for handmade craft art and home-decor products. Responsive web app
(laptop + phone), Java/Spring microservices backend, Angular frontend.

See [PLAN.md](PLAN.md) for the full architecture and roadmap.

## Stack
- Java 17, Spring Boot 3.3, Spring Cloud 2023
- PostgreSQL (schema-per-service) + Flyway
- Eureka + Spring Cloud Gateway (local microservices mode)
- Angular 17+ + Angular Material (responsive)
- Prometheus + Grafana (metrics / seller analytics)
- Heroku-ready: bundled into `hometown-api` (1–2 dynos), split to microservices later

## Backend modules (`backend/`)
| Module | Purpose |
|---|---|
| `common` | Shared JWT, error handling, constants |
| `discovery-server` | Eureka registry (local mode) |
| `api-gateway` | Single entry point: routing, CORS, JWT |
| `hometown-api` | Aggregator: bundles feature modules for Heroku |
| `user-service` | Registration, auth (JWT), roles (customer/admin) |
| `product-service` | Products, categories, pricing, discount |
| `cart-service` | Cart + guest-cart flush on login |
| `order-service` | Orders, checkout |
| `payment-service` | Mock payment gateway |
| `shipping-service` | Pluggable multi-partner shipping (mock now) |
| `analytics-service` | User-visit / seller metrics |

## Run locally

Start infra (Postgres + Prometheus + Grafana):
```
docker compose up -d
```

Build the backend:
```
cd backend
mvn clean install
```

Run a service (example):
```
mvn -pl discovery-server spring-boot:run
mvn -pl api-gateway spring-boot:run
```

- Eureka: http://localhost:8761
- Gateway: http://localhost:8080
- Grafana: http://localhost:3000 (admin/admin)
- Prometheus: http://localhost:9090

## Profiles
- `local` / `microservices` — full microservices via Eureka + gateway
- `heroku` — bundled aggregator, env-var config, `$PORT`
