package com.github.vlastikcz.ciconsumer.service.target.eventsystem;

import com.github.vlastikcz.ciconsumer.domain.entity.ReleaseDetail;

public class EventSystemMessageFactory {
    public static EventSystemMessage fromReleaseDetail(ReleaseDetail releaseDetail) {
        return new EventSystemMessage(releaseDetail.getVersion(), releaseDetail.getTimestamp().getEpochSecond());
    }
}
