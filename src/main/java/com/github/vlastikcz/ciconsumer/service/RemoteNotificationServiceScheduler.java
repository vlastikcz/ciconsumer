package com.github.vlastikcz.ciconsumer.service;

import java.util.concurrent.ScheduledFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;

import com.github.vlastikcz.ciconsumer.service.event.ReleaseDetailNotificationTaskQueueTriggerEvent;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class RemoteNotificationServiceScheduler {
    private static final int NOTIFICATION_JOB_DELAY_IN_MILLISECONDS = 15_000;

    private final TaskScheduler taskScheduler;
    private final ApplicationEventPublisher applicationEventPublisher;

    private ScheduledFuture scheduledFuture;

    @Autowired
    public RemoteNotificationServiceScheduler(TaskScheduler taskScheduler, ApplicationEventPublisher applicationEventPublisher) {
        this.taskScheduler = taskScheduler;
        this.applicationEventPublisher = applicationEventPublisher;
    }

    public void schedule() {
        if (scheduledFuture == null || scheduledFuture.isCancelled()) {
            log.debug("action.result=schedule, result=[scheduling started]");
            scheduledFuture = taskScheduler.scheduleWithFixedDelay(
                    () -> applicationEventPublisher.publishEvent(new ReleaseDetailNotificationTaskQueueTriggerEvent()),
                    NOTIFICATION_JOB_DELAY_IN_MILLISECONDS
            );
        }
    }

    public void unSchedule() {
        if (scheduledFuture != null) {
            log.debug("action.result=unSchedule, result=[scheduling cancelled]");
            scheduledFuture.cancel(false);
        }
    }

}
