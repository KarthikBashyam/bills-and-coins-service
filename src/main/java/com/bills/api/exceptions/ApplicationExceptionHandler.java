package com.bills.api.exceptions;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ApplicationExceptionHandler {

    @ExceptionHandler(CoinNotAvailableException.class)
    public ResponseEntity<APIError> handle(CoinNotAvailableException exception) {
        APIError apiError = new APIError(exception.getMessage());
        return ResponseEntity.internalServerError().body(apiError);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<APIError> handle(MethodArgumentNotValidException me) {
        APIError apiError = new APIError(me.getMessage());
        return ResponseEntity.badRequest().body(apiError);
    }

}
