package com.infobip.fer.course.shortener.shortenerservice.api;

import com.infobip.fer.course.shortener.shortenerservice.TestConfig;
import org.json.JSONException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import java.util.Arrays;
import java.util.Random;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.assertj.core.api.BDDAssertions.then;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(classes = TestConfig.class)
@Sql("/clean_test_data.sql")
public class UrlControllerTest {

    @LocalServerPort
    Integer port;

    // U testovima koristimo Mockito mock implementaciju definiranu u TestConfig klasi:
    @Autowired
    Random randomSeed;

    RestClient restClient;

    @BeforeEach
    void setUp() {
        restClient = RestClient.create("http://localhost:%d".formatted(port));
        // zadano ponašanje randomSeed beana zajedničko je večini testova:
        given(randomSeed.ints(anyLong(), anyInt(), anyInt()))
                .willAnswer((ignored) -> IntStream.of(0, 1, 2, 3, 4, 5));
    }

    @Test
    void shouldGenerateShortUrl() throws JSONException {
        // given
        var requestBody = """
                {
                    "url": "https://www.example.com/some/long/url",
                    "customerId": "customer123"
                }""";
        // when
        var response = restClient.post()
                .uri("/v1/urls")
                .body(requestBody)
                .contentType(MediaType.APPLICATION_JSON)
                .retrieve()
                .toEntity(String.class);

        // then
        then(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        JSONAssert.assertEquals("""
                {
                    "url": "https://www.example.com/some/long/url",
                    "shortUrl": "http://localhost:8080/n7Zovn",
                    "customerId": "customer123"
                }""", response.getBody(), true);
    }

    @ParameterizedTest
    @CsvSource({
            """
                    {""",
            """
                    {}""",
            """
                    {"customerId": "customer123"}""",
            """
                    {"url": null, "customerId": "customer123"}""",
            """
                    {"url": "", "customerId": "customer123"}""",
            """
                    {"url": "not-a-url", "customerId": "customer123"}""",
            """
                    {"url": "https://example.com/0123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890", "customerId": "customer123"}""",
            """
                    {"url": "https://example.com"}""",
            """
                    {"url": "https://example.com", "customerId": null}""",
            """
                    {"url": "https://example.com", "customerId": ""}""",
            """
                    {"url": "https://example.com", "customerId": ""}""",
            """
                    {"url": "https://example.com", "customerId": "012346789012346789012346789012346789012346789012346789012346789012346789012346789012346789012346789012346789012346789012346789012346789012346789012346789012346789012346789012346789012346789012346789001234678901234678901234678901234678901234678901234678901234678901234678901234678901234678901234678900123467890123467890123467890123467890123467890123467890123467890123467890123467890123467890123467890012346789012346789012346789012346789012346789012346789012346789012346789012346789012346789012346789012"}""",
    })
    void shouldValidateRequestBody(String givenInvalidBody) throws JSONException {
        // when
        var exception = catchThrowableOfType(
                HttpClientErrorException.class,
                () -> restClient.post()
                        .uri("/v1/urls")
                        .body(givenInvalidBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .retrieve()
                        .toEntity(String.class)
        );

        // then
        then(exception.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void shouldFetchAllUrls() throws JSONException {
        // given
        givenShortUrlsForCustomers("firstCustomer", "secondCustomer");

        // when
        var response = restClient.get()
                .uri("/v1/urls")
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .toEntity(String.class);

        // then
        then(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        JSONAssert.assertEquals("""
                {
                  "urls": [
                    {
                      "url": "https://www.example.com/some/long/url",
                      "shortUrl": "http://localhost:8080/WIJJnm",
                      "customerId": "firstCustomer"
                    },
                    {
                      "url": "https://www.example.com/some/long/url",
                      "shortUrl": "http://localhost:8080/StKHSQ",
                      "customerId": "secondCustomer"
                    }
                  ]
                }""", response.getBody(), true);
    }

    @Test
    void shouldFetchOneExistingUrl() throws JSONException {
        // given
        givenShortUrlsForCustomers("firstCustomer", "secondCustomer");

        // when
        var response = restClient.get()
                .uri("/v1/urls/WIJJnm")
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .toEntity(String.class);

        // then
        then(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        JSONAssert.assertEquals("""
                {
                  "url": "https://www.example.com/some/long/url",
                  "shortUrl": "http://localhost:8080/WIJJnm",
                  "customerId": "firstCustomer"
                }""", response.getBody(), true);
    }

    @Test
    void shouldReturn404ForNonExistingShortUrl() throws JSONException {
        // given
        givenShortUrlsForCustomers("firstCustomer", "secondCustomer");

        // when
        var exception = catchThrowableOfType(
                HttpClientErrorException.class,
                () -> restClient.get()
                        .uri("/v1/urls/nonExistingShortUrl")
                        .accept(MediaType.APPLICATION_JSON)
                        .retrieve()
                        .toEntity(String.class)
        );

        // then
        then(exception.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    private void givenShortUrlsForCustomers(String... customerIds) {
        Arrays.stream(customerIds)
                .map("""
                        {
                            "url": "https://www.example.com/some/long/url",
                            "customerId": "%s"
                        }"""::formatted)
                .forEach(requestBody ->
                        restClient.post()
                                .uri("/v1/urls")
                                .body(requestBody)
                                .contentType(MediaType.APPLICATION_JSON)
                                .retrieve()
                                .toEntity(String.class)
                );
    }
}
