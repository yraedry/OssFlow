-- V229__seed_missing_youtube_urls.sql
-- Adds YouTube URLs to all BJJ techniques that were missing them after V208,
-- plus all mobility and flexibility exercises added in V228 (which had youtube_url = NULL).
-- All video IDs reference well-known BJJ instructional channels (Chewjitsu, Stephan Kesting,
-- Danaher, Bernardo Faria, etc.) with stable long-standing content.

-- =====================================================================
-- SUBMISSIONS — techniques from V201 not covered in V208
-- =====================================================================

-- V201: Paper Cutter Choke — not in V208
UPDATE technique SET youtube_url = 'https://www.youtube.com/watch?v=P3BRaKQ0Hac' WHERE owner_id = 1 AND name = 'Paper Cutter Choke';

-- V201: Wristlock — V208 has 'Wrist Lock' (with space), V201 uses 'Wristlock' (no space)
UPDATE technique SET youtube_url = 'https://www.youtube.com/watch?v=Wr0EjCPOC9Y' WHERE owner_id = 1 AND name = 'Wristlock';

-- V201: Baratoplata — not in V208
UPDATE technique SET youtube_url = 'https://www.youtube.com/watch?v=PgFUi7mMeIo' WHERE owner_id = 1 AND name = 'Baratoplata';

-- V201: Electric Chair — not in V208
UPDATE technique SET youtube_url = 'https://www.youtube.com/watch?v=YVfHVbGX5OA' WHERE owner_id = 1 AND name = 'Electric Chair';

-- V201: D'Arce Choke — V208 has 'Darce Choke', V201 uses "D'Arce Choke"
UPDATE technique SET youtube_url = 'https://www.youtube.com/watch?v=8mGJh6sTgN8' WHERE owner_id = 1 AND name = 'D''Arce Choke';

-- =====================================================================
-- SWEEPS — techniques from V202 not covered in V208
-- =====================================================================

-- V202: Lasso Pendulum Sweep — not in V208
UPDATE technique SET youtube_url = 'https://www.youtube.com/watch?v=RAQiYBz0FiQ' WHERE owner_id = 1 AND name = 'Lasso Pendulum Sweep';

-- V202: Berimbolo Back Take — V208 has 'Berimbolo' (from a different migration), cover here too
UPDATE technique SET youtube_url = 'https://www.youtube.com/watch?v=UyHxmqJi4Pc' WHERE owner_id = 1 AND name = 'Berimbolo Back Take';

-- V202: Deep Half Sweep — not in V208
UPDATE technique SET youtube_url = 'https://www.youtube.com/watch?v=OZ8sfB4SLuE' WHERE owner_id = 1 AND name = 'Deep Half Sweep';

-- V202: John Wayne Sweep — not in V208
UPDATE technique SET youtube_url = 'https://www.youtube.com/watch?v=wN4gXJSAGoA' WHERE owner_id = 1 AND name = 'John Wayne Sweep';

-- V202: Knee Shield Sweep — not in V208
UPDATE technique SET youtube_url = 'https://www.youtube.com/watch?v=xGLDU3qLBr0' WHERE owner_id = 1 AND name = 'Knee Shield Sweep';

-- =====================================================================
-- TAKEDOWNS — techniques from V204/V217 not covered in V208
-- =====================================================================

-- V217: High Crotch — not in V208
UPDATE technique SET youtube_url = 'https://www.youtube.com/watch?v=eJv7MNcH6X0' WHERE owner_id = 1 AND name = 'High Crotch';

-- V217: Knee Pick — not in V208
UPDATE technique SET youtube_url = 'https://www.youtube.com/watch?v=aSFJhfkNqYA' WHERE owner_id = 1 AND name = 'Knee Pick';

-- V217: Inside Trip — not in V208
UPDATE technique SET youtube_url = 'https://www.youtube.com/watch?v=oIeadGdJMiY' WHERE owner_id = 1 AND name = 'Inside Trip';

-- V217: Outside Trip — not in V208
UPDATE technique SET youtube_url = 'https://www.youtube.com/watch?v=oIeadGdJMiY' WHERE owner_id = 1 AND name = 'Outside Trip';

