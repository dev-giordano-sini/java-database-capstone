-- Development/demo data only. Run after database/schema.sql.
-- All demo doctors use the password: DoctorDemo!2026

BEGIN;

INSERT INTO doctors (name, specialty, email, password, phone, profile_image_url, rating)
VALUES
    ('Giulia Bianchi', 'Cardiology', 'giulia.bianchi@smartcare.demo', '$2a$10$f7qkYdyY3mt8qMp8fIYDlOUPINCLnfMVTZ8FB4dD.4icde3rBaTjq', '3200000001', '/assets/images/giulia_bianchi.svg', 5),
    ('Marco Romano', 'Dermatology', 'marco.romano@smartcare.demo', '$2a$10$f7qkYdyY3mt8qMp8fIYDlOUPINCLnfMVTZ8FB4dD.4icde3rBaTjq', '3200000002', '/assets/images/marco_romano.svg', 4),
    ('Elena Conti', 'Pediatrics', 'elena.conti@smartcare.demo', '$2a$10$f7qkYdyY3mt8qMp8fIYDlOUPINCLnfMVTZ8FB4dD.4icde3rBaTjq', '3200000003', '/assets/images/elena_conti.svg', 5),
    ('Paolo Ricci', 'Neurology', 'paolo.ricci@smartcare.demo', '$2a$10$f7qkYdyY3mt8qMp8fIYDlOUPINCLnfMVTZ8FB4dD.4icde3rBaTjq', '3200000004', NULL, 4),
    ('Francesca Gallo', 'Orthopedics', 'francesca.gallo@smartcare.demo', '$2a$10$f7qkYdyY3mt8qMp8fIYDlOUPINCLnfMVTZ8FB4dD.4icde3rBaTjq', '3200000005', NULL, 5),
    ('Davide Ferri', 'Ophthalmology', 'davide.ferri@smartcare.demo', '$2a$10$f7qkYdyY3mt8qMp8fIYDlOUPINCLnfMVTZ8FB4dD.4icde3rBaTjq', '3200000006', NULL, 4),
    ('Chiara Moretti', 'Psychiatry', 'chiara.moretti@smartcare.demo', '$2a$10$f7qkYdyY3mt8qMp8fIYDlOUPINCLnfMVTZ8FB4dD.4icde3rBaTjq', '3200000007', NULL, 5),
    ('Andrea De Luca', 'Gynecology', 'andrea.deluca@smartcare.demo', '$2a$10$f7qkYdyY3mt8qMp8fIYDlOUPINCLnfMVTZ8FB4dD.4icde3rBaTjq', '3200000008', NULL, 4),
    ('Laura Riva', 'Endocrinology', 'laura.riva@smartcare.demo', '$2a$10$f7qkYdyY3mt8qMp8fIYDlOUPINCLnfMVTZ8FB4dD.4icde3rBaTjq', '3200000009', NULL, 5),
    ('Stefano Greco', 'Gastroenterology', 'stefano.greco@smartcare.demo', '$2a$10$f7qkYdyY3mt8qMp8fIYDlOUPINCLnfMVTZ8FB4dD.4icde3rBaTjq', '3200000010', NULL, 4),
    ('Martina Colombo', 'Pulmonology', 'martina.colombo@smartcare.demo', '$2a$10$f7qkYdyY3mt8qMp8fIYDlOUPINCLnfMVTZ8FB4dD.4icde3rBaTjq', '3200000011', NULL, 5),
    ('Roberto Fontana', 'Urology', 'roberto.fontana@smartcare.demo', '$2a$10$f7qkYdyY3mt8qMp8fIYDlOUPINCLnfMVTZ8FB4dD.4icde3rBaTjq', '3200000012', NULL, 4),
    ('Silvia Marchetti', 'Otolaryngology', 'silvia.marchetti@smartcare.demo', '$2a$10$f7qkYdyY3mt8qMp8fIYDlOUPINCLnfMVTZ8FB4dD.4icde3rBaTjq', '3200000013', NULL, 5),
    ('Matteo Serra', 'General Medicine', 'matteo.serra@smartcare.demo', '$2a$10$f7qkYdyY3mt8qMp8fIYDlOUPINCLnfMVTZ8FB4dD.4icde3rBaTjq', '3200000014', NULL, 4),
    ('Ilaria Martini', 'Cardiology', 'ilaria.martini@smartcare.demo', '$2a$10$f7qkYdyY3mt8qMp8fIYDlOUPINCLnfMVTZ8FB4dD.4icde3rBaTjq', '3200000015', NULL, 5),
    ('Lorenzo Villa', 'Dermatology', 'lorenzo.villa@smartcare.demo', '$2a$10$f7qkYdyY3mt8qMp8fIYDlOUPINCLnfMVTZ8FB4dD.4icde3rBaTjq', '3200000016', NULL, 4),
    ('Valentina Leone', 'Pediatrics', 'valentina.leone@smartcare.demo', '$2a$10$f7qkYdyY3mt8qMp8fIYDlOUPINCLnfMVTZ8FB4dD.4icde3rBaTjq', '3200000017', NULL, 5),
    ('Simone Costa', 'Neurology', 'simone.costa@smartcare.demo', '$2a$10$f7qkYdyY3mt8qMp8fIYDlOUPINCLnfMVTZ8FB4dD.4icde3rBaTjq', '3200000018', NULL, 4),
    ('Federica Sala', 'Orthopedics', 'federica.sala@smartcare.demo', '$2a$10$f7qkYdyY3mt8qMp8fIYDlOUPINCLnfMVTZ8FB4dD.4icde3rBaTjq', '3200000019', NULL, 5),
    ('Giorgio Bellini', 'General Medicine', 'giorgio.bellini@smartcare.demo', '$2a$10$f7qkYdyY3mt8qMp8fIYDlOUPINCLnfMVTZ8FB4dD.4icde3rBaTjq', '3200000020', NULL, 4)
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

INSERT INTO doctor_available_times (doctor_id, time_slot)
SELECT doctors.id, default_slots.time_slot
FROM doctors
CROSS JOIN (VALUES ('09:00'), ('10:00'), ('14:00')) AS default_slots(time_slot)
WHERE doctors.email LIKE '%@smartcare.demo'
ON CONFLICT (doctor_id, time_slot) DO NOTHING;

COMMIT;
