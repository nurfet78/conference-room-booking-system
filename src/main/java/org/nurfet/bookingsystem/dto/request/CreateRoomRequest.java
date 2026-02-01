package org.nurfet.bookingsystem.dto.request;

import jakarta.validation.constraints.*;

public record CreateRoomRequest(
        @NotBlank(message = "Room name is required")
        @Size(max = 100, message = "Room name must not exceed 100")
        String name,

        @NotNull(message = "Capacity is required")
        @Min(value = 1, message = "Capacity must be at least 1")
        @Max(value = 1000, message = "Capacity must be exceed 1000")
        Integer capacity,

        @Size(max = 1000, message = "Description must be exceed 1000")
        String description
) {}
