package org.nurfet.bookingsystem.dto.response;

import java.time.Instant;

public record RoomResponse(
        Long id,
        String name,
        Integer capacity,
        String description,
        boolean active,
        Instant createdAt,
        Instant updatedAt
) {}
