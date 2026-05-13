-- V253: Mejora calidad datos estudio
-- 1. Eliminar registros de test/QA
-- 2. Corregir minimum_belt en técnicas (basado en IBJJF 2024)
-- 3. Fusionar categoría SWEEP en TAKEDOWN (petición usuario: barrida=derribo)
-- 4. Completar rulesets IBJJF No-Gi y AJP con técnicas restringidas
-- 5. Actualizar fuente URL de federaciones

-- ============================================================
-- 1. LIMPIAR REGISTROS DE TEST / QA
-- ============================================================

-- Técnicas de test
DELETE FROM ruleset_technique WHERE technique_id IN (
    SELECT id FROM technique WHERE name IN ('QA Test Technique','Verify Technique 1778408905','test','Armbar') AND owner_id = 1 AND created_at > '2026-05-01'
);
UPDATE technique SET deleted_at = NOW(), purge_at = NOW() + INTERVAL '30 days'
WHERE name IN ('QA Test Technique','Verify Technique 1778408905','test')
  AND owner_id = 1;
-- El duplicado "Armbar" (id=315) con family null — reasignar FK a Closed Guard (id=19) antes de borrar posición
UPDATE technique SET start_position_id = 19 WHERE id = 315;
UPDATE technique SET deleted_at = NOW(), purge_at = NOW() + INTERVAL '30 days'
WHERE id = 315;

-- Ejercicios de test
UPDATE exercise SET deleted_at = NOW(), purge_at = NOW() + INTERVAL '30 days'
WHERE name IN ('Test exercise','QA Exercise NO_EQUIPMENT','Verify Exercise 1778408905')
  AND owner_id = 1;

-- Posiciones de test — primero reasignar FKs, luego borrar
UPDATE technique SET start_position_id = 19 WHERE start_position_id IN (SELECT id FROM position WHERE name IN ('TimeoutTest','TestUpdated','QA Test CORS Fix - UPDATED','QA Test Position','Verify Position 1778408905','Private Favorite Guard','Public BJJ Guard','Guardia Cerrada'));
UPDATE technique SET end_position_id   = 19 WHERE end_position_id   IN (SELECT id FROM position WHERE name IN ('TimeoutTest','TestUpdated','QA Test CORS Fix - UPDATED','QA Test Position','Verify Position 1778408905','Private Favorite Guard','Public BJJ Guard','Guardia Cerrada'));
-- Guardia Cerrada (id=138) = duplicado de Closed Guard (id=19)
DELETE FROM position WHERE name IN ('TimeoutTest','TestUpdated','QA Test CORS Fix - UPDATED','QA Test Position','Verify Position 1778408905','Private Favorite Guard','Public BJJ Guard','Guardia Cerrada');

-- ============================================================
-- 2. CORREGIR minimum_belt EN TÉCNICAS
--    Referencia: IBJJF Rulebook 2024 + ADCC Rules 2022
-- ============================================================

-- Heel Hook Defense: la defensa se necesita saber desde blanco (torneo de principiantes ya hay intentos)
UPDATE technique SET
    minimum_belt = 'WHITE',
    description  = 'Defensa urgente de heel hook: no rotar la rodilla, alinear caderas con el talón, vine escape o back step para liberar. Necesaria desde cinturón blanco en competición No-Gi.'
WHERE id = 90; -- Heel Hook Defense

-- Outside Heel Hook: IBJJF Gi prohibido siempre; IBJJF No-Gi desde brown; ADCC/EBI permitido antes → BROWN es correcto para NOGI
-- (ya está correcto como BROWN en id=24... espera, está en BLACK)
UPDATE technique SET
    minimum_belt = 'BROWN',
    description  = 'Torsión del talón hacia afuera atacando los ligamentos internos de la rodilla. El más peligroso de los leg locks. IBJJF No-Gi: permitido desde brown belt. Prohibido en Gi en todas las federaciones importantes.'
WHERE id = 24; -- Outside Heel Hook

