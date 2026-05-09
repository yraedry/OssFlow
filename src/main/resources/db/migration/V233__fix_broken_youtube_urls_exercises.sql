-- V233__fix_broken_youtube_urls_exercises.sql
-- Corrección de URLs rotas de ejercicios verificadas con navegador (oEmbed API).
--
-- Videos rotos (HTTP 404 en YouTube oEmbed):
--   KpSmDPKtzqA, oIeadGdJMiY, K8oMSsPJnGk, rT7gHMJVFuU, S9pqjRWBNL0 (V229)
--   Wc2_xrQNnQQ, U4s4mEQ5VOU, qnMKMFrX1nU, swFJDMSv7cg, LDBjPCHSRHo, UtN0OoJVTqA (V221)
--
-- Videos con contenido incorrecto (video existe pero no corresponde al ejercicio):
--   5ALqUk9uefk ("How to Escape Side Control" → no es Shoulder Roll Forward)
--   ypi3ie6hKTI ("John Danaher Closed Guard" → no es Bridge)
--   HCpJsH0K7l0 ("Butterfly Guard strategy" → no es Butterfly/Frog/Pancake/Straddle stretch)
--   NG9qbvAN3gQ ("Daily Hip Mobility" → no es V-Up)
--   XxWcirHIwVo ("How to Deadlift" → no es Burpee)
--   pYcpY20QaE8 ("Dumbbell Bent-Over Row" → no es Chin-Up)
--   1919eTCoESo ("10 Min Abs Workout" → no es Mountain Climber específico)
--
-- Todos los vídeos de reemplazo han sido verificados como funcionales.

-- ============================================================
-- MOVILIDAD BJJ — ejercicios con URL rota (KpSmDPKtzqA, 404)
-- ============================================================

-- "6 Best Neck Stretches For Your BJJ Warm Up"
UPDATE exercise SET youtube_url = 'https://www.youtube.com/watch?v=fsw9j3Hu-lg' WHERE owner_id = 1 AND name = 'Neck Mobility Circles';

-- "Duck Walk Exercise | Duck Walk Workout for Beginners"
UPDATE exercise SET youtube_url = 'https://www.youtube.com/watch?v=IVFB5KSxGBc' WHERE owner_id = 1 AND name = 'Duck Walk';

-- "HOW TO CRAB WALK / HOW TO DO CRAB WALK EXERCISE"
UPDATE exercise SET youtube_url = 'https://www.youtube.com/watch?v=4JuKZA2jT6Y' WHERE owner_id = 1 AND name = 'Crab Walk';

-- "Knee Circles"
UPDATE exercise SET youtube_url = 'https://www.youtube.com/watch?v=92owncvIHlY' WHERE owner_id = 1 AND name = 'Knee Circle Drill';

-- "Ankle Exercises for Strength & Mobility (Bulletproof Ankles)"
UPDATE exercise SET youtube_url = 'https://www.youtube.com/watch?v=QV-x1tK7U4w' WHERE owner_id = 1 AND name = 'Ankle Rotation Drill';

-- "Wrist Mobility Follow Along Routine: 6 Simple Wrist Exercises for Pain Relief"
UPDATE exercise SET youtube_url = 'https://www.youtube.com/watch?v=7xY-JrvtnC0' WHERE owner_id = 1 AND name = 'Wrist Mobility Drill';

-- ============================================================
-- MOVILIDAD BJJ — ejercicios con URL rota (oIeadGdJMiY, 404)
-- ============================================================

-- "BJJ in Ledgewood NJ: Basic sprawl drill"
UPDATE exercise SET youtube_url = 'https://www.youtube.com/watch?v=Adz0whT8wBc' WHERE owner_id = 1 AND name = 'Sprawl Drill';

-- "Wrestling Basics - Penetration Step by Adam Wheeler"
UPDATE exercise SET youtube_url = 'https://www.youtube.com/watch?v=74LN3ev3TAs' WHERE owner_id = 1 AND name = 'Penetration Step';

