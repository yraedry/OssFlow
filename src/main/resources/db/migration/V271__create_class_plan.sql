CREATE TABLE class_plan (
  id                BIGSERIAL PRIMARY KEY,
  coach_id          BIGINT       NOT NULL REFERENCES account(id),
  gym_id            BIGINT       NOT NULL REFERENCES gym_location(id),
  title             VARCHAR(200) NOT NULL,
  description       TEXT,
  scheduled_date    DATE,
  duration_minutes  INT,
  modality          VARCHAR(10),
  status            VARCHAR(20)  NOT NULL DEFAULT 'DRAFT',
  created_at        TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
  updated_at        TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_class_plan_gym ON class_plan(coach_id, gym_id, scheduled_date DESC NULLS LAST);

ALTER TABLE coach_study_block ALTER COLUMN plan_id DROP NOT NULL;

ALTER TABLE coach_study_block
  ADD COLUMN class_plan_id BIGINT REFERENCES class_plan(id) ON DELETE CASCADE;

ALTER TABLE coach_study_block
  ADD CONSTRAINT chk_block_exactly_one_parent
  CHECK (
    (plan_id IS NOT NULL AND class_plan_id IS NULL) OR
    (plan_id IS NULL AND class_plan_id IS NOT NULL)
  );

CREATE INDEX idx_coach_study_block_class_plan ON coach_study_block(class_plan_id, block_order);
