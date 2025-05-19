package com.infobip.fer.course.shortener.redirectservice;

import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Declarables;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String EXCHANGE_NAME = "x.redirect-events";
    public static final String QUEUE_NAME = "q.redirect-events";

    /**
     * topicBindings metoda sadrži definiciju RabbitMQ queuea i exchangea
     * koji će se automatski kreirati prilikom pokretanja redirect-service
     * aplikacije. Ovo je razlog zašto recording-service mora čekati s
     * pokretanjem dok se redirect-service ne pokrene, što je definirano
     * u docker-compose.yaml datoteci.
     */
    @Bean
    public Declarables topicBindings() {
        var queue = new Queue(QUEUE_NAME, true);
        var exchange = new DirectExchange(EXCHANGE_NAME, true, true);
        return new Declarables(
                queue,
                exchange,
                BindingBuilder
                        .bind(queue)
                        .to(exchange)
                        .withQueueName()
        );
    }

    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(CachingConnectionFactory connectionFactory, Jackson2JsonMessageConverter jsonConverter) {
        var template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonConverter);
        return template;
    }
}