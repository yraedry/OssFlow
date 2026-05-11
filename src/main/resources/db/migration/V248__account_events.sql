-- S1.5: Audit log de eventos de cuenta para seguridad y cumplimiento.
CREATE TABLE account_events (
    id         BIGSERIAL PRIMARY KEY,
    account_id BIGINT       NOT NULL REFERENCES account(id) ON DELETE CASCADE,
    event_type VARCHAR(50)  NOT NULL,
    ip_address VARCHAR(45),
    user_agent VARCHAR(255),
    created_at TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_account_events_account_id ON account_events (account_id);
CREATE INDEX idx_account_events_created_at ON account_events (created_at);
