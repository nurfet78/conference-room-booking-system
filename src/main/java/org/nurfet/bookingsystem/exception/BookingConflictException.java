package org.nurfet.bookingsystem.exception;

import lombok.Getter;

import java.time.Instant;

@Getter
public class BookingConflictException extends BusinessException {

    private final Long roomId;
    private final Instant requestedStart;
    private final Instant requestedEnd;

    public BookingConflictException(Long roomId, Instant requestedStart, Instant requestedEnd) {
        super("КОНФЛИКТ БРОНИРОВАНИЯ",
                "Комната %d уже забронирована за запрошенный период времени (%s - %s)"
                        .formatted(roomId, requestedStart, requestedEnd));

        this.roomId = roomId;
        this.requestedStart = requestedStart;
        this.requestedEnd = requestedEnd;
    }
}
