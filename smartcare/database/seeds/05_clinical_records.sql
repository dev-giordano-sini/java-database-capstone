-- Development/demo prescriptions and reports. Run after 03_appointments.sql.
-- Re-running this file replaces only the clinical records attached to demo appointments.

BEGIN;

DELETE FROM medical_reports
WHERE appointment_id IN (
    SELECT appointments.id FROM appointments
    JOIN doctors ON doctors.id = appointments.doctor_id
    WHERE doctors.email LIKE '%@smartcare.demo'
);

DELETE FROM prescriptions
WHERE appointment_id IN (
    SELECT appointments.id FROM appointments
    JOIN doctors ON doctors.id = appointments.doctor_id
    WHERE doctors.email LIKE '%@smartcare.demo'
);

WITH fixtures (doctor_email, appointment_time, medication, doctor_notes, pharmacy_name) AS (
    VALUES
        ('giulia.bianchi@smartcare.demo', TIMESTAMP '2030-05-10 10:00:00', 'Atorvastatin 20 mg', 'One tablet in the evening for 30 days.', 'SmartCare Central Pharmacy'),
        ('marco.romano@smartcare.demo', TIMESTAMP '2030-06-15 14:00:00', 'Hydrocortisone cream 1%', 'Apply a thin layer twice daily for seven days.', 'San Marco Pharmacy'),
        ('elena.conti@smartcare.demo', TIMESTAMP '2030-06-16 10:00:00', 'Paracetamol 500 mg', 'Use only if fever exceeds 38 C; maximum three doses daily.', 'Family Health Pharmacy')
)
INSERT INTO prescriptions (appointment_id, patient_name, medication, doctor_notes, pharmacy_name)
SELECT appointments.id, patients.name, fixtures.medication, fixtures.doctor_notes, fixtures.pharmacy_name
FROM fixtures
JOIN doctors ON doctors.email = fixtures.doctor_email
JOIN appointments ON appointments.doctor_id = doctors.id AND appointments.appointment_time = fixtures.appointment_time
JOIN patients ON patients.id = appointments.patient_id;

WITH fixtures (doctor_email, appointment_time, medication, title, report_type, report_date, notes, file_url) AS (
    VALUES
        ('giulia.bianchi@smartcare.demo', TIMESTAMP '2030-05-10 10:00:00', 'Atorvastatin 20 mg', 'Lipid panel', 'Blood test', TIMESTAMP '2030-05-09 08:30:00', 'LDL above target; follow-up requested in three months.', 'https://demo.smartcare.local/reports/lipid-panel.pdf'),
        ('marco.romano@smartcare.demo', TIMESTAMP '2030-06-15 14:00:00', 'Hydrocortisone cream 1%', 'Dermatology examination', 'Specialist examination', TIMESTAMP '2030-06-15 14:30:00', 'Mild contact dermatitis; no warning signs observed.', NULL),
        ('elena.conti@smartcare.demo', TIMESTAMP '2030-06-16 10:00:00', 'Paracetamol 500 mg', 'Pediatric blood count', 'Blood test', TIMESTAMP '2030-06-16 09:00:00', 'Values within the expected range for age.', 'https://demo.smartcare.local/reports/pediatric-blood-count.pdf')
)
INSERT INTO medical_reports (appointment_id, prescription_id, title, report_type, report_date, notes, file_url)
SELECT appointments.id, prescriptions.id, fixtures.title, fixtures.report_type,
       fixtures.report_date, fixtures.notes, fixtures.file_url
FROM fixtures
JOIN doctors ON doctors.email = fixtures.doctor_email
JOIN appointments ON appointments.doctor_id = doctors.id AND appointments.appointment_time = fixtures.appointment_time
LEFT JOIN prescriptions ON prescriptions.appointment_id = appointments.id AND prescriptions.medication = fixtures.medication;

COMMIT;
