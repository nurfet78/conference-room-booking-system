package org.nurfet.bookingsystem.dto.error;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(
        description = "Стандартный формат ответа об ошибке API",
        name = "ErrorResponse"
)
public record ErrorResponse(

        @Schema(
                description = "Временная метка возникновении ошибки (ISO 8601 UTC)",
                example = "2026-02-06T10:30:00Z",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        Instant timeStamp,

        @Schema(
                description = "HTTP-код статуса",
                example = "400",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        int status,

        @Schema(
                description = "Краткое описание типа ошибки",
                example = "Bad Request"
        )
        String error,

        @Schema(
                description = "Машиночитаемый код ошибки для обработке на клиенте",
                example = "VALIDATION_ERROR",
                nullable = true
        )
        String errorCode,

        @Schema(
                description = "Человекочитаемое сообщение об ошибке",
                example = "Validation failed for one or more fields",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        String message,

        @Schema(
                description = "URI запроса, вызывающего ошибку",
                example = "api/v1/bookings"
        )
        String path,

        @Schema(
                description = "Список ошибок валидации полей (только для 400 Validation failed)",
                nullable = true
        )
        List<FieldError> fieldErrors,

        @Schema(
                description = "Дополнительные детали ошибки (зависят от контекста)",
                nullable = true
        )
        Map<String, Object> details
) {

    @Schema(description = "Детали ошибки валидации конкретного поля")
    public record FieldError (

            @Schema(description = "Имя поля с ошибкой", example = "organizerEmail")
            String field,

            @Schema(description = "Описание ошибки валидации", example = "Invalid email format")
            String message,

            @Schema(description = "Значение, которое не прошло валидацию", example = "not-an-email")
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
