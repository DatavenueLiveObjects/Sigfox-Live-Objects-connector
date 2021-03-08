package com.orange.lo.sample.sigfox2lo;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.orange.lo.sample.sigfox2lo.sigfox.model.ErrorInfo;

@ControllerAdvice
public class Sigfox2loErrorController {

	@ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorInfo> handleException(Exception e) {
        return new ResponseEntity<>(new ErrorInfo(e.getMessage()), HttpStatus.NOT_FOUND);
    }   
}