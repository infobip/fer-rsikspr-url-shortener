package com.infobip.fer.course.shortener.shortenerservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.util.Random;

@EnableJpaAuditing
@SpringBootApplication
public class ShortenerServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ShortenerServiceApplication.class, args);
    }

    @Bean
    @Profile("!test")
    public Random randomSeed() {
        // U produkciji koristimo standardnu implementaciju Random klase.
        // @Profile anotacija osigurava da se ovaj bean neće koristiti ako
        // je aktivan "test" profil. Random bean za testove definiran je
        // u TestConfig klasi.
        return new Random();
    }

}
