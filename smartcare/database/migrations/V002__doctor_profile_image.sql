-- Apply once to databases created before doctor profile images were introduced.
ALTER TABLE doctors
    ADD COLUMN IF NOT EXISTS profile_image_url VARCHAR(2048);

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM pg_constraint
        WHERE conname = 'ck_doctors_profile_image_url'
          AND conrelid = 'doctors'::regclass
    ) THEN
        ALTER TABLE doctors
            ADD CONSTRAINT ck_doctors_profile_image_url CHECK (
                profile_image_url IS NULL OR profile_image_url ~ '^https://.+'
            );
    END IF;
END
$$;
