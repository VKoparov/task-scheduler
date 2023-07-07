package com.app.taskscheduler.services;

import com.app.taskscheduler.constants.EventLog;
import io.sentry.Sentry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class TaskExecuteService {

    @Value("${file-paths.web-urls}")
    private String webUrls;

    private final Logger logger = Logger.getLogger(TaskExecuteService.class.getName());

    private final WebClient webClient;

    public TaskExecuteService() {
        this.webClient = WebClient.create();
    }

    @Scheduled(cron = "${cron.timeout}")
    public void wake() throws IOException {
        Files.readAllLines(new File(webUrls).toPath())
                .forEach(this::queryUrl);
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

    private void logEvent(Exception e, Level logLevel, String message) {
        Sentry.captureException(e);
        Sentry.captureMessage(message);
        logger.log(logLevel, message, e);
    }
}
