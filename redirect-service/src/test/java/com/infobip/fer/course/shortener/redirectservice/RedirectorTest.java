package com.infobip.fer.course.shortener.redirectservice;

import com.infobip.fer.course.shortener.redirectservice.exception.UnknownShortCodeException;
import com.infobip.fer.course.shortener.redirectservice.storage.UrlRepository;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.BDDAssertions.catchThrowableOfType;
import static org.assertj.core.api.BDDAssertions.then;

public class RedirectorTest extends IntegrationTestBase {

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Autowired
    UrlRepository urlRepository;

    @Autowired
    Redirector redirector;

    @Test
    void shouldRedirectExistingShortCode() {
        // given
        var givenShortCode = "StKHSQ";
        var givenUserAgent = "Mozilla/5.0 (X11; Fedora; Linux x86_64; rv:138.0) Gecko/20100101 Firefox/138.0";

        // when
        var fullUrl = redirector.redirect(givenShortCode, givenUserAgent);
        var event = rabbitTemplate.receiveAndConvert(QUEUE_NAME, 1_000L);

        // then
        var expectedFullUrl = "https://www.example.com/some/long/url";
        var expectedCustomerId = "secondCustomer";
        then(fullUrl).isEqualTo(expectedFullUrl);
        then(event).isEqualTo(new RedirectEvent(
                givenUserAgent,
                givenShortCode,
                expectedFullUrl,
                expectedCustomerId
        ));
    }

    @Test
    void shouldThrowUnknownShortCodeExceptionIfShortCodeDoesNotExist() {
        // given
        var givenShortCode = "non-existing-short-code";
        var givenUserAgent = "Mozilla/5.0 (X11; Fedora; Linux x86_64; rv:138.0) Gecko/20100101 Firefox/138.0";

        // when
        var exception = catchThrowableOfType(
                UnknownShortCodeException.class,
                () -> redirector.redirect(givenShortCode, givenUserAgent)
        );
        var event = rabbitTemplate.receiveAndConvert(QUEUE_NAME, 1_000L);

        // then
        then(exception).isNotNull();
        then(event).isNull();
    }

    @Test
    void shouldResolveSubsequentRedirectsFromCache() {
        // given
        var givenShortCode = "StKHSQ";
        var givenUserAgent = "Mozilla/5.0 (X11; Fedora; Linux x86_64; rv:138.0) Gecko/20100101 Firefox/138.0";

        // when
        var firstFullUrl = redirector.redirect(givenShortCode, givenUserAgent);
        urlRepository.deleteAll();
        var secondFullUrl = redirector.redirect(givenShortCode, givenUserAgent);

        // then
        var expectedFullUrl = "https://www.example.com/some/long/url";
        then(firstFullUrl).isEqualTo(expectedFullUrl);
        then(secondFullUrl).isEqualTo(expectedFullUrl);
    }

}
