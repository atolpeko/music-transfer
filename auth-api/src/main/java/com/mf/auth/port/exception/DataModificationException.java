package com.mf.auth.port.exception;

public class DataModificationException extends RepositoryException {

    public DataModificationException() {
        super();
    }

    public DataModificationException(String message) {
        super(message);
    }

    public DataModificationException(String message, Throwable cause) {
        super(message, cause);
    }

    public DataModificationException(Throwable cause) {
        super(cause);
    }
}
