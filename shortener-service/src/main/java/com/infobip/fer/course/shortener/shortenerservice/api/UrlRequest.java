package com.infobip.fer.course.shortener.shortenerservice.api;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.URL;

public record UrlRequest(
        @Size(max = 2000) @NotNull @URL String url,
        @Size(max = 500) @NotEmpty String customerId
) {
}
