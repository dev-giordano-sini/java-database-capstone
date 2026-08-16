# SmartCare

SmartCare is a Java/Spring Boot application for managing patients, doctors,
appointments, and prescriptions. It demonstrates a layered backend built with
Spring MVC, Spring Data JPA, PostgreSQL, BCrypt password hashing, and signed JWTs.

> This is an educational portfolio project, not production medical software.

## Requirements

- Docker with Docker Compose, or Java 21 + Maven 3.9 + PostgreSQL
- A Base64-encoded 256-bit JWT key

## Run with Docker

```bash
cp .env.example .env
# Replace JWT_SECRET in .env; generate one with: openssl rand -base64 32
docker compose up --build
```

The application is then available at <http://localhost:8080> and PostgreSQL is
kept inside a named Docker volume.

## Run locally

```bash
export DB_URL=jdbc:postgresql://localhost:5432/smartcare
export DB_USERNAME=smartcare
export DB_PASSWORD=smartcare
export JWT_SECRET="$(openssl rand -base64 32)"
mvn spring-boot:run
```

Configuration is supplied through environment variables; secrets are not
committed to source control. Hibernate uses `update` only as a convenient local
default. A production deployment should use database migrations.

## Verify

```bash
mvn clean verify
docker compose config
```

Tests use an in-memory H2 database in PostgreSQL compatibility mode, so they do
not require a running PostgreSQL instance.

## Main API areas

- `/api/admin` — administrator authentication and operations
- `/api/doctor` — doctor login, availability, and management
- `/patient` — patient registration, login, records, and filters
- `/appointments` — appointment search, booking, update, and cancellation
- `/api/prescription` — prescription creation and retrieval

Protected operations validate both the signature/expiration of the JWT and the
role expected by the endpoint.