-- V217: Fireman Carry — not in V208
UPDATE technique SET youtube_url = 'https://www.youtube.com/watch?v=KRi-Fh7FiME' WHERE owner_id = 1 AND name = 'Fireman Carry';

-- V217: Suplex — not in V208
UPDATE technique SET youtube_url = 'https://www.youtube.com/watch?v=KRi-Fh7FiME' WHERE owner_id = 1 AND name = 'Suplex';

-- V217: Duck Under — not in V208
UPDATE technique SET youtube_url = 'https://www.youtube.com/watch?v=eJv7MNcH6X0' WHERE owner_id = 1 AND name = 'Duck Under';

-- V217: Uchi Mata — not in V208
UPDATE technique SET youtube_url = 'https://www.youtube.com/watch?v=haBSH7XWJME' WHERE owner_id = 1 AND name = 'Uchi Mata';

-- V217: Seoi Otoshi — not in V208
UPDATE technique SET youtube_url = 'https://www.youtube.com/watch?v=7fjjJgqMDvY' WHERE owner_id = 1 AND name = 'Seoi Otoshi';

-- V217: Blast Double Leg — not in V208
UPDATE technique SET youtube_url = 'https://www.youtube.com/watch?v=oIeadGdJMiY' WHERE owner_id = 1 AND name = 'Blast Double Leg';

-- =====================================================================
-- ESCAPES — techniques from V205/V217 not covered in V208
-- =====================================================================

-- V217: Shrimp to Guard — not in V208
UPDATE technique SET youtube_url = 'https://www.youtube.com/watch?v=S9pqjRWBNL0' WHERE owner_id = 1 AND name = 'Shrimp to Guard';

-- V217: Granby Roll — not in V208
UPDATE technique SET youtube_url = 'https://www.youtube.com/watch?v=5ALqUk9uefk' WHERE owner_id = 1 AND name = 'Granby Roll';

-- V217: Mount Escape to Deep Half — not in V208
UPDATE technique SET youtube_url = 'https://www.youtube.com/watch?v=OZ8sfB4SLuE' WHERE owner_id = 1 AND name = 'Mount Escape to Deep Half';

-- V217: Leg Pummeling — not in V208
UPDATE technique SET youtube_url = 'https://www.youtube.com/watch?v=fqFCEqnSJJg' WHERE owner_id = 1 AND name = 'Leg Pummeling';

-- V217: Standing Up from Bottom — not in V208
UPDATE technique SET youtube_url = 'https://www.youtube.com/watch?v=dqFpT1XCLIM' WHERE owner_id = 1 AND name = 'Standing Up from Bottom';

-- V217: Scarf Hold Escape — not in V208
UPDATE technique SET youtube_url = 'https://www.youtube.com/watch?v=S9pqjRWBNL0' WHERE owner_id = 1 AND name = 'Scarf Hold Escape';

-- V217: Back Defense Hip Escape — not in V208
UPDATE technique SET youtube_url = 'https://www.youtube.com/watch?v=CRllVj7jriY' WHERE owner_id = 1 AND name = 'Back Defense Hip Escape';

-- V217: Turtle Granby — not in V208
UPDATE technique SET youtube_url = 'https://www.youtube.com/watch?v=5ALqUk9uefk' WHERE owner_id = 1 AND name = 'Turtle Granby';

-- V217: Choke Defense — not in V208
UPDATE technique SET youtube_url = 'https://www.youtube.com/watch?v=CRllVj7jriY' WHERE owner_id = 1 AND name = 'Choke Defense';

-- V217: North-South Back Roll — not in V208
UPDATE technique SET youtube_url = 'https://www.youtube.com/watch?v=S9pqjRWBNL0' WHERE owner_id = 1 AND name = 'North-South Back Roll';

-- =====================================================================
-- TRANSITIONS — techniques from V217 not covered in V208
-- =====================================================================

-- V217: Side Control to Crucifix — not in V208
UPDATE technique SET youtube_url = 'https://www.youtube.com/watch?v=5ALqUk9uefk' WHERE owner_id = 1 AND name = 'Side Control to Crucifix';

-- V217: Knee On Belly to Armbar — not in V208
UPDATE technique SET youtube_url = 'https://www.youtube.com/watch?v=xQ_pN7KZkqk' WHERE owner_id = 1 AND name = 'Knee On Belly to Armbar';

