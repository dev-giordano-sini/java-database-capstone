-- Existing doctors remain visible; newly created profiles require admin approval.
ALTER TABLE doctors
    ADD COLUMN IF NOT EXISTS approved BOOLEAN NOT NULL DEFAULT FALSE;

UPDATE doctors SET approved = TRUE;
