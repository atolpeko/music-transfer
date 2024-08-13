package com.mf.api.util.restclient.exception;

public class ClientErrorException extends RestClientException {

    public ClientErrorException() {
        super();
    }

    public ClientErrorException(String message) {
        super(message);
    }

    public ClientErrorException(String message, Throwable cause) {
        super(message, cause);
    }

    public ClientErrorException(Throwable cause) {
        super(cause);
    }
}
