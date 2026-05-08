-- V222__add_technique_family.sql
-- Añade columna family a technique y asigna familias a todas las técnicas del seed.

ALTER TABLE technique ADD COLUMN family VARCHAR(30);

-- =====================================================================
-- CHOKES (estrangulaciones que no son triángulos ni guillotinas)
-- =====================================================================
UPDATE technique SET family = 'CHOKES' WHERE owner_id = 1 AND name IN (
  'Rear Naked Choke', 'Bow and Arrow Choke', 'Cross Collar Choke', 'Baseball Bat Choke',
  'Ezekiel Choke', 'North-South Choke', 'Clock Choke', 'Loop Choke', 'Brabo Choke',
  'Paper Cutter Choke', 'Bread Cutter Choke', 'Lapel Choke', 'Buggy Choke',
  'Shoulder Choke', 'Scarf Choke', 'Truck Roll Choke', 'Crucifix Choke', 'Von Flue Choke',
  'Peruvian Necktie', 'Twister', 'Neck Crank', 'Darce from Turtle'
);

-- =====================================================================
-- TRIANGLES
-- =====================================================================
UPDATE technique SET family = 'TRIANGLES' WHERE owner_id = 1 AND name IN (
  'Triangle Choke', 'Mounted Triangle', 'Reverse Triangle', 'Mount to Triangle',
  'Triangle Defense'
);

