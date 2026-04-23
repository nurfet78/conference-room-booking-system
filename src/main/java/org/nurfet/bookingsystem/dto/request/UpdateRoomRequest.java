package org.nurfet.bookingsystem.dto.request;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

@Schema(
        description = "Запрос на частичное обновление (PATCH) переговорной комнаты"
)
public record UpdateRoomRequest(

        @Schema(
                description = "Новое название переговорной комнаты",
                maxLength = 100,
                requiredMode = Schema.RequiredMode.NOT_REQUIRED
        )
        @Size(max = 100, message = "Название не должно превышать 100 символов")
        String name,

        @Schema(
                description = "Новая общая вместимость",
                minimum = "1",
                maximum = "1000",
                requiredMode = Schema.RequiredMode.NOT_REQUIRED
        )
        @Min(value = 1, message = "Минимальная вместимость 1")
        @Max(value = 1000, message = "Максимальная вместимость 1000")
        Integer capacity,

        @Schema(
                description = "Новое описание комнаты",
                maxLength = 1000,
                requiredMode = Schema.RequiredMode.NOT_REQUIRED
        )
        @Size(max = 1000, message = "Описание не должно превышать 1000 символов")
        String description,

        @Schema(
                description = "Активна ли комната (false - не доступна для бронирования)",
                example = "true",
                requiredMode = Schema.RequiredMode.NOT_REQUIRED
        )
        Boolean active
) {
}