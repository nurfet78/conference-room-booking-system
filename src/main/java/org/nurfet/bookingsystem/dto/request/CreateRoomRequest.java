package org.nurfet.bookingsystem.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import org.nurfet.bookingsystem.validation.annotation.EndAfterStart;

@Schema(
        description = "Запрос на создание новой переговорной комнаты"
)
public record CreateRoomRequest(

        @Schema(
                description = "Название переговорной комнаты",
                maxLength = 100,
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotBlank(message = "Необходимо указать название переговорной комнаты")
        @Size(max = 100, message = "Название не должно превышать 100 символов")
        String name,

        @Schema(
                description = "Общая вместимость переговорной комнаты",
                minimum = "1",
                maximum = "1000",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotNull(message = "Необходимо указать общую вместимость переговорной")
        @Min(value = 1, message = "Минимальное число мест 1")
        @Max(value = 1000, message = "Максимально число мест 1000")
        Integer capacity,

        @Schema(
                description = "Описание комнаты (расположение, оборудование и т.д.)",
                maximum = "1000",
                requiredMode = Schema.RequiredMode.NOT_REQUIRED
        )
        @Size(max = 500, message = "Описание не должно превышать 1000 символов")
        String description
) {
}