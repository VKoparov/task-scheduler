package com.app.taskscheduler.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDateTime;
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
            int statusCode = Objects.requireNonNull(
                    webClient.get()
                            .uri(url)
                            .retrieve()
                            .toBodilessEntity()
                            .block()
                    ).getStatusCode()
                    .value();
            logger.log(Level.INFO, "URL: " + url + " | Status code: " + statusCode + " | Timestamp: " + LocalDateTime.now());
        } catch (WebClientResponseException e) {
            logger.log(Level.SEVERE, "Error calling URL: " + url + " | Status code: " + e.getStatusCode().value() + " | Timestamp: " + LocalDateTime.now());
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Unknown error calling URL: " + url + " | Timestamp: " + LocalDateTime.now(), e);
        }
    }
}
