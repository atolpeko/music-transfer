package com.mf.api.port.exception;

public class MusicServiceException extends RuntimeException {

    public MusicServiceException() {
        super();
    }

    public MusicServiceException(String message) {
        super(message);
    }

    public MusicServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public MusicServiceException(Throwable cause) {
        super(cause);
    }
}
