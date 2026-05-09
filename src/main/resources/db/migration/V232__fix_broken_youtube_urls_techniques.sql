-- V232__fix_broken_youtube_urls_techniques.sql
-- Corrección de URLs rotas verificadas con navegador (agent-browser, 2026-05-09)
-- Todas las URLs de V231 verificadas: OK (29 videos funcionando)
-- URLs rotas encontradas: 11 técnicas de V208 (videos eliminados de YouTube)

-- SUBMISSIONS: Chokes
UPDATE technique SET youtube_url = 'https://www.youtube.com/watch?v=Q7R71XB3dig' WHERE owner_id = 1 AND name = 'Bow and Arrow';
-- Reemplaza: xLLYpOsq6GQ (eliminado) → "How To Do the Bow and Arrow Choke | The Jiu Jitsu Class" (7 min)

UPDATE technique SET youtube_url = 'https://www.youtube.com/watch?v=tc9RVnwKMlk' WHERE owner_id = 1 AND name = 'Baseball Bat Choke';
-- Reemplaza: 0Jq2A9-lbTc (eliminado) → "Spinning Baseball Bat Choke, From The Top AND Bottom!" (13 min)

UPDATE technique SET youtube_url = 'https://www.youtube.com/watch?v=MWMNq8DGHyo' WHERE owner_id = 1 AND name = 'Darce Choke';
-- Reemplaza: 8mGJh6sTgN8 (eliminado) → "The PERFECT Darce Choke with Ruotolo Brothers" (7 min)

UPDATE technique SET youtube_url = 'https://www.youtube.com/watch?v=OWVMvV4gmIo' WHERE owner_id = 1 AND name = 'Anaconda Choke';
-- Reemplaza: 6wjxGq6pOCk (eliminado) → "Best No-Gi Submission for Wrestlers: The Anaconda Choke | B-Team Technique" (4 min)

UPDATE technique SET youtube_url = 'https://www.youtube.com/watch?v=QJuyXdkYhm8' WHERE owner_id = 1 AND name = 'Clock Choke';
-- Reemplaza: JMFzQmNiIi0 (eliminado) → "All About The Clock Choke | BJJ Submissions" (8 min)

UPDATE technique SET youtube_url = 'https://www.youtube.com/watch?v=jIXrWitQZx4' WHERE owner_id = 1 AND name = 'Ezekiel Choke';
-- Reemplaza: KBkZVJR_bEw (eliminado) → "How to do Ezekiel Choke by Erik Paulson" (1 min 38 s)

UPDATE technique SET youtube_url = 'https://www.youtube.com/watch?v=6GUtd52rdzY' WHERE owner_id = 1 AND name = 'Peruvian Necktie';
-- Reemplaza: 4VUPMbQiLc8 (eliminado) → "Peruvian Necktie tutorial by Coach Álvarez #AlvarezBJJ" (short, verified working)

-- SUBMISSIONS: Arm Locks
UPDATE technique SET youtube_url = 'https://www.youtube.com/watch?v=PsYSobSqBgI' WHERE owner_id = 1 AND name = 'Gogoplata';
-- Reemplaza: V7VXiwj4wag (eliminado) → "Gogoplata CHOKE | Setups and 4 WAYS to finish" (12 min)

UPDATE technique SET youtube_url = 'https://www.youtube.com/watch?v=bt_hFveUzzI' WHERE owner_id = 1 AND name = 'Bicep Slicer';
-- Reemplaza: 0y6lmVKCgUc (eliminado) → "Multiple biceps slicers from side and guard" (8 min)

-- SWEEPS
UPDATE technique SET youtube_url = 'https://www.youtube.com/watch?v=6MMB12vteKU' WHERE owner_id = 1 AND name = 'Spider Sweep';
-- Reemplaza: UBZ8RXHBXH0 (eliminado) → "Spider guard FUNDAMENTALS | How to control, sweep and submit" (7 min)

UPDATE technique SET youtube_url = 'https://www.youtube.com/watch?v=PAf2iCezKzY' WHERE owner_id = 1 AND name = 'Berimbolo';
-- Reemplaza: UyHxmqJi4Pc (eliminado) → "Step by Step Guide to Learn The Berimbolo" (26 min)
