package com.tbs.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.Instant;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(SubscriberNotFoundException.class)
    public ResponseEntity<ApiError> handleSubscriberNotFound(SubscriberNotFoundException e,
                                                             HttpServletRequest request) {
        return build(HttpStatus.NOT_FOUND, e.getMessage(), request);
    }

    @ExceptionHandler({DuplicateCdrException.class, DataIntegrityViolationException.class})
    public ResponseEntity<ApiError> handleConflict(Exception e, HttpServletRequest request) {
        return build(HttpStatus.CONFLICT, e.getMessage(), request);
    }

    @ExceptionHandler({InsufficientBalanceException.class, IllegalArgumentException.class,
            HttpMessageNotReadableException.class, MethodArgumentTypeMismatchException.class,
            org.springframework.web.bind.MissingServletRequestParameterException.class})
    public ResponseEntity<ApiError> handleBadRequest(Exception e, HttpServletRequest request) {
        return build(HttpStatus.BAD_REQUEST, e.getMessage(), request);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiError> handleIllegalState(IllegalStateException e, HttpServletRequest request) {
        return build(HttpStatus.CONFLICT, e.getMessage(), request);
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ApiError> handleResponseStatus(ResponseStatusException e,
                                                         HttpServletRequest request) {
        return build(HttpStatus.valueOf(e.getStatusCode().value()), e.getReason(), request);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ApiError> handleNoResourceFound(NoResourceFoundException e,
                                                          HttpServletRequest request) {
        return build(HttpStatus.NOT_FOUND, "Resource not found", request);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException e,
                                                     HttpServletRequest request) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + " " + error.getDefaultMessage())
                .reduce((a, b) -> a + "; " + b)
                .orElse(e.getMessage());
        return build(HttpStatus.BAD_REQUEST, message, request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGeneric(Exception e, HttpServletRequest request) {
        log.error("Unhandled exception", e);
        return build(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage(), request);
    }

    private ResponseEntity<ApiError> build(HttpStatus status, String message, HttpServletRequest request) {
        return ResponseEntity.status(status)
                .body(new ApiError(Instant.now(), status.value(), status.getReasonPhrase(),
                        message, request.getRequestURI()));
    }

    public record ApiError(Instant timestamp, int status, String error, String message, String path) {
    }
}
