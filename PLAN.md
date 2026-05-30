# HomeTown — Architecture & Build Plan

**HomeTown** is an e-commerce platform for **handmade craft art and home-decor** products.
Responsive web app (laptop browser + phone browser), Java microservices backend, Angular frontend.

---

## 1. Tech stack

| Layer | Choice |
|---|---|
| Backend language | Java 21 |
| Framework | Spring Boot 3.x + Spring Cloud |
| Service discovery | Eureka (Netflix) |
| API Gateway | Spring Cloud Gateway |
| Inter-service calls | OpenFeign |
| Persistence | Spring Data JPA + **PostgreSQL** (schema-per-service) |
| DB migrations | **Flyway** — each module owns versioned migrations that create & evolve its schema |
| Security | Spring Security + JWT (access + refresh tokens) |
| Build | Maven (multi-module repo) |
| Frontend | Angular 17+ (standalone components), **Angular Material + CDK BreakpointObserver**, responsive SCSS |
| Metrics | Spring Boot Actuator + Micrometer |
| Monitoring | **Prometheus** (scrape) + **Grafana** (dashboards) |
| Payments | **Mock/sandbox** gateway (swappable for Razorpay/Stripe later) |
| Local hosting | Docker Compose (full microservices: Eureka + gateway + all services) |
| Cloud hosting | **Heroku** — modular monolith bundled into 1–2 dynos to start, split later |
| Image storage | **Cloudinary** (Heroku filesystem is ephemeral) |
| Cloud monitoring | **Grafana Cloud** (hosted) on Heroku; self-hosted Prometheus+Grafana for local |
| Run modes | Spring profiles: `local` / `microservices` / `heroku` |

---

## 2. Key product decisions

- **Single-seller now, multi-seller later.** Nullable `sellerId` on `Product`, `OrderItem`, and analytics
  events, defaulting to the HomeTown house seller. Becomes a marketplace without a rewrite.
- **Admin = role inside `user-service`** (`CUSTOMER` / `ADMIN`), not a separate auth service.
- **Guest cart in browser localStorage**, flushed/merged into `cart-service` on login.
- **Seller metrics ("user visits") pre-planned** via a dedicated `analytics-service` + Grafana.

---

## 3. Architecture

```
 Browser (laptop/phone) ──▶ API Gateway ──▶ ┌ user-service       (users_db)
                                            ├ product-service    (products_db)
                                            ├ cart-service       (carts_db)
                                            ├ order-service       (orders_db)
                                            ├ payment-service    (payments_db)
                                            ├ shipping-service   (shipping_db)
                                            └ analytics-service  (analytics_db)
                              all register with Eureka
                                                   │
                        Prometheus ◀──scrapes all──┘
                              │
                           Grafana  (Platform Health + Seller Metrics dashboards)
```

---

## 4. Services

### discovery-server (Eureka)
Service registry; all services register and discover each other.

### api-gateway (Spring Cloud Gateway)
Single entry point for the Angular app. Routes `/api/users/**`, `/api/products/**`, `/api/cart/**`,
`/api/orders/**`, `/api/payments/**`, `/api/analytics/**`. Handles CORS and JWT validation.

### user-service  (user-registration, user-authentication, admin-user, admin-auth)
- `User` (id, name, email, passwordHash, role `CUSTOMER`/`ADMIN`, createdAt)
- Register, login (issues JWT), refresh token, profile. Admin = `ADMIN` role.

### product-service  (product-model, admin-management of products)
- `Product` (id, name, description, price, discountPercent, category, imageUrls, stock, active, `sellerId` nullable)
- `Category`
- Public: list/search/filter/view (guests allowed)
- ADMIN-only: create/update/delete, upload description, set category/pricing/discount

### cart-service  (cart-service + guest flush)
- `Cart`, `CartItem` (productId, qty)
- Guest cart in localStorage → on login, frontend POSTs it → service merges into persisted cart → localStorage cleared

### order-service  (order-model)
- `Order` (id, userId, status, totalAmount, createdAt, shippingAddress, destPincode,
  shippingPartner, shippingCost, estimatedDeliveryDays), `OrderItem` (productId, qty, price, `sellerId` nullable)
- Builds order from cart (Feign → product-service for price/stock), gets shipping quote
  (Feign → shipping-service), adds shipping cost to total, triggers payment

### payment-service  (payment-model, mock)
- `Payment` (id, orderId, amount, status, mockTransactionId)
- Simulates success/failure; swappable for a real provider

### shipping-service  (multi-partner, pluggable — pre-planned for real partners)
- **Provider abstraction** so any number of partners (Delhivery, Shiprocket, BlueDart, India Post, ...)
  plug in behind one interface; checkout never changes when a partner is added:
  ```
  interface ShippingProvider {
    boolean          isServiceable(String destPincode);             // delivery possible or not
    DeliveryEstimate estimate(String destPincode, Parcel parcel);   // ETA days to that PIN
    Money            calculateCharge(String destPincode, Parcel p); // charges per partner
    String           name();                                        // "DELHIVERY", "MOCK", ...
  }
  ```
