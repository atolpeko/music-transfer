package com.mf.api.usecase.exception;

public class InvalidStateException extends UseCaseException {

    public InvalidStateException() {
        super();
    }

    public InvalidStateException(String message) {
        super(message);
    }

    public InvalidStateException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidStateException(Throwable cause) {
        super(cause);
    }
}
