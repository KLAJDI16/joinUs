package com.example.joinUs.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity handleInvalidEvent(ResponseStatusException ex) {
        return ResponseEntity.status(ex.getStatusCode())
                .body(ex.getBody());
    }

//    AuthenticationException
    @ExceptionHandler(AuthenticationException.class)
public ResponseEntity handleAuthenticationException(AuthenticationException exception){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(exception.getMessage());
}

}

