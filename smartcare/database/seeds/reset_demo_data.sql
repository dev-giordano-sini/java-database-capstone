-- Development/demo data only. Removes only records identified by the demo domain.

BEGIN;

DELETE FROM appointments
WHERE doctor_id IN (SELECT id FROM doctors WHERE email LIKE '%@smartcare.demo')
   OR patient_id IN (SELECT id FROM patients WHERE email LIKE '%@smartcare.demo');

DELETE FROM patients WHERE email LIKE '%@smartcare.demo';
DELETE FROM doctors WHERE email LIKE '%@smartcare.demo';

COMMIT;
