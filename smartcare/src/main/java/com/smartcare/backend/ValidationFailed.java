package com.smartcare.backend;


import org.apache.commons.logging.LogFactory;
import org.apache.juli.logging.Log;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
public class ValidationFailed {

    private final Log log = (Log) LogFactory.getLog(this.getClass());

    @ExceptionHandler({
            MethodArgumentNotValidException.class
    })
    public ResponseEntity<String> handle(MethodArgumentNotValidException e){
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;

        List<String> errors = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(FieldError::getDefaultMessage)
                .toList();

        return  new ResponseEntity<>(errors.getFirst(), status);
    }
}
