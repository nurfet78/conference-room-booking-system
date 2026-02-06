package org.nurfet.bookingsystem.entity;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
        description = "Статус бронирования переговорной комнаты",
        enumAsRef = true  // Генерирует $ref вместо inline-определения — чище схема
)
public enum BookingStatus {

    @Schema(description = "Ожидает подтверждения")
    PENDING,

    @Schema(description = "Подтверждено организатором")
    CONFIRMED,

    @Schema(description = "Отменено")
    CANCELLED,

    @Schema(description = "Время бронирования истекло")
    EXPIRED;

    public boolean isActive() {
        return this == PENDING || this == CONFIRMED;
    }

    public boolean isCancellable() {
        return this == PENDING || this == CONFIRMED;
    }

    public boolean isConfirmable() {
        return this == PENDING;
    }
}
