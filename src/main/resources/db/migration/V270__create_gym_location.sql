CREATE TABLE gym_location (
  id         BIGSERIAL PRIMARY KEY,
  coach_id   BIGINT       NOT NULL REFERENCES account(id),
  name       VARCHAR(200) NOT NULL,
  address    TEXT,
  created_at TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_gym_location_coach ON gym_location(coach_id, created_at DESC);
