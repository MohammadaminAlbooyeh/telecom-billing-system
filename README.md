# Telecom Billing System

A Spring Boot-based telecom billing platform that ingests Call Detail Records (CDRs), rates usage against subscriber plans, meters usage, generates monthly invoices, and manages subscriber balances.

## Tech Stack

- **Java 21** / **Spring Boot 3.3.5**
- **Spring Data JPA** + **Hibernate** (entity schema)
- **Flyway** (DB schema migrations)
- **Spring Batch** (monthly invoice batch job)
- **Apache Kafka** (CDR event messaging)
- **Springdoc OpenAPI** (Swagger UI)
- **H2** (default, in-memory) / **PostgreSQL** (via `postgres` profile)
- **Maven** (build)

## Project Structure

```
src/main/java/com/tbs/
├── batch/        Spring Batch job, tasklets, item writer
├── cdr/          CDR parser, validator, dedup, ingestion
├── config/       Batch, Jackson, Kafka, Swagger config
├── controller/   REST controllers (Subscriber, Plan, Usage, Invoice)
├── exception/    Custom exceptions + global handler
├── messaging/    Kafka consumer / producer
├── model/        entities, DTOs, enums
├── rating/       Rating engine + strategies (Call, Data, SMS, Tiered)
├── repository/   Spring Data repositories
├── service/      Business services
└── util/         Utility helpers (e.g. invoice number generator)
```

## Prerequisites

- Java 21+
- Maven 3.9+
- Optional: Docker (for the full Postgres + Kafka stack)

## Running Locally

### Default (H2 in-memory, no external dependencies)

```bash
mvn spring-boot:run
```

- Starts on `http://localhost:8080`
- Uses an in-memory H2 database with `ddl-auto: create-drop`
- Kafka is configured but not exercised (`messaging` classes are currently stubs)

The app boots with an empty schema; entities and data are created via the API.

### PostgreSQL + Flyway profile

Activate the `postgres` profile, which uses Flyway migrations and `ddl-auto: validate`:

```bash
SPRING_PROFILES_ACTIVE=postgres \
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/telecom_billing \
SPRING_DATASOURCE_USERNAME=postgres \
SPRING_DATASOURCE_PASSWORD=postgres \
mvn spring-boot:run
```

> The `postgres` profile enables Flyway, which applies the `V1..V7` schema migrations at startup. Hibernate `validate` then confirms the schema matches the JPA entities. Always let Flyway create the schema against an empty database.

## Running with Docker (recommended)

The `docker/docker-compose.yml` provisions **PostgreSQL**, a **single-node Kafka (KRaft)**, and **builds + runs the application**:

```bash
docker compose -f docker/docker-compose.yml up --build
```

This starts:
- `postgres` on `localhost:5432` (db: `telecom_billing`, user/pass: `postgres`/`postgres`)
- `kafka` on `localhost:9092`
- `app` on `http://localhost:8080` (with `postgres` profile + Flyway migrations applied)

Stop everything with:

```bash
docker compose -f docker/docker-compose.yml down
```

Add `-v` to also remove the Postgres data volume:
```bash
docker compose -f docker/docker-compose.yml down -v
```

## REST API

Interactive docs (OpenAPI / Swagger UI): **`http://localhost:8080/swagger-ui.html`**

### Subscribers — `/api/subscribers`
| Method | Path | Description |
|--------|------|-------------|
| POST | `/api/subscribers` | Register a subscriber |
| GET | `/api/subscribers` | List all subscribers |
| GET | `/api/subscribers/{id}` | Get subscriber by id |
| GET | `/api/subscribers/msisdn/{msisdn}` | Get subscriber by MSISDN |
| PUT | `/api/subscribers/{id}` | Update subscriber |
| PATCH | `/api/subscribers/{id}/active?active=` | Activate/deactivate |
| POST | `/api/subscribers/{id}/topup` | Top up prepaid balance |
| GET | `/api/subscribers/{id}/balance` | Get subscriber balance |

### Plans — `/api/plans`
| Method | Path | Description |
|--------|------|-------------|
| POST | `/api/plans` | Create a plan |
| GET | `/api/plans` | List all plans |
| GET | `/api/plans/{id}` | Get plan by id |
| GET | `/api/plans/name/{name}` | Get plan by name |
| GET | `/api/plans/type/{planType}` | Get plans by type (`PREPAID`/`POSTPAID`) |
| PUT | `/api/plans/{id}` | Update a plan |
| DELETE | `/api/plans/{id}` | Delete a plan |

### Usage & Rating — `/api/usage`
| Method | Path | Description |
|--------|------|-------------|
| POST | `/api/usage/cdr` | Ingest raw CDR string |
| POST | `/api/usage` | Ingest a CDR (JSON) |
| POST | `/api/usage/batch` | Ingest many CDRs |
| POST | `/api/usage/batch/raw` | Ingest raw CDR batch |
| GET | `/api/usage/subscribers/{id}/records?from=&to=` | Usage records in date range |
| GET | `/api/usage/subscribers/{id}/summary?from=&to=` | Usage summary in date range |
| GET | `/api/usage/subscribers/{id}/unrated-count` | Count of unrated records |
| POST | `/api/usage/{usageRecordId}/rate` | Rate one usage record |
| POST | `/api/usage/subscribers/{id}/rate` | Rate all pending for a subscriber |
| POST | `/api/usage/rate-all` | Rate all pending records |

### Invoices — `/api/invoices`
| Method | Path | Description |
|--------|------|-------------|
| POST | `/api/invoices/generate?subscriberId=&periodStart=&periodEnd=` | Generate an invoice |
| POST | `/api/invoices/generate-all?periodStart=&periodEnd=` | Generate invoices for all subscribers |
| GET | `/api/invoices/subscribers/{subscriberId}` | Invoices for a subscriber |
| GET | `/api/invoices/{invoiceNumber}` | Invoice by invoice number |

## Database Migrations

Flyway migrations live in `src/main/resources/db/migration/`:

| Version | Table |
|---------|-------|
| V1 | `subscribers` |
| V2 | `plans` + FK from `subscribers.plan_id` |
| V3 | `usage_records` |
| V4 | `invoices` |
| V5 | `balances` |
| V6 | `rated_usage` |
| V7 | `invoice_line_items` |

## Useful Actuator Endpoints

- `http://localhost:8080/actuator/health`
- `http://localhost:8080/actuator/info`

## Notes / Current Limitations

- Kafka producer/consumer classes are empty stubs; message-based CDR ingestion is not yet wired end-to-end.
- The default H2 profile uses `create-drop`; use the `postgres` profile (or Docker) for persistent schema managed by Flyway.

