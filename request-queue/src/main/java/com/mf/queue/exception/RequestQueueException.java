package com.mf.queue.exception;

import lombok.Getter;

@Getter
public class RequestQueueException extends RuntimeException {

    private final String url;

    public RequestQueueException(String url) {
        super();
        this.url = url;
    }

    public RequestQueueException(String url, String message) {
        super(message);
        this.url = url;
    }

    public RequestQueueException(String url, String message, Throwable cause) {
        super(message, cause);
        this.url = url;
    }
}
