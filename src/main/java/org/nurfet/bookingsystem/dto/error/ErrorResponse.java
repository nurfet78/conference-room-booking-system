package org.nurfet.bookingsystem.dto.error;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.validation.FieldError;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorResponse(
        Instant timeStamp,
        int status,
        String error,
        String errorCode,
        String message,
        String path,
        List<FieldError> fieldErrors,
        Map<String, Object> details
) {

    public record FieldError (
            String field,
            String message,
            Object rejectedValue
    ) {}

    public static ErrorResponse of(int status, String error,
                                   String message, String path) {
        return new ErrorResponse(
                Instant.now(),
                status,
                error,
                null,
                message,
                path,
                null,
                null
        );
    }

    public static ErrorResponse of(int status, String error,
                                    String errorCode,
                                    String message, String path) {
        return new ErrorResponse(
                Instant.now(),
                status,
                error,
                errorCode,
                message,
                path,
                null,
                null
        );
    }

    public static ErrorResponse ofValidation(int status, String message,
                                             String path, List<FieldError> fieldErrors) {
        return new ErrorResponse(
                Instant.now(),
                status,
                "Validation Failed",
                "VALIDATION_ERROR",
                message,
                path,
                fieldErrors,
                null
        );
    }

    public static ErrorResponse withDetails(int status, String error,
                                            String errorCode,
                                            String message, String path,
                                            Map<String, Object> details) {
        return new ErrorResponse(
                Instant.now(),
                status,
                error,
                errorCode,
                message,
                path,
                null,
                details
        );
    }
}
