# SmartCare PostgreSQL schema

SmartCare uses a single PostgreSQL database. Prescriptions are relational and
belong to appointments; MongoDB is not required.

## Relationships

- one doctor has many availability slots and appointments;
- one patient has many appointments;
- one appointment can have multiple prescription records;
- deleting a doctor or patient cascades to their appointments;
- deleting an appointment cascades to its prescriptions.

## Tables

| Table | Purpose | Important constraints |
|---|---|---|
| `admins` | Administrative accounts | unique username, BCrypt password |
| `doctors` | Doctor directory and profile | unique email/phone, rating 0–5 |
| `patients` | Patient identity and profile | unique email/phone, past birthdate |
| `doctor_available_times` | Bookable `HH:mm` slots | composite PK, FK to doctor |
| `appointments` | One-hour consultations | unique doctor/time, patient and doctor FKs |
| `prescriptions` | Medication recorded for an appointment | FK to appointment |

The canonical executable definitions are in:

- `database/create_database.sql` — creates the local role and database;
- `database/schema.sql` — creates tables, constraints and indexes;
- `database/drop_schema.sql` — development-only reset.

Hibernate runs with `ddl-auto=validate`: SQL scripts own the schema, while JPA
checks that entity mappings and the PostgreSQL structure remain aligned.
