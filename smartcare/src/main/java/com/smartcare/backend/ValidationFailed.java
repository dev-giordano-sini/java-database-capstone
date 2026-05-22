package com.smartcare.backend;


import org.apache.commons.logging.LogFactory;
import org.apache.juli.logging.Log;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ValidationFailed {

    private final Log log = (Log) LogFactory.getLog(this.getClass());
}
