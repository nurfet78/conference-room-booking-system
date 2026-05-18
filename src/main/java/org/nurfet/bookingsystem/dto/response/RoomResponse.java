package org.nurfet.bookingsystem.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.hateoas.server.core.Relation;

import java.time.Instant;

@Schema(
        description = "Информация о переговорной комнате"
)

/*
По умолчанию Spring HATEOAS назовёт массив roomResponseList — некрасиво. Исправляется аннотацией на RoomResponse

Теперь в JSON будет _embedded.rooms вместо _embedded.roomResponseList


collectionRelation = "rooms" — имя массива в _embedded при пагинации ($._embedded.rooms)
itemRelation = "room" — имя при возврате одного объекта (используется реже)
*/

@Relation(collectionRelation = "rooms", itemRelation = "room")
public record RoomResponse(

        @Schema(description = "Уникальный ID комнаты")
        Long id,

        @Schema(description = "Название комнаты")
        String name,

        @Schema(description = "Вместимость комнаты (число мест)")
        Integer capacity,

        @Schema(description = "Описание комнаты")
        String description,

        @Schema(
                description = "Активна ли комната (false - комната не доступна для бронирования)",
                example = "true"
        )
        Boolean active,

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