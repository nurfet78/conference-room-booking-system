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
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.hibernate.sql.Update;
import org.nurfet.bookingsystem.dto.request.CreateBookingRequest;
import org.nurfet.bookingsystem.dto.request.UpdateBookingRequest;
import org.nurfet.bookingsystem.dto.response.AvailabilityResponse;
import org.nurfet.bookingsystem.dto.response.BookingResponse;
import org.nurfet.bookingsystem.entity.BookingStatus;
import org.nurfet.bookingsystem.repository.BookingRepository;
import org.nurfet.bookingsystem.service.BookingService;
import org.springdoc.core.service.GenericResponseService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/bookings")
@RequiredArgsConstructor
@Tag(name = "Бронирования")
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public ResponseEntity<BookingResponse> createBooking(@Valid @RequestBody CreateBookingRequest request) {
        BookingResponse response = bookingService.createBooking(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<BookingResponse> updateBooking(@PathVariable Long id,
                                                         @Valid @RequestBody UpdateBookingRequest request) {

        BookingResponse response = bookingService.updateBooking(id, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookingResponse> getBooking(@PathVariable Long id) {
        BookingResponse response = bookingService.getBooking(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/room/{roomId}")
    public ResponseEntity<List<BookingResponse>> getBookingByRoomAndTimeRange(
            @PathVariable Long roomId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant to) {

        List<BookingResponse> responses = bookingService.getBookingByRoomAndTimeRange(roomId, from, to);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/room/{roomId}/active")
    public ResponseEntity<List<BookingResponse>> getActiveBookingsByRoom(@PathVariable Long roomId) {
        List<BookingResponse> responses = bookingService.getActiveBookingsByRoom(roomId);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/organizer")
    public ResponseEntity<List<BookingResponse>> getByOrganizer(@RequestParam @Email String email) {
        List<BookingResponse> responses = bookingService.getBookingsByOrganizerEmail(email);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/status")
    public ResponseEntity<List<BookingResponse>> getByStatus(@RequestParam BookingStatus status) {
        List<BookingResponse> responses = bookingService.findBookingsByStatus(status);
        return ResponseEntity.ok(responses);
    }

    @PostMapping("/{id}/confirm")
    public ResponseEntity<BookingResponse> confirmBooking(@PathVariable Long id) {
        BookingResponse response = bookingService.confirmBooking(id);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<BookingResponse> cancelBooking(@PathVariable Long id) {
        BookingResponse response = bookingService.cancelBooking(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/availability")
    public ResponseEntity<AvailabilityResponse> checkAvailable(
            @RequestParam Long roomId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant endTime) {

        boolean available = bookingService.isTimeSlotAvailable(roomId, startTime, endTime);

        if (available) {
            return ResponseEntity.ok(AvailabilityResponse.free());
        } else {
            List<BookingResponse> conflicts = bookingService.findConflictingBookings(roomId, startTime, endTime);
            return ResponseEntity.ok(AvailabilityResponse.unavailable(conflicts));
        }
    }

    @GetMapping("/room/{roomId}/count")
    public ResponseEntity<Map<String, Long>> countActiveBookingsByRoom(@PathVariable Long roomId) {
        long count = bookingService.countActiveBookingsByRoom(roomId);

        return ResponseEntity.ok(Map.of("ActiveBookings", count));
    }
}