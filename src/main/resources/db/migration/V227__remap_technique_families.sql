-- V227__remap_technique_families.sql
-- Rediseño de familias BJJ: taxonomía más granular con categorías específicas de guardia,
-- pasajes, sistemas de sumisión y derribos.

-- =====================================================================
-- GUARDIA CERRADA
-- Técnicas desde o relacionadas con closed guard
-- =====================================================================
UPDATE technique SET family = 'CLOSED_GUARD' WHERE owner_id = 1 AND name IN (
  'Triangle Choke', 'Mounted Triangle', 'Reverse Triangle', 'Mount to Triangle', 'Triangle Defense',
  'Armbar', 'Armbar from Mount', 'Armbar Defense',
  'Guillotine Choke', 'Arm-In Guillotine', 'High Elbow Guillotine', 'Marcelotine',
  'Kimura from Guard', 'Kimura Defense to Guard', 'Omoplata',
  'Hip Bump Sweep', 'Scissor Sweep', 'Pendulum Sweep', 'Flower Sweep',
  'Closed Guard to DLR', 'Closed Guard to Butterfly', 'Closed Guard to Spider',
  'Guard to Back Take', 'Guard to Turtle'
);

-- =====================================================================
-- MEDIA GUARDIA
-- Half Guard, Z-Guard, Deep Half, Lockdown
-- =====================================================================
UPDATE technique SET family = 'HALF_GUARD' WHERE owner_id = 1 AND name IN (
  'Half Guard Back Sweep', 'Knee Shield Sweep', 'Deep Half Sweep', 'Deep Half Back Take',
  'Hook Sweep', 'Old School Sweep', 'Dogfight Back Take',
  'Half Guard to Deep Half', 'Half Guard to Z-Guard',
  'K-Guard Sweep', 'Half Butterfly Guard',
  'Half Guard Pass', 'Half Guard Top Pass', 'Forced Half Pass', 'Reverse Half Pass',
  'Toehold from Half Guard', 'Mount Escape to Deep Half'
);

-- =====================================================================
-- GUARDIA ABIERTA
-- Spider Guard, Lasso, Collar-Sleeve, Worm Guard, Rubber Guard
-- =====================================================================
UPDATE technique SET family = 'OPEN_GUARD' WHERE owner_id = 1 AND name IN (
  'Spider Guard Sweep', 'Spider Tomo Sweep', 'Spider Guard Pass',
  'Lasso Pendulum Sweep', 'Lasso Back Take Sweep', 'Lasso Guard Pass',
  'Collar Drag Sweep', 'Collar Sleeve Sweep',
  'Worm Guard Sweep', 'Rubber Guard',
  'John Wayne Sweep', 'Koga Sweep'
);

-- =====================================================================
-- DE LA RIVA
-- De La Riva, Reverse DLR, Berimbolo
-- =====================================================================
UPDATE technique SET family = 'DLR_GUARD' WHERE owner_id = 1 AND name IN (
  'De La Riva Sweep', 'Reverse DLR Sweep',
  'DLR to Berimbolo', 'Berimbolo Back Take',
  'DLR to Single Leg X',
  'Closed Guard to DLR'
);

-- =====================================================================
-- GUARDIA MARIPOSA
-- Butterfly Guard, Half Butterfly
-- =====================================================================
UPDATE technique SET family = 'BUTTERFLY_GUARD' WHERE owner_id = 1 AND name IN (
  'Butterfly Sweep', 'Butterfly to X-Guard', 'Elevator Sweep',
  'Overhead Sweep', 'Hook Sweep',
  'Closed Guard to Butterfly'
);

-- =====================================================================
-- LEG ENTANGLEMENT / ENTRELAZADOS
-- Single X (Ashi Garami), X-Guard, 50/50, Saddle, Shin-to-Shin
-- =====================================================================
UPDATE technique SET family = 'LEG_ENTANGLEMENT' WHERE owner_id = 1 AND name IN (
  'Inside Heel Hook', 'Outside Heel Hook', 'Outside Heel Hook 50/50',
  'Ankle Lock from 50/50', 'Straight Ankle Lock', 'Kneebar', 'Kneebar from Top',
  'Toe Hold', 'Calf Slicer', 'Electric Chair',
  'SLX to Saddle', 'Heel Hook from Saddle', 'Heel Hook from Double Outside',
  'Ashi Garami to 50/50', 'Heel Hook Defense',
  'Single Leg X Sweep', 'X-Guard Sweep', 'X-Guard Back Take', 'X-Guard Back Sweep',
  'X-Guard Sweep',
  'Shin-to-Shin Sweep',
  'DLR to Single Leg X',
  'Side Control to Leg Lock', 'Turtle to Leg Lock Entry'
);

-- =====================================================================
-- PASAJES DE GUARDIA
-- Toreando, Leg Trap, Trípode, Knee Slice, Over-Under, Leg Drag, etc.
-- =====================================================================
UPDATE technique SET family = 'GUARD_PASSES' WHERE owner_id = 1 AND name IN (
  'Torreando Pass', 'Toreando 3/4 Pass', 'X-Pass',
  'Knee Slice Pass', 'Backstep Pass', 'Leg Drag Pass',
  'Over-Under Pass', 'Double Under Pass', 'Stack Pass', 'Smash Pass',
  'Pressure Pass', 'Float Pass', 'Cartwheel Pass', 'Long Step Pass',
  'Body Lock Pass', 'Drive Through Pass',
  'Tripod Sweep',   -- técnica que crea pasaje / desequilibrio
  'Esgrima Pass', 'Sao Paulo Pass', 'Leg Weave Pass', 'Shin Slide Pass',
  'Roll Under Pass', 'Folding Pass', 'Wilson Pass', 'Running Pass',
  'Headquarters Pass', 'Hip Escape Pass Counter', 'Knee Tap Pass',
  'Torreando to Mount', 'Knee Cut to Mount'
);

