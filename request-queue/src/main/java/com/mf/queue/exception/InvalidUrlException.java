package com.mf.queue.exception;

import com.mf.queue.entity.Request;
import lombok.Getter;

@Getter
public class InvalidUrlException extends RequestQueueException {

    public InvalidUrlException(Request<?, ?> request) {
        super(request);
    }

    public InvalidUrlException(Request<?, ?> request, String message) {
        super(request, message);
    }

    public InvalidUrlException(Request<?, ?> request, String message, Throwable cause) {
        super(request, message, cause);
    }
}
