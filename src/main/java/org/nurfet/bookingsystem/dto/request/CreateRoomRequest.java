package org.nurfet.bookingsystem.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

@Schema(
        description = "Запрос на создание переговорной комнаты"
)
public record CreateRoomRequest(

        @Schema(
                description = "Название переговорной",
                maxLength = 200,
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotBlank(message = "Необходимо указать название комнаты")
        @Size(max = 200, message = "Название не должно превышать 200 символов")
        String name,

        @Schema(
                description = "Вместимость комнаты (число мест)",
                minimum = "1",
                maximum = "1000",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotNull(message = "Укажите вместимость комнаты")
        @Min(value = 1, message = "Минимальное число мест 1")
        @Max(value = 1000, message = "Максимальное число мест 1000")
        Integer capacity,

        @Schema(
                description = "Описание комнаты (расположение, оборудование и т.д.)",
                maxLength = 2000,
                requiredMode = Schema.RequiredMode.NOT_REQUIRED
        )
        @Size(max = 2000, message = "Описание не должно превышать 2000 символов")
        String description
) {
}