- `ShippingService` queries all registered providers → returns ranked options `{partner, serviceable, eta, charge}`.
- Endpoints:
  - `GET  /api/shipping/serviceability?pincode=`            → verify delivery possible or not
  - `GET  /api/shipping/estimate?pincode=&weight=`          → delivery-time estimate to that PIN
  - `POST /api/shipping/quotes`                             → compare all partners (eta + charge each)
- Owns `shipping_db`: partner config, rate cards / zone matrix, serviceability cache, `Shipment` records.
- **Now:** one `MockShippingProvider` (zone/flat-rate by PIN prefix + simple ETA). Real partners added as
  new `ShippingProvider` beans later — zero checkout changes.

### analytics-service  (seller metrics / user visits)
- `analytics_db` stores visit/interaction events: `PRODUCT_VIEW`, `CATALOG_VIEW`, `ADD_TO_CART`, `CHECKOUT`
  with productId, sellerId, category, guest-or-user, timestamp, sessionId, referrer
- Query API: `/api/analytics/seller/{id}/...` → powers in-app Seller Metrics page
- Micrometer counters (`hometown_product_views_total{product,seller,category}`, `hometown_unique_visitors`)
  → scraped by Prometheus → Grafana "Seller Metrics" dashboard
- Frontend fires fire-and-forget visit events (never blocks rendering)

---

## 5. Key flows

- **Auth (JWT):** register/login → access + refresh token → Angular stores → gateway validates each call;
  `ADMIN` role unlocks admin endpoints.
- **Guest cart flush:** guest adds to cart → localStorage; on login → POST to cart-service → merge → clear local.
- **Shipping check:** product/cart → shipping-service serviceability + ETA for destination PIN (before buying).
- **Checkout:** cart → shipping quotes (pick partner) → order-service creates order (+ shipping cost)
  → payment-service (mock) confirms → order `PAID`.
- **Visit tracking:** browser → gateway → analytics-service → analytics_db + Micrometer → Prometheus → Grafana.

---

## 6. Frontend (responsive — laptop + phone)

Angular standalone + Angular Material + CDK `BreakpointObserver` (switches layout, not just resizes).

| Screen | Layout |
|---|---|
| Desktop `>1024px` | Top app-bar nav + sidebar category filters + 3–4 column product grid |
| Tablet `601–1024px` | Collapsible filters + 2-column grid |
| Phone `≤600px` | Hamburger menu, single/2-col grid, sticky bottom nav (Home/Search/Cart/Account) + bottom cart bar |

Pages: Home/catalog, Product detail, Cart, Checkout, Login/Register, Account/orders,
Admin dashboard (product CRUD, categories, pricing/discount, orders), Seller Metrics.
Guest browsing fully supported; cart in localStorage until login.

---

## 7. Repo structure

```
HomeTown/
├─ backend/
│  ├─ common/                 (shared: JWT, DTOs, error handling, config)
│  ├─ discovery-server/       (Eureka — local microservices mode)
│  ├─ api-gateway/            (gateway — local; optional Heroku dyno)
│  ├─ hometown-api/           (aggregator: bundles modules into 1–2 dynos for Heroku)
│  ├─ user-service/           (+ user-api contract module)
│  ├─ product-service/        (+ product-api)
│  ├─ cart-service/           (+ cart-api)
│  ├─ order-service/          (+ order-api)
│  ├─ payment-service/        (+ payment-api)
│  ├─ shipping-service/       (+ shipping-api, ShippingProvider abstraction)
│  ├─ analytics-service/      (+ analytics-api)
│  └─ pom.xml                 (parent multi-module)
│     each module: src/main/resources/db/migration/<module>/  (Flyway)
├─ frontend/                  (Angular app)
├─ monitoring/
│  ├─ prometheus/prometheus.yml
│  └─ grafana/provisioning/dashboards/   (Seller Metrics + Platform Health)
├─ Procfile                   (Heroku web dyno)
├─ heroku.yml                 (Heroku container deploy)
├─ docker-compose.yml         (postgres + services + prometheus + grafana)
├─ PLAN.md
└─ README.md
```

---

## 8. Build milestones

1. **Phase 1 — Foundation:** repo + parent pom, discovery-server, api-gateway, docker-compose w/ Postgres,
   Actuator/Micrometer wired into every service from the start. *(runnable skeleton)*
2. **Phase 2 — user-service:** registration, JWT auth, roles (customer/admin).
3. **Phase 3 — product-service:** product + category CRUD (admin-guarded), public browsing, nullable sellerId.
4. **Phase 4 — cart-service:** persisted cart + guest-flush endpoint.
5. **Phase 5 — order-service + payment-service + shipping-service:** checkout + mock payment +
   pluggable shipping (serviceability, ETA, charges) with a mock provider.
