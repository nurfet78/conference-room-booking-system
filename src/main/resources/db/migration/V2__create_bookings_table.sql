-- Enum статусов бронирования
CREATE TYPE booking_status AS ENUM ('PENDING', 'CONFIRMED', 'CANCELLED', 'EXPIRED');

-- Таблица бронирований
CREATE TABLE bookings (
    id              BIGSERIAL PRIMARY KEY,
    room_id         BIGINT NOT NULL REFERENCES rooms(id),
    title           VARCHAR(200) NOT NULL,
    organizer_email VARCHAR(255) NOT NULL,
    start_time      TIMESTAMP WITH TIME ZONE NOT NULL,
    end_time        TIMESTAMP WITH TIME ZONE NOT NULL,
    status          booking_status NOT NULL DEFAULT 'PENDING',
    created_at      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    version         BIGINT NOT NULL DEFAULT 0,

    -- end_time > start_time
    CONSTRAINT chk_booking_time_order CHECK (end_time > start_time),
    
    -- Минимальная длительность 15 минут
    CONSTRAINT chk_booking_min_duration CHECK (
        EXTRACT(EPOCH FROM (end_time - start_time)) >= 900
    ),
    
    -- Максимальная длительность 8 часов
    CONSTRAINT chk_booking_max_duration CHECK (
        EXTRACT(EPOCH FROM (end_time - start_time)) <= 28800
    )
);

COMMENT ON TABLE bookings IS 'Room reservations with time intervals';
