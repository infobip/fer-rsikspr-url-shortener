package com.infobip.fer.course.shortener.shortenerservice;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;

import java.util.Random;
import java.util.stream.IntStream;

import static org.assertj.core.api.BDDAssertions.then;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;

// Integracijski test podiže Spring kontekst i koristi stvarnu H2 bazu podataka.
// Omogućava provjeru poslovne logike koja ovisi o bazi za validaciju podataka.
@SpringBootTest
@ActiveProfiles("test")
@ContextConfiguration(classes = TestConfig.class)
@Sql("/clean_test_data.sql")
public class ShortenerTest {

    // U testovima koristimo Mockito mock implementaciju definiranu u TestConfig klasi:
    @Autowired
    Random randomSeed;

    // Shortener klasa koristi produkcijsku implementaciju te nju testiramo:
    @Autowired
    Shortener shortener;

    @AfterEach
    @BeforeEach
    void resetMock() {
        // Mockito reset metoda obriše zadano ponašanje i opažanja
        // na mock objektu, što nam omogućava da svaki test koristi
        // istu instancu randomSeed mocka bez nepoželjnog međudjelovanja.
        reset(randomSeed);
    }

    @Test
    void shouldShortenUrl() {
        // given
        var givenFullUrl = "https://www.example.com/some/long/url";
        var givenCustomerId = "customer123";
        given(randomSeed.ints(anyLong(), anyInt(), anyInt()))
                .willReturn(IntStream.of(0, 1, 2, 3, 4, 5));

        // when
        var shortenedUrl = shortener.shorten(givenFullUrl, givenCustomerId);

        // then
        // MD5 hash long URL-a i customer ID-a je n7ZovnHGzT+2TO03c0fVqQ==
        // randomSeed je mockan tako shortener uzima prvih 6 znakova:
        then(shortenedUrl).isEqualTo("n7Zovn");
        BDDMockito.then(randomSeed).should()
                .ints(anyLong(), anyInt(), anyInt());
    }

    @Test
    void shouldAvoidCollisionsWhenShorteningSameUrlTwice() {
        // given
        var givenFullUrl = "https://www.example.com/some/long/url";
        var givenCustomerId = "customer123";
        // randomSeed će na prva dva poziva odgovoriti sa [0, 1, 2, 3, 4, 5],
        // a na treći sa [6, 7, 8, 9, 10, 11]. To znači da će prilikom generiranja
        // drugog short code-a doći do kolizije u bazi te će shortener morati odabrati
        // novi set znakova.
        given(randomSeed.ints(anyLong(), anyInt(), anyInt()))
                .willReturn(IntStream.of(0, 1, 2, 3, 4, 5))
                .willReturn(IntStream.of(0, 1, 2, 3, 4, 5))
                .willReturn(IntStream.of(6, 7, 8, 9, 10, 11));

        // when
        var firstShortenerUrl = shortener.shorten(givenFullUrl, givenCustomerId);
        var secondShortenedUrl = shortener.shorten(givenFullUrl, givenCustomerId);

        // then
        then(firstShortenerUrl).isEqualTo("n7Zovn");
        then(secondShortenedUrl).isEqualTo("HGzT+2");
        BDDMockito.then(randomSeed)
                .should(times(3))
                .ints(anyLong(), anyInt(), anyInt());
    }

    @Test
    void shouldAvoidCollisionsForSameUrlSubmittedByDifferentCustomers() {
        // given
        var givenFullUrl = "https://www.example.com/some/long/url";
        var givenFirstCustomerId = "firstCustomer";
        var givenSecondCustomerId = "secondCustomer";
        // randomSeed će uvijek odabrati iste znakove, s tim da će
        // svaki poziv ints metode vratiti novu instancu IntStream-a
        // (ovo je nužno jer se isti IntStream ne može ponovno koristiti).
        // Isto smo mogli postići i sa dvostrukim pozivom willReturn metode:
        // given(randomSeed.ints(anyLong(), anyInt(), anyInt()))
        //      .willReturn(IntStream.of(0, 1, 2, 3, 4, 5))
        //      .willReturn(IntStream.of(0, 1, 2, 3, 4, 5));
        given(randomSeed.ints(anyLong(), anyInt(), anyInt()))
                .willAnswer((ignored) -> IntStream.of(0, 1, 2, 3, 4, 5));

        // when
        var firstShortenerUrl = shortener.shorten(givenFullUrl, givenFirstCustomerId);
        var secondShortenedUrl = shortener.shorten(givenFullUrl, givenSecondCustomerId);

        // then
        then(firstShortenerUrl).isEqualTo("WIJJnm");
        then(secondShortenedUrl).isEqualTo("StKHSQ");
        BDDMockito.then(randomSeed)
                .should(times(2))
                .ints(anyLong(), anyInt(), anyInt());
    }

    @Test
    void shouldAvoidCollisionsForDifferentUrlsSubmittedByTheSameCustomer() {
        // given
        var givenFirstFullUrl = "https://www.example.com/some/long/url";
        var givenSecondFullUrl = "https://example.com/some/other/long/url";
        var givenCustomerId = "customer123";
        given(randomSeed.ints(anyLong(), anyInt(), anyInt()))
                .willAnswer((ignored) -> IntStream.of(0, 1, 2, 3, 4, 5));

        // when
        var firstShortenerUrl = shortener.shorten(givenFirstFullUrl, givenCustomerId);
        var secondShortenedUrl = shortener.shorten(givenSecondFullUrl, givenCustomerId);

        // then
        then(firstShortenerUrl).isEqualTo("n7Zovn");
        then(secondShortenedUrl).isEqualTo("YsnfYO");
        BDDMockito.then(randomSeed)
                .should(times(2))
                .ints(anyLong(), anyInt(), anyInt());
    }
}
