# SmartCare persistence schema

SmartCare uses PostgreSQL as its single datastore. Keeping appointments,
prescriptions, and medical reports in one relational database provides atomic
transactions, foreign keys, and database-level cascade behavior.

## Relationships

- one doctor has many availability slots and appointments;
- one patient has many appointments;
- one appointment can have multiple prescriptions and medical reports;
- one medical report can optionally reference the prescription associated with
  the exam outcome;
- deleting an appointment cascades to its prescriptions and reports; deleting a
  referenced prescription keeps the report and clears `prescription_id`.

## Tables

| Table | Purpose | Important constraints |
|---|---|---|
| `admins` | Administrative accounts | unique username, BCrypt password |
| `doctors` | Doctor directory and profile | unique email/phone, rating 0–5 |
| `patients` | Patient identity and profile | unique email/phone, past birthdate |
| `doctor_available_times` | Bookable `HH:mm` slots | composite PK, FK to doctor |
| `appointments` | One-hour consultations | unique doctor/time, patient and doctor FKs |
| `prescriptions` | Medication instructions | appointment FK with cascade delete |
| `medical_reports` | Exam reports and document references | appointment FK, optional prescription FK |

The canonical executable definitions are in `database/schema.sql`; additive
changes for existing installations are under `database/migrations/`. Hibernate
runs with `ddl-auto=validate`, so SQL owns the schema and JPA checks alignment.
