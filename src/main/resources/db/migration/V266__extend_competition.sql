-- V266: Ampliar competition_log y competition_match con campos de categoría, lugar, modalidad, ronda y técnica libre
ALTER TABLE competition_log
  ADD COLUMN IF NOT EXISTS category_age  VARCHAR(20),
  ADD COLUMN IF NOT EXISTS location      VARCHAR(255),
  ADD COLUMN IF NOT EXISTS gi_nogi       VARCHAR(10);

ALTER TABLE competition_match
  ADD COLUMN IF NOT EXISTS round          VARCHAR(50),
  ADD COLUMN IF NOT EXISTS technique_text VARCHAR(255);