-- Inside Heel Hook: IBJJF No-Gi desde brown; ADCC desde todos; legal en EBI — BROWN es lo correcto
-- (ya está en BROWN — correcto, solo actualizar descripción)
UPDATE technique SET
    description = 'Torsión del talón hacia adentro atacando el ligamento externo de la rodilla. IBJJF No-Gi: permitido desde brown belt. Permitido desde blanco en ADCC, EBI y la mayoría de torneos de grappling open.'
WHERE id = 23; -- Inside Heel Hook

-- Heel Hook from Saddle / Double Outside: BROWN es correcto para IBJJF, pero son técnicas No-Gi
-- Actualizar descripciones para indicar el contexto de competición
UPDATE technique SET
    description = 'Inside heel hook desde la posición saddle/honey hole (single leg X o ashi garami). El sistema de leg locks más eficiente del grappling moderno. IBJJF No-Gi: legal desde brown belt.'
WHERE id = 125; -- Heel Hook from Saddle

UPDATE technique SET
    description = 'Heel hook desde doble ashi exterior (outside ashi garami). Alto control de la pierna, ataque al ligamento externo. IBJJF No-Gi: legal desde brown belt. Sistema avanzado de leg entanglement.'
WHERE id = 127; -- Heel Hook from Double Outside

-- Outside Heel Hook 50/50: BROWN No-Gi correcto
UPDATE technique SET
    description = 'Outside heel hook desde la posición 50/50. Requiere completar el reaping de la pierna. IBJJF No-Gi: legal desde brown belt en la posición 50/50 con control adecuado.'
WHERE id = 126; -- Outside Heel Hook 50/50

-- Kneebar: IBJJF Gi desde purple; No-Gi también desde purple — PURPLE correcto
UPDATE technique SET
    description = 'Hiperextensión de rodilla aislando la pierna entre el cuerpo. IBJJF: legal desde purple belt (Gi y No-Gi). Ataque desde leg entanglement, mount o top half guard.'
WHERE id = 25; -- Kneebar

UPDATE technique SET
    description = 'Kneebar desde posición top cuando el oponente abre las piernas para escapar. Rotación sobre la pierna y extensión de caderas para la sumisión. IBJJF: legal desde purple belt.'
WHERE id = 131; -- Kneebar from Top

-- Calf Slicer: IBJJF Gi desde purple — PURPLE correcto
UPDATE technique SET
    description = 'Compresión del gemelo: espinilla o antebrazo detrás de la rodilla doblando la pierna sobre sí misma. IBJJF: legal desde purple belt (Gi y No-Gi).'
WHERE id = 27; -- Calf Slicer

-- Bicep Slicer: IBJJF Gi desde brown — BROWN correcto
UPDATE technique SET
    description = 'Compresión del bíceps: antebrazo o espinilla en el pliegue del codo forzando hiperextensión. IBJJF: legal desde brown belt (Gi y No-Gi).'
WHERE id = 28; -- Bicep Slicer

-- Toe Hold: IBJJF Gi desde blue (con control), No-Gi también desde blue — BLUE ya es correcto en id=26 (PURPLE)
-- Corregir: está en PURPLE pero IBJJF lo permite desde BLUE
UPDATE technique SET
    minimum_belt = 'BLUE',
    description  = 'Rotación del tobillo atacando los ligamentos del pie y tobillo. IBJJF: legal desde blue belt (Gi y No-Gi). Requiere control de pierna previo.'
WHERE id = 26; -- Toe Hold

-- Estima Lock: IBJJF Gi prohibido (está en ruleset como PROHIBITED). Técnica más de brown/negro — BROWN es conservador
UPDATE technique SET
    minimum_belt = 'BROWN',
    description  = 'Lock de tobillo en rotación externa desde posición top. Prohibido en competición Gi IBJJF. Usado principalmente en grappling No-Gi y submission-only.'
WHERE id = 29; -- Estima Lock

