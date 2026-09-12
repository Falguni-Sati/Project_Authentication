package com.FAuth.proauth.exception;

import com.FAuth.proauth.dto.ErrorResponse;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleUserAlreadyExists(
            UserAlreadyExistsException ex) {

        ErrorResponse errorResponse = new ErrorResponse(
                false,
                ex.getMessage(),
                LocalDateTime.now(),
                null
        );

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(errorResponse);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
            MethodArgumentNotValidException ex) {

        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult()
                .getFieldErrors()
                .forEach(error ->
                        errors.put(
                                error.getField(),
                                error.getDefaultMessage()
                        )
                );

        ErrorResponse errorResponse = new ErrorResponse(
                false,
                "Validation failed",
                LocalDateTime.now(),
                errors
        );

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(errorResponse);
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleInvalidCredentials(
            InvalidCredentialsException ex) {

        ErrorResponse errorResponse = new ErrorResponse(
                false,
                ex.getMessage(),
                LocalDateTime.now(),
                null
        );

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(errorResponse);
    }

    @ExceptionHandler(RefreshTokenException.class)
    public ResponseEntity<ErrorResponse> handleRefreshTokenException(
            RefreshTokenException ex) {

        ErrorResponse errorResponse = new ErrorResponse(
                false,
                ex.getMessage(),
                LocalDateTime.now(),
                null
        );

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(errorResponse);
    }

    @ExceptionHandler(DuplicateKeyException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateKey(
            DuplicateKeyException ex) {

        ErrorResponse errorResponse = new ErrorResponse(
                false,
                "Email Already Exists",
                LocalDateTime.now(),
                null
        );

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(errorResponse);
    }
}
