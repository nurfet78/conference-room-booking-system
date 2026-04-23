package org.nurfet.bookingsystem.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(
        description = "Информация о доступности временного слота"
)
public record AvailabilityResponse(

        @Schema(
                description = "Доступен ли запрошенный слот",
                accessMode = Schema.AccessMode.READ_ONLY
        )
        boolean available,

        @Schema(
                description = "Список конфликтующих бронирования (только если available = false)",
                nullable = true,
                accessMode = Schema.AccessMode.READ_ONLY
        )
        List<BookingResponse> conflicts
) {

    public static AvailabilityResponse free() {
        return new AvailabilityResponse(true, List.of());
    }

    public static AvailabilityResponse unavailable(List<BookingResponse> conflicts) {
        return new AvailabilityResponse(false, conflicts);
    }
}