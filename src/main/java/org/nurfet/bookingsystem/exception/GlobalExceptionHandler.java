package org.nurfet.bookingsystem.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.net.URI;
import java.util.Map;
import java.util.Objects;


@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    // ========================
    // Business exceptions
    // ========================

    @ExceptionHandler(EntityNotFoundException.class)
    public ProblemDetail handleEntityNotFound(
            EntityNotFoundException ex, HttpServletRequest request) {

        log.debug("Entity not found: {}", ex.getMessage());

        return createProblemDetail(
                HttpStatus.NOT_FOUND, ex.getMessage(), ex.getErrorCode(), request);
    }

    @ExceptionHandler(BookingConflictException.class)
    public ProblemDetail handleBookingConflict(
            BookingConflictException ex, HttpServletRequest request) {

        log.debug("Booking conflict: {}", ex.getMessage());

        ProblemDetail problem = createProblemDetail(
                HttpStatus.CONFLICT, ex.getMessage(), ex.getErrorCode(), request);
        problem.setProperty("roomId", ex.getRoomId());
        problem.setProperty("requestedStart", ex.getRequestedStart().toString());
        problem.setProperty("requestedEnd", ex.getRequestedEnd().toString());

        return problem;
    }

    @ExceptionHandler(InvalidBookingStateException.class)
    public ProblemDetail handleInvalidBookingState(
            InvalidBookingStateException ex, HttpServletRequest request) {

        log.debug("Invalid booking state: {}", ex.getMessage());

        return createProblemDetail(
                HttpStatus.CONFLICT, ex.getMessage(), ex.getErrorCode(), request);
    }

    @ExceptionHandler(RoomNotAvailableException.class)
    public ProblemDetail handleRoomNotAvailable(
            RoomNotAvailableException ex, HttpServletRequest request) {

        log.debug("Room not available: {}", ex.getMessage());

        return createProblemDetail(
                HttpStatus.UNPROCESSABLE_CONTENT, ex.getMessage(), ex.getErrorCode(), request);
    }

    // ========================
    // Validation
    // ========================

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            @NonNull HttpHeaders headers,
            @NonNull HttpStatusCode status,
            @NonNull WebRequest request) {

        var fieldErrors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fe -> new FieldValidationError(
                        fe.getField(),
                        Objects.requireNonNullElse(fe.getDefaultMessage(), "Invalid value"),
                        Objects.toString(fe.getRejectedValue(), "null")
                )).toList();

        log.debug("Validation failed: {} errors", fieldErrors.size());

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST, "Validation failed");
        problem.setTitle("Validation Failed");
        problem.setProperty("errorCode", "VALIDATION_ERROR");
        problem.setProperty("fieldErrors", fieldErrors);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problem);
    }

    // ========================
    // Data integrity
    // ========================

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ProblemDetail handleDataIntegrity(
            DataIntegrityViolationException ex, HttpServletRequest request) {

        String cause = ex.getMostSpecificCause().getMessage();
        URI instance = URI.create(request.getRequestURI());

        if (cause != null && cause.contains("excl_booking_overlap")) {
            log.info("Booking overlap detected by database constraint");
            return createProblemDetail(
                    HttpStatus.CONFLICT, "The requested time slot is not available",
                    "BOOKING_CONFLICT", instance);
        }

        if (cause != null && cause.contains("unique")) {
            log.debug("Unique constraint violation: {}", cause);
            return createProblemDetail(
                    HttpStatus.CONFLICT, "Resource already exists",
                    "DUPLICATE_ENTRY", instance);
        }

        log.error("Data integrity violation", ex);
        return createProblemDetail(
                HttpStatus.CONFLICT, "Data integrity violation",
                "DATA_INTEGRITY_ERROR", instance);
    }

    // ========================
// Validation
// ========================

    @ExceptionHandler(ConstraintViolationException.class)
    public ProblemDetail handleConstraintViolation(
            ConstraintViolationException ex, HttpServletRequest request) {

        var violations = ex.getConstraintViolations()
                .stream()
                .map(v -> new FieldValidationError(
                        extractFieldName(v.getPropertyPath()),
                        v.getMessage(),
                        Objects.toString(v.getInvalidValue(), "null")
                )).toList();

        log.debug("Constraint violation: {} errors", violations.size());

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST, "Validation failed");
        problem.setTitle("Validation Failed");
        problem.setProperty("errorCode", "VALIDATION_ERROR");
        problem.setProperty("fieldErrors", violations);
        problem.setInstance(URI.create(request.getRequestURI()));

        return problem;
    }

    private String extractFieldName(jakarta.validation.Path propertyPath) {
        String fullPath = propertyPath.toString();
        // "getBookingByOrganizer.email" → "email"
        int dot = fullPath.lastIndexOf('.');
        return dot >= 0 ? fullPath.substring(dot + 1) : fullPath;
    }

    // ========================
    // Catch-all
    // ========================

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleGeneral(Exception ex, HttpServletRequest request) {

        log.error("Unexpected error", ex);

        return createProblemDetail(
                HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred",
                "INTERNAL_ERROR", request);
    }

    // ========================
    // Helper
    // ========================

    private ProblemDetail createProblemDetail(HttpStatus status, String detail,
                                              String errorCode, HttpServletRequest request) {
        return createProblemDetail(status, detail, errorCode,
                URI.create(request.getRequestURI()));
    }

    private ProblemDetail createProblemDetail(HttpStatus status, String detail,
                                              String errorCode, URI instance) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(status, detail);
        problem.setTitle(status.getReasonPhrase());
        problem.setProperty("errorCode", errorCode);
        problem.setInstance(instance);
        return problem;
    }
}
