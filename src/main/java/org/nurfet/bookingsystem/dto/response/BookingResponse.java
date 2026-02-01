package org.nurfet.bookingsystem.dto.response;

import org.nurfet.bookingsystem.entity.BookingStatus;

import java.time.Duration;
import java.time.Instant;

public record BookingResponse(
        Long id,
        Long roomId,
        String roomName,
        String title,
        String organizerEmail,
        Instant startTime,
        Instant endTime,
        long durationMinutes,
        BookingStatus status,
        Instant createdAt,
        Instant updatedAt) {

    public static BookingResponse of(
            Long id,
            Long roomId,
            String roomName,
            String title,
            String organizerEmail,
            Instant startTime,
            Instant endTime,
            BookingStatus status,
            Instant createdAt,
            Instant updatedAt
    ) {
        long durationMinutes = Duration.between(startTime, endTime).toMinutes();
        return new BookingResponse(
                id,
                roomId,
                roomName,
                title,
                organizerEmail,
                startTime,
                endTime,
                durationMinutes,
                status,
                createdAt,
                updatedAt
        );
    }
}
