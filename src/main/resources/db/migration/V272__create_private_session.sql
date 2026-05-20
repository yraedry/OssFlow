CREATE TABLE private_session (
  id               BIGSERIAL PRIMARY KEY,
  coach_id         BIGINT       NOT NULL REFERENCES account(id),
  athlete_id       BIGINT       NOT NULL REFERENCES account(id),
  gym_id           BIGINT       REFERENCES gym_location(id) ON DELETE SET NULL,
  session_date     DATE         NOT NULL,
  start_time       TIME,
  duration_minutes INT,
  title            VARCHAR(200),
  notes            TEXT,
  created_at       TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_private_session_coach_athlete
  ON private_session(coach_id, athlete_id, session_date DESC);
CREATE INDEX idx_private_session_athlete
  ON private_session(athlete_id, session_date DESC);
