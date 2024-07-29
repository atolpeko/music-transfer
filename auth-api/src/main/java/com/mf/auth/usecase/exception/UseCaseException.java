package com.mf.auth.usecase.exception;

public class UseCaseException extends RuntimeException {

    public UseCaseException() {
        super();
    }

    public UseCaseException(String message) {
        super(message);
    }

    public UseCaseException(String message, Throwable cause) {
        super(message, cause);
    }

    public UseCaseException(Throwable cause) {
        super(cause);
    }
}
