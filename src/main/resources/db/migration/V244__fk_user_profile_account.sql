ALTER TABLE user_profile
    ADD CONSTRAINT fk_user_profile_account
    FOREIGN KEY (owner_id) REFERENCES account(id);
