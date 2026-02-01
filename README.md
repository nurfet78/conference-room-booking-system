# Conference Room Booking System

Система бронирования переговорных комнат с фокусом на **корректность конкурентного доступа** и **целостность данных**.

## Проблема

Бронирование ресурсов с временными интервалами — классическая задача, где легко допустить ошибки:

- **Race condition**: два пользователя одновременно бронируют один слот — оба получают подтверждение
- **Потеря данных**: проверка "свободно ли?" и сохранение происходят не атомарно
- **Некорректное время**: сервер в одной timezone, БД в другой, клиент в третьей

Этот проект демонстрирует, как решить эти проблемы правильно.

---

## Ключевые архитектурные решения

### 1. Двухуровневая защита от пересечений

**Проблема**: Классическая проверка `if (!hasOverlap) { save() }` уязвима к race condition — два параллельных запроса могут пройти проверку одновременно.

**Решение**:

```
┌─────────────────────────────────────────────────────────────┐
│            Уровень приложения (BookingService)              │
│                                                             │
│  1. Пессимистичная блокировка комнаты (PESSIMISTIC_WRITE)  │
│     → Второй запрос ждёт, пока первый не завершит          │
│                                                             │
│  2. SERIALIZABLE isolation level                            │
│     → Гарантирует последовательное выполнение              │
│                                                             │
│  3. Проверка пересечений JPQL-запросом                     │
│     → start < :end AND end > :start                        │
└─────────────────────────────────────────────────────────────┘
                            │
                            ▼
┌─────────────────────────────────────────────────────────────┐
│             Уровень БД (PostgreSQL)                         │
│                                                             │
│  EXCLUDE USING gist (                                       │
│    room_id WITH =,                                          │
│    tstzrange(start_time, end_time) WITH &&                 │
│  )                                                          │
│                                                             │
│  → Даже если приложение ошибётся, БД не даст               │
│    сохранить пересекающиеся интервалы                      │
└─────────────────────────────────────────────────────────────┘
```

**Почему два уровня?**
- Уровень приложения — быстрый отказ с понятным сообщением об ошибке
- Уровень БД — страховка на случай бага в коде или обхода сервиса

### 2. Время в UTC

**Проблема**: `LocalDateTime` не содержит информации о timezone. Если сервер переедет в другой регион или перейдёт на летнее время — все бронирования "поплывут".

**Решение**:
- Java: `Instant` — момент времени в UTC
- PostgreSQL: `TIMESTAMP WITH TIME ZONE` — хранит в UTC
- API: ISO-8601 (`2024-12-20T10:00:00Z`) — однозначный формат
- Конвертация в локальное время — ответственность клиента

```java
// ❌ Плохо: что значит "10:00"? В какой timezone?
private LocalDateTime startTime;

// ✅ Хорошо: однозначный момент времени
private Instant startTime;
```

### 3. Статусы как конечный автомат

**Проблема**: Бронирование может быть отменено, подтверждено, просрочено. Без чётких правил переходов легко получить некорректное состояние.

**Решение**: Enum со встроенной логикой переходов:

```
     ┌──────────┐
     │ PENDING  │ ← создание
     └────┬─────┘
          │
    ┌─────┴─────┐
    ▼           ▼
┌───────┐  ┌──────────┐
│CONFIRM│  │ CANCELLED│
└───┬───┘  └──────────┘
    │
    ├──────────┐
    ▼          ▼
┌───────┐  ┌───────┐
│CANCELL│  │EXPIRED│ ← автоматически, по времени
└───────┘  └───────┘
```

Каждый статус знает, какие переходы из него допустимы:

```java
public enum BookingStatus {
    PENDING {
        public boolean isConfirmable() { return true; }
        public boolean isCancellable() { return true; }
    },
    CONFIRMED {
        public boolean isCancellable() { return true; }
    },
    CANCELLED, EXPIRED;
    
    public boolean isActive() {
        return this == PENDING || this == CONFIRMED;
    }
}
```

### 4. Testcontainers вместо H2

**Проблема**: H2 не поддерживает `EXCLUDE USING gist`. Тесты на H2 не проверяют реальную защиту от пересечений.

**Решение**: Testcontainers поднимает настоящий PostgreSQL в Docker. Тесты проверяют то, что будет в production.

```java
@Testcontainers
class BookingServiceIntegrationTest {
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine");
    
    @Test
    void concurrentBookings_onlyOneSucceeds() {
        // 10 потоков пытаются забронировать один слот
        // Ровно 1 успех, 9 конфликтов
    }
}
```

---

## Технологии

| Компонент | Технология | Почему |
|-----------|------------|--------|
| Runtime | Java 21 | LTS, virtual threads ready |
| Framework | Spring Boot 3.2 | Стандарт индустрии |
| ORM | Spring Data JPA | Декларативные запросы |
| База данных | PostgreSQL 15 | Exclusion constraints, range types |
| Маппинг | MapStruct | Compile-time, без reflection |
| Тесты | Testcontainers | Реальная БД в тестах |

---

## Быстрый старт

### Требования
- Java 21+
- Docker (для PostgreSQL и тестов)
- Maven 3.9+

### Запуск