-- ============================================================
-- MOVILIDAD BJJ — ejercicios con URL rota (K8oMSsPJnGk, 404)
-- ============================================================

-- "Inchworms"
UPDATE exercise SET youtube_url = 'https://www.youtube.com/watch?v=0HFXsIMKqUg' WHERE owner_id = 1 AND name = 'Inchworm';

-- "Hip Flexor Stretch Lunge Position - Ask Doctor Jo"
UPDATE exercise SET youtube_url = 'https://www.youtube.com/watch?v=tsGPYSQbZx4' WHERE owner_id = 1 AND name = 'Hip Flexor Lunge Flow';

-- "Side Lying Thoracic Rotation"
UPDATE exercise SET youtube_url = 'https://www.youtube.com/watch?v=EHZJns1bXPM' WHERE owner_id = 1 AND name = 'Thoracic Spine Rotation';

-- ============================================================
-- MOVILIDAD BJJ — ejercicios con URL rota (rT7gHMJVFuU, 404)
-- ============================================================

-- "Mounted Arm Bar Solo Drill"
UPDATE exercise SET youtube_url = 'https://www.youtube.com/watch?v=hwgY6qlmdrk' WHERE owner_id = 1 AND name = 'Armbar Hip Extension Drill';

-- ============================================================
-- MOVILIDAD BJJ — ejercicios con URL rota (S9pqjRWBNL0, 404)
-- ============================================================

-- "BJJ: 7 Basic Guard Recovery Drills | Evolve University"
UPDATE exercise SET youtube_url = 'https://www.youtube.com/watch?v=KX1G2RcZ6ro' WHERE owner_id = 1 AND name = 'Guard Recovery Hip Circle';

-- "Loosen Tight Hips With This Quick Movement Flow (Great For BJJ)"
UPDATE exercise SET youtube_url = 'https://www.youtube.com/watch?v=raTdwv3d4GU' WHERE owner_id = 1 AND name = 'Jiu-Jitsu Hip Mobility Flow';

-- ============================================================
-- FLEXIBILIDAD — ejercicios con URL rota (K8oMSsPJnGk, 404)
-- ============================================================

-- "Forward Fold Yoga Pose - Seated and Standing"
UPDATE exercise SET youtube_url = 'https://www.youtube.com/watch?v=SLIaql7h6RQ' WHERE owner_id = 1 AND name = 'Seated Forward Fold';

-- "Supine Spinal Twist for Spine Mobility"
UPDATE exercise SET youtube_url = 'https://www.youtube.com/watch?v=mNdJti7ZwKI' WHERE owner_id = 1 AND name = 'Spinal Twist Stretch';

-- "Couch Stretch"
UPDATE exercise SET youtube_url = 'https://www.youtube.com/watch?v=Fg-lwNBzVV8' WHERE owner_id = 1 AND name = 'Couch Stretch';

-- "Cobra Pose - Yoga With Adriene"
UPDATE exercise SET youtube_url = 'https://www.youtube.com/watch?v=n6jrC6WeF84' WHERE owner_id = 1 AND name = 'Cobra Pose';

-- "Child Pose"
UPDATE exercise SET youtube_url = 'https://www.youtube.com/watch?v=kH12QrSGedM' WHERE owner_id = 1 AND name = 'Child''s Pose';

-- "How to do a Thoracic Extension stretch"
UPDATE exercise SET youtube_url = 'https://www.youtube.com/watch?v=ojCkd6_VdNk' WHERE owner_id = 1 AND name = 'Thoracic Extension Stretch';

-- "Piriformis Figure 4 Stretch - Ask Doctor Jo"
UPDATE exercise SET youtube_url = 'https://www.youtube.com/watch?v=-g0nuyTHMrI' WHERE owner_id = 1 AND name = 'Glute Stretch Figure-4';

-- "Calf Stretching: Prevent Calf, Achilles & Shin Splints"
UPDATE exercise SET youtube_url = 'https://www.youtube.com/watch?v=mDxFZDA7Uq0' WHERE owner_id = 1 AND name = 'Achilles and Calf Stretch';