-- =====================================================================
-- ESTRANGULACIONES (no triángulos ni guillotinas)
-- =====================================================================
UPDATE technique SET family = 'CHOKES' WHERE owner_id = 1 AND name IN (
  'Rear Naked Choke', 'Bow and Arrow Choke', 'Cross Collar Choke', 'Baseball Bat Choke',
  'Ezekiel Choke', 'North-South Choke', 'Clock Choke', 'Loop Choke', 'Brabo Choke',
  'Paper Cutter Choke', 'Bread Cutter Choke', 'Lapel Choke', 'Buggy Choke',
  'Shoulder Choke', 'Scarf Choke', 'Truck Roll Choke', 'Crucifix Choke', 'Von Flue Choke',
  'Peruvian Necktie', 'Twister', 'Neck Crank', 'Darce from Turtle',
  'Arm Triangle Choke', 'Arm Crush'
);

-- =====================================================================
-- GUILLOTINAS
-- =====================================================================
UPDATE technique SET family = 'GUILLOTINES' WHERE owner_id = 1 AND name IN (
  'Guillotine Choke', 'Arm-In Guillotine', 'High Elbow Guillotine', 'Marcelotine'
);

-- =====================================================================
-- TRIÁNGULOS
-- =====================================================================
UPDATE technique SET family = 'TRIANGLES' WHERE owner_id = 1 AND name IN (
  'Triangle Choke', 'Mounted Triangle', 'Reverse Triangle', 'Mount to Triangle',
  'Triangle Defense'
);

-- =====================================================================
-- ARMBARS
-- =====================================================================
UPDATE technique SET family = 'ARMBARS' WHERE owner_id = 1 AND name IN (
  'Armbar', 'Armbar from Mount', 'Spinning Armbar', 'Inverted Armbar',
  'Baratoplata', 'Monoplata', 'Gogoplata',
  'Knee On Belly to Armbar', 'Armbar Defense'
);

-- =====================================================================
-- KIMURA / AMERICANA / SHOULDER LOCKS
-- =====================================================================
UPDATE technique SET family = 'SHOULDER_LOCKS' WHERE owner_id = 1 AND name IN (
  'Kimura', 'Kimura from Guard', 'Kimura from Back', 'Kimura Defense to Guard',
  'North-South to Kimura Trap',
  'Americana', 'Omoplata', 'Shoulder Lock', 'Hammerlock',
  'Wristlock', 'Hip Lock', 'Bicep Slicer', 'Compression Lock',
  'Estima Lock'
);

-- =====================================================================
-- DERRIBOS
-- Bombero (Fireman's Carry), Single Leg, Double Leg, Uchi Mata, etc.
-- =====================================================================
UPDATE technique SET family = 'TAKEDOWNS' WHERE owner_id = 1 AND name IN (
  'Double Leg Takedown', 'Single Leg Takedown', 'High Crotch',
  'Ankle Pick', 'Knee Pick',
  'Hip Throw', 'Shoulder Throw', 'Harai Goshi', 'Uchi Mata',
  'Seoi Otoshi', 'Inside Trip', 'Outside Trip', 'Foot Sweep', 'Kouchi Gari',
  'Snap Down', 'Duck Under', 'Arm Drag to Back Take',
  'Fireman Carry', 'Suplex', 'Guard Pull',
  'Tomoe Nage', 'Sacrifice Throw'
);

-- =====================================================================
-- BARRIDAS (que no encajan en guardia específica)
-- =====================================================================
UPDATE technique SET family = 'SWEEPS' WHERE owner_id = 1 AND name IN (
  'Bridge and Roll', 'Stand Up Sweep', 'Technical Stand Sweep',
  'Double Ankle Sweep', 'Waiter Sweep', 'Sickle Sweep',
  'Hip Bump Sweep', 'Collar Drag Sweep'
);

-- =====================================================================
-- TOMAS DE ESPALDA
-- =====================================================================
UPDATE technique SET family = 'BACK_TAKES' WHERE owner_id = 1 AND name IN (
  'Dogfight Back Take', 'Guard to Back Take',
  'Side Control to Back Take', 'Mount to Back Take', 'Leg Drag to Back',
  'North-South Back Roll',
  'Back to Mount', 'Turtle to Crucifix', 'Side Control to Crucifix',
  'Berimbolo Back Take', 'Deep Half Back Take'
);

-- =====================================================================
-- ESCAPADAS
-- =====================================================================
UPDATE technique SET family = 'ESCAPES' WHERE owner_id = 1 AND name IN (
  'Elbow-Knee Escape', 'Shrimp to Guard', 'Back Escape', 'Back Defense Hip Escape',
  'Granby Roll', 'Turtle Granby', 'Turtle Guard Recovery', 'Turtle to Guard Recovery',
  'North-South Escape', 'Scarf Hold Escape', 'Knee On Belly Escape',
  'Standing Up from Bottom', 'Choke Defense', 'Triangle Defense',
  'Armbar Defense', 'Kimura Defense to Guard', 'Heel Hook Defense',
  'Side Control Knee-In', 'Side Control to North-South', 'Side Control to Mount'
);

-- =====================================================================
-- OTHER: transiciones sin categoría clara + cualquier técnica sin familia
-- =====================================================================
UPDATE technique SET family = 'OTHER' WHERE owner_id = 1 AND name IN (
  'Leg Pummeling', 'Mount to Knee On Belly',
  'North-South to Kimura Trap', 'Side Control to Crucifix'
);

UPDATE technique SET family = 'OTHER' WHERE owner_id = 1 AND family IS NULL;
