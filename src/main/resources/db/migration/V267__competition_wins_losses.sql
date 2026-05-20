ALTER TABLE competition_log
  ADD COLUMN IF NOT EXISTS wins_count  INT,
  ADD COLUMN IF NOT EXISTS losses_count INT;