6. **Phase 5.5 — analytics-service:** visit ingestion + Micrometer counters + seller query API.
7. **Phase 6 — Angular frontend:** responsive catalog, cart (localStorage→flush), auth, checkout.
8. **Phase 7 — Admin dashboard + Seller Metrics page.**
9. **Phase 8 — Monitoring:** Prometheus + Grafana provisioned dashboards; frontend wires visit events.
10. **Phase 9 — Heroku deployment:** Procfile/Dockerfile, `heroku` profile, Heroku Postgres + Flyway,
    Cloudinary, Grafana Cloud, CI/CD.
11. **Phase 10 — Polish:** full docker-compose run, README, seed data.

---

## 9. Deployment strategy (local + Heroku) — "fewer dynos now, split later"

HomeTown is built as a **modular monolith that is ready to split**: every service is its own Maven module
with clean boundaries, but it can run two ways via Spring profiles — no code changes to switch.

| | Local dev (`microservices` profile) | Heroku now (`heroku` profile) | Heroku later |
|---|---|---|---|
| Topology | every module = own Spring Boot app | modules **bundled into 1–2 dynos** | split hot modules into own apps |
| Entry | api-gateway + Eureka | `hometown-api` (one web dyno) [+ optional gateway dyno] | gateway routes to split apps |
| Discovery | Eureka | none needed (in-process) → later **env-var service URLs** | env-var URLs per app |
| Inter-service calls | Feign over HTTP | resolve to **in-process beans** | Feign over HTTP |
| DB | one Postgres, schema-per-service | one Heroku Postgres, schema-per-service | add Postgres add-ons as apps split |
| Monitoring | Prometheus + Grafana containers | Grafana Cloud (remote-write) | Grafana Cloud |
| Images | local disk / MinIO | Cloudinary | Cloudinary |

**Split-later mechanism (the important pre-planning):** each module exposes its contract as a Java interface
in a shared `*-api` module. Consumers autowire that interface. A Spring profile decides the binding:
- `microservices` → a **Feign client** implementation (HTTP call to another app)
- `heroku`/`local-monolith` → the **local bean** implementation (same JVM)

So promoting `product` (or any module) to its own dyno is a config change, not a rewrite.

### Heroku specifics (baked in from the start)
- App binds to **`$PORT`** (`server.port=${PORT:8080}`).
- Config via **env vars**: `DATABASE_URL`, `JWT_SECRET`, `CLOUDINARY_URL`, `GRAFANA_CLOUD_*`, partner keys.
- **Procfile** (`web: java -jar hometown-api.jar`) or container deploy via `heroku.yml` + Dockerfile.
- Heroku Postgres `DATABASE_URL` parsed into Spring datasource; **Flyway runs migrations on boot**.

---

## 10. Flyway migrations (schema-per-service)

- Each module ships its own migrations under `src/main/resources/db/migration/<module>/`
  (e.g. `V1__create_users_schema.sql`, `V2__add_refresh_token.sql`).
- Each migration set targets and creates its **own schema** (`users`, `products`, `carts`, `orders`,
  `payments`, `shipping`, `analytics`) so logical isolation holds even on one Postgres.
- Flyway configured per module with its schema + migration location; Hibernate `ddl-auto=validate`
  (Flyway owns the schema, never Hibernate auto-DDL).
- Same migrations run locally (docker Postgres) and on Heroku Postgres — reproducible everywhere.

---

## 11. Configuration & secrets

| Concern | Local | Heroku |
|---|---|---|
| Active profile | `local` / `microservices` | `heroku` |
| DB | docker Postgres, schema-per-service | Heroku Postgres add-on (`DATABASE_URL`) |
| JWT secret | `application-local.yml` | `JWT_SECRET` env var |
| Images | MinIO / local | `CLOUDINARY_URL` env var |
| Monitoring | Prometheus + Grafana containers | `GRAFANA_CLOUD_*` env vars |
| Shipping/payment keys | mock (no keys) | env vars when real providers added |

No secrets in git; all sensitive config via env vars / Heroku config vars.

---

## 12. Future integrations roadmap (pre-planned, build later)

- **Real shipping partners** → add `ShippingProvider` beans (Delhivery, Shiprocket, BlueDart) behind the
  existing interface; no checkout change.
- **Real payments** → replace mock with Razorpay/Stripe behind a `PaymentGateway` interface.
- **Multi-seller** → activate the nullable `sellerId`: add Seller entity, seller onboarding/auth,
  per-seller metrics scoping.
- **Split to true microservices** → flip profile to `microservices`, deploy hot modules as own Heroku apps.
- **CI/CD** → GitHub → Heroku pipeline (review apps → staging → production).
