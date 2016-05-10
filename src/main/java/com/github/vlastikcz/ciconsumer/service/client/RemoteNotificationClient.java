package com.github.vlastikcz.ciconsumer.service.client;

public interface RemoteNotificationClient {
    boolean sendRequest(RemoteNotificationClientRequest remoteNotificationClientRequest);
}
