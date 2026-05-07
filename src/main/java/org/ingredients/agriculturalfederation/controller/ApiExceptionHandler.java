package org.ingredients.agriculturalfederation.controller;

import org.ingredients.agriculturalfederation.validator.exception.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class ApiExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(ApiExceptionHandler.class);

    @ExceptionHandler({InvalidMemberException.class, InvalidCollectivityException.class})
    public ResponseEntity<String> handleBadRequest(RuntimeException e) {
        log.warn("Bad request: {}", e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).contentType(MediaType.TEXT_PLAIN).body(e.getMessage());
    }

    @ExceptionHandler({MemberNotFoundException.class, CollectivityNotFoundException.class})
    public ResponseEntity<String> handleNotFound(RuntimeException e) {
        log.warn("Not found: {}", e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).contentType(MediaType.TEXT_PLAIN).body(e.getMessage());
    }

    @ExceptionHandler({CollectivityIdentityAlreadyAssignedException.class, CollectivityIdentityConflictException.class})
    public ResponseEntity<String> handleConflict(RuntimeException e) {
        log.warn("Conflict: {}", e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.CONFLICT).contentType(MediaType.TEXT_PLAIN).body(e.getMessage());
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<String> handleTechnicalError(ValidationException e) {
        log.error("Validation technical error", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).contentType(MediaType.TEXT_PLAIN).body("A technical error occurred during validation.");
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<String> handleUnreadableBody(HttpMessageNotReadableException e) {
        log.warn("Unreadable request body", e);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).contentType(MediaType.TEXT_PLAIN).body("Invalid request body.");
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgument(IllegalArgumentException e) {
        log.warn("Illegal argument: {}", e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).contentType(MediaType.TEXT_PLAIN).body("Invalid request.");
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleUnhandledRuntime(RuntimeException e) {
        log.error("Unhandled runtime exception", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).contentType(MediaType.TEXT_PLAIN).body("An unexpected error occurred.");
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleUnhandledException(Exception e, HttpServletRequest request) {
        String method = request == null ? "?" : request.getMethod();
        String uri = request == null ? "?" : request.getRequestURI();
        String query = request == null ? null : request.getQueryString();
        String full = query == null ? uri : (uri + "?" + query);
        log.error("Unhandled exception on {} {}", method, full, e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .contentType(MediaType.TEXT_PLAIN)
                .body("An unexpected error occurred.");
    }
}
