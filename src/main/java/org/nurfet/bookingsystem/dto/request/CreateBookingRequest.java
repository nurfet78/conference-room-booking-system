package org.nurfet.bookingsystem.dto.request;

import jakarta.validation.constraints.*;

import java.time.Instant;

public record CreateBookingRequest(

        @NotNull(message = "Room ID is required")
        Long roomId,

        @NotBlank(message = "Title is required")
        @Size(max = 200, message = "Title must not exceed 200 characters")
        String title,

        @NotBlank(message = "Organized email is required")
        @Email(message = "Invalid email format")
        @Size(max = 255, message = "Email must not exceed 255 characters")
        String organizerEmail,

        @NotNull(message = "Start time is required")
        @Future(message = "Start time must be in the future")
        Instant startTime,

        @NotNull(message = "End time is required")
        @Future(message = "End time must by in the future")
        Instant endTime
) {

    public CreateBookingRequest {
        if (startTime != null && endTime != null && !endTime.isAfter(startTime)) {
            throw new IllegalArgumentException("End time must be after start time");
        }
    }
}
