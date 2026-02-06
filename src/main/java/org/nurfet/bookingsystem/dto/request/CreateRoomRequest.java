package org.nurfet.bookingsystem.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

@Schema(description = "Запрос на создание новой переговорной комнаты")
public record CreateRoomRequest(

        @Schema(
                description = "Название комнаты (уникальное)",
                example = "Конференц-зал «Эверест»",
                maxLength = 100,
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotBlank(message = "Room name is required")
        @Size(max = 100, message = "Room name must not exceed 100")
        String name,

        @Schema(
                description = "Вместимость комнаты (количество человек)",
                example = "12",
                minimum = "1",
                maximum = "1000",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotNull(message = "Capacity is required")
        @Min(value = 1, message = "Capacity must be at least 1")
        @Max(value = 1000, message = "Capacity must be exceed 1000")
        Integer capacity,

        @Schema(
                description = "Описание комнаты: оборудование, расположение и т.д.",
                example = "3 этаж, проектор, маркерная доска, видеоконференцсвязь",
                maxLength = 1000
        )
        @Size(max = 1000, message = "Description must be exceed 1000")
        String description
) {}
