package org.nurfet.bookingsystem.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.nurfet.bookingsystem.dto.request.CreateBookingRequest;
import org.nurfet.bookingsystem.dto.request.UpdateBookingRequest;
import org.nurfet.bookingsystem.dto.response.ActiveBookingsCountResponse;
import org.nurfet.bookingsystem.dto.response.AvailabilityResponse;
import org.nurfet.bookingsystem.dto.response.BookingResponse;
import org.nurfet.bookingsystem.entity.BookingStatus;
import org.nurfet.bookingsystem.service.BookingService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api/v1/bookings")
@Tag(name = "Бронирования")
@RequiredArgsConstructor
@Validated
public class BookingController {

    private final BookingService service;

    @Operation(summary = "Создание бронирования")
    @ApiResponse(responseCode = "201", description = "Бронирование создано")
    @ApiResponse(responseCode = "409", description = "Конфликт времени")
    @ApiResponse(responseCode = "404", description = "Комната не найдена")
    @ApiResponse(responseCode = "400", description = "Ошибка валидации")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookingResponse createBooking(@Valid @RequestBody CreateBookingRequest request) {
        return service.createBooking(request);
    }

    @Operation(summary = "Частичное обновление бронирования")
    @ApiResponse(responseCode = "409", description = "Конфликт времени")
    @ApiResponse(responseCode = "404", description = "Бронирование не найдено")
    @ApiResponse(responseCode = "400", description = "Ошибка валидации")
    @PatchMapping("/{id}")
    public BookingResponse updateBooking(@PathVariable Long id,
                                         @Valid @RequestBody UpdateBookingRequest request) {
        return service.updateBooking(id, request);
    }

    @Operation(summary = "Получить бронирование по ID")
    @ApiResponse(responseCode = "404", description = "Бронирование не найдено")
    @GetMapping("/{id}")
    public BookingResponse getBooking(@PathVariable Long id) {
        return service.getBooking(id);
    }

    @Operation(summary = "Получить бронирования за период")
    @ApiResponse(responseCode = "404", description = "Комната не найдена")
    @GetMapping("/room/{roomId}")
    public List<BookingResponse> getByRoomAndTimeRange(
            @PathVariable Long roomId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant to) {

        return service.getBookingByRoomAndTimeRange(roomId, from, to);
    }

    @Operation(summary = "Получить активные бронирования комнаты")
    @ApiResponse(responseCode = "404", description = "Комната не найдена")
    @GetMapping("/room/{roomId}/active")
    public List<BookingResponse> getActiveBookingsByRoom(@PathVariable Long roomId) {
        return service.getActiveBookingsByRoom(roomId);
    }

    @Operation(summary = "Получить бронирования по email организатора")
    @GetMapping("/organizer")
    public List<BookingResponse> getBookingByOrganizerEmail(@RequestParam @NotBlank @Email String email) {

        return service.getBookingsByOrganizerEmail(email);
    }

    @Operation(summary = "Получить бронирования по статусу")
    @GetMapping("/status")
    public List<BookingResponse> getBookingByStatus(
            @Parameter(description = "Текущий статус бронирования", example = "CONFIRMED")
            @RequestParam BookingStatus status) {
        return service.getBookingsByStatus(status);
    }

    @Operation(summary = "Подтвердить бронирование")
    @ApiResponse(responseCode = "400", description = "Неверный статус для подтверждения")
    @ApiResponse(responseCode = "404", description = "Бронирование не найдено")
    @PostMapping("/{id}/confirm")
    public BookingResponse confirm(@PathVariable Long id) {
        return service.confirmBooking(id);
    }

    @Operation(summary = "Отменить бронирование")
    @ApiResponse(responseCode = "400", description = "Невозможно отменить бронирование")
    @ApiResponse(responseCode = "404", description = "Бронирование не найдено")
    @PostMapping("/{id}/cancel")
    public BookingResponse cancel(@PathVariable Long id) {
        return service.cancelBooking(id);
    }

    @Operation(summary = "Проверка доступности временного слота")
    @GetMapping("/availability")
    public AvailabilityResponse checkAvailable(
            @RequestParam Long roomId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant endTime) {

        return service.checkAvailability(roomId, startTime, endTime);
    }

    @Operation(summary = "Количество активных бронирований комнаты")
    @GetMapping("/room/{roomId}/count")
    public ActiveBookingsCountResponse countActiveBookingByRoom(@PathVariable Long roomId) {
        long count = service.countActiveBookingsByRoom(roomId);

        return new ActiveBookingsCountResponse(count);
    }
}