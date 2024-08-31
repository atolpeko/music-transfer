package com.mf.queue.exception;

import lombok.Getter;

@Getter
public class InvalidUrlException extends RequestQueueException {

    public InvalidUrlException(String url) {
        super(url);
    }

    public InvalidUrlException(String url, String message) {
        super(url, message);
    }

    public InvalidUrlException(String url, String message, Throwable cause) {
        super(url, message, cause);
    }
}
