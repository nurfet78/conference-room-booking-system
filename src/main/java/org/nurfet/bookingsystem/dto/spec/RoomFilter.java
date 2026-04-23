package org.nurfet.bookingsystem.dto.spec;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import org.springframework.data.jpa.domain.Specification;

public record RoomFilter(

        @Size(max = 100, message = "Название не должно превышать 100 символов")
        String name,

        @Min(1)
        @Schema(description = "Минимальная вместимость 1")
        Integer capacity,

        @Size(max = 1000, message = "Описание не должно превышать 1000 символов")
        String description,
        Boolean active
) {

    public static RoomFilter empty() {
        return new RoomFilter(null, null, null, null);
    }
}