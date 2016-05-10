package com.github.vlastikcz.ciconsumer.service.target.eventsystem;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.github.vlastikcz.ciconsumer.domain.entity.ReleaseDetail;
import com.github.vlastikcz.ciconsumer.service.client.RemoteNotificationClient;
import com.github.vlastikcz.ciconsumer.service.client.RemoteNotificationClientRequest;
import com.github.vlastikcz.ciconsumer.service.target.RemoteNotificationTarget;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class EventSystemRemoteNotificationTarget implements RemoteNotificationTarget {
    private static final String EVENT_SYSTEM_URL = "http://event-system.local/";

    private final RemoteNotificationClient remoteNotificationClient;

    @Autowired
    public EventSystemRemoteNotificationTarget(RemoteNotificationClient remoteNotificationClient) {
        this.remoteNotificationClient = remoteNotificationClient;
    }

    @Override
    public boolean notify(ReleaseDetail releaseDetail) {
        final EventSystemMessage eventSystemMessage = EventSystemMessageFactory.fromReleaseDetail(releaseDetail);
        final RemoteNotificationClientRequest remoteNotificationClientRequest = new RemoteNotificationClientRequest(EVENT_SYSTEM_URL, eventSystemMessage);
        return remoteNotificationClient.sendRequest(remoteNotificationClientRequest);
    }
}
