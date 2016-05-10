package com.github.vlastikcz.ciconsumer.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.github.vlastikcz.ciconsumer.domain.entity.ReleaseDetailNotificationTask;
import com.github.vlastikcz.ciconsumer.domain.entity.RemoteNotificationStatus;
import com.github.vlastikcz.ciconsumer.service.target.RemoteNotificationTarget;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class RemoteNotificationService {

    private final ReleaseDetailNotificationTaskService releaseDetailNotificationTaskService;
    private final RemoteNotificationResultService remoteNotificationResultService;
    private final List<RemoteNotificationTarget> remoteNotificationTargets;

    @Autowired
    public RemoteNotificationService(ReleaseDetailNotificationTaskService releaseDetailNotificationTaskService,
                                     RemoteNotificationResultService remoteNotificationResultService,
                                     List<RemoteNotificationTarget> remoteNotificationTargets) {
        this.releaseDetailNotificationTaskService = releaseDetailNotificationTaskService;
        this.remoteNotificationResultService = remoteNotificationResultService;
        this.remoteNotificationTargets = remoteNotificationTargets;
    }

    @EventListener
    @Async
    public void sendRemoteNotificationsFromEvent(ReleaseDetailNotificationTaskCreateEvent event) {
        log.debug("action.call=sendRemoteNotificationsFromEvent, arguments=[{}]", event);
        sendRemoteNotifications();
        log.debug("action.done=sendRemoteNotificationsFromEvent");
    }

    void sendRemoteNotifications() {
        while (releaseDetailNotificationTaskService.hasNext()) {
            final ReleaseDetailNotificationTask releaseDetailNotificationTask = releaseDetailNotificationTaskService.poll();
            if (releaseDetailNotificationTask != null) {
                sendRemoteNotification(releaseDetailNotificationTask);
            }
        }
    }

    private void sendRemoteNotification(ReleaseDetailNotificationTask releaseDetailNotificationTask) {
        final List<RemoteNotificationStatus> result = remoteNotificationTargets.stream()
                .filter(t -> t != null)
                .map(t -> sendRemoteNotification(releaseDetailNotificationTask, t))
                .collect(Collectors.toList());
        remoteNotificationResultService.handleRemoteNotificationResult(releaseDetailNotificationTask, result);
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
