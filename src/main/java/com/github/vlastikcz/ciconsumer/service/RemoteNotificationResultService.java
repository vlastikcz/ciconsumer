package com.github.vlastikcz.ciconsumer.service;

import java.util.List;

import org.springframework.stereotype.Component;

import com.github.vlastikcz.ciconsumer.domain.entity.ReleaseDetailNotificationTask;
import com.github.vlastikcz.ciconsumer.domain.entity.RemoteNotificationStatus;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class RemoteNotificationResultService {

    public boolean findIfNotificationTaskHasAnyFailure(ReleaseDetailNotificationTask releaseDetailNotificationTask,
                                                       List<RemoteNotificationStatus> remoteNotificationStatuses) {
        log.debug("action.call=findIfNotificationTaskHasAnyFailure, arguments=[{}, {}]", releaseDetailNotificationTask, remoteNotificationStatuses);
        final boolean result = anyNotificationHasFailed(remoteNotificationStatuses);
        log.debug("action.result=findIfNotificationTaskHasAnyFailure, result=[{}]", result);
        return result;
    }

    private static boolean anyNotificationHasFailed(List<RemoteNotificationStatus> remoteNotificationStatuses) {
        return remoteNotificationStatuses.stream().anyMatch(s -> !s.isFinished());
    }
}
