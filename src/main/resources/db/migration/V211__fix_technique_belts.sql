-- V211__fix_technique_belts.sql
-- Corrige minimum_belt según reglamento IBJJF real (owner_id=1)

-- =====================================================================
-- SWEEPS: todos WHITE (barridos legales desde blanco en IBJJF)
-- =====================================================================
UPDATE technique SET minimum_belt = 'WHITE' WHERE category = 'SWEEP' AND owner_id = 1;

-- =====================================================================
-- PASSES: todos WHITE (pases legales desde blanco en IBJJF)
-- =====================================================================
UPDATE technique SET minimum_belt = 'WHITE' WHERE category = 'PASS' AND owner_id = 1;

-- =====================================================================
-- TAKEDOWNS: todos WHITE (derribos legales desde blanco en IBJJF)
-- =====================================================================
UPDATE technique SET minimum_belt = 'WHITE' WHERE category = 'TAKEDOWN' AND owner_id = 1;

-- =====================================================================
-- ESCAPES: todos WHITE salvo Heel Hook Defense (implica conocer heel hooks)
-- =====================================================================
UPDATE technique SET minimum_belt = 'WHITE' WHERE category = 'ESCAPE' AND owner_id = 1;
UPDATE technique SET minimum_belt = 'PURPLE' WHERE name = 'Heel Hook Defense' AND owner_id = 1;

-- =====================================================================
-- TRANSITIONS: todos WHITE salvo Ashi Garami to 50/50 (leg entanglement)
-- =====================================================================
UPDATE technique SET minimum_belt = 'WHITE' WHERE category = 'TRANSITION' AND owner_id = 1;
UPDATE technique SET minimum_belt = 'PURPLE' WHERE name = 'Ashi Garami to 50/50' AND owner_id = 1;

-- =====================================================================
-- SUBMISSIONS: según reglamento IBJJF real
-- =====================================================================

-- WHITE: estrangulaciones y llaves de brazo básicas, legales desde blanco
UPDATE technique SET minimum_belt = 'WHITE'
WHERE name IN (
    'Rear Naked Choke',
    'Guillotine Choke',
    'Triangle Choke',
    'Arm Triangle',
    'Americana',
    'Kimura',
    'Straight Armlock',
    'Cross Collar Choke',
    'Armbar',
    'Omoplata',
    'Straight Ankle Lock'
) AND owner_id = 1;

-- WHITE: Ezekiel Choke — legal desde blanco en IBJJF (con gi)
UPDATE technique SET minimum_belt = 'WHITE'
WHERE name = 'Ezekiel Choke' AND owner_id = 1;

-- BLUE: estrangulaciones avanzadas legales desde azul
UPDATE technique SET minimum_belt = 'BLUE'
WHERE name IN (
    'Loop Choke',
    'Baseball Bat Choke',
    'Clock Choke',
    'Peruvian Necktie',
    'D''Arce Choke',
    'Anaconda Choke',
    'North-South Choke',
    'Bow and Arrow Choke',
    'Wristlock',
    'Gogoplata',
    'Paper Cutter Choke'
) AND owner_id = 1;

-- PURPLE: leg locks y compresiones legales desde morado
UPDATE technique SET minimum_belt = 'PURPLE'
WHERE name IN (
    'Toe Hold',
    'Calf Slicer',
    'Kneebar',
    'Baratoplata',
    'Electric Chair'
) AND owner_id = 1;

-- BROWN: leg locks peligrosos y compresiones legales desde marrón
UPDATE technique SET minimum_belt = 'BROWN'
WHERE name IN (
    'Inside Heel Hook',
    'Bicep Slicer',
    'Estima Lock'
) AND owner_id = 1;

-- BLACK: Outside Heel Hook — el más peligroso, solo negro en gi
-- (en nogi IBJJF sería marrón, pero usamos el más restrictivo para gi)
UPDATE technique SET minimum_belt = 'BLACK'
WHERE name = 'Outside Heel Hook' AND owner_id = 1;
