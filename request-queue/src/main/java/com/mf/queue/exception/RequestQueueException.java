package com.mf.queue.exception;

import com.mf.queue.entity.Request;
import lombok.Getter;

@Getter
public class RequestQueueException extends RuntimeException {

    private final Request<?, ?> request;

    public RequestQueueException(Request<?, ?> request) {
        super();
        this.request = request;
    }

    public RequestQueueException(Request<?, ?> request, String message) {
        super(message);
        this.request = request;
    }

    public RequestQueueException(Request<?, ?> request, String message, Throwable cause) {
        super(message, cause);
        this.request = request;
    }
}
