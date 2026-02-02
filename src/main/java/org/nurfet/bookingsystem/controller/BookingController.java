package org.nurfet.bookingsystem.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import org.nurfet.bookingsystem.dto.request.CreateBookingRequest;
import org.nurfet.bookingsystem.dto.response.BookingResponse;
import org.nurfet.bookingsystem.entity.BookingStatus;
import org.nurfet.bookingsystem.service.BookingService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/bookings")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public ResponseEntity<BookingResponse> createBooking(
            @Valid @RequestBody CreateBookingRequest request) {

        BookingResponse response = bookingService.createBooking(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookingResponse> getBooking(@PathVariable Long id) {
        BookingResponse response = bookingService.getBooking(id);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/room/{roomId}")
    public ResponseEntity<List<BookingResponse>> getBookingsByRoom(
            @PathVariable Long roomId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)Instant from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant to) {

        List<BookingResponse> bookings = bookingService.getBookingByRoomAndTimeRange(
                roomId, from, to);

        return ResponseEntity.ok(bookings);
    }

    @GetMapping("/room/{roomId}/active")
    public ResponseEntity<List<BookingResponse>> getActiveBookings(
            @PathVariable Long roomId) {

        List<BookingResponse> bookings = bookingService.getActiveBookingsByRoom(roomId);
        return ResponseEntity.ok(bookings);
    }

    @GetMapping("/organizer")
    public ResponseEntity<List<BookingResponse>> getBookingByOrganizer(
            @RequestParam @Email String email) {

        List<BookingResponse> bookings = bookingService.getBookingsByOrganizerEmail(email);
        return ResponseEntity.ok(bookings);
    }

    @PostMapping("/{id}/confirm")
    public ResponseEntity<BookingResponse> confirmBooking(@PathVariable Long id) {
        BookingResponse booking = bookingService.confirmBooking(id);
        return ResponseEntity.ok(booking);
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<BookingResponse> cancelBooking(@PathVariable Long id) {
        BookingResponse booking = bookingService.cancelBooking(id);
        return ResponseEntity.ok(booking);
    }

    @GetMapping("/availability")
    public ResponseEntity<Map<String, Object>> checkAvailable(
            @RequestParam Long roomId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant endTime) {

        boolean available = bookingService.isTimeSlotAvailable(roomId, startTime, endTime);

        if (available) {
            return ResponseEntity.ok(Map.of("available", true));
        } else {
            List<BookingResponse> conflicts = bookingService.findConflictingBooking(
                    roomId,
                    startTime, endTime);

            return ResponseEntity.ok(Map.of(
                    "available", false,
                    "conflicts", conflicts
            ));
        }
    }

    @GetMapping("/status")
    public ResponseEntity<List<BookingResponse>> getBookingsByStatus(
            @RequestParam BookingStatus status) {

        List<BookingResponse> bookings = bookingService.getBookingByStatus(status);
        return ResponseEntity.ok(bookings);
    }

    @GetMapping("/room/{roomId}/count")
    public ResponseEntity<Map<String, Long>> countActiveBookings(@PathVariable Long roomId) {
        long count = bookingService.countActiveBookings(roomId);
        return ResponseEntity.ok(Map.of("activeBookings", count));
    }

    @PatchMapping("/{id}/time")
    public ResponseEntity<BookingResponse> updateBookingTime(
            @PathVariable Long id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant endTime) {

        BookingResponse response = bookingService.updateBookingTime(id, startTime, endTime);
        return ResponseEntity.ok(response);
    }
}
