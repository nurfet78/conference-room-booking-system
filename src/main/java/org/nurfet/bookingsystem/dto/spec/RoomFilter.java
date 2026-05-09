package org.nurfet.bookingsystem.dto.spec;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

@Schema(description = "Фильтрация при поиске комнаты")
public record RoomFilter(

        @Schema(
                description = "Название комнаты",
                maxLength = 200
        )
        @Size(max = 200, message = "Название не должно превышать 200 символов")
        String name,

        @Schema(
                description = "Вместимость комнаты (число мест)",
                minimum = "1",
                maximum = "1000"
        )
        @Min(value = 1, message = "Минимальная вместимость 1")
        @Max(value = 1000, message = "Максимальная вместимость 1000")
        Integer capacity,

        @Schema(
                description = "Описание комнаты",
                maxLength = 2000
        )
        @Size(max = 2000, message = "Описание не должно превышать 2000 символов")
        String description,

        @Schema(
                description = "Активна ли комната",
                example = "true"
        )
        Boolean active
) {

    public static RoomFilter empty() {
        return new RoomFilter(null, null, null, null);
    }
}