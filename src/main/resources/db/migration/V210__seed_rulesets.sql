-- V210__seed_rulesets.sql
-- Rulesets por federación, cinturón y modalidad.
-- Modelo: (federation_id, belt, modality, effective_from) + source_url
-- Cinturones: WHITE, BLUE, PURPLE, BROWN, BLACK
-- Modalidades: GI, NOGI, BOTH

-- IBJJF Gi — todas las categorías de cinturón
INSERT INTO ruleset (federation_id, belt, modality, effective_from, source_url, created_at, updated_at, version)
SELECT f.id, 'WHITE',  'GI', '2024-01-01', 'https://ibjjf.com/rules', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0 FROM federation f WHERE f.code = 'IBJJF'
UNION ALL
SELECT f.id, 'BLUE',   'GI', '2024-01-01', 'https://ibjjf.com/rules', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0 FROM federation f WHERE f.code = 'IBJJF'
UNION ALL
SELECT f.id, 'PURPLE', 'GI', '2024-01-01', 'https://ibjjf.com/rules', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0 FROM federation f WHERE f.code = 'IBJJF'
UNION ALL
SELECT f.id, 'BROWN',  'GI', '2024-01-01', 'https://ibjjf.com/rules', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0 FROM federation f WHERE f.code = 'IBJJF'
UNION ALL
SELECT f.id, 'BLACK',  'GI', '2024-01-01', 'https://ibjjf.com/rules', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0 FROM federation f WHERE f.code = 'IBJJF';

-- IBJJF No-Gi — cinturón equivalente por años de experiencia
INSERT INTO ruleset (federation_id, belt, modality, effective_from, source_url, created_at, updated_at, version)
SELECT f.id, 'WHITE',  'NOGI', '2024-01-01', 'https://ibjjf.com/rules', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0 FROM federation f WHERE f.code = 'IBJJF'
UNION ALL
SELECT f.id, 'BLUE',   'NOGI', '2024-01-01', 'https://ibjjf.com/rules', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0 FROM federation f WHERE f.code = 'IBJJF'
UNION ALL
SELECT f.id, 'PURPLE', 'NOGI', '2024-01-01', 'https://ibjjf.com/rules', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0 FROM federation f WHERE f.code = 'IBJJF'
UNION ALL
SELECT f.id, 'BROWN',  'NOGI', '2024-01-01', 'https://ibjjf.com/rules', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0 FROM federation f WHERE f.code = 'IBJJF'
UNION ALL
SELECT f.id, 'BLACK',  'NOGI', '2024-01-01', 'https://ibjjf.com/rules', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0 FROM federation f WHERE f.code = 'IBJJF';

-- ADCC — solo no-gi, categoría única (usa BLACK como equivalente pro)
INSERT INTO ruleset (federation_id, belt, modality, effective_from, source_url, created_at, updated_at, version)
SELECT f.id, 'BLACK', 'NOGI', '2022-01-01', 'https://adcombat.com/rules', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0 FROM federation f WHERE f.code = 'ADCC';

-- AJP — gi y no-gi
INSERT INTO ruleset (federation_id, belt, modality, effective_from, source_url, created_at, updated_at, version)
SELECT f.id, 'WHITE',  'GI', '2023-01-01', 'https://ajptour.com/rules', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0 FROM federation f WHERE f.code = 'AJP'
UNION ALL
SELECT f.id, 'BLUE',   'GI', '2023-01-01', 'https://ajptour.com/rules', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0 FROM federation f WHERE f.code = 'AJP'
UNION ALL
SELECT f.id, 'PURPLE', 'GI', '2023-01-01', 'https://ajptour.com/rules', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0 FROM federation f WHERE f.code = 'AJP'
UNION ALL
SELECT f.id, 'BROWN',  'GI', '2023-01-01', 'https://ajptour.com/rules', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0 FROM federation f WHERE f.code = 'AJP'
UNION ALL
SELECT f.id, 'BLACK',  'GI', '2023-01-01', 'https://ajptour.com/rules', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0 FROM federation f WHERE f.code = 'AJP'
UNION ALL
SELECT f.id, 'WHITE',  'NOGI', '2023-01-01', 'https://ajptour.com/rules', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0 FROM federation f WHERE f.code = 'AJP'
UNION ALL
SELECT f.id, 'BLUE',   'NOGI', '2023-01-01', 'https://ajptour.com/rules', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0 FROM federation f WHERE f.code = 'AJP'
UNION ALL
SELECT f.id, 'PURPLE', 'NOGI', '2023-01-01', 'https://ajptour.com/rules', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0 FROM federation f WHERE f.code = 'AJP'
UNION ALL
SELECT f.id, 'BROWN',  'NOGI', '2023-01-01', 'https://ajptour.com/rules', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0 FROM federation f WHERE f.code = 'AJP'
UNION ALL
SELECT f.id, 'BLACK',  'NOGI', '2023-01-01', 'https://ajptour.com/rules', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0 FROM federation f WHERE f.code = 'AJP';

