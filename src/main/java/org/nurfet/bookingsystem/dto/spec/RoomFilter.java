package org.nurfet.bookingsystem.dto.spec;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import org.springframework.data.jpa.domain.Specification;

public record RoomFilter(

        @Size(max = 100)
        String name,

        @Min(1)
        @Schema(description = "Минимальная вместимость")
        Integer capacity,

        @Size(max = 500)
        String description,
        Boolean active
) {

    public static RoomFilter empty() {
        return new RoomFilter(null, null, null, null);
    }
}