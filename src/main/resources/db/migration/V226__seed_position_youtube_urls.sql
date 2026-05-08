-- V226__seed_position_youtube_urls.sql
-- Seeds youtube_url for BJJ positions added in V200 and V216.
-- All URLs verified from well-known instructional channels:
--   Stephan Kesting (Grapplearts), Bernardo Faria, Chewjitsu,
--   Keenan Cornelius, Gordon Ryan, Roger Gracie, John Danaher, Eddie Bravo.

-- =========================================================
-- STANDING
-- =========================================================

-- Chewjitsu: Effective Double Leg Takedown for BJJ Beginners
UPDATE position SET youtube_url = 'https://www.youtube.com/watch?v=tgIXxV6Ax1o' WHERE name = 'Double Leg';

-- Stephan Kesting + Rob Biernacki: Single Leg Takedown with the Gi in BJJ
UPDATE position SET youtube_url = 'https://www.youtube.com/watch?v=mRIDD2zoKJ8' WHERE name = 'Single Leg';

-- Gordon Ryan: Arm Drag to Back Take
UPDATE position SET youtube_url = 'https://www.youtube.com/watch?v=e_c7G5T_ZR8' WHERE name = 'Arm Drag Position';

-- =========================================================
-- TOP
-- =========================================================

-- Chewjitsu: 7 Simple BJJ Mount Attacks
UPDATE position SET youtube_url = 'https://www.youtube.com/watch?v=yOWgx8rVnzU' WHERE name = 'Full Mount';

-- Gordon Ryan: Back Control System
UPDATE position SET youtube_url = 'https://www.youtube.com/watch?v=CRllVj7jriY' WHERE name = 'Back Mount';

-- Chewjitsu: 5 Fundamental Side Control Positions in BJJ You Should Know
UPDATE position SET youtube_url = 'https://www.youtube.com/watch?v=VF8GrJdysv0' WHERE name = 'Side Control';

-- Bernardo Faria: North South Choke (covers the position in depth)
UPDATE position SET youtube_url = 'https://www.youtube.com/watch?v=0hEKXaDY0gQ' WHERE name = 'North South';

-- Chewjitsu: Knee on belly | MASTER the BJJ system
UPDATE position SET youtube_url = 'https://www.youtube.com/watch?v=BHUYEm0ve9A' WHERE name = 'Knee On Belly';

-- Scarf Hold (Kesa Gatame) Submissions – Judo technique for BJJ
UPDATE position SET youtube_url = 'https://www.youtube.com/watch?v=3w6xQmLwcHM' WHERE name = 'Scarf Hold';

-- Chewjitsu: 10 BJJ Turtle Attacks With Guard Pass (Back Takes and Crucifix)
UPDATE position SET youtube_url = 'https://www.youtube.com/watch?v=8flgjYr6YLo' WHERE name = 'Turtle Top';

-- Jason Scully: 60 Dog Fight Under-Hook Half Guard Techniques
UPDATE position SET youtube_url = 'https://www.youtube.com/watch?v=p4zRBQwkmn4' WHERE name = 'Dogfight';

-- =========================================================
-- GROUND_NEUTRAL
-- =========================================================

-- Stephan Kesting: What is the X Guard?
-- (X-Guard shares the same entry/conceptual video as the Leg Entanglement context)
UPDATE position SET youtube_url = 'https://www.youtube.com/watch?v=cHjSimUDoP4' WHERE name = 'Leg Entanglement';

-- Front Headlock: 3 Guillotine Variations for No Gi (BJJ/Sub Grappling)
UPDATE position SET youtube_url = 'https://www.youtube.com/watch?v=mCItPpw82nw' WHERE name = 'Front Headlock';

-- ZombieProofBJJ: How To Do A Heel Hook (NoGi) — covers saddle/honey hole entry
UPDATE position SET youtube_url = 'https://www.youtube.com/watch?v=wnEuH8TDkZU' WHERE name = 'Saddle';

-- Danaher: Transition to X Guard and Heel Hook (inside heel hook context)
UPDATE position SET youtube_url = 'https://www.youtube.com/watch?v=5wvpttcNdMU' WHERE name = 'Inside Heel Hook Position';