-- =====================================================================
-- GUILLOTINES
-- =====================================================================
UPDATE technique SET family = 'GUILLOTINES' WHERE owner_id = 1 AND name IN (
  'Guillotine Choke', 'Arm-In Guillotine', 'High Elbow Guillotine', 'Marcelotine'
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
-- SHOULDER LOCKS (kimuras, americanas, omoplatas, wristlocks)
-- =====================================================================
UPDATE technique SET family = 'SHOULDER_LOCKS' WHERE owner_id = 1 AND name IN (
  'Kimura', 'Kimura from Guard', 'Kimura from Back', 'Kimura Defense to Guard',
  'North-South to Kimura Trap',
  'Americana', 'Arm Triangle Choke', 'Arm Crush',
  'Omoplata', 'Shoulder Lock', 'Hammerlock',
  'Wristlock', 'Hip Lock', 'Bicep Slicer', 'Compression Lock',
  'Estima Lock'
);

-- =====================================================================
-- LEG LOCKS
-- =====================================================================
UPDATE technique SET family = 'LEG_LOCKS' WHERE owner_id = 1 AND name IN (
  'Inside Heel Hook', 'Outside Heel Hook', 'Outside Heel Hook 50/50',
  'Ankle Lock from 50/50', 'Straight Ankle Lock', 'Kneebar', 'Kneebar from Top',
  'Toe Hold', 'Toehold from Half Guard', 'Calf Slicer', 'Electric Chair',
  'SLX to Saddle', 'Heel Hook from Saddle', 'Heel Hook from Double Outside',
  'Ashi Garami to 50/50', 'Heel Hook Defense',
  'Side Control to Leg Lock', 'Turtle to Leg Lock Entry'
);

-- =====================================================================
-- SWEEPS
-- =====================================================================
UPDATE technique SET family = 'SWEEPS' WHERE owner_id = 1 AND name IN (
  'Scissor Sweep', 'Butterfly Sweep', 'Hip Bump Sweep', 'Elevator Sweep',
  'Flower Sweep', 'Pendulum Sweep', 'De La Riva Sweep', 'Reverse DLR Sweep',
  'Lasso Pendulum Sweep', 'Lasso Back Take Sweep', 'Collar Drag Sweep',
  'Collar Sleeve Sweep', 'Overhead Sweep', 'Double Ankle Sweep',
  'Tripod Sweep', 'Waiter Sweep', 'Old School Sweep', 'Sickle Sweep',
  'Deep Half Sweep', 'Deep Half Back Take',
  'Half Guard Back Sweep', 'Hook Sweep', 'Knee Shield Sweep',
  'K-Guard Sweep', 'Stand Up Sweep', 'Technical Stand Sweep',
  'Single Leg X Sweep', 'X-Guard Sweep', 'X-Guard Back Sweep',
  'Shin-to-Shin Sweep', 'Koga Sweep', 'Spider Guard Sweep',
  'Spider Tomo Sweep', 'Worm Guard Sweep', 'John Wayne Sweep',
  'Tomoe Nage', 'Sacrifice Throw', 'Bridge and Roll'
);

-- =====================================================================
-- GUARD PASSES
-- =====================================================================
UPDATE technique SET family = 'GUARD_PASSES' WHERE owner_id = 1 AND name IN (
  'Torreando Pass', 'Toreando 3/4 Pass', 'X-Pass', 'Knee Slice Pass',
  'Backstep Pass', 'Leg Drag Pass', 'Over-Under Pass', 'Double Under Pass',
  'Stack Pass', 'Smash Pass', 'Pressure Pass', 'Float Pass', 'Cartwheel Pass',
  'Long Step Pass', 'Body Lock Pass', 'Drive Through Pass', 'Half Guard Pass',
  'Half Guard Top Pass', 'Forced Half Pass', 'Reverse Half Pass',
  'Lasso Guard Pass', 'Spider Guard Pass', 'Esgrima Pass', 'Sao Paulo Pass',
  'Leg Weave Pass', 'Shin Slide Pass', 'Roll Under Pass', 'Folding Pass',
  'Wilson Pass', 'Running Pass', 'Headquarters Pass', 'Hip Escape Pass Counter',
  'Knee Tap Pass'
);

-- =====================================================================
-- TAKEDOWNS
-- =====================================================================
UPDATE technique SET family = 'TAKEDOWNS' WHERE owner_id = 1 AND name IN (
  'Double Leg Takedown', 'Single Leg Takedown', 'High Crotch',
  'Ankle Pick', 'Knee Pick', 'Knee Tap Pass',
  'Hip Throw', 'Shoulder Throw', 'Harai Goshi', 'Uchi Mata',
  'Seoi Otoshi', 'Inside Trip', 'Outside Trip', 'Foot Sweep', 'Kouchi Gari',
  'Snap Down', 'Duck Under', 'Arm Drag to Back Take',
  'Fireman Carry', 'Suplex', 'Guard Pull'
);

-- =====================================================================
-- BACK TAKES
-- =====================================================================
UPDATE technique SET family = 'BACK_TAKES' WHERE owner_id = 1 AND name IN (
  'Berimbolo Back Take', 'Dogfight Back Take', 'Guard to Back Take',
  'Side Control to Back Take', 'Mount to Back Take', 'Leg Drag to Back',
  'DLR to Berimbolo', 'North-South Back Roll', 'Butterfly to X-Guard',
  'Closed Guard to DLR', 'Closed Guard to Butterfly', 'Closed Guard to Spider',
  'DLR to Single Leg X', 'Back to Mount',
  'Turtle to Crucifix', 'Side Control to Crucifix'
);

-- =====================================================================
-- ESCAPES / DEFENSIVE
-- =====================================================================
UPDATE technique SET family = 'ESCAPES' WHERE owner_id = 1 AND name IN (
  'Elbow-Knee Escape', 'Shrimp to Guard', 'Bridge and Roll',
  'Back Escape', 'Back Defense Hip Escape', 'Guard to Turtle',
  'Granby Roll', 'Turtle Granby', 'Turtle Guard Recovery',
  'Turtle to Guard Recovery', 'North-South Escape', 'Scarf Hold Escape',
  'Knee On Belly Escape', 'Mount Escape to Deep Half',
  'Standing Up from Bottom', 'Choke Defense', 'Triangle Defense',
  'Armbar Defense', 'Kimura Defense to Guard', 'Heel Hook Defense',
  'Half Guard to Deep Half', 'Half Guard to Z-Guard',
  'Side Control Knee-In', 'Side Control to North-South', 'Side Control to Mount'
);

-- =====================================================================
-- OTHER (transiciones y movimientos que no encajan en familia clara)
-- =====================================================================
UPDATE technique SET family = 'OTHER' WHERE owner_id = 1 AND name IN (
  'Leg Pummeling', 'Guard to Turtle', 'Half Guard to Deep Half',
  'Torreando to Mount', 'Knee Cut to Mount', 'Mount to Knee On Belly',
  'Side Control to Crucifix', 'North-South to Kimura Trap'
);

-- Todo lo que no tenga familia asignada → OTHER
UPDATE technique SET family = 'OTHER' WHERE owner_id = 1 AND family IS NULL;
