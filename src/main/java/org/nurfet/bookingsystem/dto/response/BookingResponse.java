package org.nurfet.bookingsystem.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import org.nurfet.bookingsystem.entity.BookingStatus;

import java.time.Duration;
import java.time.Instant;
import java.util.stream.LongStream;

@Schema(
        description = "Информация о бронировании"
)
public record BookingResponse(

        @Schema(
                description = "Уникальный ID бронирования",
                example = "42",
                accessMode = Schema.AccessMode.READ_ONLY
        )
        Long id,

        @Schema(
                description = "ID переговорной комнаты",
                example = "1",
                accessMode = Schema.AccessMode.READ_ONLY
        )
        Long roomId,

        @Schema(
                description = "Название переговорной комнаты",
                accessMode = Schema.AccessMode.READ_ONLY
        )
        String roomName,

        @Schema(
                description = "Название встречи",
                accessMode = Schema.AccessMode.READ_ONLY
        )
        String title,

        @Schema(
                description = "Email организатора встречи",
                format = "email",
                accessMode = Schema.AccessMode.READ_ONLY
        )
        String organizerEmail,

        @Schema(
                description = "Дата начала (ISO 8601 UTC)",
                example = "2026-01-01T09:00:00Z",
                type = "string",
                format = "date-time",
                accessMode = Schema.AccessMode.READ_ONLY
        )
        Instant startTime,

        @Schema(
                description = "Дата окончания (ISO 8601 UTC)",
                example = "2026-01-01T09:00:00Z",
                type = "string",
                format = "date-time",
                accessMode = Schema.AccessMode.READ_ONLY
        )
        Instant endTime,

        @Schema(
                description = "Продолжительность в минутах",
                accessMode = Schema.AccessMode.READ_ONLY
        )
        long durationMinutes,

        @Schema(
                description = "Текущий статус бронирования",
                example = "CONFIRMED",
                accessMode = Schema.AccessMode.READ_ONLY
        )
        BookingStatus status,

        @Schema(
                description = "Дата создания бронирования (ISO 8601 UTC)",
                example = "2026-01-01T09:00:00Z",
                type = "string",
                format = "date-time",
                accessMode = Schema.AccessMode.READ_ONLY
        )
        Instant createdAt,

        @Schema(
                description = "Дата последнего обновления бронирования (ISO 8601 UTC)",
                example = "2026-01-01T09:00:00Z",
                type = "string",
                format = "date-time",
                accessMode = Schema.AccessMode.READ_ONLY
        )
        Instant updatedAt
) {

    public static BookingResponse of (
            Long id,
            Long roomId,
            String roomName,
            String title,
            String organizerEmail,
            Instant startTime,
            Instant endTime,
            BookingStatus status,
            Instant createdAt,
            Instant updatedAt) {

        long durationMinutes = Duration.between(startTime, endTime).toMinutes();

        return new BookingResponse(id, roomId, roomName, title, organizerEmail,
                startTime, endTime, durationMinutes,
                status, createdAt, updatedAt);
    }
}