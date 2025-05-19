package com.infobip.fer.course.shortener.recordingservice.api;

import com.infobip.fer.course.shortener.recordingservice.storage.RedirectStatisticRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/v1/stats")
public class StatisticController {

    private final RedirectStatisticRepository repository;

    public StatisticController(RedirectStatisticRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/{customerId}")
    public List<RedirectStatistic> getStatistics(@PathVariable String customerId) {
        return repository.findRedirectStatisticEntitiesByCustomerId(customerId)
                .stream()
                .map(e -> new RedirectStatistic(
                        e.getRedirectId().getShortCode(),
                        e.getRedirectId().getUserAgent(),
                        e.getFullUrl(),
                        e.getClickCount()
                )).toList();
    }

}