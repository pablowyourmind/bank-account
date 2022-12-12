package com.github.pablowyourmind.utils;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import pl.foltak.polishidnumbers.pesel.InvalidPeselException;

import java.io.InvalidObjectException;

@ControllerAdvice
public class MainAccountInfoExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(InvalidPeselException.class)
    public ResponseEntity<Object> handleInvalidPesel(InvalidPeselException ipe, WebRequest request) {
        String responseBody = String.format("PESEL %s: %s", request.getParameter("pesel"), ipe.getLocalizedMessage());
        return handleExceptionInternal(ipe, responseBody, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

    @ExceptionHandler(InvalidObjectException.class)
    public ResponseEntity<Object> handleInvalidObject(InvalidObjectException ioe, WebRequest request) {
        return handleExceptionInternal(ioe, ioe.getLocalizedMessage(), new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        return handleExceptionInternal(ex, ex.getMessage(), new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }
}