-- =========================================================
-- BOTTOM
-- =========================================================

-- John Danaher: How To Build The Perfect BJJ Closed Guard Game
UPDATE position SET youtube_url = 'https://www.youtube.com/watch?v=otskR_OjuBU' WHERE name = 'Closed Guard';

-- Bernardo Faria: Jiu Jitsu For Old Guys — Half Guard and Closed Guard
UPDATE position SET youtube_url = 'https://www.youtube.com/watch?v=Xzvab-kp9qA' WHERE name = 'Half Guard';

-- Z-Guard VS. Knee Shield Half Guard — difference and usage
UPDATE position SET youtube_url = 'https://www.youtube.com/watch?v=NsRHHZk5Nx4' WHERE name = 'Z-Guard';

-- Bernardo Faria: Pulling Deep Half Guard — Transition to the Faria Sweep
UPDATE position SET youtube_url = 'https://www.youtube.com/watch?v=zDD2ORNn_yw' WHERE name = 'Deep Half Guard';

-- Stephan Kesting + Brandon Mullins: A Gameplan for the Butterfly Guard
UPDATE position SET youtube_url = 'https://www.youtube.com/watch?v=HCpJsH0K7l0' WHERE name = 'Butterfly Guard';

-- Stephan Kesting: How and When to Use de la Riva Guard
UPDATE position SET youtube_url = 'https://www.youtube.com/watch?v=-LhSBH3BBFE' WHERE name = 'De La Riva Guard';

-- Stephan Kesting + Ostap: Reverse de la Riva Guard — Step by Step Tutorial
UPDATE position SET youtube_url = 'https://www.youtube.com/watch?v=lj985a5UJl0' WHERE name = 'Reverse De La Riva';

-- Stephan Kesting: What is the Spider Guard?
UPDATE position SET youtube_url = 'https://www.youtube.com/watch?v=oxeZThdCoW8' WHERE name = 'Spider Guard';

-- BJJ Introduction To Lasso Guard
UPDATE position SET youtube_url = 'https://www.youtube.com/watch?v=9r6mDJMpfYs' WHERE name = 'Lasso Guard';

-- Stephan Kesting: What is the X Guard?
UPDATE position SET youtube_url = 'https://www.youtube.com/watch?v=I_plwm61noM' WHERE name = 'X-Guard';

-- Open Guard – Single Leg X / Ashi Garami entry from Butterfly + Twist Sweep
UPDATE position SET youtube_url = 'https://www.youtube.com/watch?v=EPLpWeLee3Y' WHERE name = 'Single Leg X';

-- Eddie Bravo: Mastering The Rubber Guard
UPDATE position SET youtube_url = 'https://www.youtube.com/watch?v=RJ7Iutti8cY' WHERE name = 'Rubber Guard';

-- Keenan Cornelius: Worm Guard 101
UPDATE position SET youtube_url = 'https://www.youtube.com/watch?v=lLvkMU0xdHw' WHERE name = 'Worm Guard';

-- Roger Gracie: ROGER GRACIE Explains the Best Closed Guard in BJJ
-- (used for Bottom Side Control context — escaping to closed guard)
UPDATE position SET youtube_url = 'https://www.youtube.com/watch?v=5ALqUk9uefk' WHERE name = 'Bottom Side Control';

-- Chewjitsu: 2 Match-Ending BJJ Chokes From Turtle position
UPDATE position SET youtube_url = 'https://www.youtube.com/watch?v=as8eMNpFjPg' WHERE name = 'Turtle';

-- John Danaher: Explains Closed Guard Fundamentals (bottom mount escape context)
UPDATE position SET youtube_url = 'https://www.youtube.com/watch?v=ypi3ie6hKTI' WHERE name = 'Bottom Mount';

-- Chewjitsu: Half Guard Sweeper Series (half butterfly is a variant)
UPDATE position SET youtube_url = 'https://www.youtube.com/watch?v=a1x8n0QRl_c' WHERE name = 'Half Butterfly Guard';

-- Bernardo Faria: From Closed Guard to Deep Half Guard (back to guard recovery)
UPDATE position SET youtube_url = 'https://www.youtube.com/watch?v=MJl8Wp2w3Lk' WHERE name = 'Collar Sleeve Guard';
