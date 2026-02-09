package org.nurfet.bookingsystem.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import org.nurfet.bookingsystem.dto.error.ErrorResponse;
import org.nurfet.bookingsystem.dto.request.CreateBookingRequest;
import org.nurfet.bookingsystem.dto.request.UpdateBookingRequest;
import org.nurfet.bookingsystem.dto.response.AvailabilityResponse;
import org.nurfet.bookingsystem.dto.response.BookingResponse;
import org.nurfet.bookingsystem.entity.BookingStatus;
import org.nurfet.bookingsystem.service.BookingService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/bookings")
@Tag(name = "Бронирования")  // Тег по умолчанию для всех эндпоинтов контроллера
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @Operation(summary = "Создать бронирование")
    @ApiResponse(responseCode = "201", description = "Бронирование создано",
            content = @Content(schema = @Schema(implementation = BookingResponse.class)))
    @PostMapping
    public ResponseEntity<BookingResponse> createBooking(@Valid @RequestBody CreateBookingRequest request) {

        BookingResponse response = bookingService.createBooking(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(
            summary = "Получить бронирование по ID",
            operationId = "getBookingById"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Бронирование найдено",
                    content = @Content(schema = @Schema(implementation = BookingResponse.class))),
            @ApiResponse(responseCode = "404", description = "Бронирование не найдено",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<BookingResponse> getBooking(@PathVariable Long id) {
        BookingResponse response = bookingService.getBooking(id);

        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Получить бронирования комнаты за период",
            description = "Возвращает все бронирования указанной комнаты в заданном временном диапазоне",
            operationId = "getBookingsByRoomAndTimeRange"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Список бронирований",
                    content = @Content(array = @ArraySchema(
                            schema = @Schema(implementation = BookingResponse.class)))),
            @ApiResponse(responseCode = "404", description = "Комната не найдена",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/room/{roomId}")
    public ResponseEntity<List<BookingResponse>> getBookingsByRoom(
            @PathVariable Long roomId,
            @Parameter(
                    description = "Начало диапазона (ISO 8601, UTC)",
                    required = true,
                    example = "2025-07-01T00:00:00Z"
            )
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)Instant from,
            @Parameter(
                    description = "Конец диапазона (ISO 8601, UTC)",
                    required = true,
                    example = "2025-07-31T23:59:59Z"
            )
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant to) {

        List<BookingResponse> bookings = bookingService.getBookingByRoomAndTimeRange(
                roomId, from, to);

        return ResponseEntity.ok(bookings);
    }

    @Operation(
            summary = "Получить активные бронирования комнаты",
            description = "Возвращает бронирования со статусом PENDING или CONFIRMED",
            operationId = "getActiveBookingsByRoom"
    )
    @ApiResponse(responseCode = "200", description = "Список активных бронирований",
            content = @Content(array = @ArraySchema(
                    schema = @Schema(implementation = BookingResponse.class))))
    @GetMapping("/room/{roomId}/active")
    public ResponseEntity<List<BookingResponse>> getActiveBookings(@PathVariable Long roomId) {

        List<BookingResponse> bookings = bookingService.getActiveBookingsByRoom(roomId);
        return ResponseEntity.ok(bookings);
    }

    @Operation(
            summary = "Найти бронирования по email организатора",
            operationId = "getBookingsByOrganizer"
    )
    @ApiResponse(responseCode = "200", description = "Список бронирований организатора",
            content = @Content(array = @ArraySchema(
                    schema = @Schema(implementation = BookingResponse.class))))
    @GetMapping("/organizer")
    public ResponseEntity<List<BookingResponse>> getBookingByOrganizer(
            @Parameter(
                    description = "Email организатора",
                    required = true,
                    example = "ivan.petrov@example.com",
                    schema = @Schema(format = "email")  // Подсказка формата в параметре
            )
            @RequestParam @Email String email) {

        List<BookingResponse> bookings = bookingService.getBookingsByOrganizerEmail(email);
        return ResponseEntity.ok(bookings);
    }

    @Operation(
            summary = "Подтвердить бронирование",
            description = "Переводит бронирование из статуса PENDING в CONFIRMED. Только бронирования в статусе PENDING могут быть подтверждены",
            operationId = "confirmBooking"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Бронирование подтверждено",
                    content = @Content(schema = @Schema(implementation = BookingResponse.class))),
            @ApiResponse(responseCode = "400", description = "Бронирование не может быть подтверждено (неверный статус)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Бронирование не найдено",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/{id}/confirm")
    public ResponseEntity<BookingResponse> confirmBooking(
            @PathVariable Long id) {
        BookingResponse booking = bookingService.confirmBooking(id);
        return ResponseEntity.ok(booking);
    }

    @Operation(
            summary = "Отменить бронирование",
            description = "Переводит бронирование в статус CANCELLED. Отменить можно только PENDING или CONFIRMED бронирования",
            operationId = "cancelBooking"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Бронирование отменено",
                    content = @Content(schema = @Schema(implementation = BookingResponse.class))),
            @ApiResponse(responseCode = "400", description = "Бронирование не может быть отменено",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Бронирование не найдено",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/{id}/cancel")
    public ResponseEntity<BookingResponse> cancelBooking(
            @PathVariable Long id) {
        BookingResponse booking = bookingService.cancelBooking(id);
        return ResponseEntity.ok(booking);
    }

    @Operation(
            summary = "Проверка доступности временного слота",
            description = "Возвращает информацию о доступности временного слота для указанной комнаты."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Результат проверки доступности",
            content = @Content(schema = @Schema(implementation = AvailabilityResponse.class))
    )
    @Tag(name = "Доступность")
    @GetMapping("/availability")
    public ResponseEntity<AvailabilityResponse> checkAvailable(
            @Parameter(description = "ID комнаты", required = true, example = "1")
            @RequestParam Long roomId,

            @Parameter(description = "Время начала слота (ISO 8601, UTC)", required = true,
                    example = "2025-07-01T09:00:00Z")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant startTime,

            @Parameter(description = "Время окончания слота (ISO 8601, UTC)", required = true,
                    example = "2025-07-01T10:00:00Z")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant endTime) {

        boolean available = bookingService.isTimeSlotAvailable(roomId, startTime, endTime);

        if (available) {
            return ResponseEntity.ok(AvailabilityResponse.free());
        } else {
            List<BookingResponse> conflicts = bookingService.findConflictingBooking(
                    roomId, startTime, endTime);
            return ResponseEntity.ok(AvailabilityResponse.unavailable(conflicts));
        }
    }

    @Operation(
            summary = "Получить бронирования по статусу",
            operationId = "getBookingsByStatus"
    )
    @ApiResponse(responseCode = "200", description = "Список бронирований с указанным статусом",
            content = @Content(array = @ArraySchema(
                    schema = @Schema(implementation = BookingResponse.class))))
    @GetMapping("/status")
    public ResponseEntity<List<BookingResponse>> getBookingsByStatus(
            @Parameter(
                    description = "Статус бронирования для фильтрации",
                    required = true,
                    example = "CONFIRMED",
                    // schema с enumAsRef — Swagger UI покажет выпадающий список значений enum
                    schema = @Schema(implementation = BookingStatus.class)
            )
            @RequestParam BookingStatus status) {

        List<BookingResponse> bookings = bookingService.getBookingByStatus(status);
        return ResponseEntity.ok(bookings);
    }

    @Operation(
            summary = "Количество активных бронирований комнаты"
    )
    @GetMapping("/room/{roomId}/count")
    public ResponseEntity<Map<String, Long>> countActiveBookings(
            @Parameter(description = "ID комнаты", required = true, example = "1")
            @PathVariable Long roomId) {
        long count = bookingService.countActiveBookings(roomId);
        return ResponseEntity.ok(Map.of("activeBookings", count));
    }

    @Operation(
            summary = "Обновить бронирование (частично)",
            description = """
                    Частичное обновление бронирования. Передайте только поля, которые нужно изменить.
                    
                    **Ограничения:**
                    - Нельзя обновить CANCELLED или EXPIRED бронирование
                    - При изменении времени проверяется отсутствие конфликтов
                    """,
            operationId = "updateBooking"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Бронирование обновлено",
                    content = @Content(schema = @Schema(implementation = BookingResponse.class))),
            @ApiResponse(responseCode = "400", description = "Ошибка валидации или конфликт",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Бронирование не найдено",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PatchMapping("/{id}")
    public ResponseEntity<BookingResponse> updateBooking(
            @PathVariable Long id,
            @Valid @RequestBody UpdateBookingRequest request) {

        BookingResponse response = bookingService.updateBooking(id, request);
        return ResponseEntity.ok(response);
    }
}
