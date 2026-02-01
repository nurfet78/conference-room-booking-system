package org.nurfet.bookingsystem.exception;

import org.nurfet.bookingsystem.entity.BookingStatus;

public class InvalidBookingStateException extends BusinessException {

    public InvalidBookingStateException(String operation, BookingStatus status) {
        super("НЕКОРРЕКТНОЕ СОСТОЯНИЕ БРОНИРОВАНИЯ",
                "Невозможно забронировать %s со статусом %s"
                        .formatted(operation, status));
    }

    public InvalidBookingStateException(String message) {
        super("НЕКОРРЕКТНОЕ СОСТОЯНИЕ БРОНИРОВАНИЯ", message);
    }
}
