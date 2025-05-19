package com.infobip.fer.course.shortener.shortenerservice;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

// Aktivacija "test" profila omogućava korištenje testne konfiguracije
// definirane u application-test.yaml datoteci.
@SpringBootTest
@ActiveProfiles("test")
@ContextConfiguration(classes = TestConfig.class)
class ShortenerServiceApplicationTests {

    @Test
    void contextLoads() {
    }

}
