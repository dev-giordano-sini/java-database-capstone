# SmartCare persistence schema

SmartCare uses PostgreSQL for transactional clinic data and MongoDB only for
prescription documents. The `appointmentId` stored in MongoDB is the application
reference to the PostgreSQL appointment; cross-database foreign keys are not
available, so this relationship is enforced by the service layer.

## Relationships

- one doctor has many availability slots and appointments;
- one patient has many appointments;
- one appointment can have multiple prescription documents;
- deleting a doctor or patient cascades to their appointments;
- PostgreSQL cascades relational deletes; the appointment/doctor services remove
  related MongoDB prescriptions because cross-database cascades are unavailable.

## Tables

| Table | Purpose | Important constraints |
|---|---|---|
| `admins` | Administrative accounts | unique username, BCrypt password |
| `doctors` | Doctor directory and profile | unique email/phone, rating 0–5 |
| `patients` | Patient identity and profile | unique email/phone, past birthdate |
| `doctor_available_times` | Bookable `HH:mm` slots | composite PK, FK to doctor |
| `appointments` | One-hour consultations | unique doctor/time, patient and doctor FKs |

MongoDB contains one `prescriptions` collection with a JSON Schema validator and
an index on `appointmentId`.

The canonical executable definitions are in:

- `database/create_database.sql` — creates the local role and database;
- `database/schema.sql` — creates tables, constraints and indexes;
- `database/drop_schema.sql` — development-only reset.
- `database/mongodb/create_prescriptions.js` — creates and validates the MongoDB collection;
- `database/mongodb/drop_prescriptions.js` — development-only document reset.

Hibernate runs with `ddl-auto=validate`: SQL scripts own the schema, while JPA
checks that entity mappings and the PostgreSQL structure remain aligned.
