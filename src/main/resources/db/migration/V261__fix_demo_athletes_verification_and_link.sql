-- Verificar las cuentas demo que se crearon via API (email_verified = false)
UPDATE account
SET email_verified = true
WHERE email IN ('demo.atleta1@ossflow.es', 'demo.atleta2@ossflow.es', 'demo.atleta3@ossflow.es')
  AND email_verified = false;

-- Crear perfiles si no existen (las cuentas se registraron via API pero sin completar onboarding)
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

-- Vincular atletas a todos los coaches existentes (cualquier ATHLETE_COACH en la BD)
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
