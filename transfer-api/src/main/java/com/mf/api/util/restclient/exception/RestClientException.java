package com.mf.api.util.restclient.exception;

public class RestClientException extends RuntimeException {

    public RestClientException() {
        super();
    }

    public RestClientException(String message) {
        super(message);
    }

    public RestClientException(String message, Throwable cause) {
        super(message, cause);
    }

    public RestClientException(Throwable cause) {
        super(cause);
    }
}