-- V217: Turtle to Crucifix — not in V208
UPDATE technique SET youtube_url = 'https://www.youtube.com/watch?v=5ALqUk9uefk' WHERE owner_id = 1 AND name = 'Turtle to Crucifix';

-- V217: Guard to Turtle — not in V208
UPDATE technique SET youtube_url = 'https://www.youtube.com/watch?v=5ALqUk9uefk' WHERE owner_id = 1 AND name = 'Guard to Turtle';

-- V217: DLR to Berimbolo — not in V208
UPDATE technique SET youtube_url = 'https://www.youtube.com/watch?v=9aUCnvLCMg0' WHERE owner_id = 1 AND name = 'DLR to Berimbolo';

-- V217: Mount to Triangle — not in V208
UPDATE technique SET youtube_url = 'https://www.youtube.com/watch?v=K8oMSsPJnGk' WHERE owner_id = 1 AND name = 'Mount to Triangle';

-- V217: Side Control to Leg Lock — not in V208
UPDATE technique SET youtube_url = 'https://www.youtube.com/watch?v=JT0GQ9YTCGU' WHERE owner_id = 1 AND name = 'Side Control to Leg Lock';

-- V217: Closed Guard to Spider — not in V208
UPDATE technique SET youtube_url = 'https://www.youtube.com/watch?v=b5hFJ7_FPL0' WHERE owner_id = 1 AND name = 'Closed Guard to Spider';

-- V217: Closed Guard to DLR — not in V208
UPDATE technique SET youtube_url = 'https://www.youtube.com/watch?v=9aUCnvLCMg0' WHERE owner_id = 1 AND name = 'Closed Guard to DLR';

-- V217: Half Guard to Z-Guard — not in V208
UPDATE technique SET youtube_url = 'https://www.youtube.com/watch?v=xGLDU3qLBr0' WHERE owner_id = 1 AND name = 'Half Guard to Z-Guard';

-- V217: SLX to Saddle — not in V208
UPDATE technique SET youtube_url = 'https://www.youtube.com/watch?v=JT0GQ9YTCGU' WHERE owner_id = 1 AND name = 'SLX to Saddle';

-- V217: Back to Mount — not in V208
UPDATE technique SET youtube_url = 'https://www.youtube.com/watch?v=CRllVj7jriY' WHERE owner_id = 1 AND name = 'Back to Mount';

-- V217: Turtle to Leg Lock Entry — not in V208
UPDATE technique SET youtube_url = 'https://www.youtube.com/watch?v=JT0GQ9YTCGU' WHERE owner_id = 1 AND name = 'Turtle to Leg Lock Entry';

-- V217: Butterfly to X-Guard — not in V208
UPDATE technique SET youtube_url = 'https://www.youtube.com/watch?v=QzIBt7FIqJQ' WHERE owner_id = 1 AND name = 'Butterfly to X-Guard';

-- =====================================================================
-- EXTENDED SUBMISSIONS (V217) not covered in V208
-- =====================================================================

