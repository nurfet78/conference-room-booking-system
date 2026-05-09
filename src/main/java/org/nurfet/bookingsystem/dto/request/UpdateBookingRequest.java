package org.nurfet.bookingsystem.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Size;
import org.nurfet.bookingsystem.validation.TimeRangeValidatable;
import org.nurfet.bookingsystem.validation.annotation.EndAfterStart;

import java.time.Instant;

@Schema(
        description = "Частичное обновление бронирования"
)
@EndAfterStart
public record UpdateBookingRequest(

        @Schema(
                description = "Новый ID комнаты (для смены комнаты)"
        )
        Long roomId,

        @Schema(
                description = "Новое название встречи",
                maxLength = 100
        )
        @Size(max = 100, message = "Название не должно превышать 100 символов")
        String title,

        @Schema(
                description = "Начало встречи (ISO 8601 UTC)",
                example = "2026-01-01T09:00:00Z",
                type = "string",
                format = "date-time",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @Future(message = "Время должно быть в будущем")
        Instant startTime,

        @Schema(
                description = "Окончание встречи (ISO 8601 UTC)",
                example = "2026-01-01T09:00:00Z",
                type = "string",
                format = "date-time",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @Future(message = "Время должно быть в будущем")
        Instant endTime
) implements TimeRangeValidatable{
}