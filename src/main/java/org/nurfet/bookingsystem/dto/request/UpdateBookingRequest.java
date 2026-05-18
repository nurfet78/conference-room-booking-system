package org.nurfet.bookingsystem.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Size;
import org.nurfet.bookingsystem.validation.TimeRangeValidatable;
import org.nurfet.bookingsystem.validation.annotation.EndAfterStart;

import java.time.Instant;

@Schema(
        description = "Запрос на частичное обновление бронирования"
)
@EndAfterStart
public record UpdateBookingRequest(

        @Schema(
                description = "Новый ID комнаты (для смены переговорной)"
        )
        Long roomId,

        @Schema(
                description = "Новое название встречи",
                maxLength = 200
        )
        @Size(max = 200, message = "Название не должно превышать 200 символов")
        String title,

        @Schema(
                description = "Время начала встречи (ISO 8601 UTC)",
                example = "2026-01-01T09:00:00Z",
                type = "string",
                format = "date-time"
        )
        @Future(message = "Время должно быть в будущем")
        Instant startTime,

        @Schema(
                description = "Время окончания встречи (ISO 8601 UTC)",
                example = "2026-01-01T09:00:00Z",
                type = "string",
                format = "date-time"
        )
        @Future(message = "Время должно быть в будущем")
        Instant endTime
) implements TimeRangeValidatable {
}