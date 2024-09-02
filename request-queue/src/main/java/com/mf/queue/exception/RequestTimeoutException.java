package com.mf.queue.exception;

import com.mf.queue.entity.Request;
import lombok.Getter;

@Getter
public class RequestTimeoutException extends RequestQueueException {

    public RequestTimeoutException(Request<?, ?> request) {
        super(request);
    }

    public RequestTimeoutException(Request<?, ?> request, String message) {
        super(request, message);
    }

    public RequestTimeoutException(Request<?, ?> request, String message, Throwable cause) {
        super(request, message, cause);
    }
}
