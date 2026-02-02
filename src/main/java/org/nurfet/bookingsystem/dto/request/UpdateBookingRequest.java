package org.nurfet.bookingsystem.dto.request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Size;

import java.time.Instant;

public record UpdateBookingRequest(
        Long roomId,

        @Size(max = 200, message = "Title must not exceed 200 characters")
        String title,

        @Future(message = "Start time must be in the future")
        Instant startTime,

        @Future(message = "End time must be in the future")
        Instant endTime
) {
    public UpdateBookingRequest {
        // Валидация только если оба времени переданы
        if (startTime != null && endTime != null && !endTime.isAfter(startTime)) {
            throw new IllegalArgumentException("End time must be after start time");
        }
    }
}
