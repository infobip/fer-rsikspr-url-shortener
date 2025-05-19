package com.infobip.fer.course.shortener.recordingservice.api;

public record RedirectStatistic(
        String shortCode,
        String fullUrl,
        String userAgent,
        Long clickCount
) {
}