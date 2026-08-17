# SmartCare

SmartCare is a Java/Spring Boot application for managing patients, doctors,
appointments, and prescriptions. It demonstrates a layered backend built with
Spring MVC, Thymeleaf, Spring Data JPA, PostgreSQL, BCrypt password hashing, and
signed JWTs.

> This is an educational portfolio project, not production medical software.

## Requirements

- Docker with Docker Compose, or Java 21 + Maven 3.9 + PostgreSQL + MongoDB
- A Base64-encoded 256-bit JWT key

## Run with Docker

```bash
cp .env.example .env
# Replace JWT_SECRET in .env; generate one with: openssl rand -base64 32
# Replace the bootstrap administrator credentials as well.
docker compose up --build
```

The application is then available at <http://localhost:8080> and PostgreSQL is
kept inside a named Docker volume.

## Databases

PostgreSQL stores admins, doctors, patients, availability, and appointments.
MongoDB is used only for prescription documents. Docker Compose initializes new
volumes from `database/schema.sql` and
`database/mongodb/create_prescriptions.js` automatically.

For a PostgreSQL installation outside Docker, run:

```bash
psql -U postgres -f database/create_database.sql
psql -U smartcare -d smartcare -f database/schema.sql
```

The first command creates the development role/database idempotently using
`psql`'s `\gexec`. Change the development password in that file for any shared
environment. To reset a local schema deliberately:

```bash
psql -U smartcare -d smartcare -f database/drop_schema.sql
psql -U smartcare -d smartcare -f database/schema.sql
```

Hibernate uses `ddl-auto=validate`; the SQL schema is authoritative and the
application fails fast when entity mappings drift from it.

### Load demo data

After creating the PostgreSQL schema, load the deterministic portfolio fixtures
in dependency order:

```bash
psql -U smartcare -d smartcare -f database/seeds/01_doctors.sql
psql -U smartcare -d smartcare -f database/seeds/02_patients.sql
psql -U smartcare -d smartcare -f database/seeds/03_appointments.sql
```

The scripts can be executed repeatedly: doctors and patients are matched by
email, while appointments are matched by doctor and timestamp. Demo logins are:

- doctors: any `@smartcare.demo` doctor, password `DoctorDemo!2026`;
- patients: any `@smartcare.demo` patient, password `PatientDemo!2026`.

These credentials and records are for local development only. Remove all demo
records without touching other data with:

```bash
psql -U smartcare -d smartcare -f database/seeds/reset_demo_data.sql
```

For MongoDB outside Docker, create the validated collection and index with:

```bash
mongosh mongodb://localhost:27017/smartcare database/mongodb/create_prescriptions.js
```

To reset only prescription documents in development:

```bash
mongosh mongodb://localhost:27017/smartcare database/mongodb/drop_prescriptions.js
mongosh mongodb://localhost:27017/smartcare database/mongodb/create_prescriptions.js
```

## Web experience

The Thymeleaf interface implements the role journeys described in the project
stories:

- visitors can explore the doctor directory and patients can register;
- patients can sign in, inspect their profile and appointments, book available
  one-hour slots, and cancel their own bookings;
- doctors can inspect a dated schedule, review patient contact details, manage
  availability, and record prescriptions;
- administrators can manage the doctor directory and review monthly appointment
  activity;
- every dashboard clears the browser session on logout.

The interface is responsive and progressively reports loading, empty, success,
and error states. Protected API calls send the JWT in the `Authorization` header.

## Run locally

```bash
export DB_URL=jdbc:postgresql://localhost:5432/smartcare
export DB_USERNAME=smartcare
export DB_PASSWORD=smartcare
export MONGODB_URI=mongodb://localhost:27017/smartcare
export JWT_SECRET="$(openssl rand -base64 32)"
export ADMIN_USERNAME=admin
export ADMIN_PASSWORD='replace-with-a-strong-password'
mvn spring-boot:run
```

Configuration is supplied through environment variables; secrets are not
committed to source control. A production deployment should evolve the same
schema through versioned migrations rather than editing an applied script.

## Verify

```bash
mvn clean verify
docker compose config
```

## Startup troubleshooting

If Hibernate reports `Unable to determine Dialect without JDBC metadata`, first
verify that PostgreSQL is running and reachable with the configured `DB_URL`:

```bash
docker compose ps
docker compose logs database
docker compose exec database pg_isready -U smartcare -d smartcare
```

The application explicitly configures the PostgreSQL JDBC driver and dialect,
but it still requires a reachable database and the schema created by
`database/schema.sql`. When switching from an older schema during local
development, recreate the disposable volumes with `docker compose down -v`
before running `docker compose up --build` again. This deletes local data.

Tests use an in-memory H2 database in PostgreSQL compatibility mode, so they do
not require a running PostgreSQL instance.

## Main API areas

- `/api/admin` — administrator authentication and operations
- `/api/doctor` — doctor login, availability, and management
- `/api/patients` — patient registration, login, records, and filters
- `/api/appointments` — appointment search, booking, update, and cancellation
- `/api/prescription` — prescription creation and retrieval

Protected operations receive `Authorization: Bearer <token>` and validate both
the signature/expiration of the JWT and the role expected by the endpoint.

## Engineering status

The project is intentionally scoped as a portfolio backend rather than
production medical software. Automated CI runs the complete Maven verification
lifecycle on every backend change. Appointment mutations derive the patient
identity from the signed token and enforce resource ownership in the service.
