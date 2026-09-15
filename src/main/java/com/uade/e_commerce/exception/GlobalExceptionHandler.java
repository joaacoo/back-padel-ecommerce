package com.uade.e_commerce.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Map;
import org.springframework.web.bind.MethodArgumentNotValidException;
import java.util.HashMap;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, String>> resourceNotFound(ResourceNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(ArgumentInvalidException.class)
    public ResponseEntity<Map<String, String>> argumentInvalid(ArgumentInvalidException ex) {
        return ResponseEntity.badRequest()
                .body(Map.of("error", ex.getMessage()));
    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> validationErrors(
        MethodArgumentNotValidException ex) {

    Map<String, String> errores = new HashMap<>();

    ex.getBindingResult()
            .getFieldErrors()
            .forEach(error ->
                    errores.put(
                            error.getField(),
                            error.getDefaultMessage()
                    )
            );

    return ResponseEntity.badRequest().body(errores);
    }
}
