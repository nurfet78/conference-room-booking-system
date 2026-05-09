package org.nurfet.bookingsystem.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;

import java.time.Instant;

@Schema(
        description = "Информация о переговорной комнате"
)
public record RoomResponse(

        @Schema(
                description = "Уникальный ID комнаты"
        )
        Long id,

        @Schema(
                description = "Название комнаты"
        )
        String name,

        @Schema(
                description = "Вместимость комнаты (число мест)"
        )
        Integer capacity,

        @Schema(
                description = "Описание комнаты"
        )
        String description,

        @Schema(
                description = "Активна ли комната (false - комната не доступна)",
                example = "true"
        )
        boolean active,

        @Schema(
                description = "Дата создания комнаты (ISO 8601 UTC)",
                example = "2026-01-01T09:00:00Z",
                type = "string",
                format = "date-time"
        )
        Instant createdAt,

        @Schema(
                description = "Дата последнего обновления комнаты (ISO 8601 UTC)",
                example = "2026-01-01T09:00:00Z",
                type = "string",
                format = "date-time"
        )
        Instant updatedAt
) {
}