-- Electric Chair: sumisión desde half guard leg entanglement — PURPLE es razonable
UPDATE technique SET
    description  = 'Separación de las piernas del oponente desde lockdown/dogbar half guard, atacando la ingle y espalda baja. IBJJF: no tiene restricción específica, se trata como leg entanglement. Aplicable desde purple belt.'
WHERE id = 30; -- Electric Chair

-- Compression Lock: IBJJF no la clasifica explícitamente — PURPLE razonable
UPDATE technique SET
    minimum_belt = 'PURPLE',
    description  = 'Compresión de articulación desde posición de control. Variante de shoulder lock. IBJJF: sin restricción específica, se aplica criterio de técnica avanzada desde purple belt.'
WHERE id = 128; -- Compression Lock

-- Bridge and Roll: está en ESCAPE pero family=SWEEPS — la categoría es escape, OK, pero la familia debería ser ESCAPES
UPDATE technique SET
    family = 'ESCAPES'
WHERE id = 79; -- Bridge and Roll

-- Side Control to Mount / North-South: están en TRANSITION pero family=ESCAPES — son transiciones ofensivas, no escapes
UPDATE technique SET
    family = 'GUARD_PASSES'
WHERE id = 92; -- Side Control to Mount (family era ESCAPES, es transición ofensiva)

UPDATE technique SET
    family = 'GUARD_PASSES'
WHERE id = 95; -- Side Control to North-South

UPDATE technique SET
    family = 'ESCAPES'
WHERE id = 102; -- Turtle to Guard Recovery (correctamente es escape/recovery)

-- ============================================================
-- 3. FUSIONAR SWEEP → TAKEDOWN  (petición usuario)
-- ============================================================

UPDATE technique SET category = 'TAKEDOWN'
WHERE category = 'SWEEP';

-- ============================================================
-- 4. COMPLETAR RULESETS IBJJF NO-GI
--    (actualmente vacío — añadir técnicas prohibidas/permitidas)
-- ============================================================

-- IBJJF No-Gi WHITE: mismas restricciones que Gi en leg locks
INSERT INTO ruleset_technique (ruleset_id, technique_id, status, condition_notes)
SELECT r.id, t.id, 'PROHIBITED', 'Prohibido en IBJJF No-Gi cinturón blanco'
FROM ruleset r, technique t
WHERE r.federation_id = (SELECT id FROM federation WHERE code = 'IBJJF')
  AND r.belt = 'WHITE' AND r.modality = 'NOGI'
  AND t.name IN ('Inside Heel Hook','Outside Heel Hook','Outside Heel Hook 50/50',
                 'Heel Hook from Saddle','Heel Hook from Double Outside',
                 'Kneebar','Kneebar from Top','Bicep Slicer','Calf Slicer',
                 'Neck Crank','Toe Hold')
ON CONFLICT (ruleset_id, technique_id) DO NOTHING;

-- IBJJF No-Gi BLUE: Toe Hold permitido, heel hooks y kneebar prohibidos
INSERT INTO ruleset_technique (ruleset_id, technique_id, status, condition_notes)
SELECT r.id, t.id,
    CASE t.name
        WHEN 'Toe Hold' THEN 'ALLOWED'
        ELSE 'PROHIBITED'
    END,
    CASE t.name
        WHEN 'Toe Hold' THEN 'Permitido desde blue belt No-Gi IBJJF'
        ELSE 'Prohibido en IBJJF No-Gi hasta cinturón azul'
    END
FROM ruleset r, technique t
WHERE r.federation_id = (SELECT id FROM federation WHERE code = 'IBJJF')
  AND r.belt = 'BLUE' AND r.modality = 'NOGI'
  AND t.name IN ('Inside Heel Hook','Outside Heel Hook','Outside Heel Hook 50/50',
                 'Heel Hook from Saddle','Heel Hook from Double Outside',
                 'Kneebar','Kneebar from Top','Bicep Slicer','Calf Slicer',
                 'Neck Crank','Toe Hold')
