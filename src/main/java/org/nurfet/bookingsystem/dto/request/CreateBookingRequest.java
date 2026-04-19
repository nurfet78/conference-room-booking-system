package org.nurfet.bookingsystem.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import org.jspecify.annotations.NonNull;
import org.nurfet.bookingsystem.entity.BookingStatus;
import org.nurfet.bookingsystem.validation.TimeRangeValidatable;
import org.nurfet.bookingsystem.validation.annotation.EndAfterStart;

import java.time.Instant;

@Schema(
        description = "Запрос на создание нового бронирования переговорной комнаты"
)
@EndAfterStart
public record CreateBookingRequest (

        @Schema(
                description = "ID переговорной комнаты",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotNull(message = "Необходимо указать ID комнаты")
        Long roomId,

        @Schema(
                description = "Название бронирования",
                maxLength = 200,
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotBlank(message = "Необходимо указать название бронирования")
        @Size(max = 200, message = "Название не должно превышать 200 символов")
        String title,

        @Schema(
                description = "Email организатора бронирования",
                maxLength = 254,
                format = "email",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotBlank(message = "Необходимо указать Email организатора")
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
                description = "Время начала (ISO 8601 UTC)",
                example = "2026-04-19T09:00:00Z",
                type = "string",
                format = "date-time",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotNull(message = "Необходимо указать время начала")
        @Future(message = "Начальное время должно быть в будущем")
        Instant startTime,

        @Schema(
                description = "Время окончания (ISO 8601 UTC)",
                example = "2026-04-19T09:00:00Z",
                type = "string",
                format = "date-time",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotNull(message = "Необходимо указать время окончания")
        @Future(message = "Конченое время должно быть в будущем")
        Instant endTime
) implements TimeRangeValidatable {
}