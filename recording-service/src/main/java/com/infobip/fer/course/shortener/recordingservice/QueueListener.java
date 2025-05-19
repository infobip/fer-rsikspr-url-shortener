package com.infobip.fer.course.shortener.recordingservice;

import com.infobip.fer.course.shortener.recordingservice.storage.RedirectId;
import com.infobip.fer.course.shortener.recordingservice.storage.RedirectStatisticEntity;
import com.infobip.fer.course.shortener.recordingservice.storage.RedirectStatisticRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

@Service
public class QueueListener {

    private final RedirectStatisticRepository repository;

    public QueueListener(RedirectStatisticRepository repository) {
        this.repository = repository;
    }

    @RabbitListener(queues = {RabbitMQConfig.QUEUE_NAME})
    public void receiveMessage(RedirectEvent event) {
        var redirectId = new RedirectId(event.shortCode(), truncate(event.userAgent()));
        try {
            var entity = new RedirectStatisticEntity(redirectId, event.fullUrl(), event.customerId(), 0L);
            repository.saveAndFlush(entity);
        } catch (DataIntegrityViolationException ignored) {
            // Već postoji redak u bazi s istim short codeom i user agentom
            // Možemo samo pozvati incrementClickCount metodu.
        }
        repository.incrementClickCount(redirectId);
    }

    private String truncate(String userAgent) {
        if (userAgent.length() > 512) {
            return userAgent.substring(0, 512);
        }

        return userAgent;
    }

}
