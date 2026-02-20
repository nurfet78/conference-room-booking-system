package org.nurfet.bookingsystem;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.nurfet.bookingsystem.dto.request.CreateBookingRequest;
import org.nurfet.bookingsystem.dto.request.CreateRoomRequest;
import org.nurfet.bookingsystem.dto.response.BookingResponse;
import org.nurfet.bookingsystem.dto.response.RoomResponse;
import org.nurfet.bookingsystem.entity.BookingStatus;
import org.nurfet.bookingsystem.repository.BookingRepository;
import org.nurfet.bookingsystem.repository.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

class BookingControllerIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private BookingRepository bookingRepository;

    private RoomResponse testRoom;

    @BeforeEach
    void setUp() {
        bookingRepository.deleteAll();
        roomRepository.deleteAll();

        // Создаём комнату через API
        CreateRoomRequest roomRequest = new CreateRoomRequest(
                "API Test Room",
                10,
                "Room for API tests"
        );

        testRoom = webTestClient.post()
                .uri("/api/v1/rooms")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(roomRequest)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(RoomResponse.class)
                .returnResult()
                .getResponseBody();
    }

    @Nested
    @DisplayName("POST /api/v1/bookings")
    class CreateBookingApiTests {

        @Test
        @DisplayName("201 Created при успешном создании")
        void shouldReturn201WhenBookingCreated() {
            // Given
            CreateBookingRequest request = new CreateBookingRequest(
                    testRoom.id(),
                    "API Test Meeting",
                    "api@example.com",
                    Instant.now().plus(1, ChronoUnit.HOURS).truncatedTo(ChronoUnit.SECONDS),
                    Instant.now().plus(2, ChronoUnit.HOURS).truncatedTo(ChronoUnit.SECONDS)
            );

            // When/Then
            webTestClient.post()
                    .uri("/api/v1/bookings")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(request)
                    .exchange()
                    .expectStatus().isCreated()
                    .expectBody(BookingResponse.class)
                    .value(response -> {
                        assertThat(response.id()).isNotNull();
                        assertThat(response.title()).isEqualTo("API Test Meeting");
                        assertThat(response.status()).isEqualTo(BookingStatus.PENDING);
                    });
        }

        @Test
        @DisplayName("409 Conflict при пересечении")
        void shouldReturn409WhenTimeSlotOccupied() {
            // Given: создаём первое бронирование
            Instant startTime = Instant.now().plus(1, ChronoUnit.DAYS)
                    .truncatedTo(ChronoUnit.SECONDS);
            Instant endTime = startTime.plus(1, ChronoUnit.HOURS);

            CreateBookingRequest firstRequest = new CreateBookingRequest(
                    testRoom.id(),
                    "First Meeting",
                    "first@example.com",
                    startTime,
                    endTime
            );

            webTestClient.post()
                    .uri("/api/v1/bookings")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(firstRequest)
                    .exchange()
                    .expectStatus().isCreated();

            // When: пытаемся создать пересекающееся
            CreateBookingRequest conflictRequest = new CreateBookingRequest(
                    testRoom.id(),
                    "Conflicting Meeting",
                    "conflict@example.com",
                    startTime,
                    endTime
            );

            // Then
            webTestClient.post()
                    .uri("/api/v1/bookings")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(conflictRequest)
                    .exchange()
                    .expectStatus().isEqualTo(409)
                    .expectBody()
                    .jsonPath("$.errorCode").isEqualTo("BOOKING_CONFLICT");
        }

        @Test
        @DisplayName("400 Bad Request при невалидных данных")
        void shouldReturn400WhenValidationFails() {
            // Given: пустой title и невалидный email
            String invalidJson = """
                {
                    "roomId": %d,
                    "title": "",
                    "organizerEmail": "invalid-email",
                    "startTime": "%s",
                    "endTime": "%s"
                }
                """.formatted(
                    testRoom.id(),
                    Instant.now().plus(1, ChronoUnit.HOURS),
                    Instant.now().plus(2, ChronoUnit.HOURS)
            );

            // When/Then
            webTestClient.post()
                    .uri("/api/v1/bookings")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(invalidJson)
                    .exchange()
                    .expectStatus().isBadRequest()
                    .expectBody()
                    .jsonPath("$.errorCode").isEqualTo("VALIDATION_ERROR");
        }

        @Test
        @DisplayName("404 Not Found при несуществующей комнате")
        void shouldReturn404WhenRoomNotFound() {
            // Given
            CreateBookingRequest request = new CreateBookingRequest(
                    999L,
                    "Meeting",
                    "test@example.com",
                    Instant.now().plus(1, ChronoUnit.HOURS),
                    Instant.now().plus(2, ChronoUnit.HOURS)
            );

            // When/Then
            webTestClient.post()
                    .uri("/api/v1/bookings")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(request)
                    .exchange()
                    .expectStatus().isNotFound();
        }
    }

    @Nested
    @DisplayName("POST /api/v1/bookings/{id}/confirm")
    class ConfirmBookingApiTests {

        @Test
        @DisplayName("200 OK при успешном подтверждении")
        void shouldReturn200WhenConfirmed() {
            // Given
            BookingResponse booking = createBooking();

            // When/Then
            webTestClient.post()
                    .uri("/api/v1/bookings/{id}/confirm", booking.id())
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody(BookingResponse.class)
                    .value(response ->
                            assertThat(response.status()).isEqualTo(BookingStatus.CONFIRMED)
                    );
        }
    }

    @Nested
    @DisplayName("POST /api/v1/bookings/{id}/cancel")
    class CancelBookingApiTests {

        @Test
        @DisplayName("200 OK при успешной отмене")
        void shouldReturn200WhenCancelled() {
            // Given
            BookingResponse booking = createBooking();

            // When/Then
            webTestClient.post()
                    .uri("/api/v1/bookings/{id}/cancel", booking.id())
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody(BookingResponse.class)
                    .value(response ->
                            assertThat(response.status()).isEqualTo(BookingStatus.CANCELLED)
                    );
        }
    }

    @Nested
    @DisplayName("GET /api/v1/bookings/availability")
    class AvailabilityApiTests {

        @Test
        @DisplayName("Возвращает available=true для свободного слота")
        void shouldReturnAvailableTrue() {
            // Given
            Instant startTime = Instant.now().plus(1, ChronoUnit.DAYS);
            Instant endTime = startTime.plus(1, ChronoUnit.HOURS);

            // When/Then
            webTestClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/api/v1/bookings/availability")
                            .queryParam("roomId", testRoom.id())
                            .queryParam("startTime", startTime.toString())
                            .queryParam("endTime", endTime.toString())
                            .build())
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody()
                    .jsonPath("$.available").isEqualTo(true);
        }

        @Test
        @DisplayName("Возвращает available=false и конфликты для занятого слота")
        void shouldReturnAvailableFalseWithConflicts() {
            // Given
            Instant startTime = Instant.now().plus(1, ChronoUnit.DAYS)
                    .truncatedTo(ChronoUnit.SECONDS);
            Instant endTime = startTime.plus(1, ChronoUnit.HOURS);

            // Создаём бронирование
            CreateBookingRequest request = new CreateBookingRequest(
                    testRoom.id(),
                    "Existing Meeting",
                    "existing@example.com",
                    startTime,
                    endTime
            );

            webTestClient.post()
                    .uri("/api/v1/bookings")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(request)
                    .exchange()
                    .expectStatus().isCreated();

            // When/Then
            webTestClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/api/v1/bookings/availability")
                            .queryParam("roomId", testRoom.id())
                            .queryParam("startTime", startTime.toString())
                            .queryParam("endTime", endTime.toString())
                            .build())
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody()
                    .jsonPath("$.available").isEqualTo(false)
                    .jsonPath("$.conflicts").isArray()
                    .jsonPath("$.conflicts").isNotEmpty();
        }
    }

    @Test
    @DisplayName("PATCH /api/v1/bookings/{id}")
    void updateBooking() {
        BookingResponse bookingResponse = createBooking();

        Instant start = Instant.now().plus(2, ChronoUnit.HOURS).truncatedTo(ChronoUnit.SECONDS);
        Instant end = Instant.now().plus(4, ChronoUnit.HOURS).truncatedTo(ChronoUnit.SECONDS);

        String json = """
            {
                "roomId": %d,
                "title": "ВКС",
                "startTime": "%s",
                "endTime": "%s"
            }
            """.formatted(testRoom.id(), start, end);

        webTestClient.patch()
                .uri("/api/v1/bookings/{id}", bookingResponse.id())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(json)
                .exchange()
                .expectStatus().isOk()
                .expectBody(BookingResponse.class)
                .value(response -> {
                    assertThat(response.id()).isEqualTo(bookingResponse.id());
                    assertThat(response.roomId()).isEqualTo(testRoom.id());
                    assertThat(response.title()).isEqualTo("ВКС");
                    assertThat(response.startTime()).isEqualTo(start);
                    assertThat(response.endTime()).isEqualTo(end);
                    // Email не меняется — проверяем что остался прежним
                    assertThat(response.organizerEmail()).isEqualTo(bookingResponse.organizerEmail());
                    assertThat(response.status()).isEqualTo(BookingStatus.PENDING);
                });
    }

    @Test
    @DisplayName("PATCH /api/v1/bookings/{id} — только title")
    void updateBookingTitleOnly() {
        BookingResponse original = createBooking();

        String json = """
            {
                "title": "Новое название"
            }
            """;

        webTestClient.patch()
                .uri("/api/v1/bookings/{id}", original.id())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(json)
                .exchange()
                .expectStatus().isOk()
                .expectBody(BookingResponse.class)
                .value(response -> {
                    assertThat(response.title()).isEqualTo("Новое название");
                    // Остальные поля не изменились
                    assertThat(response.startTime()).isEqualTo(original.startTime());
                    assertThat(response.endTime()).isEqualTo(original.endTime());
                    assertThat(response.roomId()).isEqualTo(original.roomId());
                });
    }

    @Test
    @DisplayName("PATCH /api/v1/bookings/{id} — 404 если не найден")
    void updateBookingNotFound() {
        String json = """
            {
                "title": "Новое название"
            }
            """;

        webTestClient.patch()
                .uri("/api/v1/bookings/{id}", 999)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(json)
                .exchange()
                .expectStatus().isNotFound();
    }

    // Вспомогательный метод
    private BookingResponse createBooking() {
        CreateBookingRequest request = new CreateBookingRequest(
                testRoom.id(),
                "Test Meeting",
                "admin@gmail.com",
                Instant.now().plus(1, ChronoUnit.HOURS).truncatedTo(ChronoUnit.SECONDS),
                Instant.now().plus(2, ChronoUnit.HOURS).truncatedTo(ChronoUnit.SECONDS)
        );

        return webTestClient.post()
                .uri("/api/v1/bookings")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(BookingResponse.class)
                .returnResult()
                .getResponseBody();
    }
}
