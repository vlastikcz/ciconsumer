package com.github.vlastikcz.ciconsumer.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.github.vlastikcz.ciconsumer.domain.entity.ReleaseDetailNotificationTask;
import com.github.vlastikcz.ciconsumer.domain.entity.RemoteNotificationStatus;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class RemoteNotificationResultService {
    private final ReleaseDetailNotificationTaskService releaseDetailNotificationTaskService;

    @Autowired
    public RemoteNotificationResultService(ReleaseDetailNotificationTaskService releaseDetailNotificationTaskService) {
        this.releaseDetailNotificationTaskService = releaseDetailNotificationTaskService;
    }

    public void handleRemoteNotificationResult(ReleaseDetailNotificationTask releaseDetailNotificationTask, List<RemoteNotificationStatus> result) {
        log.debug("action.call=updateReleaseDetailStateQueue, arguments=[{}, {}]", releaseDetailNotificationTask, result);
        if (anyNotificationHasFailed(result)) {
            releaseDetailNotificationTaskService.reQueue(new ReleaseDetailNotificationTask(releaseDetailNotificationTask.getReleaseDetail(), result));
            log.debug("action.result=updateReleaseDetailStateQueue, result=[re-queued]");
        } else {
            log.debug("action.result=updateReleaseDetailStateQueue, result=[done]");
        }
    }

    private static boolean anyNotificationHasFailed(List<RemoteNotificationStatus> remoteNotificationStatuses) {
        return remoteNotificationStatuses.stream().anyMatch(s -> !s.isFinished());
    }
}
