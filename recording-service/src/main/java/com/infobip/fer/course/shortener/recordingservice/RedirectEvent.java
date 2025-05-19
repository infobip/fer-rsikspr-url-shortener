package com.infobip.fer.course.shortener.recordingservice;

public record RedirectEvent(
        String userAgent,
        String shortCode,
        String fullUrl,
        String customerId
) {
}
