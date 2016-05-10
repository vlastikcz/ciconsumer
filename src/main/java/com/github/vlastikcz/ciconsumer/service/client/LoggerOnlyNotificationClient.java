package com.github.vlastikcz.ciconsumer.service.client;

import java.time.Duration;
import java.time.Instant;
import java.util.Random;

import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class LoggerOnlyNotificationClient implements RemoteNotificationClient {
    private static final int MAXIMAL_DELAY_IN_MILLISECONDS = 4000;

    @Override
    public boolean sendRequest(RemoteNotificationClientRequest remoteNotificationClientRequest) {
        final Instant startTime = Instant.now();
        log.info("action.call=sendRequest, arguments=[{}]", remoteNotificationClientRequest);
        try {
            Thread.sleep(new Random().nextInt(MAXIMAL_DELAY_IN_MILLISECONDS));
        } catch (InterruptedException e) {
            log.error("action.fail=sendRequest", e);
        }
        final boolean result = new Random().nextBoolean();
        log.info("action.result=sendRequest, result=[{}], duration=[{}]", result, Duration.between(startTime, Instant.now()));
        return result;
    }
}