-- "16 Min Stretching & Mobility Routine For Jiu Jitsu"
UPDATE exercise SET youtube_url = 'https://www.youtube.com/watch?v=WxfPt3n44sc' WHERE owner_id = 1 AND name = 'Full Body BJJ Cool-Down Flow';

-- ============================================================
-- FLEXIBILIDAD — ejercicios con URL rota (KpSmDPKtzqA, 404)
-- ============================================================

-- "Cross Arm Stretch"
UPDATE exercise SET youtube_url = 'https://www.youtube.com/watch?v=-1K0m5ywRcY' WHERE owner_id = 1 AND name = 'Shoulder Cross-Body Stretch';

-- "Neck Side-Bend Stretch"
UPDATE exercise SET youtube_url = 'https://www.youtube.com/watch?v=54y0JAT46vE' WHERE owner_id = 1 AND name = 'Neck Lateral Stretch';

-- "Hanging Lat Stretch"
UPDATE exercise SET youtube_url = 'https://www.youtube.com/watch?v=quALD8pisKs' WHERE owner_id = 1 AND name = 'Lat Stretch Hanging';

-- "Wrist exercise - wrist flexor stretch"
UPDATE exercise SET youtube_url = 'https://www.youtube.com/watch?v=R7dI2ZcbiMI' WHERE owner_id = 1 AND name = 'Wrist Flexor Stretch';

-- ============================================================
-- MOVILIDAD BJJ — contenido incorrecto (5ALqUk9uefk)
-- El vídeo original es "How to Escape Side Control", no Shoulder Roll
-- ============================================================

-- "BJJ Fundamentals Warm Up - Front Roll"
UPDATE exercise SET youtube_url = 'https://www.youtube.com/watch?v=Q3MT94wBXbI' WHERE owner_id = 1 AND name = 'Shoulder Roll Forward';

-- "How to Do the Backwards Shoulder Roll Safely in BJJ and Grappling"
UPDATE exercise SET youtube_url = 'https://www.youtube.com/watch?v=iVOG3YyOrPQ' WHERE owner_id = 1 AND name = 'Shoulder Roll Backward';

-- ============================================================
-- MOVILIDAD BJJ — contenido incorrecto (ypi3ie6hKTI)
-- El vídeo original es "John Danaher Closed Guard Fundamentals", no Bridge
-- ============================================================

-- "How To BACK BRIDGE For Beginners (FLEXIBLE & STRONG)"
UPDATE exercise SET youtube_url = 'https://www.youtube.com/watch?v=tSvmWU-0Zo0' WHERE owner_id = 1 AND name = 'Bridge (Puente)';

-- ============================================================
-- MOVILIDAD BJJ — contenido incorrecto (Stand-Up in Base)
-- Verificado OK en V231 (quMmk9Xs2HE) pero V229 había puesto dqFpT1XCLIM
-- ============================================================

-- "Technical Stand up in BJJ - Base, Posture and Structure | Guard Retention"
UPDATE exercise SET youtube_url = 'https://www.youtube.com/watch?v=JzEaiLDwIwc' WHERE owner_id = 1 AND name = 'Stand-Up in Base';

