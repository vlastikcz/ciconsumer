package com.github.vlastikcz.ciconsumer.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.github.vlastikcz.ciconsumer.domain.entity.ReleaseDetailState;
import com.github.vlastikcz.ciconsumer.domain.entity.RemoteNotificationState;
import com.github.vlastikcz.ciconsumer.service.target.RemoteNotificationTarget;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class RemoteNotificationService {
    private static final int NOTIFICATION_DELAY_IN_MILLISECONDS = 5000;

    private final ReleaseDetailStateService releaseDetailStateService;
    private final List<RemoteNotificationTarget> remoteNotificationTargets;

    @Autowired
    public RemoteNotificationService(ReleaseDetailStateService releaseDetailStateService,
                                     List<RemoteNotificationTarget> remoteNotificationTargets) {
        this.releaseDetailStateService = releaseDetailStateService;
        this.remoteNotificationTargets = remoteNotificationTargets;
    }

    @Async
    @Scheduled(fixedDelay = NOTIFICATION_DELAY_IN_MILLISECONDS)
    @EventListener(ReleaseDetailStateSaveNewEvent.class)
    public void sendRemoteNotifications() {
        log.debug("action.call=sendRemoteNotifications");
        releaseDetailStateService.asStream().forEach(s -> sendRemoteNotification(s));
        log.debug("action.done=sendRemoteNotifications");
    }

    private void sendRemoteNotification(ReleaseDetailState releaseDetailState) {
        final List<RemoteNotificationState> result = remoteNotificationTargets.stream()
                .map(t -> sendRemoteNotification(releaseDetailState, t))
                .collect(Collectors.toList());
        updateReleaseDetailStateQueue(releaseDetailState, result);
    }

    private void updateReleaseDetailStateQueue(ReleaseDetailState releaseDetailState, List<RemoteNotificationState> result) {
        log.debug("action.call=updateReleaseDetailStateQueue, arguments=[{}, {}]", releaseDetailState, result);
        if (allNotificationFinished(result)) {
            releaseDetailStateService.delete(releaseDetailState);
            log.debug("action.result=updateReleaseDetailStateQueue, result=[done]");
        } else {
            releaseDetailStateService.update(releaseDetailState, new ReleaseDetailState(releaseDetailState.getReleaseDetail(), result));
            log.debug("action.result=updateReleaseDetailStateQueue, result=[re-queued]");
        }
    }

    private RemoteNotificationState sendRemoteNotification(ReleaseDetailState releaseDetailState, RemoteNotificationTarget remoteNotificationTarget) {
        if (targetAlreadyNotified(releaseDetailState.getRemoteNotificationStates(), remoteNotificationTarget)) {
            return new RemoteNotificationState(remoteNotificationTarget, true);
        }

        final boolean result = remoteNotificationTarget.notify(releaseDetailState.getReleaseDetail());
        return new RemoteNotificationState(remoteNotificationTarget, result);
    }

    private static boolean targetAlreadyNotified(List<RemoteNotificationState> remoteNotificationStates,
                                                 RemoteNotificationTarget remoteNotificationTarget) {
        return remoteNotificationStates.stream().anyMatch(s -> s.getRemoteNotificationTarget().equals(remoteNotificationTarget) && s.isFinished());
    }

    private static boolean allNotificationFinished(List<RemoteNotificationState> remoteNotificationStates) {
        return remoteNotificationStates.stream().allMatch(s -> s.isFinished());
    }
}
