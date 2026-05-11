-- A4 (plan hardening): permite implementar la ventana de gracia y rastrear la cadena
-- de rotación. Si una request reusa un token revocado, sabremos a qué token lo reemplazó
-- y podremos decidir si es un double-click legítimo o un reuse malicioso.
ALTER TABLE refresh_token
    ADD COLUMN IF NOT EXISTS replaced_by_id BIGINT REFERENCES refresh_token(id);
