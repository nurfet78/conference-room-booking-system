package org.nurfet.bookingsystem.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;

@Schema(
        description = "Информация о переговорной комнате"
)
public record RoomResponse(

        @Schema(
                description = "Уникальный ID комнаты",
                accessMode = Schema.AccessMode.READ_ONLY
        )
        Long id,

        @Schema(
                description = "Название комнаты",
                accessMode = Schema.AccessMode.READ_ONLY
        )
        String name,

        @Schema(
                description = "Общая вместимость комнаты",
                accessMode = Schema.AccessMode.READ_ONLY
        )
        Integer capacity,

        @Schema(
                description = "Описание комнаты",
                accessMode = Schema.AccessMode.READ_ONLY
        )
        String description,

        @Schema(
                description = "Активна ли комната для бронирования",
                example = "true",
                accessMode = Schema.AccessMode.READ_ONLY
        )
        Boolean active,

        @Schema(
                description = "Дата создание комнаты (ISO 8601 UTC)",
                example = "2026-04-19T09:00:00Z",
                type = "string",
                format = "date-time",
                accessMode = Schema.AccessMode.READ_ONLY
        )
        Instant createdAt,

        @Schema(
                description = "Дата последнего обновления комнаты (ISO 8601 UTC)",
                example = "2026-04-19T09:00:00Z",
                type = "string",
                format = "date-time",
                accessMode = Schema.AccessMode.READ_ONLY
        )
        Instant updatedAt
) {
}