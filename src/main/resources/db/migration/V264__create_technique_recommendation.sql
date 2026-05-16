CREATE TABLE technique_recommendation (
  id               BIGSERIAL PRIMARY KEY,
  coach_id         BIGINT      NOT NULL REFERENCES account(id) ON DELETE CASCADE,
  athlete_id       BIGINT      NOT NULL REFERENCES account(id) ON DELETE CASCADE,
  technique_id     BIGINT      NOT NULL REFERENCES technique(id) ON DELETE CASCADE,
  note             TEXT,
  status           VARCHAR(20) NOT NULL DEFAULT 'PENDING',
  recommended_at   TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  resolved_at      TIMESTAMPTZ
);

CREATE INDEX idx_rec_coach_athlete ON technique_recommendation(coach_id, athlete_id, recommended_at DESC);
CREATE INDEX idx_rec_athlete_status ON technique_recommendation(athlete_id, status) WHERE status = 'PENDING';