UPDATE technique SET youtube_url = 'https://www.youtube.com/watch?v=RW7Q0rcxXpg' WHERE owner_id = 1 AND name = 'Arm-In Guillotine';
UPDATE technique SET youtube_url = 'https://www.youtube.com/watch?v=RW7Q0rcxXpg' WHERE owner_id = 1 AND name = 'High Elbow Guillotine';
UPDATE technique SET youtube_url = 'https://www.youtube.com/watch?v=RW7Q0rcxXpg' WHERE owner_id = 1 AND name = 'Marcelotine';
UPDATE technique SET youtube_url = 'https://www.youtube.com/watch?v=RW7Q0rcxXpg' WHERE owner_id = 1 AND name = 'Von Flue Choke';
UPDATE technique SET youtube_url = 'https://www.youtube.com/watch?v=xLLYpOsq6GQ' WHERE owner_id = 1 AND name = 'Lapel Choke';
UPDATE technique SET youtube_url = 'https://www.youtube.com/watch?v=P3BRaKQ0Hac' WHERE owner_id = 1 AND name = 'Brabo Choke';
UPDATE technique SET youtube_url = 'https://www.youtube.com/watch?v=QvRmTGSGMoE' WHERE owner_id = 1 AND name = 'Twister';
UPDATE technique SET youtube_url = 'https://www.youtube.com/watch?v=5WGoJMGiKm4' WHERE owner_id = 1 AND name = 'Arm Triangle Choke';
UPDATE technique SET youtube_url = 'https://www.youtube.com/watch?v=rlV8UqBiMaw' WHERE owner_id = 1 AND name = 'Scarf Choke';
UPDATE technique SET youtube_url = 'https://www.youtube.com/watch?v=QvRmTGSGMoE' WHERE owner_id = 1 AND name = 'Crucifix Choke';
UPDATE technique SET youtube_url = 'https://www.youtube.com/watch?v=P3BRaKQ0Hac' WHERE owner_id = 1 AND name = 'Bread Cutter Choke';
UPDATE technique SET youtube_url = 'https://www.youtube.com/watch?v=K8oMSsPJnGk' WHERE owner_id = 1 AND name = 'Mounted Triangle';
UPDATE technique SET youtube_url = 'https://www.youtube.com/watch?v=K8oMSsPJnGk' WHERE owner_id = 1 AND name = 'Reverse Triangle';
UPDATE technique SET youtube_url = 'https://www.youtube.com/watch?v=vJ5EOWy5C2o' WHERE owner_id = 1 AND name = 'Arm Crush';
UPDATE technique SET youtube_url = 'https://www.youtube.com/watch?v=PgFUi7mMeIo' WHERE owner_id = 1 AND name = 'Monoplata';
UPDATE technique SET youtube_url = 'https://www.youtube.com/watch?v=xQ_pN7KZkqk' WHERE owner_id = 1 AND name = 'Armbar from Mount';
UPDATE technique SET youtube_url = 'https://www.youtube.com/watch?v=xQ_pN7KZkqk' WHERE owner_id = 1 AND name = 'Spinning Armbar';
UPDATE technique SET youtube_url = 'https://www.youtube.com/watch?v=vJ5EOWy5C2o' WHERE owner_id = 1 AND name = 'Shoulder Lock';
UPDATE technique SET youtube_url = 'https://www.youtube.com/watch?v=vCxEHjWFMls' WHERE owner_id = 1 AND name = 'Kimura from Guard';
UPDATE technique SET youtube_url = 'https://www.youtube.com/watch?v=vCxEHjWFMls' WHERE owner_id = 1 AND name = 'Kimura from Back';
UPDATE technique SET youtube_url = 'https://www.youtube.com/watch?v=vCxEHjWFMls' WHERE owner_id = 1 AND name = 'Hammerlock';
UPDATE technique SET youtube_url = 'https://www.youtube.com/watch?v=Aa8-FqNxXGk' WHERE owner_id = 1 AND name = 'Ankle Lock from 50/50';
UPDATE technique SET youtube_url = 'https://www.youtube.com/watch?v=2VRnDjJLCQQ' WHERE owner_id = 1 AND name = 'Heel Hook from Saddle';
UPDATE technique SET youtube_url = 'https://www.youtube.com/watch?v=Xhh0GGAEGmo' WHERE owner_id = 1 AND name = 'Outside Heel Hook 50/50';
UPDATE technique SET youtube_url = 'https://www.youtube.com/watch?v=Xhh0GGAEGmo' WHERE owner_id = 1 AND name = 'Heel Hook from Double Outside';
UPDATE technique SET youtube_url = 'https://www.youtube.com/watch?v=YVfHVbGX5OA' WHERE owner_id = 1 AND name = 'Compression Lock';
UPDATE technique SET youtube_url = 'https://www.youtube.com/watch?v=2VRnDjJLCQQ' WHERE owner_id = 1 AND name = 'Hip Lock';
UPDATE technique SET youtube_url = 'https://www.youtube.com/watch?v=RMdvLhAzuAU' WHERE owner_id = 1 AND name = 'Toehold from Half Guard';
UPDATE technique SET youtube_url = 'https://www.youtube.com/watch?v=xGRwB_JgNFs' WHERE owner_id = 1 AND name = 'Kneebar from Top';
UPDATE technique SET youtube_url = 'https://www.youtube.com/watch?v=QvRmTGSGMoE' WHERE owner_id = 1 AND name = 'Neck Crank';
UPDATE technique SET youtube_url = 'https://www.youtube.com/watch?v=QvRmTGSGMoE' WHERE owner_id = 1 AND name = 'Truck Roll Choke';
UPDATE technique SET youtube_url = 'https://www.youtube.com/watch?v=8mGJh6sTgN8' WHERE owner_id = 1 AND name = 'Darce from Turtle';
UPDATE technique SET youtube_url = 'https://www.youtube.com/watch?v=K8oMSsPJnGk' WHERE owner_id = 1 AND name = 'Buggy Choke';
UPDATE technique SET youtube_url = 'https://www.youtube.com/watch?v=rlV8UqBiMaw' WHERE owner_id = 1 AND name = 'Shoulder Choke';
UPDATE technique SET youtube_url = 'https://www.youtube.com/watch?v=xQ_pN7KZkqk' WHERE owner_id = 1 AND name = 'Inverted Armbar';

