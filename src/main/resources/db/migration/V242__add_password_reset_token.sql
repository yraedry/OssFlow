CREATE TABLE password_reset_token (
    id         BIGSERIAL   PRIMARY KEY,
    account_id BIGINT      NOT NULL REFERENCES account(id) ON DELETE CASCADE,
    token_hash VARCHAR(64) NOT NULL,
    expires_at TIMESTAMPTZ NOT NULL,
    used_at    TIMESTAMPTZ,
    CONSTRAINT uq_prtoken_hash UNIQUE (token_hash)
);
CREATE INDEX ix_prtoken_account ON password_reset_token(account_id);
CREATE INDEX ix_prtoken_expires ON password_reset_token(expires_at) WHERE used_at IS NULL;
