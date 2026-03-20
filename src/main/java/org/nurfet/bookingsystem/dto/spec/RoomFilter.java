package org.nurfet.bookingsystem.dto.spec;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

public record RoomFilter(

        @Size(max = 100)
        String name,

        @Min(1)
        Integer capacity,

        @Size(max = 500)
        String description,

        Boolean active)
{
        public static RoomFilter empty() {
                return new RoomFilter(null, null, null, null);
        }
}