ON CONFLICT (ruleset_id, technique_id) DO NOTHING;

-- IBJJF No-Gi PURPLE: Toe Hold + Kneebar + Calf Slicer + Bicep Slicer permitidos
INSERT INTO ruleset_technique (ruleset_id, technique_id, status, condition_notes)
SELECT r.id, t.id,
    CASE t.name
        WHEN 'Toe Hold'    THEN 'ALLOWED'
        WHEN 'Kneebar'     THEN 'ALLOWED'
        WHEN 'Kneebar from Top' THEN 'ALLOWED'
        WHEN 'Calf Slicer' THEN 'ALLOWED'
        ELSE 'PROHIBITED'
    END,
    CASE t.name
        WHEN 'Toe Hold'    THEN 'Permitido desde purple belt No-Gi IBJJF'
        WHEN 'Kneebar'     THEN 'Permitido desde purple belt No-Gi IBJJF'
        WHEN 'Kneebar from Top' THEN 'Permitido desde purple belt No-Gi IBJJF'
        WHEN 'Calf Slicer' THEN 'Permitido desde purple belt No-Gi IBJJF'
        ELSE 'Prohibido en IBJJF No-Gi hasta cinturón morado'
    END
FROM ruleset r, technique t
WHERE r.federation_id = (SELECT id FROM federation WHERE code = 'IBJJF')
  AND r.belt = 'PURPLE' AND r.modality = 'NOGI'
  AND t.name IN ('Inside Heel Hook','Outside Heel Hook','Outside Heel Hook 50/50',
                 'Heel Hook from Saddle','Heel Hook from Double Outside',
                 'Kneebar','Kneebar from Top','Bicep Slicer','Calf Slicer',
                 'Neck Crank','Toe Hold')
ON CONFLICT (ruleset_id, technique_id) DO NOTHING;

-- IBJJF No-Gi BROWN: Inside Heel Hook + Heel Hook from Saddle/Double Outside permitidos
INSERT INTO ruleset_technique (ruleset_id, technique_id, status, condition_notes)
SELECT r.id, t.id,
    CASE t.name
        WHEN 'Outside Heel Hook'           THEN 'PROHIBITED'
        WHEN 'Neck Crank'                   THEN 'PROHIBITED'
        ELSE 'ALLOWED'
    END,
    CASE t.name
        WHEN 'Outside Heel Hook'           THEN 'Outside Heel Hook prohibido en IBJJF hasta cinturón negro'
        WHEN 'Neck Crank'                   THEN 'Neck Crank prohibido en IBJJF No-Gi en todas las categorías'
        ELSE 'Permitido desde brown belt No-Gi IBJJF'
    END
FROM ruleset r, technique t
WHERE r.federation_id = (SELECT id FROM federation WHERE code = 'IBJJF')
  AND r.belt = 'BROWN' AND r.modality = 'NOGI'
  AND t.name IN ('Inside Heel Hook','Outside Heel Hook','Outside Heel Hook 50/50',
                 'Heel Hook from Saddle','Heel Hook from Double Outside',
                 'Kneebar','Kneebar from Top','Bicep Slicer','Calf Slicer',
                 'Neck Crank','Toe Hold')
ON CONFLICT (ruleset_id, technique_id) DO NOTHING;

-- IBJJF No-Gi BLACK: casi todo permitido excepto Outside Heel Hook y Neck Crank
INSERT INTO ruleset_technique (ruleset_id, technique_id, status, condition_notes)
SELECT r.id, t.id,
    CASE t.name
        WHEN 'Outside Heel Hook' THEN 'PROHIBITED'
        WHEN 'Neck Crank'        THEN 'PROHIBITED'
        ELSE 'ALLOWED'
    END,
    CASE t.name
        WHEN 'Outside Heel Hook' THEN 'Outside Heel Hook prohibido en IBJJF (reaping) en todas las categorías'
        WHEN 'Neck Crank'        THEN 'Neck Crank prohibido en IBJJF No-Gi en todas las categorías'
        ELSE 'Permitido desde black belt No-Gi IBJJF'
    END
