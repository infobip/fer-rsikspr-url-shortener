package com.infobip.fer.course.shortener.shortenerservice.exception;

public class ShortUrlNotFountException extends RuntimeException {

    public ShortUrlNotFountException(String message) {
        super(message);
    }

    public static ShortUrlNotFountException becauseShortCodeDoesNotExistInDatabase(String shortCode) {
        var message = "URL with short code %s does not exist in database".formatted(shortCode);
        return new ShortUrlNotFountException(message);
    }

}
