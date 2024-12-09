package com.app.taskscheduler.services;

import com.app.taskscheduler.constants.EventLog;
import io.sentry.Sentry;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public class WebUrlQueryService implements Runnable {

    private final String escapeToken;

    private final String[] webUrls;

    private final WebClient webClient;

    private final Logger logger = Logger.getLogger(WebUrlQueryService.class.getName());

    public WebUrlQueryService(String escapeToken, String[] webUrls) {
        this.escapeToken = escapeToken;
        this.webUrls = webUrls;
        this.webClient = WebClient.create();
    }

    @Override
    public void run() {
        try {
            Arrays.stream(webUrls).toList().forEach(this::queryUrl);
        } catch (Exception e) {
            logEvent(e, Level.SEVERE, e.getMessage());

            throw new RuntimeException(e);
        }
    }

    private void queryUrl(String url) {
        try {
            int statusCode = Objects.requireNonNull(sendRequest(url))
                    .getStatusCode()
                    .value();
            logger.log(Level.INFO, EventLog.SUCCESS(url, statusCode));
        } catch (WebClientRequestException e) {
            sendRequest(url);

            logEvent(e, Level.WARNING, EventLog.WARNING(url));
        } catch (WebClientResponseException e) {
            logEvent(e, Level.SEVERE, EventLog.ERROR(url, e.getStatusCode().value()));
        } catch (Exception e) {
            logEvent(e, Level.SEVERE, EventLog.UNKNOWN(url));
        }
    }

    private ResponseEntity<?> sendRequest(String url) {
        return webClient.get()
                .uri(url)
                .retrieve()
                .toBodilessEntity()
                .block();
    }

    private Boolean escapeConditions(String url) {
        return !String.valueOf(url.charAt(0))
                .equals(this.escapeToken);
    }

    private void logEvent(Exception e, Level logLevel, String message) {
        Sentry.captureException(e);
        Sentry.captureMessage(message);
        logger.log(logLevel, message, e);
    }
}
