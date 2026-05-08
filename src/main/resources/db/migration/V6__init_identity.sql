CREATE TABLE user_profile (
    id                       BIGSERIAL PRIMARY KEY,
    owner_id                 BIGINT NOT NULL UNIQUE,
    display_name             VARCHAR(120) NOT NULL,
    current_belt             VARCHAR(15) NOT NULL,
    belt_since               DATE,
    academy                  VARCHAR(200),
    preferred_modality       VARCHAR(10) NOT NULL,
    onboarding_completed     BOOLEAN NOT NULL DEFAULT FALSE,
    created_at               TIMESTAMP NOT NULL,
    updated_at               TIMESTAMP NOT NULL,
    version                  BIGINT NOT NULL DEFAULT 0,
    deleted_at               TIMESTAMP,
    purge_at                 TIMESTAMP
);

CREATE TABLE user_profile_federation (
    user_profile_id   BIGINT NOT NULL,
    federation_id     BIGINT NOT NULL,
    is_primary        BOOLEAN NOT NULL DEFAULT FALSE,
    PRIMARY KEY (user_profile_id, federation_id),
    FOREIGN KEY (user_profile_id) REFERENCES user_profile(id) ON DELETE CASCADE,
    FOREIGN KEY (federation_id)   REFERENCES federation(id)
);

CREATE UNIQUE INDEX ux_user_profile_one_primary
    ON user_profile_federation(user_profile_id) WHERE is_primary = 1;
