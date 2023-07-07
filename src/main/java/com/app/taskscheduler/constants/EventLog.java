package com.app.taskscheduler.constants;

import java.text.MessageFormat;
import java.time.LocalDateTime;

public class EventLog {

    public static String SUCCESS(String url, int statusCode) {
        return MessageFormat.format(
                "URL: {0} | Status code: {1} | Timestamp: {2}",
                url,
                statusCode,
                LocalDateTime.now()
        );
    }

    public static String WARNING(String url) {
        return MessageFormat.format(
                "Retry calling URL: {0} | Timestamp: {1}",
                url,
                LocalDateTime.now()
        );
    }

    public static String ERROR(String url, int statusCode) {
        return MessageFormat.format(
                "Error calling URL: {0} | Status code: {1} | Timestamp: {2}",
                url,
                statusCode,
                LocalDateTime.now()
        );
    }

    public static String UNKNOWN(String url) {
        return MessageFormat.format(
                "Unknown error calling URL: {0} | Timestamp: {1}",
                url,
                LocalDateTime.now()
        );
    }
}
