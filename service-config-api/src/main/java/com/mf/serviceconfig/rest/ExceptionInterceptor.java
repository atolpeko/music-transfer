package com.mf.serviceconfig.rest;

import com.mf.serviceconfig.rest.entity.ErrorResponse;

import java.time.LocalDateTime;
import java.time.ZoneId;

import javax.servlet.http.HttpServletRequest;

import lombok.extern.log4j.Log4j2;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.UnsatisfiedServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@Log4j2
@RestControllerAdvice
public class ExceptionInterceptor {

    @ExceptionHandler({ HttpMessageNotReadableException.class,
        HttpMediaTypeNotSupportedException.class,
        UnsatisfiedServletRequestParameterException.class,
        MissingServletRequestParameterException.class,
        MethodArgumentTypeMismatchException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponse> handleBadRequestException(
        Exception e,
        HttpServletRequest request
    ) {
        return handleException(e.getMessage(), request, HttpStatus.BAD_REQUEST);
    }

    private ResponseEntity<ErrorResponse> handleException(
        String msg,
        HttpServletRequest request,
        HttpStatus status
    ) {
        var path = request.getServletPath();
        log.error("Request for {} error: {}", path, msg);
        var timestamp = LocalDateTime.now()
            .atZone(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli();
        var error = ErrorResponse.builder()
            .status(status.value())
            .error(msg)
            .path(path)
            .timestamp(timestamp)
            .build();

        return ResponseEntity.status(status.value()).body(error);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleNotSupportedException(
        HttpRequestMethodNotSupportedException e,
        HttpServletRequest request
    ) {
        return handleException(e.getMessage(), request, HttpStatus.METHOD_NOT_ALLOWED);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnknownException(
        Exception e,
        HttpServletRequest request
    ) {
        return handleException(e.getMessage(), request, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
