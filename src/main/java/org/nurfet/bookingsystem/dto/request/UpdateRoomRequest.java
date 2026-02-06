package org.nurfet.bookingsystem.dto.request;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

@Schema(description = "Запрос на частичное обновление комнаты (PATCH). Передайте только изменяемые поля")
public record UpdateRoomRequest(

        @Schema(
                description = "Новое название комнаты",
                example = "Переговорная «Байкал»",
                maxLength = 100,
                requiredMode = Schema.RequiredMode.NOT_REQUIRED
        )
        @Size(max = 100, message = "Room name must not exceed 100 characters")
        String name,

        @Schema(
                description = "Новая вместимость",
                example = "20",
                minimum = "1",
                maximum = "1000",
                requiredMode = Schema.RequiredMode.NOT_REQUIRED
        )
        @Min(value = 1, message = "Capacity must be at least 1")
        @Max(value = 1000, message = "Capacity must not exceed 1000")
        Integer capacity,

        @Schema(
                description = "Новое описание комнаты",
                example = "Обновлён проектор, добавлена видеоконференцсвязь",
                maxLength = 1000,
                requiredMode = Schema.RequiredMode.NOT_REQUIRED
        )
        @Size(max = 1000, message = "Description must not exceed 1000 characters")
        String description,

        @Schema(
                description = "Активна ли комната (false = недоступна для бронирования)",
                example = "true",
                requiredMode = Schema.RequiredMode.NOT_REQUIRED
        )
        Boolean active
) {}
