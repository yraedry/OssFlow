-- Añadir ON DELETE CASCADE a user_profile.owner_id → account(id)
ALTER TABLE user_profile DROP CONSTRAINT IF EXISTS fk_user_profile_account;
ALTER TABLE user_profile DROP CONSTRAINT IF EXISTS user_profile_owner_id_fkey;

ALTER TABLE user_profile
    ADD CONSTRAINT fk_user_profile_account
    FOREIGN KEY (owner_id) REFERENCES account(id) ON DELETE CASCADE;
