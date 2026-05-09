package org.nurfet.bookingsystem.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import org.nurfet.bookingsystem.validation.TimeRangeValidatable;
import org.nurfet.bookingsystem.validation.annotation.EndAfterStart;

import java.time.Instant;

@Schema(
        description = "Запрос на создание бронирования"
)
@EndAfterStart
public record CreateBookingRequest(

        @Schema(
                description = "ID переговорной комнаты для бронирования",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotNull(message = "Необходимо указать ID комнаты")
        Long roomId,

        @Schema(
                description = "Название встречи",
                maxLength = 100,
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotBlank(message = "Укажите название встречи")
        @Size(max = 100, message = "Название не должно превышать 100 символов")
        String title,

        @Schema(
                description = "Email организатора встречи",
                maxLength = 254,
                format = "email",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotBlank(message = "Укажите email организатора встречи")
        @Pattern(
                regexp = "^(?=.{1,254}$)(?=.{1,64}@)"
                        + "[A-Za-z0-9!#$%&'*+/=?^_`{|}~-]+"
                        + "(\\.[A-Za-z0-9!#$%&'*+/=?^_`{|}~-]+)*@"
                        + "[A-Za-z0-9]([A-Za-z0-9-]{0,61}[A-Za-z0-9])?"
                        + "(\\.[A-Za-z0-9]([A-Za-z0-9-]{0,61}[A-Za-z0-9])?)+$",
                message = "Некорректный адрес электронной почты"
        )
        String organizerEmail,

        @Schema(
                description = "Начало встречи (ISO 8601 UTC)",
                example = "2026-01-01T09:00:00Z",
                type = "string",
                format = "date-time",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotNull(message = "Укажите дату и время начала встречи")
        @Future(message = "Время должно быть в будущем")
        Instant startTime,

        @Schema(
                description = "Окончание встречи (ISO 8601 UTC)",
                example = "2026-01-01T09:00:00Z",
                type = "string",
                format = "date-time",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotNull(message = "Укажите дату и время окончания встречи")
        @Future(message = "Время должно быть в будущем")
        Instant endTime
) implements TimeRangeValidatable{
}