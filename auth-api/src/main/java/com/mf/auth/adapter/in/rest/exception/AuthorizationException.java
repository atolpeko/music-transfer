package com.mf.auth.adapter.in.rest.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class AuthorizationException extends RuntimeException {

    private final HttpStatus status;

    public AuthorizationException(HttpStatus status) {
        super();
        this.status = status;
    }

    public AuthorizationException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }

    public AuthorizationException(HttpStatus status, String message, Throwable cause) {
        super(message, cause);
        this.status = status;
    }
}
