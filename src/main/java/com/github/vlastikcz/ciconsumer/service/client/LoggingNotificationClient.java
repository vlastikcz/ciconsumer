package com.github.vlastikcz.ciconsumer.service.client;

import java.util.Random;

import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class LoggingNotificationClient implements RemoteNotificationClient {
    @Override
    public boolean sendRequest(RemoteNotificationClientRequest remoteNotificationClientRequest) {
        log.info("action.call=sendRequest, arguments=[{}]", remoteNotificationClientRequest);
        final boolean result = new Random().nextBoolean();
        log.info("action.result=sendRequest, result=[{}]", result);
        return result;
    }
}
