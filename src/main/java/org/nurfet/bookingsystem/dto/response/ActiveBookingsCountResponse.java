package org.nurfet.bookingsystem.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Активные бронирования комнаты")
public record ActiveBookingsCountResponse(long activeBookingsCount) {
}