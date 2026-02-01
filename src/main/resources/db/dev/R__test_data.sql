-- ============================================================================
-- ТЕСТОВЫЕ ДАННЫЕ (Repeatable Migration)
-- ============================================================================
-- R__ prefix означает Repeatable — выполняется после всех V__ миграций
-- и перезапускается при изменении checksum файла.
-- ============================================================================

-- Очистка перед вставкой (для повторного запуска)
TRUNCATE TABLE bookings RESTART IDENTITY CASCADE;
TRUNCATE TABLE rooms RESTART IDENTITY CASCADE;

-- ============================================================================
-- КОМНАТЫ
-- ============================================================================

INSERT INTO rooms (name, capacity, description, is_active) VALUES
    ('Альфа', 6, 'Малая переговорная на 2 этаже. Проектор, маркерная доска.', true),
    ('Бета', 10, 'Средняя переговорная на 3 этаже. ВКС-оборудование, TV 65".', true),
    ('Гамма', 20, 'Большой конференц-зал. Сцена, микрофоны, 2 проектора.', true),
    ('Дельта', 4, 'Мини-переговорная для 1-on-1. Тихая зона.', true),
    ('Эпсилон', 8, 'Переговорная на ремонте. Временно недоступна.', false);

-- ============================================================================
-- БРОНИРОВАНИЯ
-- ============================================================================

-- Комната "Альфа" (id=1)
INSERT INTO bookings (room_id, title, organizer_email, start_time, end_time, status) VALUES
    (1, 'Ретроспектива спринта 42', 'scrum@example.com', 
     NOW() - INTERVAL '2 days' + TIME '10:00', 
     NOW() - INTERVAL '2 days' + TIME '11:30', 
     'CONFIRMED'),
    (1, 'Daily standup', 'team-lead@example.com', 
     NOW()::date + TIME '09:00', 
     NOW()::date + TIME '09:15', 
     'CONFIRMED'),
    (1, 'Собеседование: Java Developer', 'hr@example.com', 
     NOW()::date + INTERVAL '1 day' + TIME '14:00', 
     NOW()::date + INTERVAL '1 day' + TIME '15:30', 
     'PENDING'),
    (1, 'Отменённая встреча', 'manager@example.com', 
     NOW()::date + INTERVAL '2 days' + TIME '11:00', 
     NOW()::date + INTERVAL '2 days' + TIME '12:00', 
     'CANCELLED');

-- Комната "Бета" (id=2)
INSERT INTO bookings (room_id, title, organizer_email, start_time, end_time, status) VALUES
    (2, 'Квартальное планирование Q2', 'director@example.com', 
     NOW()::date + TIME '10:00', 
     NOW()::date + TIME '14:00', 
     'CONFIRMED'),
    (2, 'Workshop: System Design', 'architect@example.com', 
     NOW()::date + INTERVAL '7 days' + TIME '10:00', 
     NOW()::date + INTERVAL '7 days' + TIME '18:00', 
     'PENDING');

-- Комната "Гамма" (id=3)
INSERT INTO bookings (room_id, title, organizer_email, start_time, end_time, status) VALUES
    (3, 'All-hands: Итоги года', 'ceo@example.com', 
     NOW()::date + INTERVAL '3 days' + TIME '15:00', 
     NOW()::date + INTERVAL '3 days' + TIME '17:00', 
     'CONFIRMED'),
    (3, 'Demo Day: Новые фичи', 'product@example.com', 
     NOW()::date + INTERVAL '7 days' + TIME '16:00', 
     NOW()::date + INTERVAL '7 days' + TIME '18:00', 
     'PENDING');

-- Комната "Дельта" (id=4) — смежные интервалы
INSERT INTO bookings (room_id, title, organizer_email, start_time, end_time, status) VALUES
    (4, '1-on-1: Иван', 'manager@example.com', 
     NOW()::date + TIME '11:00', 
     NOW()::date + TIME '11:30', 
     'CONFIRMED'),
    (4, '1-on-1: Мария', 'manager@example.com', 
     NOW()::date + TIME '11:30', 
     NOW()::date + TIME '12:00', 
     'CONFIRMED'),
    (4, '1-on-1: Алексей', 'manager@example.com', 
     NOW()::date + TIME '14:00', 
     NOW()::date + TIME '14:30', 
     'PENDING');

-- Комната "Эпсилон" (id=5) — неактивна
INSERT INTO bookings (room_id, title, organizer_email, start_time, end_time, status) VALUES
    (5, 'Встреча до ремонта', 'old@example.com', 
     NOW() - INTERVAL '30 days' + TIME '10:00', 
     NOW() - INTERVAL '30 days' + TIME '11:00', 
     'EXPIRED');
