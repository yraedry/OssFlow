CREATE TABLE email_verification_token (
    id         BIGSERIAL   PRIMARY KEY,
    account_id BIGINT      NOT NULL REFERENCES account(id) ON DELETE CASCADE,
    token_hash VARCHAR(64) NOT NULL,
    expires_at TIMESTAMPTZ NOT NULL,
    used_at    TIMESTAMPTZ,
    CONSTRAINT uq_evtoken_hash UNIQUE (token_hash)
);
CREATE INDEX ix_evtoken_account ON email_verification_token(account_id);
CREATE INDEX ix_evtoken_expires ON email_verification_token(expires_at) WHERE used_at IS NULL;
