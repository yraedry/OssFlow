ALTER TABLE private_session
  ADD COLUMN techniques_worked TEXT[] NOT NULL DEFAULT '{}';
