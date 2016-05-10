package com.github.vlastikcz.ciconsumer.service.target.logsystem;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.github.vlastikcz.ciconsumer.domain.entity.ReleaseDetail;
import com.github.vlastikcz.ciconsumer.service.client.RemoteNotificationClient;
import com.github.vlastikcz.ciconsumer.service.client.RemoteNotificationClientRequest;
import com.github.vlastikcz.ciconsumer.service.target.RemoteNotificationTarget;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class LogSystemRemoteNotificationTarget implements RemoteNotificationTarget {
    private static final String LOG_SYSTEM_URL = "http://log-system.local/";

    private final RemoteNotificationClient remoteNotificationClient;

    @Autowired
    public LogSystemRemoteNotificationTarget(RemoteNotificationClient remoteNotificationClient) {
        this.remoteNotificationClient = remoteNotificationClient;
    }

    @Override
    public boolean notify(ReleaseDetail releaseDetail) {
        final LogSystemMessage logSystemMessage = LogSystemMessageFactory.fromReleaseDetail(releaseDetail);
        final RemoteNotificationClientRequest remoteNotificationClientRequest = new RemoteNotificationClientRequest(LOG_SYSTEM_URL, logSystemMessage);
        return remoteNotificationClient.sendRequest(remoteNotificationClientRequest);
    }
}
