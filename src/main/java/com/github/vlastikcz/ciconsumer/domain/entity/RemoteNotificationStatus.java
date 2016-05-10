package com.github.vlastikcz.ciconsumer.domain.entity;

import com.github.vlastikcz.ciconsumer.service.target.RemoteNotificationTarget;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@EqualsAndHashCode
@ToString
public class RemoteNotificationStatus {
    @Getter
    private final RemoteNotificationTarget remoteNotificationTarget;
    @Getter
    private final boolean finished;

    public RemoteNotificationStatus(RemoteNotificationTarget remoteNotificationTarget, boolean finished) {
        this.remoteNotificationTarget = remoteNotificationTarget;
        this.finished = finished;
    }

}