-- Técnicas prohibidas por cinturón: IBJJF Gi
-- WHITE: No heel hooks, no knee bars, no toe holds, no reaping
INSERT INTO ruleset_technique (ruleset_id, technique_id, status, condition_notes)
SELECT r.id, t.id, 'PROHIBITED', 'No permitido en cinturón blanco gi IBJJF'
FROM ruleset r
JOIN federation f ON f.id = r.federation_id AND f.code = 'IBJJF'
JOIN technique t ON t.name IN ('Inside Heel Hook', 'Outside Heel Hook', 'Kneebar', 'Toe Hold', 'Estima Lock', 'Calf Slicer', 'Bicep Slicer')
  AND t.owner_id = 1
WHERE r.belt = 'WHITE' AND r.modality = 'GI';

-- BLUE: toe hold permitido, pero no heel hooks ni kneebar
INSERT INTO ruleset_technique (ruleset_id, technique_id, status, condition_notes)
SELECT r.id, t.id, 'PROHIBITED', 'No permitido en cinturón azul gi IBJJF'
FROM ruleset r
JOIN federation f ON f.id = r.federation_id AND f.code = 'IBJJF'
JOIN technique t ON t.name IN ('Inside Heel Hook', 'Outside Heel Hook', 'Kneebar', 'Estima Lock', 'Calf Slicer', 'Bicep Slicer')
  AND t.owner_id = 1
WHERE r.belt = 'BLUE' AND r.modality = 'GI';

INSERT INTO ruleset_technique (ruleset_id, technique_id, status, condition_notes)
SELECT r.id, t.id, 'ALLOWED', 'Permitido desde cinturón azul gi IBJJF (con control)'
FROM ruleset r
JOIN federation f ON f.id = r.federation_id AND f.code = 'IBJJF'
JOIN technique t ON t.name = 'Toe Hold' AND t.owner_id = 1
WHERE r.belt = 'BLUE' AND r.modality = 'GI';

-- PURPLE: kneebar permitido, sigue sin heel hooks
INSERT INTO ruleset_technique (ruleset_id, technique_id, status, condition_notes)
SELECT r.id, t.id, 'PROHIBITED', 'No permitido en cinturón morado gi IBJJF'
FROM ruleset r
JOIN federation f ON f.id = r.federation_id AND f.code = 'IBJJF'
JOIN technique t ON t.name IN ('Inside Heel Hook', 'Outside Heel Hook', 'Estima Lock')
  AND t.owner_id = 1
WHERE r.belt = 'PURPLE' AND r.modality = 'GI';

INSERT INTO ruleset_technique (ruleset_id, technique_id, status, condition_notes)
SELECT r.id, t.id, 'ALLOWED', 'Permitido desde cinturón morado gi IBJJF'
FROM ruleset r
JOIN federation f ON f.id = r.federation_id AND f.code = 'IBJJF'
JOIN technique t ON t.name IN ('Toe Hold', 'Kneebar', 'Calf Slicer', 'Bicep Slicer') AND t.owner_id = 1
WHERE r.belt = 'PURPLE' AND r.modality = 'GI';

-- BROWN/BLACK: inside heel hook permitido (gi)
INSERT INTO ruleset_technique (ruleset_id, technique_id, status, condition_notes)
SELECT r.id, t.id, 'ALLOWED', 'Permitido en cinturón marrón/negro gi IBJJF'
FROM ruleset r
JOIN federation f ON f.id = r.federation_id AND f.code = 'IBJJF'
JOIN technique t ON t.name IN ('Inside Heel Hook', 'Toe Hold', 'Kneebar', 'Calf Slicer', 'Bicep Slicer') AND t.owner_id = 1
WHERE r.belt IN ('BROWN', 'BLACK') AND r.modality = 'GI';

INSERT INTO ruleset_technique (ruleset_id, technique_id, status, condition_notes)
SELECT r.id, t.id, 'PROHIBITED', 'Reaping/Outside Heel Hook prohibido en gi IBJJF siempre'
FROM ruleset r
JOIN federation f ON f.id = r.federation_id AND f.code = 'IBJJF'
JOIN technique t ON t.name = 'Outside Heel Hook' AND t.owner_id = 1
WHERE r.belt IN ('BROWN', 'BLACK') AND r.modality = 'GI';

-- ADCC: todo permitido
INSERT INTO ruleset_technique (ruleset_id, technique_id, status, condition_notes)
SELECT r.id, t.id, 'ALLOWED', 'Todas las técnicas permitidas en ADCC'
FROM ruleset r
JOIN federation f ON f.id = r.federation_id AND f.code = 'ADCC'
JOIN technique t ON t.name IN ('Inside Heel Hook', 'Outside Heel Hook', 'Kneebar', 'Toe Hold', 'Straight Ankle Lock',
  'Estima Lock', 'Calf Slicer', 'Bicep Slicer', 'Achilles Lock') AND t.owner_id = 1
WHERE r.belt = 'BLACK' AND r.modality = 'NOGI';