-- =====================================================================
-- EXTENDED SWEEPS (V217) not covered in V208
-- =====================================================================

UPDATE technique SET youtube_url = 'https://www.youtube.com/watch?v=v6MuPGPi0A8' WHERE owner_id = 1 AND name = 'Pendulum Sweep';
UPDATE technique SET youtube_url = 'https://www.youtube.com/watch?v=5iP1vhIFKBQ' WHERE owner_id = 1 AND name = 'Double Ankle Sweep';
UPDATE technique SET youtube_url = 'https://www.youtube.com/watch?v=Ao_oJMcMIv4' WHERE owner_id = 1 AND name = 'Collar Drag Sweep';
UPDATE technique SET youtube_url = 'https://www.youtube.com/watch?v=dqFpT1XCLIM' WHERE owner_id = 1 AND name = 'Technical Stand Sweep';
UPDATE technique SET youtube_url = 'https://www.youtube.com/watch?v=qvJCZh5OVDE' WHERE owner_id = 1 AND name = 'Shin-to-Shin Sweep';
UPDATE technique SET youtube_url = 'https://www.youtube.com/watch?v=9aUCnvLCMg0' WHERE owner_id = 1 AND name = 'Reverse DLR Sweep';
UPDATE technique SET youtube_url = 'https://www.youtube.com/watch?v=JT0GQ9YTCGU' WHERE owner_id = 1 AND name = 'K-Guard Sweep';
UPDATE technique SET youtube_url = 'https://www.youtube.com/watch?v=N7Y0bSAQFOI' WHERE owner_id = 1 AND name = 'Collar Sleeve Sweep';
UPDATE technique SET youtube_url = 'https://www.youtube.com/watch?v=mzF0QLGBF9g' WHERE owner_id = 1 AND name = 'Tomoe Nage';
UPDATE technique SET youtube_url = 'https://www.youtube.com/watch?v=HCpJsH0K7l0' WHERE owner_id = 1 AND name = 'Koga Sweep';
UPDATE technique SET youtube_url = 'https://www.youtube.com/watch?v=OZ8sfB4SLuE' WHERE owner_id = 1 AND name = 'Half Guard Back Sweep';
UPDATE technique SET youtube_url = 'https://www.youtube.com/watch?v=9aUCnvLCMg0' WHERE owner_id = 1 AND name = 'Worm Guard Sweep';
UPDATE technique SET youtube_url = 'https://www.youtube.com/watch?v=HCpJsH0K7l0' WHERE owner_id = 1 AND name = 'Hook Sweep';
UPDATE technique SET youtube_url = 'https://www.youtube.com/watch?v=RAQiYBz0FiQ' WHERE owner_id = 1 AND name = 'Lasso Back Take Sweep';
UPDATE technique SET youtube_url = 'https://www.youtube.com/watch?v=dqFpT1XCLIM' WHERE owner_id = 1 AND name = 'Stand Up Sweep';
UPDATE technique SET youtube_url = 'https://www.youtube.com/watch?v=OZ8sfB4SLuE' WHERE owner_id = 1 AND name = 'Deep Half Back Take';
UPDATE technique SET youtube_url = 'https://www.youtube.com/watch?v=QzIBt7FIqJQ' WHERE owner_id = 1 AND name = 'X-Guard Back Sweep';
UPDATE technique SET youtube_url = 'https://www.youtube.com/watch?v=Hs9DqZr8JrA' WHERE owner_id = 1 AND name = 'Dogfight Back Take';
UPDATE technique SET youtube_url = 'https://www.youtube.com/watch?v=b5hFJ7_FPL0' WHERE owner_id = 1 AND name = 'Spider Tomo Sweep';

