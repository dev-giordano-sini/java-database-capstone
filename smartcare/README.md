# SmartCare

SmartCare is a Java/Spring Boot application for managing patients, doctors,
appointments, and prescriptions. It demonstrates a layered backend built with
Spring MVC, Thymeleaf, Spring Data JPA, PostgreSQL, BCrypt password hashing, and
signed JWTs.

> This is an educational portfolio project, not production medical software.

## Requirements

- Docker with Docker Compose, or Java 21 + Maven 3.9 + PostgreSQL
- A Base64-encoded 256-bit JWT key

## Run with Docker

```bash
cp .env.example .env
# Replace JWT_SECRET in .env; generate one with: openssl rand -base64 32
# Replace the bootstrap administrator credentials as well.
docker compose up --build
```

The application is then available at <http://localhost:8080> and PostgreSQL is
kept inside a named Docker volume. For a quick local evaluation, copying the
environment file is optional because Compose supplies development-only defaults;
always replace those defaults outside a local machine.

### If `localhost:8080` does not open

Use the HTTP URL **after** the application log contains `Started SmartcareApplication`:

```bash
curl --fail --show-error http://localhost:8080/
docker compose ps
docker compose logs application --tail=100
```

If the application container is not running, the log normally identifies one
of these causes:

- port `8080` is already occupied; stop the other process or change the host
  side of `8080:8080` in `compose.yaml`;
- PostgreSQL uses an older named volume whose schema predates the migrations;
  apply `database/migrations/V002...V005` or, for disposable demo data, run
  `docker compose down -v` followed by `docker compose up --build`;
- when starting from an IDE instead of Compose, PostgreSQL must already be
  available at `localhost:5432` with the schema from `database/schema.sql`.

The server explicitly listens on `0.0.0.0:8080`, so it is reachable through
the Docker port mapping as well as directly from a local Java process.

## Run from IntelliJ or Maven without Docker

The default `local` Spring profile uses a persistent H2 database and therefore
does not require PostgreSQL. In IntelliJ, run `SmartcareApplication` with the
working directory set to the `smartcare` folder, or run:

```bash
cd smartcare
mvn spring-boot:run
```

Wait for `Started SmartcareApplication`, then open
<http://localhost:8080/>. Local H2 data is stored under `smartcare/data/` and
survives application restarts. The optional development console is available
at <http://localhost:8080/h2-console> with JDBC URL
`jdbc:h2:file:./data/smartcare`, username `sa`, and an empty password.

To use an existing local PostgreSQL database instead, select the `postgres`
profile in the IntelliJ run configuration:

```text
Active profiles: postgres
```

or start Maven with:

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=postgres
```

The PostgreSQL database and schema must already exist when using that profile.

## Databases

PostgreSQL is the single datastore for accounts, availability, appointments,
prescriptions, and medical reports. Docker Compose initializes new volumes from
`database/schema.sql` automatically.

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

Doctor photos are stored as URLs in PostgreSQL rather than as binary database
values. Production can use HTTPS object storage/CDN URLs, while the portfolio
fixtures use versioned `/assets/images/...` files served by Spring Boot. For an
existing local database, apply the migrations before restarting the application:

```bash
psql -U smartcare -d smartcare -f database/migrations/V002__doctor_profile_image.sql
psql -U smartcare -d smartcare -f database/migrations/V003__allow_local_doctor_images.sql
psql -U smartcare -d smartcare -f database/migrations/V004__postgres_prescriptions_and_reports.sql
psql -U smartcare -d smartcare -f database/migrations/V005__doctor_approval.sql
```

The UI lazy-loads the image and falls back to the doctor's initials when the URL
is absent or the remote object is unavailable.

### Load demo data

After creating the PostgreSQL schema, load the deterministic portfolio fixtures
in dependency order:

```bash
psql -U smartcare -d smartcare -f database/seeds/01_doctors.sql
psql -U smartcare -d smartcare -f database/seeds/02_patients.sql
psql -U smartcare -d smartcare -f database/seeds/03_appointments.sql
psql -U smartcare -d smartcare -f database/seeds/04_doctor_images.sql
psql -U smartcare -d smartcare -f database/seeds/05_clinical_records.sql
```

The image seed expects `giulia_bianchi.svg`, `marco_romano.svg`, and
`elena_conti.svg` in `src/main/resources/static/assets/images`. The scripts can
be executed repeatedly: doctors and patients are matched by
email, while appointments are matched by doctor and timestamp. The clinical
record seed adds example prescriptions and linked medical reports for the demo
appointments.

The doctor seed creates twenty profiles across multiple specialties. The public
directory displays five doctors by default, supports specialty filtering, and
allows five or ten results per page. Profiles without a photo use the bundled
`doctor_default.svg` illustration. New doctor profiles are inactive until an
administrator approves them from the doctor directory; pending profiles cannot
sign in, expose availability, or receive bookings.

Demo logins are:

- doctors: any `@smartcare.demo` doctor, password `DoctorDemo!2026`;
- patients: any `@smartcare.demo` patient, password `PatientDemo!2026`.

These credentials and records are for local development only. Remove all demo
records without touching other data with:

```bash
psql -U smartcare -d smartcare -f database/seeds/reset_demo_data.sql
```

## Web experience

The Thymeleaf interface implements the role journeys described in the project
stories:

- visitors can explore the doctor directory; self-service registration and the
  public administrator login are temporarily hidden from the home page;
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
- `/api/prescription` — PostgreSQL prescription creation and retrieval
- `/api/reports` — appointment exam reports and optional prescription links

Protected operations receive `Authorization: Bearer <token>` and validate both
the signature/expiration of the JWT and the role expected by the endpoint.

## Engineering status

The project is intentionally scoped as a portfolio backend rather than
production medical software. Automated CI runs the complete Maven verification
lifecycle on every backend change. Appointment mutations derive the patient
identity from the signed token and enforce resource ownership in the service.
