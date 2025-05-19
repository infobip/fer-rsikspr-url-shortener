package com.infobip.fer.course.shortener.shortenerservice.exception;

import java.security.NoSuchAlgorithmException;

public class FailedShorteningException extends RuntimeException {

    public FailedShorteningException(String message, Throwable cause) {
        super(message, cause);
    }

    public FailedShorteningException(String message) {
        super(message);
    }

    public static FailedShorteningException becauseMd5HashingAlgorithmIsMissing(NoSuchAlgorithmException cause) {
        return new FailedShorteningException("Missing MD5 hash algorithm", cause);
    }

    public static FailedShorteningException becauseOfUnexpectedDatabaseError(Throwable cause) {
        return new FailedShorteningException("Unexpected database error", cause);
    }

    public static FailedShorteningException becauseFindingUniqueShortCodeTookTooLong(String hash, long maxAttempts) {
        var message = "Failed to find a unique short code for hash `%s` in %d attempts".formatted(hash, maxAttempts);
        return new FailedShorteningException(message);
    }

}
