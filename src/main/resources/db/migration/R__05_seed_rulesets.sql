-- R__05_seed_rulesets.sql
-- Reglamentos por federación, cinturón y modalidad. Idempotente.
-- Consolida: V210.
-- Depende de: R__01_seed_federations.sql, R__03_seed_techniques.sql

-- IBJJF Gi
INSERT INTO ruleset (federation_id, belt, modality, effective_from, source_url, created_at, updated_at, version)
SELECT f.id, b.belt, 'GI', DATE '2024-01-01', 'https://ibjjf.com/rules', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0
FROM federation f
CROSS JOIN (VALUES ('WHITE'),('BLUE'),('PURPLE'),('BROWN'),('BLACK')) AS b(belt)
WHERE f.code = 'IBJJF'
ON CONFLICT (federation_id, belt, modality, effective_from) DO NOTHING;

-- IBJJF No-Gi
INSERT INTO ruleset (federation_id, belt, modality, effective_from, source_url, created_at, updated_at, version)
SELECT f.id, b.belt, 'NOGI', DATE '2024-01-01', 'https://ibjjf.com/rules', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0
FROM federation f
CROSS JOIN (VALUES ('WHITE'),('BLUE'),('PURPLE'),('BROWN'),('BLACK')) AS b(belt)
WHERE f.code = 'IBJJF'
ON CONFLICT (federation_id, belt, modality, effective_from) DO NOTHING;

-- ADCC — solo no-gi, categoría pro (BLACK)
INSERT INTO ruleset (federation_id, belt, modality, effective_from, source_url, created_at, updated_at, version)
SELECT f.id, 'BLACK', 'NOGI', DATE '2022-01-01', 'https://adcombat.com/rules', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0
FROM federation f WHERE f.code = 'ADCC'
ON CONFLICT (federation_id, belt, modality, effective_from) DO NOTHING;

-- AJP — gi y no-gi
INSERT INTO ruleset (federation_id, belt, modality, effective_from, source_url, created_at, updated_at, version)
SELECT f.id, b.belt, m.modality, DATE '2023-01-01', 'https://ajptour.com/rules', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0
FROM federation f
CROSS JOIN (VALUES ('WHITE'),('BLUE'),('PURPLE'),('BROWN'),('BLACK')) AS b(belt)
CROSS JOIN (VALUES ('GI'),('NOGI')) AS m(modality)
WHERE f.code = 'AJP'
ON CONFLICT (federation_id, belt, modality, effective_from) DO NOTHING;

-- =====================================================================
-- Técnicas prohibidas/permitidas por cinturón (IBJJF Gi)
-- =====================================================================

-- WHITE gi: prohibidas todas las leg locks avanzadas
INSERT INTO ruleset_technique (ruleset_id, technique_id, status, condition_notes)
SELECT r.id, t.id, 'PROHIBITED', 'No permitido en cinturón blanco gi IBJJF'
FROM ruleset r
JOIN federation f ON f.id = r.federation_id AND f.code = 'IBJJF'
JOIN technique t ON t.name IN ('Inside Heel Hook', 'Outside Heel Hook', 'Kneebar', 'Toe Hold', 'Estima Lock', 'Calf Slicer', 'Bicep Slicer')
  AND t.owner_id = 1
WHERE r.belt = 'WHITE' AND r.modality = 'GI'
ON CONFLICT (ruleset_id, technique_id) DO NOTHING;

-- BLUE gi: toe hold permitido, resto prohibido
INSERT INTO ruleset_technique (ruleset_id, technique_id, status, condition_notes)
SELECT r.id, t.id, 'PROHIBITED', 'No permitido en cinturón azul gi IBJJF'
FROM ruleset r
JOIN federation f ON f.id = r.federation_id AND f.code = 'IBJJF'
JOIN technique t ON t.name IN ('Inside Heel Hook', 'Outside Heel Hook', 'Kneebar', 'Estima Lock', 'Calf Slicer', 'Bicep Slicer')
  AND t.owner_id = 1
