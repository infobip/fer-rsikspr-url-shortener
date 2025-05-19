package com.infobip.fer.course.shortener.shortenerservice.api;

public record UrlResponse(
        String url,
        String shortUrl,
        String customerId
) {
}