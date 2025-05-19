package com.infobip.fer.course.shortener.shortenerservice;

import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import java.util.Random;

@TestConfiguration
public class TestConfig {

    @Bean
    public Random randomSeed() {
        // U testovima koristimo Mockito mock implementaciju Random klase.
        // Na taj način možemo kontrolirati generiranje nasumičnih brojeva
        // u Shortener klasi i osigurati da testovi budu deterministički.
        return Mockito.mock(Random.class);
    }
}
