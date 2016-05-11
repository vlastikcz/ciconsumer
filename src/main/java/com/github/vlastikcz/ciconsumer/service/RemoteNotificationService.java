package com.github.vlastikcz.ciconsumer.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.github.vlastikcz.ciconsumer.domain.entity.ReleaseDetailNotificationTask;
import com.github.vlastikcz.ciconsumer.domain.entity.RemoteNotificationStatus;
import com.github.vlastikcz.ciconsumer.service.event.ReleaseDetailNotificationTaskCreateEvent;
import com.github.vlastikcz.ciconsumer.service.event.ReleaseDetailNotificationTaskQueueTriggerEvent;
import com.github.vlastikcz.ciconsumer.service.target.RemoteNotificationTarget;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class RemoteNotificationService {
    private final ReleaseDetailNotificationTaskService releaseDetailNotificationTaskService;
    private final RemoteNotificationResultService remoteNotificationResultService;
    private final List<RemoteNotificationTarget> remoteNotificationTargets;
    private final RemoteNotificationServiceScheduler remoteNotificationServiceScheduler;

    @Autowired
    public RemoteNotificationService(ReleaseDetailNotificationTaskService releaseDetailNotificationTaskService,
                                     RemoteNotificationResultService remoteNotificationResultService,
                                     List<RemoteNotificationTarget> remoteNotificationTargets, RemoteNotificationServiceScheduler
                                                 remoteNotificationServiceScheduler) {
        this.releaseDetailNotificationTaskService = releaseDetailNotificationTaskService;
        this.remoteNotificationResultService = remoteNotificationResultService;
        this.remoteNotificationTargets = remoteNotificationTargets;
        this.remoteNotificationServiceScheduler = remoteNotificationServiceScheduler;
    }

    @EventListener
    @Async
    public void sendRemoteNotificationsFromEvent(ReleaseDetailNotificationTaskCreateEvent event) {
        log.debug("action.call=sendRemoteNotificationsFromEvent, arguments=[{}]", event);
        final ReleaseDetailNotificationTask releaseDetailNotificationTask = event.getReleaseDetailNotificationTask();
        final List<RemoteNotificationStatus> result = sendRemoteNotification(releaseDetailNotificationTask);
        reQueueIfNotFinished(releaseDetailNotificationTask, result);
        setupScheduledQueueProcessor();
        log.debug("action.done=sendRemoteNotificationsFromEvent");
    }

    @EventListener
    public void sendQueuedRemoteNotifications(ReleaseDetailNotificationTaskQueueTriggerEvent releaseDetailNotificationTaskQueueTriggerEvent) {
        log.debug("action.call=sendQueuedRemoteNotifications, arguments=[{}]", releaseDetailNotificationTaskQueueTriggerEvent);
        sendQueuedRemoteNotifications();
        log.debug("action.done=sendQueuedRemoteNotifications");
    }

    void sendQueuedRemoteNotifications() {
        final Map<ReleaseDetailNotificationTask, List<RemoteNotificationStatus>> result = new HashMap<>();
        while (releaseDetailNotificationTaskService.hasNext()) {
            final ReleaseDetailNotificationTask releaseDetailNotificationTask = releaseDetailNotificationTaskService.poll();
            if (releaseDetailNotificationTask != null) {
                result.put(releaseDetailNotificationTask, sendRemoteNotification(releaseDetailNotificationTask));
            }
        }
        reQueueIfNotFinished(result);
        setupScheduledQueueProcessor();
    }

    private void setupScheduledQueueProcessor() {
        if (releaseDetailNotificationTaskService.queueEmpty()) {
            remoteNotificationServiceScheduler.unSchedule();
        } else {
            remoteNotificationServiceScheduler.schedule();
        }
    }

    private void reQueueIfNotFinished(Map<ReleaseDetailNotificationTask, List<RemoteNotificationStatus>> result) {
        result.entrySet().stream().forEach(e -> reQueueIfNotFinished(e.getKey(), e.getValue()));
    }

    private void reQueueIfNotFinished(ReleaseDetailNotificationTask releaseDetailNotificationTask, List<RemoteNotificationStatus> result) {
        if (remoteNotificationResultService.findIfNotificationTaskHasAnyFailure(releaseDetailNotificationTask, result)) {
            releaseDetailNotificationTaskService.reQueue(new ReleaseDetailNotificationTask(releaseDetailNotificationTask.getReleaseDetail(), result));
        }
    }

    private List<RemoteNotificationStatus> sendRemoteNotification(ReleaseDetailNotificationTask releaseDetailNotificationTask) {
        return remoteNotificationTargets.stream()
                .filter(t -> t != null)
                .map(t -> sendRemoteNotification(releaseDetailNotificationTask, t))
                .collect(Collectors.toList());
    }

    private RemoteNotificationStatus sendRemoteNotification(ReleaseDetailNotificationTask releaseDetailNotificationTask, RemoteNotificationTarget
            remoteNotificationTarget) {
        if (targetAlreadyNotified(releaseDetailNotificationTask.getRemoteNotificationStatuses(), remoteNotificationTarget)) {
            return new RemoteNotificationStatus(remoteNotificationTarget, true);
        }

        final boolean result = remoteNotificationTarget.notify(releaseDetailNotificationTask.getReleaseDetail());
        return new RemoteNotificationStatus(remoteNotificationTarget, result);
    }

    private static boolean targetAlreadyNotified(List<RemoteNotificationStatus> remoteNotificationStatuses,
                                                 RemoteNotificationTarget remoteNotificationTarget) {
        return remoteNotificationStatuses.stream().anyMatch(s -> s.getRemoteNotificationTarget().equals(remoteNotificationTarget) && s.isFinished());
    }

}
