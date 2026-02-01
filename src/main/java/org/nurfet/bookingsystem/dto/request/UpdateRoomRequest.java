package org.nurfet.bookingsystem.dto.request;


import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

public record UpdateRoomRequest(
        @Size(max = 100, message = "Room name must not exceed 100 characters")
        String name,

        @Min(value = 1, message = "Capacity must be at least 1")
        @Max(value = 1000, message = "Capacity must not exceed 1000")
        Integer capacity,

        @Size(max = 1000, message = "Description must not exceed 1000 characters")
        String description,
        Boolean active
) {}
