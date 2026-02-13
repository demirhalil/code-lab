package com.dev.concurrency.interfaces.rest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.ConcurrencyFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * Handles Optimistic Locking failures.
     * In a high-scale system, you might want to retry automatically 
     * or return a specific "Conflict" error to the client.
     */
    @ExceptionHandler({ObjectOptimisticLockingFailureException.class, ConcurrencyFailureException.class})
    public ResponseEntity<Map<String, String>> handleConcurrencyConflict(Exception e) {
        log.error("Concurrency conflict detected: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Map.of("error", "The resource was updated by another process. Please retry."));
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> handleRuntimeException(RuntimeException e) {
        log.error("Unexpected error: {}", e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", e.getMessage()));
    }
}