FROM ruleset r, technique t
WHERE r.federation_id = (SELECT id FROM federation WHERE code = 'IBJJF')
  AND r.belt = 'BLACK' AND r.modality = 'NOGI'
  AND t.name IN ('Inside Heel Hook','Outside Heel Hook','Outside Heel Hook 50/50',
                 'Heel Hook from Saddle','Heel Hook from Double Outside',
                 'Kneebar','Kneebar from Top','Bicep Slicer','Calf Slicer',
                 'Neck Crank','Toe Hold')
ON CONFLICT (ruleset_id, technique_id) DO NOTHING;

-- ============================================================
-- 5. COMPLETAR RULESETS AJP (Abu Dhabi Jiu-Jitsu Pro)
--    AJP usa reglas similares a UAEJJF: Gi reglas conservadoras,
--    No-Gi más permisivo. Heel hooks en No-Gi desde intermediate (brown+)
-- ============================================================

-- AJP Gi WHITE/BLUE: mismas restricciones que IBJJF Gi
INSERT INTO ruleset_technique (ruleset_id, technique_id, status, condition_notes)
SELECT r.id, t.id, 'PROHIBITED',
    'Prohibido en competición AJP Gi cinturón ' || r.belt
FROM ruleset r, technique t
WHERE r.federation_id = (SELECT id FROM federation WHERE code = 'AJP')
  AND r.modality = 'GI'
  AND r.belt IN ('WHITE','BLUE')
  AND t.name IN ('Inside Heel Hook','Outside Heel Hook','Outside Heel Hook 50/50',
                 'Heel Hook from Saddle','Heel Hook from Double Outside',
                 'Kneebar','Kneebar from Top','Bicep Slicer','Calf Slicer',
                 'Neck Crank','Toe Hold','Estima Lock')
ON CONFLICT (ruleset_id, technique_id) DO NOTHING;

-- AJP Gi PURPLE: Kneebar + Toe Hold permitidos
INSERT INTO ruleset_technique (ruleset_id, technique_id, status, condition_notes)
SELECT r.id, t.id,
    CASE t.name
        WHEN 'Toe Hold' THEN 'ALLOWED'
        WHEN 'Kneebar'  THEN 'ALLOWED'
        WHEN 'Kneebar from Top' THEN 'ALLOWED'
        ELSE 'PROHIBITED'
    END,
    CASE t.name
        WHEN 'Toe Hold' THEN 'Permitido en AJP Gi desde purple belt'
        WHEN 'Kneebar'  THEN 'Permitido en AJP Gi desde purple belt'
        WHEN 'Kneebar from Top' THEN 'Permitido en AJP Gi desde purple belt'
        ELSE 'Prohibido en AJP Gi hasta purple belt'
    END
FROM ruleset r, technique t
WHERE r.federation_id = (SELECT id FROM federation WHERE code = 'AJP')
  AND r.modality = 'GI' AND r.belt = 'PURPLE'
  AND t.name IN ('Inside Heel Hook','Outside Heel Hook','Outside Heel Hook 50/50',
                 'Heel Hook from Saddle','Heel Hook from Double Outside',
                 'Kneebar','Kneebar from Top','Bicep Slicer','Calf Slicer',
                 'Neck Crank','Toe Hold','Estima Lock')
ON CONFLICT (ruleset_id, technique_id) DO NOTHING;

