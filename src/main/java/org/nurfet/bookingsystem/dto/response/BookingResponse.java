package org.nurfet.bookingsystem.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import org.nurfet.bookingsystem.entity.BookingStatus;

import java.time.Duration;
import java.time.Instant;

@Schema(description = "Информация о бронировании")
public record BookingResponse(

        @Schema(
                description = "Уникальный ID бронирования"
        )
        Long id,

        @Schema(description = "ID забронированной комнаты")
        Long roomId,

        @Schema(description = "Название переговорной комнаты")
        String roomName,

        @Schema(description = "Название встречи")
        String title,

        @Schema(description = "Email организатора встречи")
        String organizerEmail,

        @Schema(
                description = "Время начала встречи (ISO 8601 UTC)",
                example = "2026-01-01T09:00:00Z",
                type = "string",
                format = "date-time"
        )
        Instant startTime,

        @Schema(
                description = "Время окончания встречи (ISO 8601 UTC)",
                example = "2026-01-01T09:00:00Z",
                type = "string",
                format = "date-time"
        )
        Instant endTime,

        @Schema(description = "Продолжительность встречи в минутах")
        long durationMinutes,

        @Schema(
                description = "Статус бронирования",
                example = "CONFIRMED"
        )
        BookingStatus status,

        @Schema(
                description = "Время создание бронирования (ISO 8601 UTC)",
                example = "2026-01-01T09:00:00Z",
                type = "string",
                format = "date-time"
        )
        Instant createdAt,

        @Schema(
                description = "Время последнего обновления бронирования (ISO 8601 UTC)",
                example = "2026-01-01T09:00:00Z",
                type = "string",
                format = "date-time"
        )
        Instant updatedAt
) {

    public static BookingResponse of(
            Long id, Long roomId, String roomName,
            String title, String organizerEmail,
            Instant startTime, Instant endTime,
            BookingStatus status,
            Instant createdAt, Instant updatedAt) {

        long durationMinutes = Duration.between(startTime, endTime).toMinutes();

        return new BookingResponse(id, roomId, roomName, title,
                organizerEmail, startTime, endTime, durationMinutes,
                status, createdAt, updatedAt);
    }
}