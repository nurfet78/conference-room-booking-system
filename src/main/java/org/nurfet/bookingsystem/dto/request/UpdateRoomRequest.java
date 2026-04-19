package org.nurfet.bookingsystem.dto.request;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

@Schema(
        description = "Запрос на частичное обновление переговорной комнаты (PATCH) (Заполните только необходимые поля)"
)
public record UpdateRoomRequest(

        @Schema(
                description = "Новое название переговорной",
                maxLength = 100,
                requiredMode = Schema.RequiredMode.NOT_REQUIRED
        )
        @Size(max = 100, message = "Название не должно превышать 100 символов")
        String name,

        @Schema(
                description = "Обновление общей вместимости помещения",
                minimum = "1",
                maximum = "1000",
                requiredMode = Schema.RequiredMode.NOT_REQUIRED
        )
        @Min(value = 1, message = "Минимальная вместимость 1 место")
        @Max(value = 1000, message = "Максимальная вместимость 1000 мест")
        Integer capacity,

        @Schema(
                description = "Новое описание переговорной комнаты",
                maxLength = 1000,
                requiredMode = Schema.RequiredMode.NOT_REQUIRED
        )
        @Size(max = 1000, message = "Описание не должно превышать 1000 символов")
        String description,

        @Schema(
                description = "Активна ли комната (false - недоступна для бронирования)",
                example = "true",
                requiredMode = Schema.RequiredMode.NOT_REQUIRED
        )
        Boolean active
) {
}