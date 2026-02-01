package org.nurfet.bookingsystem.entity;

public enum BookingStatus {
    PENDING,
    CONFIRMED,
    CANCELLED,
    EXPIRED;

    public boolean isActive() {
        return this == PENDING || this == CONFIRMED;
    }

    public boolean isCancellable() {
        return this == PENDING || this == CONFIRMED;
    }

    public boolean isConfirmable() {
        return this == PENDING;
    }
}
