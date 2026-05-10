CREATE TABLE account (
    id               BIGSERIAL PRIMARY KEY,
    email            VARCHAR(254) NOT NULL,
    password_hash    VARCHAR(72),
    provider         VARCHAR(20)  NOT NULL DEFAULT 'LOCAL',
    provider_id      VARCHAR(255),
    email_verified   BOOLEAN      NOT NULL DEFAULT FALSE,
    token_version    INTEGER      NOT NULL DEFAULT 0,
    created_at       TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at       TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    CONSTRAINT uq_account_email       UNIQUE (email),
    CONSTRAINT uq_account_provider_id UNIQUE (provider, provider_id),
    CONSTRAINT ck_account_auth        CHECK (password_hash IS NOT NULL OR provider_id IS NOT NULL)
);
CREATE INDEX ix_account_email ON account(email);

INSERT INTO account (id, email, password_hash, email_verified, token_version)
OVERRIDING SYSTEM VALUE
VALUES (1, 'dev@ossflow.local', '$2a$12$ubsm5H.fe5sfCo6olxbRwO6UTIJhlGhlgqHzXtg8FmRZqz90z/1R.', TRUE, 0);

SELECT setval('account_id_seq', (SELECT MAX(id) FROM account));