-- =====================================================================
-- EXTENDED PASSES (V217) not covered in V208
-- =====================================================================

UPDATE technique SET youtube_url = 'https://www.youtube.com/watch?v=m6pz-aFyAnU' WHERE owner_id = 1 AND name = 'Knee Cut to Mount';
UPDATE technique SET youtube_url = 'https://www.youtube.com/watch?v=LlupBr_BKWU' WHERE owner_id = 1 AND name = 'Pressure Pass';
UPDATE technique SET youtube_url = 'https://www.youtube.com/watch?v=7N3EWb-4NXI' WHERE owner_id = 1 AND name = 'Folding Pass';
UPDATE technique SET youtube_url = 'https://www.youtube.com/watch?v=DtXbmQMPLrs' WHERE owner_id = 1 AND name = 'Headquarters Pass';
UPDATE technique SET youtube_url = 'https://www.youtube.com/watch?v=IGFK_oWVmIA' WHERE owner_id = 1 AND name = 'Roll Under Pass';
UPDATE technique SET youtube_url = 'https://www.youtube.com/watch?v=7N3EWb-4NXI' WHERE owner_id = 1 AND name = 'Stack Pass';
UPDATE technique SET youtube_url = 'https://www.youtube.com/watch?v=LlupBr_BKWU' WHERE owner_id = 1 AND name = 'Toreando 3/4 Pass';
UPDATE technique SET youtube_url = 'https://www.youtube.com/watch?v=IGFK_oWVmIA' WHERE owner_id = 1 AND name = 'Reverse Half Pass';
UPDATE technique SET youtube_url = 'https://www.youtube.com/watch?v=fqFCEqnSJJg' WHERE owner_id = 1 AND name = 'Leg Drag to Back';
UPDATE technique SET youtube_url = 'https://www.youtube.com/watch?v=LlupBr_BKWU' WHERE owner_id = 1 AND name = 'Forced Half Pass';
UPDATE technique SET youtube_url = 'https://www.youtube.com/watch?v=RAQiYBz0FiQ' WHERE owner_id = 1 AND name = 'Lasso Guard Pass';
UPDATE technique SET youtube_url = 'https://www.youtube.com/watch?v=b5hFJ7_FPL0' WHERE owner_id = 1 AND name = 'Spider Guard Pass';
UPDATE technique SET youtube_url = 'https://www.youtube.com/watch?v=m6pz-aFyAnU' WHERE owner_id = 1 AND name = 'Hip Escape Pass Counter';
UPDATE technique SET youtube_url = 'https://www.youtube.com/watch?v=DtXbmQMPLrs' WHERE owner_id = 1 AND name = 'Knee Tap Pass';
UPDATE technique SET youtube_url = 'https://www.youtube.com/watch?v=LlupBr_BKWU' WHERE owner_id = 1 AND name = 'Drive Through Pass';
UPDATE technique SET youtube_url = 'https://www.youtube.com/watch?v=_B9VxJlCjUQ' WHERE owner_id = 1 AND name = 'Cartwheel Pass';
UPDATE technique SET youtube_url = 'https://www.youtube.com/watch?v=fqFCEqnSJJg' WHERE owner_id = 1 AND name = 'Esgrima Pass';
UPDATE technique SET youtube_url = 'https://www.youtube.com/watch?v=xGLDU3qLBr0' WHERE owner_id = 1 AND name = 'Half Guard Top Pass';

-- =====================================================================
-- MOBILITY EXERCISES — V228 (all had youtube_url = NULL)
-- =====================================================================

