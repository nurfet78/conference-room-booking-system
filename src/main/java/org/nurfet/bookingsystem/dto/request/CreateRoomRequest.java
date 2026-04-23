package org.nurfet.bookingsystem.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

@Schema(
        description = "Запрос на создание переговорной комнаты"
)
public record CreateRoomRequest(

        @Schema(
                description = "Название переговорной комнаты",
                maxLength = 100,
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotBlank(message = "Необходимо указать название")
        String name,

        @Schema(
                description = "Общая вместимость переговорной комнаты",
                minimum = "1",
                maximum = "1000",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotNull(message = "Необходимо указать вместимость")
        @Min(value = 1, message = "Минимальная вместимость 1")
        @Max(value = 1000, message = "Максимальная вместимость 1000")
        Integer capacity,

        @Schema(
                description = "Описание переговорной комнаты (расположение, оборудование и т.д.)",
                maxLength = 1000,
                requiredMode = Schema.RequiredMode.NOT_REQUIRED
        )
        @Size(max = 1000, message = "Описание не должно превышать 1000 символов")
        String description
) {
}