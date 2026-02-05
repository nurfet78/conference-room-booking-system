package org.nurfet.bookingsystem;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.nurfet.bookingsystem.entity.Booking;
import org.nurfet.bookingsystem.entity.BookingStatus;
import org.nurfet.bookingsystem.entity.Room;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.*;

public class BookingTest {

    private Room testRoom;
    private Instant baseTime;

    @BeforeEach
    void setUp() {
        testRoom = new Room("Test room", 10);
        baseTime = Instant.now().plus(1, ChronoUnit.DAYS)
                .truncatedTo(ChronoUnit.DAYS);
    }

    @Nested
    @DisplayName("Создание бронирования")
    class CreationTest {

        @Test
        void createWithValidParameters() {
            Instant startTime = baseTime;
            Instant endTime = baseTime.plus(1, ChronoUnit.HOURS);

            Booking booking = new Booking(
                    testRoom,
                    "Team Meeting",
                    "organizer@mail.ru",
                    startTime,
                    endTime
            );

            assertThat(booking.getRoom()).isEqualTo(testRoom);
            assertThat(booking.getTitle()).isEqualTo("Team Meeting");
            assertThat(booking.getOrganizerEmail()).isEqualTo("organizer@mail.ru");
            assertThat(booking.getStartTime()).isEqualTo(startTime);
            assertThat(booking.getEndTime()).isEqualTo(endTime);
            assertThat(booking.getStatus()).isEqualTo(BookingStatus.PENDING);
        }

        @Test
        void throwExceptionWhenRoomIsNull() {
            assertThatThrownBy(() -> new Booking(
                    null,
                    "Meeting",
                    "test@example.com",
                    baseTime,
                    baseTime.plus(1, ChronoUnit.HOURS)
            ))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("Room");
        }

        @Test
        void throwExceptionWhenEndTimeBeforeStartTime() {
            assertThatThrownBy(() -> new Booking(
                    testRoom,
                    "Meeting",
                    "test@example.com",
                    baseTime.plus(1, ChronoUnit.HOURS),
                    baseTime
            ))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("after");
        }

        @Test
        void throwExceptionWhenDurationTooShort() {
            assertThatThrownBy(() -> new Booking(
                    testRoom,
                    "Meeting",
                    "test@example.com",
                    baseTime,
                    baseTime.plus(10, ChronoUnit.MINUTES)
            ))

                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("15 minutes");
        }

        @Test
        void throwExceptionWhenDurationTooLong() {
            assertThatThrownBy(() -> new Booking(
                    testRoom,
                    "Meeting",
                    "test@example.com",
                    baseTime,
                    baseTime.plus(9, ChronoUnit.HOURS)
            ))

                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("8 hours");
        }

        @Test
        void allMinimumDuration() {
            Booking booking = new Booking(
                    testRoom,
                    "Meeting",
                    "test@example.com",
                    baseTime,
                    baseTime.plus(15, ChronoUnit.MINUTES)
            );

            assertThat(booking.getDuration().toMinutes()).isEqualTo(15);
        }

        @Test
        void allMaximumDuration() {
            Booking booking = new Booking(
                    testRoom,
                    "Meeting",
                    "test@example.com",
                    baseTime,
                    baseTime.plus(8, ChronoUnit.HOURS)
            );

            assertThat(booking.getDuration().toHours()).isEqualTo(8);
        }
    }

    @Nested
    @DisplayName("Проверка пересечений")
    class OverlapTests {

        private Booking existingBooking;

        @BeforeEach
        void setUpBooking() {
            existingBooking = new Booking(
                    testRoom,
                    "Meeting",
                    "test@example.com",
                    baseTime.plus(14, ChronoUnit.HOURS),
                    baseTime.plus(16, ChronoUnit.HOURS)
            );
        }

        @ParameterizedTest(name = "Интервал [{0}:00 - {1}:00] пересекается = {2}")
        @CsvSource({
                "14, 16, true",
                "15, 17, true",
                "13, 15, true",
                "14, 15, true",
                "13, 17, true",
                "12, 14, false",
                "16, 18, false",
                "10, 12, false",
                "18, 20, false"
        })
        void detectOverlap(int startHour, int endHour, boolean expect) {
            Instant start = baseTime.plus(startHour, ChronoUnit.HOURS);
            Instant end = baseTime.plus(endHour, ChronoUnit.HOURS);

            boolean overlaps = existingBooking.overlaps(start, end);

            assertThat(overlaps)
                    .as("Interval [%d:00 - %d:00] overlap with [14:00 - 16:00]",
            startHour, endHour)
                    .isEqualTo(expect);
        }
    }

    private Booking createValidBooking() {
        return new Booking(
                testRoom,
                "Test Meeting",
                "test@example.com",
                baseTime.plus(9, ChronoUnit.HOURS),
                baseTime.plus(11, ChronoUnit.HOURS)
        );
    }

    @Nested
    @DisplayName("Переходы статусов")
    class StatusTransitionTest {

        @Test
        @DisplayName("PENDING -> CONFIRMED")
        void confirmPendingBooking() {
            Booking booking = createValidBooking();
            assertThat(booking.getStatus()).isEqualTo(BookingStatus.PENDING);

            booking.confirm();
            assertThat(booking.getStatus()).isEqualTo(BookingStatus.CONFIRMED);
        }

        @Test
        @DisplayName("PENDING -> CANCELLED")
        void cancelPendingBooking() {
            Booking booking = createValidBooking();
            assertThat(booking.getStatus()).isEqualTo(BookingStatus.PENDING);

            booking.cancel();
            assertThat(booking.getStatus()).isEqualTo(BookingStatus.CANCELLED);
        }

        @Test
        @DisplayName("CONFIRMED -> CANCELLED")
        void cancelConfirmedBooking() {
            Booking booking = createValidBooking();
            booking.confirm();

            booking.cancel();

            assertThat(booking.getStatus()).isEqualTo(BookingStatus.CANCELLED);
        }

        @Test
        @DisplayName("No confirm already confirmed")
        void NotConfirmAlreadyConfirmed() {
            Booking booking = createValidBooking();
            booking.confirm();

            assertThatThrownBy(booking::confirm)
                    .isInstanceOf(IllegalStateException.class);
        }
    }

    @Nested
    @DisplayName("Проверка активности")
    class ActivityTests {

        @Test
        @DisplayName("PENDING считается активным")
        void pendingIsActive() {
            Booking booking = createValidBooking();
            assertThat(booking.isActive()).isTrue();
        }

        @Test
        @DisplayName("CONFIRMED считается активным")
        void confirmIsActive() {
            Booking booking = createValidBooking();
            booking.confirm();
            assertThat(booking.isActive()).isTrue();
        }

        @Test
        @DisplayName("CANCELLED не активен")
        void cancelIsNotActive() {
            Booking booking = createValidBooking();
            booking.cancel();
            assertThat(booking.isActive()).isFalse();
        }
    }
}
