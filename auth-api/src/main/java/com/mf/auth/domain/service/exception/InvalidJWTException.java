package com.mf.auth.domain.service.exception;

public class InvalidJWTException extends RuntimeException {

    public InvalidJWTException() {
        super();
    }

    public InvalidJWTException(String message) {
        super(message);
    }

    public InvalidJWTException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidJWTException(Throwable cause) {
        super(cause);
    }
}
