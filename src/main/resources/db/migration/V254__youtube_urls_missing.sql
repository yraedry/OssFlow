-- V254: URLs de YouTube para técnicas, posiciones y ejercicios sin video
-- Videos verificados en YouTube (mayo 2026)

-- ============================================================
-- TÉCNICAS SIN VIDEO (5 técnicas legítimas)
-- ============================================================

-- Bow and Arrow Choke — Bernardo Faria / BJJ Fanatics
UPDATE technique SET youtube_url = 'https://www.youtube.com/watch?v=M-tP5I6fwvs'
WHERE name = 'Bow and Arrow Choke' AND deleted_at IS NULL;

-- North-South Choke — BJJ Fanatics (John Danaher / Bernardo Faria)
UPDATE technique SET youtube_url = 'https://www.youtube.com/watch?v=HuwV3-g2knA'
WHERE name = 'North-South Choke' AND deleted_at IS NULL;

-- Elevator Sweep — Butterfly guard (Chattanooga BJJ Academy)
UPDATE technique SET youtube_url = 'https://www.youtube.com/watch?v=awCHI_VFJQI'
WHERE name = 'Elevator Sweep' AND deleted_at IS NULL;

-- Overhead Sweep — Butterfly guard (Chewjitsu)
UPDATE technique SET youtube_url = 'https://www.youtube.com/watch?v=mnZi33gJ5pU'
WHERE name = 'Overhead Sweep' AND deleted_at IS NULL;

-- Spider Guard Sweep — JeanJacquesMachado
UPDATE technique SET youtube_url = 'https://www.youtube.com/watch?v=zoabs7H2ZN8'
WHERE name = 'Spider Guard Sweep' AND deleted_at IS NULL;

-- ============================================================
-- POSICIONES SIN VIDEO
-- ============================================================

-- 50/50 — Kata Jiu Jitsu
UPDATE position SET youtube_url = 'https://www.youtube.com/watch?v=mKbMqB6I9FI'
WHERE name = '50/50';

-- Crucifix — Stephan Kesting
UPDATE position SET youtube_url = 'https://www.youtube.com/watch?v=rCQsaENYkTE'
WHERE name = 'Crucifix';

-- Double Outside Ashi — 4 Foot Positions for Leg locks
UPDATE position SET youtube_url = 'https://www.youtube.com/watch?v=eiI_4VBA-qA'
WHERE name = 'Double Outside Ashi';

-- Kimura Trap — The Kimura Trap system
UPDATE position SET youtube_url = 'https://www.youtube.com/watch?v=xyCakxmx-2E'
WHERE name = 'Kimura Trap';

-- ============================================================
-- EJERCICIOS SIN VIDEO
-- (rellenados por el agente de búsqueda — se añadirán en V255)
-- ============================================================
