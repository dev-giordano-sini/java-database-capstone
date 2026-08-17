-- Development/demo data only. Run after database/schema.sql.
-- All demo doctors use the password: DoctorDemo!2026

BEGIN;

INSERT INTO doctors (name, specialty, email, password, phone, profile_image_url, rating)
VALUES
    ('Giulia Bianchi', 'Cardiology', 'giulia.bianchi@smartcare.demo', '$2a$10$f7qkYdyY3mt8qMp8fIYDlOUPINCLnfMVTZ8FB4dD.4icde3rBaTjq', '3200000001', '/assets/images/giulia_bianchi.png', 5),
    ('Marco Romano', 'Dermatology', 'marco.romano@smartcare.demo', '$2a$10$f7qkYdyY3mt8qMp8fIYDlOUPINCLnfMVTZ8FB4dD.4icde3rBaTjq', '3200000002', '/assets/images/marco_romano.png', 4),
    ('Elena Conti', 'Pediatrics', 'elena.conti@smartcare.demo', '$2a$10$f7qkYdyY3mt8qMp8fIYDlOUPINCLnfMVTZ8FB4dD.4icde3rBaTjq', '3200000003', '/assets/images/elena_conti.png', 5)
ON CONFLICT (email) DO UPDATE SET
    name = EXCLUDED.name,
    specialty = EXCLUDED.specialty,
    password = EXCLUDED.password,
    phone = EXCLUDED.phone,
    profile_image_url = EXCLUDED.profile_image_url,
    rating = EXCLUDED.rating;

INSERT INTO doctor_available_times (doctor_id, time_slot)
SELECT doctors.id, slots.time_slot
FROM doctors
JOIN (
    VALUES
        ('giulia.bianchi@smartcare.demo', '09:00'),
        ('giulia.bianchi@smartcare.demo', '10:00'),
        ('giulia.bianchi@smartcare.demo', '11:00'),
        ('marco.romano@smartcare.demo', '14:00'),
        ('marco.romano@smartcare.demo', '15:00'),
        ('marco.romano@smartcare.demo', '16:00'),
        ('elena.conti@smartcare.demo', '09:00'),
        ('elena.conti@smartcare.demo', '10:00'),
        ('elena.conti@smartcare.demo', '15:00')
) AS slots(email, time_slot) ON slots.email = doctors.email
ON CONFLICT (doctor_id, time_slot) DO NOTHING;

COMMIT;
