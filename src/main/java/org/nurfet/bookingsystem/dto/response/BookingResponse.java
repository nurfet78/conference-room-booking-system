package org.nurfet.bookingsystem.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import org.nurfet.bookingsystem.entity.BookingStatus;

import java.time.Duration;
import java.time.Instant;

@Schema(description = "Информация о бронировании переговорной комнаты")
public record BookingResponse(

        @Schema(description = "Уникальный ID бронирования", example = "42", accessMode = Schema.AccessMode.READ_ONLY)
        Long id,

        @Schema(description = "ID забронированной комнаты", example = "1")
        Long roomId,

        @Schema(description = "Название комнаты", example = "Конференц-зал «Эверест»")
        String roomName,

        @Schema(description = "Название встречи", example = "Еженедельный стендап команды Backend")
        String title,

        @Schema(description = "Email организатора", example = "ivan.petrov@example.com")
        String organizerEmail,

        @Schema(description = "Время начала", example = "2025-07-01T09:00:00Z")
        Instant startTime,

        @Schema(description = "Время окончания", example = "2025-07-01T10:00:00Z")
        Instant endTime,

        @Schema(description = "Продолжительность в минутах (вычисляется автоматически)", example = "60", accessMode = Schema.AccessMode.READ_ONLY)
        long durationMinutes,

        @Schema(description = "Текущий статус бронирования", example = "CONFIRMED")
        BookingStatus status,

        @Schema(description = "Дата создания", example = "2025-06-20T14:00:00Z", accessMode = Schema.AccessMode.READ_ONLY)
        Instant createdAt,

        @Schema(description = "Дата последнего обновления", example = "2025-06-20T14:05:00Z", accessMode = Schema.AccessMode.READ_ONLY)
        Instant updatedAt) {

    public static BookingResponse of(
            Long id,
            Long roomId,
            String roomName,
            String title,
            String organizerEmail,
            Instant startTime,
            Instant endTime,
            BookingStatus status,
            Instant createdAt,
            Instant updatedAt
    ) {
        long durationMinutes = Duration.between(startTime, endTime).toMinutes();
        return new BookingResponse(
                id,
                roomId,
                roomName,
                title,
                organizerEmail,
                startTime,
                endTime,
                durationMinutes,
                status,
                createdAt,
                updatedAt
        );
    }
}
