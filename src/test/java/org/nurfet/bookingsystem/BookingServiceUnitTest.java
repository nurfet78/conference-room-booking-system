package org.nurfet.bookingsystem;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.nurfet.bookingsystem.dto.request.CreateBookingRequest;
import org.nurfet.bookingsystem.dto.response.BookingResponse;
import org.nurfet.bookingsystem.entity.Booking;
import org.nurfet.bookingsystem.entity.BookingStatus;
import org.nurfet.bookingsystem.entity.Room;
import org.nurfet.bookingsystem.exception.BookingConflictException;
import org.nurfet.bookingsystem.exception.EntityNotFoundException;
import org.nurfet.bookingsystem.mapper.booking.BookingMapper;
import org.nurfet.bookingsystem.repository.BookingRepository;
import org.nurfet.bookingsystem.repository.RoomRepository;
import org.nurfet.bookingsystem.service.BookingService;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.*;

/**
 * Unit-тесты для BookingService с использованием Mockito.

 * Основные аннотации:
 * - @Mock — создаёт mock-объект (заглушку)
 * - @InjectMocks — создаёт реальный объект и внедряет в него mock'и
 * - @Captor — захватывает аргументы, переданные в mock

 * Основные методы Mockito:
 * - given(...).willReturn(...) — BDD-стиль (то же самое)
 * - then(...).should() — BDD-стиль проверки
 * - ArgumentCaptor — захват и проверка аргументов
 */

