-- Allow versioned application assets as well as external HTTPS object storage.
ALTER TABLE doctors
    DROP CONSTRAINT IF EXISTS ck_doctors_profile_image_url;

ALTER TABLE doctors
    ADD CONSTRAINT ck_doctors_profile_image_url CHECK (
        profile_image_url IS NULL
        OR profile_image_url ~ '^https://.+'
        OR profile_image_url ~ '^/assets/images/[A-Za-z0-9_-]+\.(png|jpg|jpeg|webp)$'
    );
