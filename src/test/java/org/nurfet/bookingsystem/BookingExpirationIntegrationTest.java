package org.nurfet.bookingsystem;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.nurfet.bookingsystem.entity.Booking;
import org.nurfet.bookingsystem.entity.BookingStatus;
import org.nurfet.bookingsystem.entity.Room;
import org.nurfet.bookingsystem.repository.BookingRepository;
import org.nurfet.bookingsystem.repository.RoomRepository;
import org.nurfet.bookingsystem.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("BookingCleanupScheduler Integration Test")
public class BookingExpirationIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private BookingService bookingService;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private RoomRepository roomRepository;

    private Room room;

    @BeforeEach
    void setUp() {
        bookingRepository.deleteAll();
        roomRepository.deleteAll();

        room = new Room("Тестовая комната", 10);
        roomRepository.save(room);
    }

    private Long createConfirmedBookingInPast(Instant start, Instant end) {
        jdbcTemplate.update("""
        INSERT INTO bookings(room_id, title, organizer_email, start_time, end_time, status)
        VALUES(?, 'Test Meeting', 'test@example.com', ?, ?, 'CONFIRMED')
        """,
                room.getId(), Timestamp.from(start), Timestamp.from(end));

        return jdbcTemplate.queryForObject(
                "SELECT MAX(id) FROM bookings",
                Long.class
        );
    }

    @Test
    @DisplayName("Должен помечать истекшие PENDING бронирования")
    void markExpiredPendingBookings() {
        Instant pastStart = Instant.now().minus(2, ChronoUnit.HOURS);
        Instant pastEnd = Instant.now().minus(1, ChronoUnit.HOURS);

        Booking booking = new Booking(
                room,
                "Meeting",
                "test@example.com",
                pastStart,
                pastEnd);
        bookingRepository.save(booking);

        assertThat(booking.getStatus()).isEqualTo(BookingStatus.PENDING);

        int count = bookingService.markExpiredBookings();

        assertThat(count).isEqualTo(1);
        Booking updated = bookingRepository.findById(booking.getId()).orElseThrow();
        assertThat(updated.getStatus()).isEqualTo(BookingStatus.EXPIRED);
    }

    @Test
    @DisplayName("Должен помечать истекшие CONFIRM бронирования")
    void markExpiredConfirmBookings() {
        Instant pastStart = Instant.now().minus(2, ChronoUnit.HOURS);
        Instant pastEnd = Instant.now().minus(1, ChronoUnit.HOURS);

        Long bookingId = createConfirmedBookingInPast(pastStart, pastEnd);

        int count = bookingService.markExpiredBookings();

        assertThat(count).isEqualTo(1);

        Booking updated = bookingRepository.findById(bookingId).orElseThrow();
        assertThat(updated.getStatus()).isEqualTo(BookingStatus.EXPIRED);
    }

    @Test
    @DisplayName("Не должен помечать будущие бронирования")
    void notMarkFutureBookings() {
        Instant futureStart = Instant.now().plus(1, ChronoUnit.HOURS);
        Instant futureEnd = Instant.now().plus(2, ChronoUnit.HOURS);

        Booking booking = new Booking(
                room,
                "Будущая встреча",
                "test@example.com",
                futureStart,
                futureEnd);

        bookingRepository.save(booking);

        int count = bookingService.markExpiredBookings();

        assertThat(count).isEqualTo(0);

        Booking updated = bookingRepository.findById(booking.getId()).orElseThrow();
        assertThat(updated.getStatus()).isEqualTo(BookingStatus.PENDING);
    }

    @Test
    @DisplayName("Не должен помечать отмененные бронирования")
    void notMarkCancelledBookings() {
        Instant start = Instant.now().minus(2, ChronoUnit.HOURS);
        Instant end = Instant.now().minus(1, ChronoUnit.HOURS);

        Booking booking = new Booking(
                room,
                "Отмененная встреча",
                "test@example.com",
                start,
                end);
        booking.cancel();
        bookingRepository.save(booking);

        int count = bookingService.markExpiredBookings();

        assertThat(count).isEqualTo(0);

        Booking updated = bookingRepository.findById(booking.getId()).orElseThrow();
        assertThat(updated.getStatus()).isEqualTo(BookingStatus.CANCELLED);
    }
}
