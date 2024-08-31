package com.mf.queue.exception;

import lombok.Getter;

@Getter
public class RequestTimeoutException extends RequestQueueException {

    public RequestTimeoutException(String url) {
        super(url);
    }

    public RequestTimeoutException(String url, String message) {
        super(url, message);
    }

    public RequestTimeoutException(String url, String message, Throwable cause) {
        super(url, message, cause);
    }
}
