package org.nurfet.bookingsystem.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.nurfet.bookingsystem.exception.*;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
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

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.NOT_FOUND, ex.getMessage());
        problem.setTitle("Not Found");
        problem.setProperty("errorCode", ex.getErrorCode());
        problem.setInstance(URI.create(request.getRequestURI()));

        return problem;
    }

    @ExceptionHandler(BookingConflictException.class)
    public ProblemDetail handleBookingConflict(
            BookingConflictException ex, HttpServletRequest request) {

        log.debug("Booking conflict: {}", ex.getMessage());

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.CONFLICT, ex.getMessage());
        problem.setTitle("Conflict");
        problem.setProperty("errorCode", ex.getErrorCode());
        problem.setInstance(URI.create(request.getRequestURI()));
        problem.setProperty("roomId", ex.getRoomId());
        problem.setProperty("requestedStart", ex.getRequestedStart().toString());
        problem.setProperty("requestedEnd", ex.getRequestedEnd().toString());

        return problem;
    }

    @ExceptionHandler(InvalidBookingStateException.class)
    public ProblemDetail handleInvalidBookingState(
            InvalidBookingStateException ex, HttpServletRequest request) {

        log.debug("Invalid booking state: {}", ex.getMessage());

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.CONFLICT, ex.getMessage());
        problem.setTitle("Conflict");
        problem.setProperty("errorCode", ex.getErrorCode());
        problem.setInstance(URI.create(request.getRequestURI()));

        return problem;
    }

    @ExceptionHandler(RoomNotAvailableException.class)
    public ProblemDetail handleRoomNotAvailable(
            RoomNotAvailableException ex, HttpServletRequest request) {

        log.debug("Room not available: {}", ex.getMessage());

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.UNPROCESSABLE_CONTENT, ex.getMessage());
        problem.setTitle("Unprocessable Entity");
        problem.setProperty("errorCode", ex.getErrorCode());
        problem.setInstance(URI.create(request.getRequestURI()));

        return problem;
    }

    // ========================
    // Validation exceptions
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
                .map(fe -> Map.<String, Object>of(
                        "field", fe.getField(),
                        "message", Objects.requireNonNullElse(
                                fe.getDefaultMessage(), "Invalid value"),
                        "rejectedValue", Objects.requireNonNullElse(
                                fe.getRejectedValue(), "null")
                )).toList();

        log.debug("Validation failed: {} errors", fieldErrors.size());

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST, "Validation failed");
        problem.setTitle("Validation Failed");
        problem.setProperty("errorCode", "VALIDATION_ERROR");
        problem.setProperty("fieldErrors", fieldErrors);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problem);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ProblemDetail handleConstraintViolation(
            ConstraintViolationException ex, HttpServletRequest request) {

        log.debug("Constraint violation: {}", ex.getMessage());

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST, "Invalid request parameters");
        problem.setTitle("Bad Request");
        problem.setProperty("errorCode", "CONSTRAINT_VIOLATION");
        problem.setInstance(URI.create(request.getRequestURI()));

        return problem;
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ProblemDetail handleIllegalArgument(
            IllegalArgumentException ex, HttpServletRequest request) {

        log.debug("Illegal argument: {}", ex.getMessage());

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST, ex.getMessage());
        problem.setTitle("Bad Request");
        problem.setProperty("errorCode", "INVALID_ARGUMENT");
        problem.setInstance(URI.create(request.getRequestURI()));

        return problem;
    }

    // ========================
    // Request format exceptions
    // ========================

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(
            org.springframework.http.converter.HttpMessageNotReadableException ex,
            @NonNull HttpHeaders headers,
            @NonNull HttpStatusCode status,
            @NonNull WebRequest request) {

        log.debug("Message not readable: {}", ex.getMessage());

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST, "Invalid request body format");
        problem.setTitle("Bad Request");
        problem.setProperty("errorCode", "INVALID_REQUEST_BODY");

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problem);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ProblemDetail handleTypeMismatch(
            MethodArgumentTypeMismatchException ex, HttpServletRequest request) {

        String detail = String.format("Parameter '%s' must be of type %s",
                ex.getName(),
                ex.getRequiredType() != null
                        ? ex.getRequiredType().getSimpleName()
                        : "unknown");

        log.debug("Type mismatch: {}", detail);

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST, detail);
        problem.setTitle("Bad Request");
        problem.setProperty("errorCode", "TYPE_MISMATCH");
        problem.setInstance(URI.create(request.getRequestURI()));

        return problem;
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

            ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                    HttpStatus.CONFLICT, "The requested time slot is not available");
            problem.setTitle("Conflict");
            problem.setProperty("errorCode", "BOOKING_CONFLICT");
            problem.setInstance(instance);
            return problem;
        }

        if (cause != null && cause.contains("unique")) {
            log.debug("Unique constraint violation: {}", cause);

            ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                    HttpStatus.CONFLICT, "Resource already exists");
            problem.setTitle("Conflict");
            problem.setProperty("errorCode", "DUPLICATE_ENTRY");
            problem.setInstance(instance);
            return problem;
        }

        log.error("Data integrity violation", ex);

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.CONFLICT, "Data integrity violation");
        problem.setTitle("Conflict");
        problem.setProperty("errorCode", "DATA_INTEGRITY_ERROR");
        problem.setInstance(instance);
        return problem;
    }

    // ========================
    // Catch-all
    // ========================

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleGeneral(Exception ex, HttpServletRequest request) {

        log.error("Unexpected error", ex);

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred");
        problem.setTitle("Internal Server Error");
        problem.setProperty("errorCode", "INTERNAL_ERROR");
        problem.setInstance(URI.create(request.getRequestURI()));

        return problem;
    }
}
