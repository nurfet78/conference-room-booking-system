-- Расширение для exclusion constraints
CREATE EXTENSION IF NOT EXISTS btree_gist;

-- Таблица переговорных комнат
CREATE TABLE rooms (
    id              BIGSERIAL PRIMARY KEY,
    name            VARCHAR(100) NOT NULL UNIQUE,
    capacity        INTEGER NOT NULL CHECK (capacity > 0),
    description     TEXT,
    is_active       BOOLEAN NOT NULL DEFAULT true,
    created_at      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    version         BIGINT NOT NULL DEFAULT 0
);

COMMENT ON TABLE rooms IS 'Conference rooms available for booking';
