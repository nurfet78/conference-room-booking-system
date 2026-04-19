package org.nurfet.bookingsystem.entity;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
        description = "Статус бронирования переговорной",
        enumAsRef = true
)
public enum BookingStatus {

    @Schema(description = "Ждет подтверждения")
    PENDING,

    @Schema(description = "Подтвержден организатором")
    CONFIRMED,

    @Schema(description = "Отменен")
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
        return
                this == PENDING;
    }
}