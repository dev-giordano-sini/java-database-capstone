-- Development-only reset. This permanently deletes all SmartCare data.
BEGIN;
DROP TABLE IF EXISTS medical_reports CASCADE;
DROP TABLE IF EXISTS prescriptions CASCADE;
DROP TABLE IF EXISTS appointments CASCADE;
DROP TABLE IF EXISTS doctor_available_times CASCADE;
DROP TABLE IF EXISTS patients CASCADE;
DROP TABLE IF EXISTS doctors CASCADE;
DROP TABLE IF EXISTS admins CASCADE;
COMMIT;
