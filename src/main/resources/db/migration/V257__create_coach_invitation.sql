CREATE TABLE coach_invitation (
    id          BIGSERIAL    PRIMARY KEY,
    coach_id    BIGINT       NOT NULL REFERENCES account(id) ON DELETE CASCADE,
    code        VARCHAR(6)   NOT NULL,
    status      VARCHAR(20)  NOT NULL DEFAULT 'PENDING',
    used_count  INTEGER      NOT NULL DEFAULT 0,
    expires_at  TIMESTAMPTZ  NOT NULL,
    created_at  TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    CONSTRAINT ck_invitation_status CHECK (status IN ('PENDING','EXPIRED','REVOKED'))
);

CREATE UNIQUE INDEX ux_coach_invitation_active
    ON coach_invitation(coach_id) WHERE status = 'PENDING';

CREATE INDEX ix_coach_invitation_code
    ON coach_invitation(code);