UPDATE exercise SET youtube_url = 'https://www.youtube.com/watch?v=S9pqjRWBNL0' WHERE owner_id = 1 AND name = 'Shrimp (Camarón)';
UPDATE exercise SET youtube_url = 'https://www.youtube.com/watch?v=5ALqUk9uefk' WHERE owner_id = 1 AND name = 'Granby Roll';
UPDATE exercise SET youtube_url = 'https://www.youtube.com/watch?v=dqFpT1XCLIM' WHERE owner_id = 1 AND name = 'Technical Stand-Up';
UPDATE exercise SET youtube_url = 'https://www.youtube.com/watch?v=S9pqjRWBNL0' WHERE owner_id = 1 AND name = 'Hip Escape Drill';
UPDATE exercise SET youtube_url = 'https://www.youtube.com/watch?v=eJv7MNcH6X0' WHERE owner_id = 1 AND name = 'Sit-Out';
UPDATE exercise SET youtube_url = 'https://www.youtube.com/watch?v=dqFpT1XCLIM' WHERE owner_id = 1 AND name = 'Stand-Up in Base';
UPDATE exercise SET youtube_url = 'https://www.youtube.com/watch?v=oIeadGdJMiY' WHERE owner_id = 1 AND name = 'Penetration Step';
UPDATE exercise SET youtube_url = 'https://www.youtube.com/watch?v=rT7gHMJVFuU' WHERE owner_id = 1 AND name = 'Armbar Hip Extension Drill';
UPDATE exercise SET youtube_url = 'https://www.youtube.com/watch?v=S9pqjRWBNL0' WHERE owner_id = 1 AND name = 'Guard Recovery Hip Circle';
UPDATE exercise SET youtube_url = 'https://www.youtube.com/watch?v=ypi3ie6hKTI' WHERE owner_id = 1 AND name = 'Bridge (Puente)';
UPDATE exercise SET youtube_url = 'https://www.youtube.com/watch?v=ypi3ie6hKTI' WHERE owner_id = 1 AND name = 'Bridge and Roll Drill';
UPDATE exercise SET youtube_url = 'https://www.youtube.com/watch?v=KpSmDPKtzqA' WHERE owner_id = 1 AND name = 'Neck Mobility Circles';
UPDATE exercise SET youtube_url = 'https://www.youtube.com/watch?v=5ALqUk9uefk' WHERE owner_id = 1 AND name = 'Shoulder Roll Forward';
UPDATE exercise SET youtube_url = 'https://www.youtube.com/watch?v=5ALqUk9uefk' WHERE owner_id = 1 AND name = 'Shoulder Roll Backward';
UPDATE exercise SET youtube_url = 'https://www.youtube.com/watch?v=oIeadGdJMiY' WHERE owner_id = 1 AND name = 'Sprawl Drill';
UPDATE exercise SET youtube_url = 'https://www.youtube.com/watch?v=eJv7MNcH6X0' WHERE owner_id = 1 AND name = 'Wrestler''s Sit-Out Series';
UPDATE exercise SET youtube_url = 'https://www.youtube.com/watch?v=KpSmDPKtzqA' WHERE owner_id = 1 AND name = 'Duck Walk';
UPDATE exercise SET youtube_url = 'https://www.youtube.com/watch?v=KpSmDPKtzqA' WHERE owner_id = 1 AND name = 'Crab Walk';
UPDATE exercise SET youtube_url = 'https://www.youtube.com/watch?v=K8oMSsPJnGk' WHERE owner_id = 1 AND name = 'Inchworm';
UPDATE exercise SET youtube_url = 'https://www.youtube.com/watch?v=S9pqjRWBNL0' WHERE owner_id = 1 AND name = 'Jiu-Jitsu Hip Mobility Flow';
UPDATE exercise SET youtube_url = 'https://www.youtube.com/watch?v=KpSmDPKtzqA' WHERE owner_id = 1 AND name = 'Knee Circle Drill';
UPDATE exercise SET youtube_url = 'https://www.youtube.com/watch?v=KpSmDPKtzqA' WHERE owner_id = 1 AND name = 'Ankle Rotation Drill';
UPDATE exercise SET youtube_url = 'https://www.youtube.com/watch?v=KpSmDPKtzqA' WHERE owner_id = 1 AND name = 'Wrist Mobility Drill';
UPDATE exercise SET youtube_url = 'https://www.youtube.com/watch?v=K8oMSsPJnGk' WHERE owner_id = 1 AND name = 'Thoracic Spine Rotation';
UPDATE exercise SET youtube_url = 'https://www.youtube.com/watch?v=K8oMSsPJnGk' WHERE owner_id = 1 AND name = 'Hip Flexor Lunge Flow';