@ExtendWith(MockitoExtension.class)
@DisplayName("BookingService Unit Tests")
public class BookingServiceUnitTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private RoomRepository roomRepository;

    @Mock
    private BookingMapper bookingMapper;

    @InjectMocks
    private BookingService bookingService;

    @Captor
    private ArgumentCaptor<Booking> bookingCaptor;

    private Room testRoom;
    private Booking testBooking;
    private CreateBookingRequest createRequest;
    private BookingResponse bookingResponse;
    private Instant startTime;
    private Instant endTime;

    @BeforeEach
    void setUp() {
        startTime = Instant.now().plus(1, ChronoUnit.HOURS);
        endTime = Instant.now().plus(2, ChronoUnit.HOURS);

        testRoom = new Room("Тестовая комната", 10);
        ReflectionTestUtils.setField(testRoom, "id", 1L);

        testBooking = new Booking(
                testRoom,
                "Test Meeting",
                "test@example.com",
                startTime,
                endTime
        );
        ReflectionTestUtils.setField(testBooking, "id", 1L);

        createRequest = new CreateBookingRequest(
                1L,
                "Test Meeting",
                "test@example.com",
                startTime,
                endTime
        );

        bookingResponse = new BookingResponse(
                1L,
                1L,
                "Тестовая комната",
                "Test Meeting",
                "test@example.com",
                startTime,
                endTime,
                60L,
                BookingStatus.PENDING,
                Instant.now(),
                Instant.now()
        );
    }

    @Nested
    @DisplayName("createBooking")
    class CreateBookingTests {

        @Test
        @DisplayName("успешно создаёт бронирование")
        void shouldCreateBookingSuccessfully() {
            // Given — настраиваем поведение mock'ов
            given(roomRepository.findByIdWithLock(1L)).willReturn(Optional.of(testRoom));
            given(bookingRepository.existsOverlappingBooking(anyLong(), any(), any())).willReturn(false);
            given(bookingRepository.save(any(Booking.class))).willReturn(testBooking);
            given(bookingMapper.toResponse(any(Booking.class))).willReturn(bookingResponse);

            // When — вызываем тестируемый метод
            BookingResponse result = bookingService.createBooking(createRequest);

            // Then — проверяем результат
            assertThat(result).isNotNull();
            assertThat(result.title()).isEqualTo("Test Meeting");

            // Проверяем что методы были вызваны
            then(roomRepository).should().findByIdWithLock(1L);
            then(bookingRepository).should().existsOverlappingBooking(anyLong(), any(), any());
            then(bookingRepository).should().save(any(Booking.class));
            then(bookingMapper).should().toResponse(any(Booking.class));
        }

        @Test
        @DisplayName("Выбрасывает исключение если комната не найдена")
        void throwExceptionWhenRoomNotFound() {
            given(roomRepository.findByIdWithLock(999L)).willReturn(Optional.empty());

            CreateBookingRequest request = new CreateBookingRequest(
                    999L,
                    "Meeting", "test@example.com",
                    startTime, endTime);

            assertThatThrownBy(() -> bookingService.createBooking(request))
                    .isInstanceOf(EntityNotFoundException.class);

            then(bookingRepository).should(never()).save(any());
        }

        @Test
        @DisplayName("Выбрасывает исключение при конфликте времени")
        void throwExceptionWhenTimeConflict() {
            given(roomRepository.findByIdWithLock(1L)).willReturn(Optional.of(testRoom));
            given(bookingRepository.existsOverlappingBooking(anyLong(), any(), any())).willReturn(true);

            assertThatThrownBy(() -> bookingService.createBooking(createRequest))
                    .isInstanceOf(BookingConflictException.class);

            then(bookingRepository).should(never()).save(any());
        }

        @Test
        @DisplayName("Использует ArgumentCaptor для проверки сохраняемого объекта")
        void saveBookWithCorrectData() {
            given(roomRepository.findByIdWithLock(1L)).willReturn(Optional.of(testRoom));
            given(bookingRepository.existsOverlappingBooking(anyLong(), any(), any())).willReturn(false);
            given(bookingRepository.save(any(Booking.class))).willReturn(testBooking);
            given(bookingMapper.toResponse(any(Booking.class))).willReturn(bookingResponse);

            bookingService.createBooking(createRequest);

            then(bookingRepository).should().save(bookingCaptor.capture());

            Booking saved = bookingCaptor.getValue();
            assertThat(saved.getTitle()).isEqualTo("Test Meeting");
            assertThat(saved.getOrganizerEmail()).isEqualTo("test@example.com");
            assertThat(saved.getRoom()).isEqualTo(testRoom);
            assertThat(saved.getStatus()).isEqualTo(BookingStatus.PENDING);
        }
    }

    @Nested
    @DisplayName("ConfirmAndCancelBookings")
    class ConfirmBookingTests {

        @Test
        @DisplayName("успешно подтверждает PENDING бронирование")
        void confirmPendingBooking() {
            given(bookingRepository.findById(1L)).willReturn(Optional.of(testBooking));
            given(bookingRepository.save(any(Booking.class))).willReturn(testBooking);
            given(bookingMapper.toResponse(any(Booking.class))).willReturn(bookingResponse);

            BookingResponse result = bookingService.confirmBooking(1L);

            assertThat(result).isNotNull();

            then(bookingRepository).should().findById(1L);
            assertThat(testBooking.getStatus()).isEqualTo(BookingStatus.CONFIRMED);
        }

        @Test
        @DisplayName("успешно отменяет бронирование")
        void cancelBooking() {
            given(bookingRepository.findById(1L)).willReturn(Optional.of(testBooking));
            given(bookingRepository.save(any(Booking.class))).willReturn(testBooking);
            given(bookingMapper.toResponse(any(Booking.class))).willReturn(bookingResponse);

            bookingService.cancelBooking(1L);

            assertThat(testBooking.getStatus()).isEqualTo(BookingStatus.CANCELLED);
        }
    }

    @Nested
    @DisplayName("markExpiredBookings")
    class MarkExpiredBookingsTests {

        @Test
        @DisplayName("возвращает количество помеченных бронирований")
        void returnCountOfMarkedBookings() {
            given(bookingRepository.markExpiredBookings(any(Instant.class))).willReturn(5);

            int count = bookingService.markExpiredBookings();

            assertThat(count).isEqualTo(5);
            then(bookingRepository).should().markExpiredBookings(any(Instant.class));
        }
    }

    @Nested
    @DisplayName("getBooking and additional features")
    class GetBookingAndAdditionalFeatures {

        @Test
        @DisplayName("возвращает бронирование по ID")
        void returnBookingById() {
            given(bookingRepository.findById(1L)).willReturn(Optional.of(testBooking));
            given(bookingMapper.toResponse(testBooking)).willReturn(bookingResponse);

            BookingResponse result = bookingService.getBooking(1L);

            assertThat(result).isEqualTo(bookingResponse);
            then(bookingRepository).should().findById(1L);
            then(bookingMapper).should().toResponse(testBooking);
        }

        @Test
        @DisplayName("@Spy — частичный mock (реальный объект + переопределение)")
        void spyExample() {
            // Spy — реальный объект, но можно переопределить методы
            Room realRoom = new Room("Real Room", 10);
            Room spyRoom = spy(realRoom);

            // Реальный метод работает
            assertThat(spyRoom.getName()).isEqualTo("Real Room");

            // Переопределяем поведение
            willReturn("Mocked Name").given(spyRoom).getName();
            assertThat(spyRoom.getName()).isEqualTo("Mocked Name");
        }
    }
}
