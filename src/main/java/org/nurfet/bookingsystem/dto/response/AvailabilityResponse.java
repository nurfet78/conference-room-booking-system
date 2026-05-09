package org.nurfet.bookingsystem.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(
        description = "Проверка доступности временного слота"
)
public record AvailabilityResponse(

        @Schema(description = "Доступен ли временной слот")
        boolean available,

        @Schema(description = "Список конфликтующих бронирований (если available = false")
        List<BookingResponse> conflicts
) {

    public static AvailabilityResponse free() {
        return new AvailabilityResponse(true, List.of());
    }

    public static AvailabilityResponse unavailable(List<BookingResponse> conflicts) {
        return new AvailabilityResponse(false, conflicts);
    }
}