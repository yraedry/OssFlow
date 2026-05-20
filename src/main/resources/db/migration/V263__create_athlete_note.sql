CREATE TABLE athlete_note (
  id               BIGSERIAL PRIMARY KEY,
  coach_id         BIGINT      NOT NULL REFERENCES account(id) ON DELETE CASCADE,
  athlete_id       BIGINT      NOT NULL REFERENCES account(id) ON DELETE CASCADE,
  body             TEXT        NOT NULL,
  technique_family VARCHAR(30),
  deleted_at       TIMESTAMPTZ,
  read_at          TIMESTAMPTZ,
  created_at       TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_athlete_note_received  ON athlete_note(athlete_id, created_at DESC) WHERE deleted_at IS NULL;
CREATE INDEX idx_athlete_note_coach     ON athlete_note(coach_id, athlete_id, created_at DESC);
CREATE INDEX idx_athlete_note_unread    ON athlete_note(athlete_id) WHERE read_at IS NULL AND deleted_at IS NULL;
CREATE INDEX idx_athlete_note_cleanup   ON athlete_note(deleted_at) WHERE deleted_at IS NOT NULL;
