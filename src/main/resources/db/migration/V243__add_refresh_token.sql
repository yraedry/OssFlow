CREATE TABLE refresh_token (
    id            BIGSERIAL   PRIMARY KEY,
    account_id    BIGINT      NOT NULL REFERENCES account(id) ON DELETE CASCADE,
    token_hash    VARCHAR(64) NOT NULL,
    token_version INTEGER     NOT NULL,
    expires_at    TIMESTAMPTZ NOT NULL,
    created_at    TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    revoked_at    TIMESTAMPTZ,
    CONSTRAINT uq_rftoken_hash UNIQUE (token_hash)
);
CREATE INDEX ix_rftoken_account ON refresh_token(account_id) WHERE revoked_at IS NULL;
