package org.nurfet.bookingsystem.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Size;

import java.time.Instant;

@Schema(description = "Запрос на частичное обновление бронирования (PATCH). Передайте только изменяемые поля")
public record UpdateBookingRequest(

        @Schema(
                description = "Новый ID комнаты (для переноса в другую комнату)",
                example = "2",
                requiredMode = Schema.RequiredMode.NOT_REQUIRED
        )
        Long roomId,

        @Schema(
                description = "Новое название встречи",
                example = "Ретроспектива спринта #42",
                maxLength = 200,
                requiredMode = Schema.RequiredMode.NOT_REQUIRED
        )
        @Size(max = 200, message = "Title must not exceed 200 characters")
        String title,

        @Schema(
                description = "Новое время начала (ISO 8601, UTC)",
                example = "2025-07-01T14:00:00Z",
                type = "string",
                format = "date-time",
                requiredMode = Schema.RequiredMode.NOT_REQUIRED
        )
        @Future(message = "Start time must be in the future")
        Instant startTime,

        @Schema(
                description = "Новое время окончания (ISO 8601, UTC)",
                example = "2025-07-01T15:00:00Z",
                type = "string",
                format = "date-time",
                requiredMode = Schema.RequiredMode.NOT_REQUIRED
        )
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
