-- Индексы для быстрого поиска
CREATE INDEX idx_bookings_room_time 
    ON bookings (room_id, start_time, end_time) 
    WHERE status IN ('PENDING', 'CONFIRMED');

CREATE INDEX idx_bookings_status ON bookings (status);
CREATE INDEX idx_bookings_organizer ON bookings (organizer_email);

-- ============================================================================
-- EXCLUSION CONSTRAINT — главная защита от race conditions на уровне БД
-- ============================================================================
-- Гарантирует, что для одной комнаты не может быть двух пересекающихся
-- активных бронирований, даже при конкурентных INSERT.
-- ============================================================================

ALTER TABLE bookings ADD CONSTRAINT excl_booking_overlap
    EXCLUDE USING gist (
        room_id WITH =,
        tstzrange(start_time, end_time) WITH &&
    )
    WHERE (status IN ('PENDING', 'CONFIRMED'));

COMMENT ON CONSTRAINT excl_booking_overlap ON bookings IS 
    'Prevents overlapping bookings for the same room using PostgreSQL exclusion constraint';
