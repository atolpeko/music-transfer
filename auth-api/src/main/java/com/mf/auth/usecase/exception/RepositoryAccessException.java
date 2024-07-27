package com.mf.auth.usecase.exception;

public class RepositoryAccessException extends UseCaseException {

    public RepositoryAccessException() {
        super();
    }

    public RepositoryAccessException(String message) {
        super(message);
    }

    public RepositoryAccessException(String message, Throwable cause) {
        super(message, cause);
    }

    public RepositoryAccessException(Throwable cause) {
        super(cause);
    }
}
