package org.nurfet.bookingsystem.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.time.Instant;

@Schema(
        description = "Запрос на создание нового бронирования переговорной комнаты"
)
public record CreateBookingRequest(

        @Schema(
                description = "ID переговорной комнаты для бронирования",
                example = "1",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotNull(message = "Room ID is required")
        Long roomId,

        @Schema(
                description = "Название встречи / мероприятия",
                example = "Еженедельный стендап команды Backend",
                maxLength = 200,
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotBlank(message = "Title is required")
        @Size(max = 200, message = "Title must not exceed 200 characters")
        String title,

        @Schema(
                description = "Email организатора бронирования",
                example = "ivan.petrov@example.com",
                format = "email",       // Подсказка для генераторов кода (формат email)
                maxLength = 255,
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotBlank(message = "Organized email is required")
        @Email(message = "Invalid email format")
        @Size(max = 255, message = "Email must not exceed 255 characters")
        String organizerEmail,

        @Schema(
                description = "Время начала бронирования (ISO 8601, UTC). Должно быть в будущем",
                example = "2025-07-01T09:00:00Z",
                type = "string",
                format = "date-time",   // OpenAPI стандартный формат
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotNull(message = "Start time is required")
        @Future(message = "Start time must be in the future")
        Instant startTime,

        @Schema(
                description = "Время окончания бронирования (ISO 8601, UTC). Должно быть после startTime",
                example = "2025-07-01T10:00:00Z",
                type = "string",
                format = "date-time",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
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