WHERE r.belt = 'BLUE' AND r.modality = 'GI'
ON CONFLICT (ruleset_id, technique_id) DO NOTHING;

INSERT INTO ruleset_technique (ruleset_id, technique_id, status, condition_notes)
SELECT r.id, t.id, 'ALLOWED', 'Permitido desde cinturón azul gi IBJJF'
FROM ruleset r
JOIN federation f ON f.id = r.federation_id AND f.code = 'IBJJF'
JOIN technique t ON t.name = 'Toe Hold' AND t.owner_id = 1
WHERE r.belt = 'BLUE' AND r.modality = 'GI'
ON CONFLICT (ruleset_id, technique_id) DO NOTHING;

-- PURPLE gi: kneebar permitido, sigue sin heel hooks
INSERT INTO ruleset_technique (ruleset_id, technique_id, status, condition_notes)
SELECT r.id, t.id, 'PROHIBITED', 'No permitido en cinturón morado gi IBJJF'
FROM ruleset r
JOIN federation f ON f.id = r.federation_id AND f.code = 'IBJJF'
JOIN technique t ON t.name IN ('Inside Heel Hook', 'Outside Heel Hook', 'Estima Lock') AND t.owner_id = 1
WHERE r.belt = 'PURPLE' AND r.modality = 'GI'
ON CONFLICT (ruleset_id, technique_id) DO NOTHING;

INSERT INTO ruleset_technique (ruleset_id, technique_id, status, condition_notes)
SELECT r.id, t.id, 'ALLOWED', 'Permitido desde cinturón morado gi IBJJF'
FROM ruleset r
JOIN federation f ON f.id = r.federation_id AND f.code = 'IBJJF'
JOIN technique t ON t.name IN ('Toe Hold', 'Kneebar', 'Calf Slicer', 'Bicep Slicer') AND t.owner_id = 1
WHERE r.belt = 'PURPLE' AND r.modality = 'GI'
ON CONFLICT (ruleset_id, technique_id) DO NOTHING;

-- BROWN/BLACK gi: inside heel hook permitido
INSERT INTO ruleset_technique (ruleset_id, technique_id, status, condition_notes)
SELECT r.id, t.id, 'ALLOWED', 'Permitido en cinturón marrón/negro gi IBJJF'
FROM ruleset r
JOIN federation f ON f.id = r.federation_id AND f.code = 'IBJJF'
JOIN technique t ON t.name IN ('Inside Heel Hook', 'Toe Hold', 'Kneebar', 'Calf Slicer', 'Bicep Slicer') AND t.owner_id = 1
WHERE r.belt IN ('BROWN', 'BLACK') AND r.modality = 'GI'
ON CONFLICT (ruleset_id, technique_id) DO NOTHING;

INSERT INTO ruleset_technique (ruleset_id, technique_id, status, condition_notes)
SELECT r.id, t.id, 'PROHIBITED', 'Outside Heel Hook prohibido en gi IBJJF siempre'
FROM ruleset r
JOIN federation f ON f.id = r.federation_id AND f.code = 'IBJJF'
JOIN technique t ON t.name = 'Outside Heel Hook' AND t.owner_id = 1
WHERE r.belt IN ('BROWN', 'BLACK') AND r.modality = 'GI'
ON CONFLICT (ruleset_id, technique_id) DO NOTHING;

-- ADCC: todo permitido
INSERT INTO ruleset_technique (ruleset_id, technique_id, status, condition_notes)
SELECT r.id, t.id, 'ALLOWED', 'Todas las técnicas permitidas en ADCC'
FROM ruleset r
JOIN federation f ON f.id = r.federation_id AND f.code = 'ADCC'
JOIN technique t ON t.name IN ('Inside Heel Hook', 'Outside Heel Hook', 'Kneebar', 'Toe Hold',
  'Straight Ankle Lock', 'Estima Lock', 'Calf Slicer', 'Bicep Slicer') AND t.owner_id = 1
WHERE r.belt = 'BLACK' AND r.modality = 'NOGI'
ON CONFLICT (ruleset_id, technique_id) DO NOTHING;
