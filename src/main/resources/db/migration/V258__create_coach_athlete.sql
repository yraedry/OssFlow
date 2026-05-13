CREATE TABLE coach_athlete (
    id              BIGSERIAL    PRIMARY KEY,
    coach_id        BIGINT       NOT NULL REFERENCES account(id) ON DELETE CASCADE,
    athlete_id      BIGINT       NOT NULL REFERENCES account(id) ON DELETE CASCADE,
    invitation_id   BIGINT       REFERENCES coach_invitation(id) ON DELETE SET NULL,
    linked_at       TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    CONSTRAINT uq_coach_athlete UNIQUE (coach_id, athlete_id)
);

CREATE INDEX ix_coach_athlete_coach_id   ON coach_athlete(coach_id);
CREATE INDEX ix_coach_athlete_athlete_id ON coach_athlete(athlete_id);