-- AJP Gi BROWN/BLACK: Kneebar + Toe Hold + Bicep/Calf Slicer permitidos; Heel Hooks prohibidos en Gi
INSERT INTO ruleset_technique (ruleset_id, technique_id, status, condition_notes)
SELECT r.id, t.id,
    CASE t.name
        WHEN 'Inside Heel Hook'            THEN 'PROHIBITED'
        WHEN 'Outside Heel Hook'           THEN 'PROHIBITED'
        WHEN 'Outside Heel Hook 50/50'     THEN 'PROHIBITED'
        WHEN 'Heel Hook from Saddle'       THEN 'PROHIBITED'
        WHEN 'Heel Hook from Double Outside' THEN 'PROHIBITED'
        WHEN 'Neck Crank'                  THEN 'PROHIBITED'
        WHEN 'Estima Lock'                 THEN 'PROHIBITED'
        ELSE 'ALLOWED'
    END,
    CASE t.name
        WHEN 'Inside Heel Hook'            THEN 'Heel hooks prohibidos en Gi AJP en todas las categorías'
        WHEN 'Outside Heel Hook'           THEN 'Heel hooks prohibidos en Gi AJP en todas las categorías'
        WHEN 'Outside Heel Hook 50/50'     THEN 'Heel hooks prohibidos en Gi AJP en todas las categorías'
        WHEN 'Heel Hook from Saddle'       THEN 'Heel hooks prohibidos en Gi AJP en todas las categorías'
        WHEN 'Heel Hook from Double Outside' THEN 'Heel hooks prohibidos en Gi AJP en todas las categorías'
        WHEN 'Neck Crank'                  THEN 'Prohibido en AJP Gi'
        WHEN 'Estima Lock'                 THEN 'Prohibido en AJP Gi'
        ELSE 'Permitido en AJP Gi desde ' || r.belt || ' belt'
    END
FROM ruleset r, technique t
WHERE r.federation_id = (SELECT id FROM federation WHERE code = 'AJP')
  AND r.modality = 'GI' AND r.belt IN ('BROWN','BLACK')
  AND t.name IN ('Inside Heel Hook','Outside Heel Hook','Outside Heel Hook 50/50',
                 'Heel Hook from Saddle','Heel Hook from Double Outside',
                 'Kneebar','Kneebar from Top','Bicep Slicer','Calf Slicer',
                 'Neck Crank','Toe Hold','Estima Lock')
ON CONFLICT (ruleset_id, technique_id) DO NOTHING;

-- AJP No-Gi WHITE/BLUE: sin leg locks peligrosos
INSERT INTO ruleset_technique (ruleset_id, technique_id, status, condition_notes)
SELECT r.id, t.id, 'PROHIBITED',
    'Prohibido en AJP No-Gi cinturón ' || r.belt
FROM ruleset r, technique t
WHERE r.federation_id = (SELECT id FROM federation WHERE code = 'AJP')
  AND r.modality = 'NOGI' AND r.belt IN ('WHITE','BLUE')
  AND t.name IN ('Inside Heel Hook','Outside Heel Hook','Outside Heel Hook 50/50',
                 'Heel Hook from Saddle','Heel Hook from Double Outside',
                 'Kneebar','Kneebar from Top','Bicep Slicer','Neck Crank')
ON CONFLICT (ruleset_id, technique_id) DO NOTHING;

-- AJP No-Gi PURPLE: Kneebar + Toe Hold permitidos
INSERT INTO ruleset_technique (ruleset_id, technique_id, status, condition_notes)
SELECT r.id, t.id,
    CASE t.name
        WHEN 'Kneebar' THEN 'ALLOWED'
        WHEN 'Kneebar from Top' THEN 'ALLOWED'
        ELSE 'PROHIBITED'
    END,
    CASE t.name
        WHEN 'Kneebar' THEN 'Permitido en AJP No-Gi desde purple belt'
        WHEN 'Kneebar from Top' THEN 'Permitido en AJP No-Gi desde purple belt'
        ELSE 'Prohibido en AJP No-Gi hasta cinturón morado'
    END
FROM ruleset r, technique t
WHERE r.federation_id = (SELECT id FROM federation WHERE code = 'AJP')
  AND r.modality = 'NOGI' AND r.belt = 'PURPLE'
  AND t.name IN ('Inside Heel Hook','Outside Heel Hook','Outside Heel Hook 50/50',
                 'Heel Hook from Saddle','Heel Hook from Double Outside',
                 'Kneebar','Kneebar from Top','Bicep Slicer','Neck Crank')
