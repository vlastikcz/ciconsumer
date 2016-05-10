package com.github.vlastikcz.ciconsumer.domain.entity;

import java.util.ArrayList;
import java.util.List;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@EqualsAndHashCode
@ToString
public class ReleaseDetailState {
    @Getter
    private final ReleaseDetail releaseDetail;
    @Getter
    private final List<RemoteNotificationState> remoteNotificationStates;

    public ReleaseDetailState(ReleaseDetail releaseDetail, List<RemoteNotificationState> remoteNotificationStates) {
        this.releaseDetail = releaseDetail;
        this.remoteNotificationStates = new ArrayList<>(remoteNotificationStates);
    }
}
