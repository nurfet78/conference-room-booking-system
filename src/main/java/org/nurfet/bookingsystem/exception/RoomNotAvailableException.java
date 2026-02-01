package org.nurfet.bookingsystem.exception;

public class RoomNotAvailableException extends BusinessException {

    public RoomNotAvailableException(Long id, String reason) {
        super("КОМНАТА НЕ ДОСТУПНА",
                "Комната %d не доступна %s".formatted(id, reason));
    }
}
