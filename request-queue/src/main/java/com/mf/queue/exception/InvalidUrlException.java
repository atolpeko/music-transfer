package com.mf.queue.exception;

import lombok.Getter;

@Getter
public class InvalidUrlException extends RuntimeException {

    private final String url;

    public InvalidUrlException(String url) {
        super();
        this.url = url;
    }

    public InvalidUrlException(String url, String message) {
        super(message);
        this.url = url;
    }

    public InvalidUrlException(String url, String message, Throwable cause) {
        super(message, cause);
        this.url = url;
    }
}
