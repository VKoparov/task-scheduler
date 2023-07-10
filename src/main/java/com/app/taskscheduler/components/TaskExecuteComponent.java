package com.app.taskscheduler.components;

import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.annotation.Scheduled;

import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_PROTOTYPE;

@Scope(SCOPE_PROTOTYPE)
public class TaskExecuteComponent {

    private final Runnable executableProcess;

    public TaskExecuteComponent(Runnable executableProcess) {
        this.executableProcess = executableProcess;
    }

    @Scheduled(cron = "${cron.timeout}")
    public void execute() {
        this.executableProcess.run();
    }
}
