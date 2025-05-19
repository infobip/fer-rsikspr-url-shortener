package com.infobip.fer.course.shortener.redirectservice;

import com.infobip.fer.course.shortener.redirectservice.exception.UnknownShortCodeException;
import com.infobip.fer.course.shortener.redirectservice.storage.UrlRepository;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
public class Redirector {

    private final UrlRepository urlRepository;
    private final RabbitTemplate rabbitTemplate;

    public Redirector(UrlRepository urlRepository, RabbitTemplate rabbitTemplate) {
        this.urlRepository = urlRepository;
        this.rabbitTemplate = rabbitTemplate;
    }

    public String redirect(String shortCode, String userAgent) {
        var url = urlRepository.findById(shortCode)
                .orElseThrow(() -> UnknownShortCodeException.becauseUrlWasNotFoundFor(shortCode));
        var event = new RedirectEvent(userAgent, url.getShortCode(), url.getFullUrl(), url.getCustomerId());
        rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_NAME, RabbitMQConfig.QUEUE_NAME, event);
        return url.getFullUrl();
    }

}