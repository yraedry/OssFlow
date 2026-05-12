-- S1.1 (sprint 2): outbox pattern para email. El registro/forgot/resend NO depende ya
-- de Resend funcionando: insertamos fila PENDING y un job programado intenta enviar
-- con backoff. Si el proveedor está caído, el usuario recibe 201 igual y el email
-- llega cuando vuelva el servicio.
CREATE TABLE IF NOT EXISTS email_outbox (
    id                BIGSERIAL PRIMARY KEY,
    account_id        BIGINT REFERENCES account(id) ON DELETE SET NULL,
    recipient         VARCHAR(254) NOT NULL,
    subject           VARCHAR(255) NOT NULL,
    body_html         TEXT NOT NULL,
    body_text         TEXT,
    status            VARCHAR(16) NOT NULL DEFAULT 'PENDING',
    attempts          INT NOT NULL DEFAULT 0,
    last_attempt_at   TIMESTAMP WITH TIME ZONE,
    last_error        TEXT,
    created_at        TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),
    sent_at           TIMESTAMP WITH TIME ZONE,
    CONSTRAINT email_outbox_status_chk CHECK (status IN ('PENDING', 'SENT', 'FAILED'))
);

CREATE INDEX IF NOT EXISTS idx_email_outbox_status_attempts ON email_outbox (status, attempts, last_attempt_at);
CREATE INDEX IF NOT EXISTS idx_email_outbox_account ON email_outbox (account_id);
