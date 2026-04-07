package org.nurfet.bookingsystem.exception;

public record FieldValidationError(String field,
                                   String message,
                                   String rejectedValue) {
}
