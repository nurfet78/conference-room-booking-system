package org.nurfet.bookingsystem.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Size;
import org.nurfet.bookingsystem.validation.TimeRangeValidatable;
import org.nurfet.bookingsystem.validation.annotation.EndAfterStart;

import java.time.Instant;

@Schema(
        description = "Запрос на частичное обновление (PATCH) бронирования"
)
@EndAfterStart
public record UpdateBookingRequest(

        @Schema(
                description = "Новый ID переговорной комнаты (для смены комнаты)",
                requiredMode = Schema.RequiredMode.NOT_REQUIRED
        )
        Long roomId,

        @Schema(
                description = "Новое название встречи",
                maxLength = 200,
                requiredMode = Schema.RequiredMode.NOT_REQUIRED
        )
        @Size(max = 200, message = "Название не должно превышать 200 символов")
        String title,

        @Schema(
                description = "Время начала (ISO 8601 UTC)",
                example = "2026-01-01T09:00:00Z",
                type = "string",
                format = "date-time",
                requiredMode = Schema.RequiredMode.NOT_REQUIRED
        )
        @Future(message = "Начальное время должно быть в будущем")
        Instant startTime,

        @Schema(
                description = "Время окончания (ISO 8601 UTC)",
                example = "2026-01-01T09:00:00Z",
                type = "string",
                format = "date-time",
                requiredMode = Schema.RequiredMode.NOT_REQUIRED
        )
        @Future(message = "Время окончания должно быть в будущем")
        Instant endTime
) implements TimeRangeValidatable {
}