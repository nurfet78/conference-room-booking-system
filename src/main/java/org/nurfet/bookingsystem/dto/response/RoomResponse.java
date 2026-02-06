package org.nurfet.bookingsystem.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;

@Schema(description = "Информация о переговорной комнате")
public record RoomResponse(

        @Schema(description = "Уникальный ID комнаты", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
        Long id,

        @Schema(description = "Название комнаты", example = "Конференц-зал «Эверест»")
        String name,

        @Schema(description = "Вместимость (количество человек)", example = "12")
        Integer capacity,

        @Schema(description = "Описание комнаты", example = "3 этаж, проектор, маркерная доска")
        String description,

        @Schema(description = "Активна ли комната для бронирования", example = "true")
        boolean active,

        @Schema(description = "Дата создания", example = "2025-01-15T08:00:00Z", accessMode = Schema.AccessMode.READ_ONLY)
        Instant createdAt,

        @Schema(description = "Дата последнего обновления", example = "2025-06-01T12:30:00Z", accessMode = Schema.AccessMode.READ_ONLY)
        Instant updatedAt
) {}