```bash
# 1. PostgreSQL
docker run -d --name roombook-postgres \
  -e POSTGRES_DB=roombook \
  -e POSTGRES_USER=postgres \
  -e POSTGRES_PASSWORD=postgres \
  -p 5432:5432 \
  postgres:15-alpine

# 2. Запуск (Flyway автоматически создаст схему)
./mvnw spring-boot:run

# 3. (Рекомендуется) Запуск в dev-режиме с тестовыми данными
#    База очищается при завершении приложения
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

### Тесты

```bash
./mvnw test
```

Тесты автоматически поднимают PostgreSQL через Testcontainers.

---

## Миграции (Flyway)

Схема БД управляется через Flyway. Миграции применяются автоматически при старте.

```
src/main/resources/db/
├── migration/                    # Основные миграции (всегда)
│   ├── V1__create_rooms_table.sql
│   ├── V2__create_bookings_table.sql
│   └── V3__add_indexes_and_constraints.sql
└── testdata/                     # Тестовые данные (только dev)
    └── R__test_data.sql
```

| Профиль | Миграции | Тестовые данные | Очистка при завершении |
|---------|----------|-----------------|------------------------|
| (default) | ✅ | ❌ | ❌ |
| dev | ✅ | ✅ | ✅ |
| test | ✅ | ❌ | ❌ (Testcontainers) |

---

## API

### Комнаты `/api/v1/rooms`

| Метод | Endpoint | Описание |
|-------|----------|----------|
| POST | `/` | Создать комнату |
| GET | `/` | Список комнат |
| GET | `/{id}` | Получить комнату |
| PATCH | `/{id}` | Обновить |
| POST | `/{id}/deactivate` | Деактивировать |

### Бронирования `/api/v1/bookings`

| Метод | Endpoint | Описание |
|-------|----------|----------|
| POST | `/` | Создать бронирование |
| GET | `/{id}` | Получить |
| GET | `/room/{roomId}?from=&to=` | По комнате за период |
| GET | `/availability?roomId=&startTime=&endTime=` | Проверить доступность |
| POST | `/{id}/confirm` | Подтвердить |
| POST | `/{id}/cancel` | Отменить |

### Примеры

```bash
# Создать комнату
curl -X POST http://localhost:8080/api/v1/rooms \
  -H "Content-Type: application/json" \
  -d '{"name": "Meeting Room A", "capacity": 10}'

# Забронировать
curl -X POST http://localhost:8080/api/v1/bookings \
  -H "Content-Type: application/json" \
  -d '{
    "roomId": 1,
    "title": "Sprint Planning",
    "organizerEmail": "pm@example.com",
    "startTime": "2024-12-20T10:00:00Z",
    "endTime": "2024-12-20T11:00:00Z"
  }'

# Проверить доступность
curl "http://localhost:8080/api/v1/bookings/availability?roomId=1&startTime=2024-12-20T14:00:00Z&endTime=2024-12-20T15:00:00Z"
```

---

## Тестирование

### Что тестируется

| Тип | Что проверяем |
|-----|---------------|
| Unit | Логика пересечений интервалов, переходы статусов |
| Integration | CRUD операции, валидация, обработка ошибок |
| **Concurrency** | **10 потоков → 1 слот → только 1 успех** |

### Тест конкурентного доступа

Ключевой тест проекта — проверка, что при одновременных запросах только один получит бронирование:

```java
@Test
void whenTenThreadsBookSameSlot_thenOnlyOneSucceeds() {
    int threads = 10;
    ExecutorService executor = Executors.newFixedThreadPool(threads);
    CountDownLatch latch = new CountDownLatch(1);
    AtomicInteger successes = new AtomicInteger(0);
    AtomicInteger conflicts = new AtomicInteger(0);

    for (int i = 0; i < threads; i++) {
        executor.submit(() -> {
            latch.await(); // Все стартуют одновременно
            try {
                bookingService.createBooking(request);
                successes.incrementAndGet();
            } catch (BookingConflictException e) {
                conflicts.incrementAndGet();
            }
        });
    }
    
    latch.countDown(); // Старт!
    executor.awaitTermination(10, SECONDS);

    assertThat(successes.get()).isEqualTo(1);
    assertThat(conflicts.get()).isEqualTo(9);
}
```

---

## Структура проекта

```
src/main/java/com/example/roombook/
├── controller/      # REST API, валидация входных данных
├── service/         # Бизнес-логика, транзакции
├── repository/      # Доступ к данным, блокировки
├── entity/          # Доменная модель
├── dto/             # Request/Response объекты
├── exception/       # Обработка ошибок
├── mapper/          # Entity ↔ DTO
└── config/          # Jackson, etc.
```

---

## Бизнес-правила

- Минимальная длительность: 15 минут
- Максимальная длительность: 8 часов
- Только активные бронирования (PENDING, CONFIRMED) блокируют слоты
- Отменённые и истёкшие бронирования не учитываются при проверке пересечений

---

## Частые ошибки и как их избежать

### Race condition при проверке доступности

```java
// ❌ Между проверкой и сохранением другой поток может вклиниться
if (!hasOverlap(roomId, start, end)) {
    save(booking);
}

// ✅ Блокировка комнаты + constraint в БД
@Lock(LockModeType.PESSIMISTIC_WRITE)
Room room = roomRepository.findById(roomId);
// Теперь другие транзакции ждут
```

### LocalDateTime теряет timezone

```java
// ❌ "10:00" — в какой timezone?
LocalDateTime startTime;

// ✅ Однозначный момент времени
Instant startTime;
```

### H2 не тестирует реальные constraints

```java
// ❌ H2 не поддерживает EXCLUDE USING gist
@DataJpaTest // Использует H2

// ✅ Testcontainers с PostgreSQL
@Testcontainers
@SpringBootTest
```

---

## Лицензия

MIT