ON CONFLICT (ruleset_id, technique_id) DO NOTHING;

-- AJP No-Gi BROWN/BLACK: Heel Hooks permitidos (inside y outside)
INSERT INTO ruleset_technique (ruleset_id, technique_id, status, condition_notes)
SELECT r.id, t.id,
    CASE t.name
        WHEN 'Neck Crank' THEN 'PROHIBITED'
        ELSE 'ALLOWED'
    END,
    CASE t.name
        WHEN 'Neck Crank' THEN 'Prohibido en AJP No-Gi en todas las categorías'
        ELSE 'Permitido en AJP No-Gi desde ' || r.belt || ' belt'
    END
FROM ruleset r, technique t
WHERE r.federation_id = (SELECT id FROM federation WHERE code = 'AJP')
  AND r.modality = 'NOGI' AND r.belt IN ('BROWN','BLACK')
  AND t.name IN ('Inside Heel Hook','Outside Heel Hook','Outside Heel Hook 50/50',
                 'Heel Hook from Saddle','Heel Hook from Double Outside',
                 'Kneebar','Kneebar from Top','Bicep Slicer','Neck Crank')
ON CONFLICT (ruleset_id, technique_id) DO NOTHING;

-- ============================================================
-- 6. ACTUALIZAR URL OFICIAL DE FEDERACIONES QUE FALTAN
-- ============================================================

UPDATE federation SET official_url = 'https://ibjjf.com/competition-rules' WHERE code = 'IBJJF';
UPDATE federation SET official_url = 'https://aejj.es'                       WHERE code = 'AEJJ';
UPDATE federation SET official_url = 'https://fejjb.es'                      WHERE code = 'FEJJB';
UPDATE federation SET official_url = 'https://cbjje.com.br'                  WHERE code = 'CBJJE';
UPDATE federation SET official_url = 'https://sbjj.es'                       WHERE code = 'SBJJ';

-- ============================================================
-- 7. AÑADIR ADCC RULESET (solo No-Gi, un único nivel: ADVANCED)
--    ADCC no tiene cinturones — añadir como BLACK No-Gi (avanzado)
--    ADCC permite heel hooks, kneebars, reaping desde el inicio
-- ============================================================

INSERT INTO ruleset_technique (ruleset_id, technique_id, status, condition_notes)
SELECT r.id, t.id, 'ALLOWED', 'Permitido en ADCC No-Gi (categoría avanzada sin restricciones de cinturón)'
FROM ruleset r, technique t
WHERE r.federation_id = (SELECT id FROM federation WHERE code = 'ADCC')
  AND r.belt = 'BLACK' AND r.modality = 'NOGI'
  AND t.name IN (
      'Inside Heel Hook','Outside Heel Hook','Outside Heel Hook 50/50',
      'Heel Hook from Saddle','Heel Hook from Double Outside',
      'Kneebar','Kneebar from Top','Bicep Slicer','Calf Slicer',
      'Toe Hold','Electric Chair','Estima Lock','Compression Lock',
      'Wristlock','Neck Crank'
  )
ON CONFLICT (ruleset_id, technique_id) DO NOTHING;

-- En ADCC la única técnica que sí está prohibida es la Neck Crank (cervical)
-- Nota: ADCC sí permite neck cranks en su reglamento reciente, pero con condiciones
UPDATE ruleset_technique SET
    status = 'CONDITIONAL',
    condition_notes = 'Permitido en ADCC solo si hay control del cuerpo completo (no cervical aislada)'
WHERE ruleset_id = (
    SELECT r.id FROM ruleset r
    JOIN federation f ON f.id = r.federation_id
    WHERE f.code = 'ADCC' AND r.belt = 'BLACK' AND r.modality = 'NOGI'
)
AND technique_id = (SELECT id FROM technique WHERE name = 'Neck Crank' AND deleted_at IS NULL LIMIT 1);
