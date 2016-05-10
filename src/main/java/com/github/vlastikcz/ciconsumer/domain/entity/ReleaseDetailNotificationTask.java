package com.github.vlastikcz.ciconsumer.domain.entity;

import java.util.ArrayList;
import java.util.List;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@EqualsAndHashCode
@ToString
public class ReleaseDetailNotificationTask {
    @Getter
    private final ReleaseDetail releaseDetail;
    @Getter
    private final List<RemoteNotificationStatus> remoteNotificationStatuses;

    public ReleaseDetailNotificationTask(ReleaseDetail releaseDetail, List<RemoteNotificationStatus> remoteNotificationStatuses) {
        this.releaseDetail = releaseDetail;
        this.remoteNotificationStatuses = new ArrayList<>(remoteNotificationStatuses);
    }
}
