package com.infobip.fer.course.shortener.shortenerservice.api;

import com.infobip.fer.course.shortener.shortenerservice.Shortener;
import com.infobip.fer.course.shortener.shortenerservice.exception.FailedShorteningException;
import com.infobip.fer.course.shortener.shortenerservice.exception.ShortUrlNotFountException;
import com.infobip.fer.course.shortener.shortenerservice.storage.UrlRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/urls")
public class UrlController {

    private static final Logger LOG = LoggerFactory.getLogger(UrlController.class);

    private final Shortener shortener;
    private final UrlRepository urlRepository;

    public UrlController(Shortener shortener, UrlRepository urlRepository) {
        this.shortener = shortener;
        this.urlRepository = urlRepository;
    }

    @PostMapping
    public UrlResponse shorten(@RequestBody @Valid @NotNull UrlRequest request) {
        var shortCode = shortener.shorten(request.url(), request.customerId());
        var shortUrl = toUrl(shortCode);
        return new UrlResponse(request.url(), shortUrl, request.customerId());
    }

    @GetMapping
    public UrlResponses fetch() {
        return new UrlResponses(
                urlRepository.findAll()
                        .stream()
                        .map(e -> new UrlResponse(
                                e.getFullUrl(),
                                toUrl(e.getShortCode()),
                                e.getCustomerId()
                        )).toList()
        );
    }

    @GetMapping("/{shortCode}")
    public UrlResponse fetchOne(@PathVariable @NotEmpty String shortCode) {
        return urlRepository.findById(shortCode)
                .map(e -> new UrlResponse(
                        e.getFullUrl(),
                        toUrl(e.getShortCode()),
                        e.getCustomerId()
                )).orElseThrow(() -> ShortUrlNotFountException
                        .becauseShortCodeDoesNotExistInDatabase(shortCode)
                );
    }

    private String toUrl(String shortCode) {
        // http://localhost:8080 mora odgovarati adresi na kojoj se pristupa
        // api-gateway servisu. U našem slučaju je to localhost:8080, pri
        // čemu je port 8080 definiran u docker-compose.yaml datoteci.
        return "http://localhost:8080/" + shortCode;
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(FailedShorteningException.class)
    public ErrorResponse handleShorteningException(FailedShorteningException thrown) {
        LOG.error("Failed to shorten URL", thrown);
        return new ErrorResponse("Failed to shorten URL");
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(ShortUrlNotFountException.class)
    public ErrorResponse handleNullPointerException(ShortUrlNotFountException thrown) {
        LOG.debug("Short URL not found", thrown);
        return new ErrorResponse("Short URL not found");
    }
}
