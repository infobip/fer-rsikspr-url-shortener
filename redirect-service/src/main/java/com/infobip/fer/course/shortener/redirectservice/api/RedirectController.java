package com.infobip.fer.course.shortener.redirectservice.api;

import com.infobip.fer.course.shortener.redirectservice.Redirector;
import com.infobip.fer.course.shortener.redirectservice.exception.UnknownShortCodeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

@Controller
public class RedirectController {

    private static final Logger LOG = LoggerFactory.getLogger(RedirectController.class);

    private final Redirector redirector;

    public RedirectController(Redirector redirector) {
        this.redirector = redirector;
    }

    @RequestMapping("/{shortCode}")
    public RedirectView redirect(@PathVariable String shortCode, @RequestHeader("User-Agent") String userAgent) {
        var fullUrl = redirector.redirect(shortCode, userAgent);
        return new RedirectView(fullUrl);
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(UnknownShortCodeException.class)
    public ErrorResponse handleUnknownShortCodeException(UnknownShortCodeException ignored) {
        LOG.debug("Short URL not found", ignored);
        return new ErrorResponse("URL not found");
    }
}