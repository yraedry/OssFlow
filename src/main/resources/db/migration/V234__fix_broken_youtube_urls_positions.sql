-- V234__fix_broken_youtube_urls_positions.sql
-- Fixes broken YouTube URLs for BJJ positions originally seeded in V226.
-- Verified against YouTube oEmbed API on 2026-05-09.
--
-- Broken: Single Leg X (EPLpWeLee3Y) — "Este vídeo ya no está disponible"
-- Replacement: Stephan Kesting — "How to Make Your Opponent Respect Your Single Leg X Guard!"
-- Channel: https://www.youtube.com/@StephanKesting

UPDATE position SET youtube_url = 'https://www.youtube.com/watch?v=OaHVPQVHrMs' WHERE owner_id = 1 AND name = 'Single Leg X';
