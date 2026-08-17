-- Development/demo data only. Run after 01_doctors.sql and 02_patients.sql.
-- Fixed timestamps make this script deterministic and safe to execute repeatedly.

BEGIN;

WITH fixtures (doctor_email, patient_email, appointment_time, status, notes) AS (
    VALUES
        ('giulia.bianchi@smartcare.demo', 'luca.verdi@smartcare.demo', TIMESTAMP '2030-06-15 09:00:00', 0, 'Routine cardiology consultation'),
        ('marco.romano@smartcare.demo', 'sara.esposito@smartcare.demo', TIMESTAMP '2030-06-15 14:00:00', 0, 'Dermatology follow-up'),
        ('elena.conti@smartcare.demo', 'andrea.neri@smartcare.demo', TIMESTAMP '2030-06-16 10:00:00', 0, 'General pediatric consultation'),
        ('giulia.bianchi@smartcare.demo', 'sara.esposito@smartcare.demo', TIMESTAMP '2030-05-10 10:00:00', 1, 'Completed consultation')
)
INSERT INTO appointments (doctor_id, patient_id, appointment_time, status, notes)
SELECT doctors.id, patients.id, fixtures.appointment_time, fixtures.status, fixtures.notes
FROM fixtures
JOIN doctors ON doctors.email = fixtures.doctor_email
JOIN patients ON patients.email = fixtures.patient_email
ON CONFLICT (doctor_id, appointment_time) DO UPDATE SET
    patient_id = EXCLUDED.patient_id,
    status = EXCLUDED.status,
    notes = EXCLUDED.notes;

COMMIT;
