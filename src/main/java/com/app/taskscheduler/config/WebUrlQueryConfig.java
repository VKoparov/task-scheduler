package com.app.taskscheduler.config;

import com.app.taskscheduler.components.TaskExecuteComponent;
import com.app.taskscheduler.services.WebUrlQueryService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WebUrlQueryConfig {

    @Value("${escape-token.value}")
    private String escapeToken;

    @Value("${web-urls}")
    private String[] webUrls;

    @Bean
    public TaskExecuteComponent taskExecuteComponent() {
        return new TaskExecuteComponent(new WebUrlQueryService(escapeToken, webUrls));
    }
}
