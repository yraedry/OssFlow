-- Demo data: 3 atletas vinculados al coach de prueba (account_id = 2).
-- Los passwords son 'demo1234' hasheados con BCrypt.
-- Si account id=2 no existe (primer arranque), estos inserts simplemente no tendrán coach vinculado.

-- Crear cuentas de atletas demo si no existen
INSERT INTO account (email, password_hash, role, email_verified, created_at, updated_at, token_version)
VALUES
  ('demo.atleta1@ossflow.es', '$2a$10$rOzM1r3OYCcmPo7R5TIWGePQLz3/XK5A8RhvCpfhXNB5L9bFkbfxO', 'ATHLETE', true, NOW(), NOW(), 0),
  ('demo.atleta2@ossflow.es', '$2a$10$rOzM1r3OYCcmPo7R5TIWGePQLz3/XK5A8RhvCpfhXNB5L9bFkbfxO', 'ATHLETE', true, NOW(), NOW(), 0),
  ('demo.atleta3@ossflow.es', '$2a$10$rOzM1r3OYCcmPo7R5TIWGePQLz3/XK5A8RhvCpfhXNB5L9bFkbfxO', 'ATHLETE', true, NOW(), NOW(), 0)
ON CONFLICT (email) DO NOTHING;

-- Crear perfiles de los atletas demo
INSERT INTO user_profile (owner_id, display_name, first_name, last_name, alias, current_belt, belt_since, academy, preferred_modality, onboarding_completed, created_at, updated_at)
SELECT a.id, 'Carlos Ruiz', 'Carlos', 'Ruiz', 'charlie_bjj', 'blue', '2022-03-15', 'Checkmat Madrid', 'GI', true, NOW(), NOW()
FROM account a WHERE a.email = 'demo.atleta1@ossflow.es'
ON CONFLICT DO NOTHING;

INSERT INTO user_profile (owner_id, display_name, first_name, last_name, alias, current_belt, belt_since, academy, preferred_modality, onboarding_completed, created_at, updated_at)
SELECT a.id, 'María López', 'María', 'López', 'mlopez_grappling', 'purple', '2021-06-10', 'Checkmat Madrid', 'NOGI', true, NOW(), NOW()
FROM account a WHERE a.email = 'demo.atleta2@ossflow.es'
ON CONFLICT DO NOTHING;

INSERT INTO user_profile (owner_id, display_name, first_name, last_name, alias, current_belt, belt_since, academy, preferred_modality, onboarding_completed, created_at, updated_at)
SELECT a.id, 'Jorge Sánchez', 'Jorge', 'Sánchez', 'jsanchez', 'white', '2024-01-20', 'Checkmat Madrid', 'BOTH', true, NOW(), NOW()
FROM account a WHERE a.email = 'demo.atleta3@ossflow.es'
ON CONFLICT DO NOTHING;

-- Vincular atletas al primer coach (account con rol ATHLETE_COACH y el email del maestro de prueba)
-- Usamos subquery para no hardcodear IDs
INSERT INTO coach_athlete (coach_id, athlete_id, linked_at)
SELECT
  coach.id,
  atleta.id,
  NOW() - INTERVAL '30 days'
FROM account coach
CROSS JOIN account atleta
WHERE coach.role = 'ATHLETE_COACH'
  AND atleta.email IN ('demo.atleta1@ossflow.es', 'demo.atleta2@ossflow.es', 'demo.atleta3@ossflow.es')
ON CONFLICT (coach_id, athlete_id) DO NOTHING;
