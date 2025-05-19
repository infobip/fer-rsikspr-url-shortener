package com.infobip.fer.course.shortener.recordingservice;

import com.infobip.fer.course.shortener.recordingservice.storage.RedirectId;
import com.infobip.fer.course.shortener.recordingservice.storage.RedirectStatisticEntity;
import com.infobip.fer.course.shortener.recordingservice.storage.RedirectStatisticRepository;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.testcontainers.shaded.org.awaitility.Awaitility;

import java.time.Duration;
import java.util.List;

import static org.assertj.core.api.BDDAssertions.then;

public class QueueListenerTest extends IntegrationTestBase {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private RedirectStatisticRepository repository;

    @Test
    void shouldProcessSingleRabbitEvent() {
        // given
        var givenShortCode = "WIJJnm";
        var givenUserAgent = "Mozilla/5.0 (X11; Fedora; Linux x86_64; rv:138.0) Gecko/20100101 Firefox/138.0";
        var givenFullUrl = "https://www.example.com/some/long/url";
        var givenCustomerId = "firstCustomer";
        var givenEvent = new RedirectEvent(givenUserAgent, givenShortCode, givenFullUrl, givenCustomerId);

        // when
        rabbitTemplate.convertAndSend(EXCHANGE_NAME, QUEUE_NAME, givenEvent);

        // then
        Awaitility.await()
                .atMost(Duration.ofMillis(300))
                .pollInterval(Duration.ofMillis(30))
                .untilAsserted(() -> {
                    var stats = repository.findAll();
                    var expectedEntry = expectedEntry(givenShortCode, givenUserAgent, givenFullUrl, givenCustomerId, 1L);
                    then(stats).containsExactly(expectedEntry);
                });
    }

    @Test
    void shouldAggregateDifferentEventsBasedOnShortCodeAndUserAgent() {
        // given
        var givenFirstCustomerShortCode = "WIJJnm";
        var givenSecondCustomerShortCode = "StKHSQ";
        var givenFirefoxUserAgent = "Mozilla/5.0 (X11; Fedora; Linux x86_64; rv:138.0) Gecko/20100101 Firefox/138.0";
        var givenChromeUserAgent = "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/136.0.0.0 Safari/537.36";
        var givenSafariUserAgent = "Mozilla/5.0 (Macintosh; Intel Mac OS X 14_7_5) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/18.4 Safari/605.1.15";
        var givenFullUrl = "https://www.example.com/some/long/url";
        var givenFirstCustomerId = "firstCustomer";
        var givenSecondCustomerId = "secondCustomer";
        var givenEvents = List.of(
                new RedirectEvent(givenFirefoxUserAgent, givenFirstCustomerShortCode, givenFullUrl, givenFirstCustomerId),
                new RedirectEvent(givenChromeUserAgent, givenFirstCustomerShortCode, givenFullUrl, givenFirstCustomerId),
                new RedirectEvent(givenSafariUserAgent, givenFirstCustomerShortCode, givenFullUrl, givenFirstCustomerId),
                new RedirectEvent(givenFirefoxUserAgent, givenSecondCustomerShortCode, givenFullUrl, givenSecondCustomerId),
                new RedirectEvent(givenSafariUserAgent, givenSecondCustomerShortCode, givenFullUrl, givenSecondCustomerId),
                new RedirectEvent(givenFirefoxUserAgent, givenFirstCustomerShortCode, givenFullUrl, givenFirstCustomerId),
                new RedirectEvent(givenChromeUserAgent, givenFirstCustomerShortCode, givenFullUrl, givenFirstCustomerId),
                new RedirectEvent(givenFirefoxUserAgent, givenSecondCustomerShortCode, givenFullUrl, givenSecondCustomerId),
                new RedirectEvent(givenFirefoxUserAgent, givenFirstCustomerShortCode, givenFullUrl, givenFirstCustomerId)
        );

        // when
        givenEvents.forEach(event -> rabbitTemplate.convertAndSend(EXCHANGE_NAME, QUEUE_NAME, event));

        // then
        Awaitility.await()
                .atMost(Duration.ofMillis(300))
                .pollInterval(Duration.ofMillis(30))
                .untilAsserted(() -> {
                    var stats = repository.findAll();
                    then(stats).containsExactlyInAnyOrder(
                            expectedEntry(givenFirstCustomerShortCode, givenFirefoxUserAgent, givenFullUrl, givenFirstCustomerId, 3L),
                            expectedEntry(givenFirstCustomerShortCode, givenChromeUserAgent, givenFullUrl, givenFirstCustomerId, 2L),
                            expectedEntry(givenFirstCustomerShortCode, givenSafariUserAgent, givenFullUrl, givenFirstCustomerId, 1L),
                            expectedEntry(givenSecondCustomerShortCode, givenFirefoxUserAgent, givenFullUrl, givenSecondCustomerId, 2L),
                            expectedEntry(givenSecondCustomerShortCode, givenSafariUserAgent, givenFullUrl, givenSecondCustomerId, 1L)
                    );
                });
    }

    private static RedirectStatisticEntity expectedEntry(String shortCode, String userAgent, String fullUrl, String customerId, long count) {
        return new RedirectStatisticEntity(new RedirectId(shortCode, userAgent), fullUrl, customerId, count);
    }
}