-- ============================================================
-- MOVILIDAD BJJ — contenido incorrecto (Sit-Out y Wrestler's Sit-Out)
-- eJv7MNcH6X0 (High Crotch technique) era incorrecto para estos ejercicios
-- ============================================================

-- "Bottom Wrestling- sit out, turn in, out back door and Peterson"
UPDATE exercise SET youtube_url = 'https://www.youtube.com/watch?v=_kbBhyxV0RY' WHERE owner_id = 1 AND name = 'Sit-Out';
UPDATE exercise SET youtube_url = 'https://www.youtube.com/watch?v=_kbBhyxV0RY' WHERE owner_id = 1 AND name = 'Wrestler''s Sit-Out Series';

-- ============================================================
-- FLEXIBILIDAD — contenido incorrecto (HCpJsH0K7l0)
-- El vídeo original es "A Gameplan for the Butterfly Guard" (BJJ technique), no ejercicio de flexibilidad
-- Nota: Butterfly Stretch, Frog Stretch y Pancake Stretch ya fueron corregidos en V231.
-- ============================================================

-- "Standing Hip Circles"
UPDATE exercise SET youtube_url = 'https://www.youtube.com/watch?v=oU_oVMJL3-8' WHERE owner_id = 1 AND name = 'Standing Hip Circle';

-- "How to make progress in your straddle flexibility"
UPDATE exercise SET youtube_url = 'https://www.youtube.com/watch?v=TqK2dAvw-H4' WHERE owner_id = 1 AND name = 'Straddle Stretch';

-- ============================================================
-- EJERCICIOS FÍSICOS — contenido incorrecto
-- ============================================================

-- V-Up: NG9qbvAN3gQ = "Daily Hip Mobility Routine" (incorrecto)
-- "Exercise Tutorial - V-Up"
UPDATE exercise SET youtube_url = 'https://www.youtube.com/watch?v=iP2fjvG0g3w' WHERE owner_id = 1 AND name = 'V-Up';

-- Mountain Climber: 1919eTCoESo = "10 Min Abs Workout" (genérico, no específico)
-- "How to Do Mountain Climbers | The Right Way | Well+Good"
UPDATE exercise SET youtube_url = 'https://www.youtube.com/watch?v=cnyTQDSE884' WHERE owner_id = 1 AND name = 'Mountain Climber';

-- Burpee: XxWcirHIwVo = "How to PROPERLY Deadlift" (completamente incorrecto)
-- "How To Do A Burpee | The Right Way | Well+Good"
UPDATE exercise SET youtube_url = 'https://www.youtube.com/watch?v=qLBImHhCXSw' WHERE owner_id = 1 AND name = 'Burpee';

-- Chin-Up: pYcpY20QaE8 = "How To: Dumbbell Bent-Over Row" (completamente incorrecto)
-- "PERFECT CHIN-UPS | The Only Chin-up Tutorial You'll Ever Need"
UPDATE exercise SET youtube_url = 'https://www.youtube.com/watch?v=e1YSApl-QcM' WHERE owner_id = 1 AND name = 'Chin-Up';

-- ============================================================
-- EJERCICIOS FÍSICOS — URLs rotas (404)
-- ============================================================

-- Pike Push-Up: Wc2_xrQNnQQ (404)
-- "How to Pike Push Up | Beginner (Progressions)"
UPDATE exercise SET youtube_url = 'https://www.youtube.com/watch?v=fXgou2W10ok' WHERE owner_id = 1 AND name = 'Pike Push-Up';

-- Jump Squat: U4s4mEQ5VOU (404)
-- "How To Jump Squat"
UPDATE exercise SET youtube_url = 'https://www.youtube.com/watch?v=BRfxI2Es2lE' WHERE owner_id = 1 AND name = 'Jump Squat';

-- Plank: qnMKMFrX1nU (404)
-- "How To: Plank"
UPDATE exercise SET youtube_url = 'https://www.youtube.com/watch?v=pSHjTRCQxIw' WHERE owner_id = 1 AND name = 'Plank';

-- Hollow Body Hold: swFJDMSv7cg (404)
-- "Hollow Body Hold Progression - Gymnastic Core Stability Exercise"
UPDATE exercise SET youtube_url = 'https://www.youtube.com/watch?v=LlDNef_Ztsc' WHERE owner_id = 1 AND name = 'Hollow Body Hold';

-- L-Sit: LDBjPCHSRHo (404)
-- "Floor L-sit Progression Tutorial by Antranik"
UPDATE exercise SET youtube_url = 'https://www.youtube.com/watch?v=IUZJoSP66HI' WHERE owner_id = 1 AND name = 'L-Sit';

-- Pull-Up: UtN0OoJVTqA (404)
-- "The Perfect Pull Up - Do it right!"
UPDATE exercise SET youtube_url = 'https://www.youtube.com/watch?v=eGo4IYlbE5g' WHERE owner_id = 1 AND name = 'Pull-Up';
