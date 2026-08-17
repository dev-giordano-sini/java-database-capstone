-- Development/demo data only. Run after database/schema.sql.
-- All demo patients use the password: PatientDemo!2026

BEGIN;

INSERT INTO patients (name, email, password, phone, address, birthdate, alias)
VALUES
    ('Luca Verdi', 'luca.verdi@smartcare.demo', '$2a$10$xLtGnb3ckZUjW3W81dY19.Lsx15GR9zudQXbOI27V8jc2wTz1NlEe', '3300000001', 'Via Roma 10, Milano', TIMESTAMP '1988-04-12 00:00:00', 'luca-verdi'),
    ('Sara Esposito', 'sara.esposito@smartcare.demo', '$2a$10$xLtGnb3ckZUjW3W81dY19.Lsx15GR9zudQXbOI27V8jc2wTz1NlEe', '3300000002', 'Via Toledo 25, Napoli', TIMESTAMP '1992-09-23 00:00:00', 'sara-esposito'),
    ('Andrea Neri', 'andrea.neri@smartcare.demo', '$2a$10$xLtGnb3ckZUjW3W81dY19.Lsx15GR9zudQXbOI27V8jc2wTz1NlEe', '3300000003', 'Via Po 8, Torino', TIMESTAMP '2001-01-17 00:00:00', 'andrea-neri')
ON CONFLICT (email) DO UPDATE SET
    name = EXCLUDED.name,
    password = EXCLUDED.password,
    phone = EXCLUDED.phone,
    address = EXCLUDED.address,
    birthdate = EXCLUDED.birthdate,
    alias = EXCLUDED.alias;

COMMIT;
