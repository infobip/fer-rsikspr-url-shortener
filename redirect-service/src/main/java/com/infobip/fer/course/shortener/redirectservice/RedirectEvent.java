package com.infobip.fer.course.shortener.redirectservice;

public record RedirectEvent(
        String userAgent,
        String shortCode,
        String fullUrl,
        String customerId
) {
}