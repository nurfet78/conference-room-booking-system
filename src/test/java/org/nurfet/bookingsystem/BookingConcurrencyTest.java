package org.nurfet.bookingsystem;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.nurfet.bookingsystem.dto.request.CreateBookingRequest;
import org.nurfet.bookingsystem.dto.request.CreateRoomRequest;
import org.nurfet.bookingsystem.dto.response.RoomResponse;
import org.nurfet.bookingsystem.exception.BookingConflictException;
import org.nurfet.bookingsystem.repository.BookingRepository;
import org.nurfet.bookingsystem.repository.RoomRepository;
import org.nurfet.bookingsystem.service.BookingService;
import org.nurfet.bookingsystem.service.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Concurrency Tests")
class BookingConcurrencyTest extends AbstractIntegrationTest {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private RoomService roomService;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private TransactionTemplate transactionTemplate;

    private RoomResponse testRoom;
    private Instant startTime;
    private Instant endTime;

    @BeforeEach
    void setUp() {
        // Очистка в отдельной транзакции
        transactionTemplate.executeWithoutResult(status -> {
            bookingRepository.deleteAll();
            roomRepository.deleteAll();
        });

        // Создание комнаты в отдельной транзакции
        testRoom = transactionTemplate.execute(status -> {
            CreateRoomRequest roomRequest = new CreateRoomRequest(
                    "Конкурентная комната",
                    10,
                    "Для тестов конкурентного доступа"
            );
            return roomService.createRoom(roomRequest);
        });

        startTime = Instant.now().plus(1, ChronoUnit.DAYS).truncatedTo(ChronoUnit.SECONDS);
        endTime = startTime.plus(1, ChronoUnit.HOURS);
    }

    @Test
    @DisplayName("При 10 одновременных запросах на один слот — только 1 успешный")
    void whenTenThreadsBookSameSlot_thenOnlyOneSucceeds() throws InterruptedException {
        int threads = 10;
        ExecutorService executor = Executors.newFixedThreadPool(threads);
        CountDownLatch readyLatch = new CountDownLatch(threads);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(threads);

        AtomicInteger successes = new AtomicInteger(0);
        AtomicInteger conflicts = new AtomicInteger(0);
        AtomicInteger errors = new AtomicInteger(0);

        for (int i = 0; i < threads; i++) {
            final int threadNum = i;
            executor.submit(() -> {
                try {
                    CreateBookingRequest request = new CreateBookingRequest(
                            testRoom.id(),
                            "Встреча потока " + threadNum,
                            "user" + threadNum + "@example.com",
                            startTime,
                            endTime
                    );

                    readyLatch.countDown();
                    startLatch.await();

                    // Каждый поток в своей транзакции
                    transactionTemplate.executeWithoutResult(status -> {
                        bookingService.createBooking(request);
                    });
                    successes.incrementAndGet();

                } catch (BookingConflictException e) {
                    conflicts.incrementAndGet();
                } catch (Exception e) {
                    // BookingConflictException может быть обёрнут
                    if (e.getCause() instanceof BookingConflictException) {
                        conflicts.incrementAndGet();
                    } else {
                        errors.incrementAndGet();
                        System.err.println("Неожиданная ошибка: " + e.getMessage());
                    }
                } finally {
                    doneLatch.countDown();
                }
            });
        }

        readyLatch.await(5, TimeUnit.SECONDS);
        startLatch.countDown();
        boolean finished = doneLatch.await(30, TimeUnit.SECONDS);
        executor.shutdown();

        assertThat(finished).isTrue();
        assertThat(successes.get()).isEqualTo(1);
        assertThat(conflicts.get()).isEqualTo(threads - 1);
        assertThat(errors.get()).isEqualTo(0);

        // Проверяем что в БД только одно бронирование
        Long count = transactionTemplate.execute(status -> bookingRepository.count());
        assertThat(count).isEqualTo(1);
    }

    @Test
    @DisplayName("Разные слоты — все бронирования успешны")
    void whenTenThreadsBookDifferentSlots_thenAllSucceed() throws InterruptedException {
        int threads = 10;
        ExecutorService executor = Executors.newFixedThreadPool(threads);
        CountDownLatch readyLatch = new CountDownLatch(threads);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(threads);

        AtomicInteger successes = new AtomicInteger(0);
        AtomicInteger failures = new AtomicInteger(0);

        for (int i = 0; i < threads; i++) {
            final int threadNum = i;
            executor.submit(() -> {
                try {
                    Instant threadStart = startTime.plus(threadNum, ChronoUnit.HOURS);
                    Instant threadEnd = threadStart.plus(1, ChronoUnit.HOURS);

                    CreateBookingRequest request = new CreateBookingRequest(
                            testRoom.id(),
                            "Встреча " + threadNum,
                            "user" + threadNum + "@example.com",
                            threadStart,
                            threadEnd
                    );

                    readyLatch.countDown();
                    startLatch.await();

                    transactionTemplate.executeWithoutResult(status -> {
                        bookingService.createBooking(request);
                    });
                    successes.incrementAndGet();

                } catch (Exception e) {
                    failures.incrementAndGet();
                    System.err.println("Ошибка: " + e.getMessage());
                } finally {
                    doneLatch.countDown();
                }
            });
        }

        readyLatch.await(5, TimeUnit.SECONDS);
        startLatch.countDown();
        boolean finished = doneLatch.await(30, TimeUnit.SECONDS);
        executor.shutdown();

        assertThat(finished).isTrue();
        assertThat(successes.get()).isEqualTo(threads);
        assertThat(failures.get()).isEqualTo(0);

        Long count = transactionTemplate.execute(status -> bookingRepository.count());
        assertThat(count).isEqualTo(Long.valueOf(threads));
    }

    @Test
    @DisplayName("Смежные слоты не конфликтуют")
    void whenAdjacentSlots_thenNoConflict() throws InterruptedException {
        int threads = 5;
        ExecutorService executor = Executors.newFixedThreadPool(threads);
        CountDownLatch readyLatch = new CountDownLatch(threads);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(threads);

        AtomicInteger successes = new AtomicInteger(0);

        for (int i = 0; i < threads; i++) {
            final int threadNum = i;
            executor.submit(() -> {
                try {
                    Instant slotStart = startTime.plus(threadNum, ChronoUnit.HOURS);
                    Instant slotEnd = slotStart.plus(1, ChronoUnit.HOURS);

                    CreateBookingRequest request = new CreateBookingRequest(
                            testRoom.id(),
                            "Слот " + threadNum,
                            "user@example.com",
                            slotStart,
                            slotEnd
                    );

                    readyLatch.countDown();
                    startLatch.await();

                    transactionTemplate.executeWithoutResult(status -> {
                        bookingService.createBooking(request);
                    });
                    successes.incrementAndGet();

                } catch (Exception e) {
                    System.err.println("Ошибка: " + e.getMessage());
                } finally {
                    doneLatch.countDown();
                }
            });
        }

        readyLatch.await(5, TimeUnit.SECONDS);
        startLatch.countDown();
        doneLatch.await(30, TimeUnit.SECONDS);
        executor.shutdown();

        assertThat(successes.get()).isEqualTo(threads);
    }
}
