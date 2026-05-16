CREATE TABLE coach_observation (
  id               BIGSERIAL PRIMARY KEY,
  coach_id         BIGINT    NOT NULL REFERENCES account(id),
  athlete_id       BIGINT    NOT NULL REFERENCES account(id),
  body             TEXT      NOT NULL,
  tone             VARCHAR(10) NOT NULL,
  technique_family VARCHAR(30),
  labelled_by      VARCHAR(20),
  observed_at      TIMESTAMP NOT NULL DEFAULT NOW(),
  created_at       TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_coach_obs_athlete ON coach_observation(coach_id, athlete_id, observed_at DESC);
CREATE INDEX idx_coach_obs_radar ON coach_observation(coach_id, athlete_id, technique_family) WHERE technique_family IS NOT NULL;
CREATE INDEX idx_coach_obs_unlabelled ON coach_observation(id) WHERE labelled_by IS NULL;
