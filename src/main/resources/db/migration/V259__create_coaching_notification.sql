CREATE TABLE coaching_notification (
    id                   BIGSERIAL    PRIMARY KEY,
    recipient_account_id BIGINT       NOT NULL REFERENCES account(id) ON DELETE CASCADE,
    type                 VARCHAR(40)  NOT NULL,
    payload              TEXT,
    read                 BOOLEAN      NOT NULL DEFAULT FALSE,
    created_at           TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

CREATE INDEX ix_coaching_notification_recipient
    ON coaching_notification(recipient_account_id, read, created_at DESC);