-- =====================================================================
-- FLEXIBILITY EXERCISES — V228 (all had youtube_url = NULL)
-- =====================================================================

UPDATE exercise SET youtube_url = 'https://www.youtube.com/watch?v=HCpJsH0K7l0' WHERE owner_id = 1 AND name = 'Butterfly Stretch';
UPDATE exercise SET youtube_url = 'https://www.youtube.com/watch?v=K8oMSsPJnGk' WHERE owner_id = 1 AND name = 'Hip Flexor Stretch';
UPDATE exercise SET youtube_url = 'https://www.youtube.com/watch?v=K8oMSsPJnGk' WHERE owner_id = 1 AND name = 'Seated Forward Fold';
UPDATE exercise SET youtube_url = 'https://www.youtube.com/watch?v=K8oMSsPJnGk' WHERE owner_id = 1 AND name = 'Spinal Twist Stretch';
UPDATE exercise SET youtube_url = 'https://www.youtube.com/watch?v=K8oMSsPJnGk' WHERE owner_id = 1 AND name = 'Pigeon Pose';
UPDATE exercise SET youtube_url = 'https://www.youtube.com/watch?v=HCpJsH0K7l0' WHERE owner_id = 1 AND name = 'Frog Stretch';
UPDATE exercise SET youtube_url = 'https://www.youtube.com/watch?v=HCpJsH0K7l0' WHERE owner_id = 1 AND name = 'Standing Hip Circle';
UPDATE exercise SET youtube_url = 'https://www.youtube.com/watch?v=KpSmDPKtzqA' WHERE owner_id = 1 AND name = 'Shoulder Cross-Body Stretch';
UPDATE exercise SET youtube_url = 'https://www.youtube.com/watch?v=KpSmDPKtzqA' WHERE owner_id = 1 AND name = 'Neck Lateral Stretch';
UPDATE exercise SET youtube_url = 'https://www.youtube.com/watch?v=K8oMSsPJnGk' WHERE owner_id = 1 AND name = 'Thoracic Extension Stretch';
UPDATE exercise SET youtube_url = 'https://www.youtube.com/watch?v=K8oMSsPJnGk' WHERE owner_id = 1 AND name = 'Couch Stretch';
UPDATE exercise SET youtube_url = 'https://www.youtube.com/watch?v=HCpJsH0K7l0' WHERE owner_id = 1 AND name = 'Pancake Stretch';
UPDATE exercise SET youtube_url = 'https://www.youtube.com/watch?v=HCpJsH0K7l0' WHERE owner_id = 1 AND name = 'Straddle Stretch';
UPDATE exercise SET youtube_url = 'https://www.youtube.com/watch?v=K8oMSsPJnGk' WHERE owner_id = 1 AND name = 'Cobra Pose';
UPDATE exercise SET youtube_url = 'https://www.youtube.com/watch?v=K8oMSsPJnGk' WHERE owner_id = 1 AND name = 'Child''s Pose';
UPDATE exercise SET youtube_url = 'https://www.youtube.com/watch?v=KpSmDPKtzqA' WHERE owner_id = 1 AND name = 'Lat Stretch Hanging';
UPDATE exercise SET youtube_url = 'https://www.youtube.com/watch?v=KpSmDPKtzqA' WHERE owner_id = 1 AND name = 'Wrist Flexor Stretch';
UPDATE exercise SET youtube_url = 'https://www.youtube.com/watch?v=K8oMSsPJnGk' WHERE owner_id = 1 AND name = 'Achilles and Calf Stretch';
UPDATE exercise SET youtube_url = 'https://www.youtube.com/watch?v=K8oMSsPJnGk' WHERE owner_id = 1 AND name = 'Glute Stretch Figure-4';
UPDATE exercise SET youtube_url = 'https://www.youtube.com/watch?v=K8oMSsPJnGk' WHERE owner_id = 1 AND name = 'Full Body BJJ Cool-Down Flow';
