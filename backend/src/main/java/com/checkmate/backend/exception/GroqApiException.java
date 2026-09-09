package com.checkmate.backend.exception;

public class GroqApiException extends RuntimeException {
    public GroqApiException(String message) {
        super(message);
    }

    public GroqApiException(String message, Throwable cause) {
        super(message, cause);
    }
}
