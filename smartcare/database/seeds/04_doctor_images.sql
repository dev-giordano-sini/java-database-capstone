-- Re-apply the demo image associations without changing other doctor fields.
BEGIN;

UPDATE doctors
SET profile_image_url = CASE email
    WHEN 'giulia.bianchi@smartcare.demo' THEN '/assets/images/giulia_bianchi.png'
    WHEN 'marco.romano@smartcare.demo' THEN '/assets/images/marco_romano.png'
    WHEN 'elena.conti@smartcare.demo' THEN '/assets/images/elena_conti.png'
END
WHERE email IN (
    'giulia.bianchi@smartcare.demo',
    'marco.romano@smartcare.demo',
    'elena.conti@smartcare.demo'
);

COMMIT;
