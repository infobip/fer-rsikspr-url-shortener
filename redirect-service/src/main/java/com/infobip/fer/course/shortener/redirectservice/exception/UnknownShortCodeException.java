package com.infobip.fer.course.shortener.redirectservice.exception;

public class UnknownShortCodeException extends RuntimeException {

    public UnknownShortCodeException(String message) {
        super(message);
    }

    public static UnknownShortCodeException becauseUrlWasNotFoundFor(String shortCode) {
        return new UnknownShortCodeException("No URL for short code `%s` was found".formatted(shortCode));
    }
}